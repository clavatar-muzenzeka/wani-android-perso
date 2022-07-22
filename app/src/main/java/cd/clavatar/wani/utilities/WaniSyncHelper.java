package cd.clavatar.wani.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.orm.query.Condition;
import com.orm.query.Select;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.json.JSONObject;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.ChangePasswordBody;
import cd.clavatar.wani.data.model.Check;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.CompactStatus;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.GlobalState;
import cd.clavatar.wani.data.model.Location;
import cd.clavatar.wani.data.model.LoginResult;
import cd.clavatar.wani.data.model.PaimentType;
import cd.clavatar.wani.data.model.People;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.Story;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.data.model.WaniNetworkDataSource;
import cd.clavatar.wani.data.model.WaniParameter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* renamed from: cd.clavatar.wani.utilities.WaniSyncHelper */
public class WaniSyncHelper {
    private static WaniSyncHelper INSTANCE = null;
    private static final int PAIMENT_TYPE_ITERATION_LIMIT = 3;
    private static final int VACCIN_ITERATION_LIMIT = 3;
    private static final int SERVER_DELAY=10*60*1000;
    private static ArrayList<Throwable> errors = new ArrayList<>();
    /* access modifiers changed from: private */

    /* renamed from: i */
    public static int f88i;
    public static Context mContext;
    /* access modifiers changed from: private */
    public static WaniDataSource mDataSource;
    private static WaniNetworkDataSource mNetworkDataSource;
    static SyncThread mSyncThread = null;
    private static ArrayList<WaniSynkCallback> synkCallbacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public static ArrayList<Date> synkRequests = new ArrayList<>();
    /* access modifiers changed from: private */
    public static boolean synking = false;

    /* renamed from: cd.clavatar.wani.utilities.WaniSyncHelper$WaniSynkCallback */
    public interface WaniSynkCallback {
        void onSynced(ArrayList<Throwable> arrayList);

        void onSynkError(ArrayList<Throwable> arrayList);

        void onSynkStarted();

        void onValidationError(ArrayList<Throwable> arrayList);

        void onProgress(Integer progressValue);
    }

    static /* synthetic */ int access$2408() {
        int i = f88i;
        f88i = i + 1;
        return i;
    }

    public static WaniSyncHelper getInstance(Context pContext) {
        if (INSTANCE == null) {
            synchronized (WaniSyncHelper.class) {
                INSTANCE = new WaniSyncHelper();
            }
        }
        INSTANCE.setContext(pContext);
        return INSTANCE;
    }

    public WaniSyncHelper() {
        mNetworkDataSource = WaniNetworkAccess.getInstanceForToken((String) null, mContext);
        mDataSource = WaniDataSource.getInstance(mContext);
    }

    private void setContext(Context pContext) {
        mContext = pContext;
    }

    public void procedSynk(WaniSynkCallback callback) {
        Log.d("zaza", String.format("synking %b", new Object[]{Boolean.valueOf(synking)}));
        Common.backgroundWork.postValue(true);
        synkCallbacks.add(callback);
        synkRequests.add(new Date());
        if (!synking) {
            synking = true;
            mNetworkDataSource.checkApi().enqueue(new Callback<JSONObject>() {
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.code() == 200) {
                        WaniSyncHelper.procedSynkRequests(WaniSyncHelper.synkRequests);
                    }
                }

                public void onFailure(Call<JSONObject> call, Throwable t) {
                    boolean unused = WaniSyncHelper.synking = false;
                    ArrayList<Throwable> mErrors = new ArrayList<>();
                    mErrors.add(t);
                    WaniSyncHelper.captureNadConsignFaillure(mErrors);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public static void procedSynkRequests(ArrayList<Date> synkRequests2) {
        if (synkRequests2.size() > 0) {
            synking = true;
            SyncThread.getInstance(new Handler(Looper.getMainLooper()) {
                public void handleMessage(Message msg) {
                    if (Integer.valueOf(msg.arg1).intValue() == 0) {
                        WaniSyncHelper.captureAndConsignSynced((ArrayList) msg.obj);
                    } else
                        if (Integer.valueOf(msg.arg1).intValue() == 1){
                            WaniSyncHelper.consignValidationError((ArrayList) msg.obj);
                        }
                        else
                        {
                        WaniSyncHelper.consignProgress((Integer) msg.obj);
                    }
                }
            }, mDataSource, mNetworkDataSource).start();
        }
    }

    private static void consignProgress(Integer progress) {
        Iterator<WaniSynkCallback> it = synkCallbacks.iterator();
        while (it.hasNext()) {
            WaniSynkCallback mcb = it.next();
            if (mcb != null) {
                mcb.onProgress(progress);
            }
        }
    }

    public static void captureAndConsignSynced(ArrayList<Throwable> errors2) {
        synking = false;
        Iterator<WaniSynkCallback> it = synkCallbacks.iterator();
        while (it.hasNext()) {
            WaniSynkCallback mcb = it.next();
            if (mcb != null) {
                mcb.onSynced(errors2);
            }
        }
        Iterator<Throwable> it2 = errors2.iterator();
        while (it2.hasNext()) {
            Throwable er = it2.next();
            if (er.getMessage() != null) {
                Log.d("zaza", "Error occured" + er.getMessage());
            }
        }
        synkCallbacks.clear();
        errors2.clear();
    }

    /* access modifiers changed from: private */
    public static void captureNadConsignFaillure(ArrayList<Throwable> errors2) {
        Iterator<WaniSynkCallback> it = synkCallbacks.iterator();
        while (it.hasNext()) {
            WaniSynkCallback mcb = it.next();
            if (mcb != null) {
                mcb.onSynkError(errors2);
            }
        }
        errors2.clear();
    }

    public static void consignValidationError(ArrayList<Throwable> errors2) {
        Iterator<WaniSynkCallback> it = synkCallbacks.iterator();
        while (it.hasNext()) {
            WaniSynkCallback mcb = it.next();
            if (mcb != null) {
                mcb.onValidationError(errors2);
            }
        }
        errors2.clear();
    }

    /* renamed from: cd.clavatar.wani.utilities.WaniSyncHelper$SyncThread */
    static class SyncThread extends Thread {
        private static SyncThread INSTANCE = null;
        /* access modifiers changed from: private */
        public ArrayList<Throwable> errors = new ArrayList<>();
        /* access modifiers changed from: private */
        public final WaniDataSource mDataSource;
        private Handler mHandler;
        /* access modifiers changed from: private */
        public WaniNetworkDataSource mNetworkDataSource;
        /* access modifiers changed from: private */
        public int paimentTypeInterations = 0;
        /* access modifiers changed from: private */
        public int vaccinInterations = 0;
        private Long HORIZON_MILLIS;

        /* renamed from: cd.clavatar.wani.utilities.WaniSyncHelper$SyncThread$GloblaStateInpuTask */
        class GloblaStateInpuTask extends AsyncTask<Response<GlobalState>, Void, String> {
            GloblaStateInpuTask() {
            }

            /* access modifiers changed from: protected */
            public String doInBackground(Response<GlobalState>... responses) {
                Date lastCandidateDate = null;
                try {
                    lastCandidateDate = ISO8601.toCalendar(responses[0].headers().get("lastCandidate")).getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SyncThread.this.mDataSource.insertWaniParametersOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getParameters());
                SyncThread.this.mDataSource.insertLocationsOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getLocations());
                SyncThread.this.mDataSource.insertPeoplesOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getPeoples());
                SyncThread.this.mDataSource.insertCompactUsersOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getUsers());
                SyncThread.this.mDataSource.insertPaimentTypesOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getPaiementTypes());
                SyncThread.this.mDataSource.insertCompactPaiementsOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getPaiements());
                SyncThread.this.mDataSource.insertChecksOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getChecks());
                SyncThread.this.mDataSource.insertVaccinsOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getVaccins());
                SyncThread.this.mDataSource.insertVaccinOccurencesOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getVaccinOccurences());
                SyncThread.this.mDataSource.insertCompactStatussOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getCarnetStatus());
                SyncThread.this.mDataSource.insertStorysOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getStorys());
                SyncThread.this.mDataSource.insertCompactCarnetsOrUpdateByRemoteId(lastCandidateDate, responses[0].body().getCarnets());
                return responses[0].headers().get("lastCandidate");
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(String lastCandidate) {
                Log.d("zaza", "Within on post execute "+lastCandidate);
                SyncThread.this.mNetworkDataSource.validateSync(lastCandidate, "syncglobal").enqueue(new Callback<Object>() {
                    public void onResponse(Call<Object> call, Response<Object> response) {
                        if(WaniApp.isFirstAccess()){
                            mContext.getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putBoolean(WaniApp.ALREADY_ACCESSED_KEY, true).commit();
                            WaniApp.setAlradyAccessed();
                        }
                        try {
                            Date lastCandidateDate = ISO8601.toCalendar(lastCandidate).getTime();
                            Date today = new Date();
                            Long progressMillis = today.getTime()- lastCandidateDate.getTime();

                            if( progressMillis> SERVER_DELAY ){
                                SyncThread.this.consignProgress((Integer) Math.round((progressMillis/HORIZON_MILLIS)*100));
                                Log.d("zaza", "Another sync request will be handled" );
                                synkRequests.add(new Date());

                                WaniSyncHelper.procedSynkRequests(WaniSyncHelper.synkRequests);
                            } else  {
                                Log.d("zaza", "last validated date "+lastCandidate );
                                SyncThread.this.consignSynced();}
                        } catch (ParseException e) {
                            Log.d("zaza", "Exception occured in finish stub");
                            e.printStackTrace();
                            SyncThread.this.consignSynced();
                        }


                    }

                    public void onFailure(Call<Object> call, Throwable t) {
                        Log.d("zaza", "Error on validate sync");
                        SyncThread.this.errors.add(t);
                        WaniSyncHelper.consignFaillure(SyncThread.this.errors);
                    }
                });
            }
        }


        public SyncThread(Handler handler, WaniDataSource dataSource, WaniNetworkDataSource networkDataSource) {
            this.mHandler = handler;
            this.mDataSource = dataSource;
            this.mNetworkDataSource = networkDataSource;
            Date horizonDate = null;
            try {
                horizonDate = ISO8601.toCalendar("2020-08-30T00:00:00.000Z").getTime();
                Date today = new Date();
                HORIZON_MILLIS = today.getTime() - horizonDate.getTime();
            } catch (ParseException e) {

                e.printStackTrace();
            }
        }

        public static SyncThread getInstance(Handler mHandler2, WaniDataSource mDataSource2, WaniNetworkDataSource mNetworkDataSource2) {
            SyncThread syncThread = new SyncThread(mHandler2, mDataSource2, mNetworkDataSource2);
            INSTANCE = syncThread;
            return syncThread;
        }

        public void run() {
            syncDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncUpToDown() {
            Log.d("zaza", "before call");
            this.mNetworkDataSource.loadGlobalState(WaniApp.isFirstAccess()).enqueue(new Callback<GlobalState>() {
                public void onResponse(Call<GlobalState> call, Response<GlobalState> response) {
                    Log.d("zaza", "Au moins la réponse est là");
                    if (response.code() != 200 || response.body() == null) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path global"));
                        } else {
                            Log.d("zaza", "je te tiens");
                        }
                        SyncThread.this.consignSynced();
                        return;
                    }
                    new GloblaStateInpuTask().execute(new Response[]{response});
                }

                public void onFailure(Call<GlobalState> call, Throwable t) {
                    Log.d("zaza", "Merde elle est où la réponse");
                    t.printStackTrace();
                    SyncThread.this.errors.add(t);
                    //SyncThread.this.syncWaniParameterUpToDown();
                    SyncThread.this.consignSynced();
                }
            });
        }

        private void syncLocationUpToDown() {
            this.mNetworkDataSource.loadSyncableLocations(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<Location>>() {
                public void onResponse(Call<ArrayList<Location>> call, Response<ArrayList<Location>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncWaniParameterUpToDown();
                        return;
                    }
                    ArrayList<Location> mLocations = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<Location> it = mLocations.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertLocationsOrUpdateByRemoteId(lastCandidateDate, mLocations);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "location").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            SyncThread.this.syncWaniParameterUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncWaniParameterUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<Location>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncWaniParameterUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncWaniParameterUpToDown() {
            this.mNetworkDataSource.loadSyncableWaniParameters(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<WaniParameter>>() {
                public void onResponse(Call<ArrayList<WaniParameter>> call, Response<ArrayList<WaniParameter>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncPictureUpToDown();
                        return;
                    }
                    final ArrayList<WaniParameter> mWaniParameters = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<WaniParameter> it = mWaniParameters.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertWaniParametersOrUpdateByRemoteId(lastCandidateDate, mWaniParameters);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "parameter").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            WaniApp.setParameter((WaniParameter) mWaniParameters.get(0));
                            SyncThread.this.syncPictureUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncPictureUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<WaniParameter>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncPictureUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncPictureUpToDown() {
            this.mNetworkDataSource.loadSyncablePictures(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<Picture>>() {
                public void onResponse(Call<ArrayList<Picture>> call, Response<ArrayList<Picture>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncPeopleUpToDown();
                        return;
                    }
                    ArrayList<Picture> mPictures = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<Picture> it = mPictures.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertPicturesOrUpdateByRemoteId(lastCandidateDate, mPictures);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "picture").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncPeopleUpToDown();
                            } else if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                                SyncThread.this.syncPeopleUpToDown();
                            }
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncPeopleUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<Picture>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncPeopleUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncPeopleUpToDown() {
            this.mNetworkDataSource.loadSyncablePeoples(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<People>>() {
                public void onResponse(Call<ArrayList<People>> call, Response<ArrayList<People>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncCompactUserUpToDown();
                        return;
                    }
                    ArrayList<People> mPeoples = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SyncThread.this.mDataSource.insertPeoplesOrUpdateByRemoteId(lastCandidateDate, mPeoples);
                    Iterator<People> it = mPeoples.iterator();
                    while (it.hasNext()) {
                        People mPeople = it.next();
                        mPeople.setSynced(lastCandidateDate);
                        Picture mPc = SyncThread.this.mDataSource.getPictureByRemoteId(mPeople.getIdProfile());
                        if (mPc != null) {
                            mPeople.setLocaleIdProfile(mPc.getId());
                            mPeople.update();
                        }
                    }
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "people").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncCompactUserUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncCompactUserUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncCompactUserUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<People>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncCompactUserUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncCompactUserUpToDown() {
            this.mNetworkDataSource.loadSyncableCompactUsers(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<CompactUser>>() {
                public void onResponse(Call<ArrayList<CompactUser>> call, Response<ArrayList<CompactUser>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncPaimentTypeUpToDown();
                        return;
                    }
                    ArrayList<CompactUser> mCompactUsers = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<CompactUser> it = mCompactUsers.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    WaniSyncHelper.mDataSource.insertCompactUsersOrUpdateByRemoteId(lastCandidateDate, mCompactUsers);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "user").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncPaimentTypeUpToDown();
                            } else if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                                SyncThread.this.syncPaimentTypeUpToDown();
                            }
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncPaimentTypeUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<CompactUser>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncPaimentTypeUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncPaimentTypeUpToDown() {
            this.paimentTypeInterations++;
            this.mNetworkDataSource.loadSyncablePaimentTypes(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<PaimentType>>() {
                public void onResponse(Call<ArrayList<PaimentType>> call, Response<ArrayList<PaimentType>> response) {
                    if (response.code() == 200 && response.body() != null && response.body().size() > 0) {
                        ArrayList<PaimentType> mPaimentTypes = response.body();
                        Date lastCandidateDate = null;
                        try {
                            lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Iterator<PaimentType> it = mPaimentTypes.iterator();
                        while (it.hasNext()) {
                            it.next().setSynced(lastCandidateDate);
                        }
                        SyncThread.this.mDataSource.insertPaimentTypesOrUpdateByRemoteId(lastCandidateDate, mPaimentTypes);
                        if (((long) Integer.parseInt(response.headers().get("count"))) == PaimentType.count(PaimentType.class)) {
                            SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "paiementtype").enqueue(new Callback<Object>() {
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    if (response.code() == 200) {
                                        SyncThread.this.syncCompactPaiementUpToDown();
                                        return;
                                    }
                                    if (response.code() != 200) {
                                        SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                                    }
                                    SyncThread.this.syncCompactPaiementUpToDown();
                                }

                                public void onFailure(Call<Object> call, Throwable t) {
                                    SyncThread.this.errors.add(t);
                                    SyncThread.this.syncCompactPaiementUpToDown();
                                }
                            });
                        } else if (SyncThread.this.paimentTypeInterations < 3) {
                            SyncThread.this.syncPaimentTypeUpToDown();
                        } else {
                            SyncThread.this.consignValidationError();
                        }
                    } else if (response.code() != 200) {
                        SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        if (!WaniApp.isFirstAccess()) {
                            SyncThread.this.syncCompactPaiementUpToDown();
                        } else if (SyncThread.this.paimentTypeInterations < 3) {
                            SyncThread.this.syncPaimentTypeUpToDown();
                        } else {
                            SyncThread.this.consignValidationError();
                        }
                    } else {
                        SyncThread.this.syncCompactPaiementUpToDown();
                    }
                }

                public void onFailure(Call<ArrayList<PaimentType>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    if (!WaniApp.isFirstAccess()) {
                        SyncThread.this.syncCompactPaiementUpToDown();
                    } else if (SyncThread.this.paimentTypeInterations < 3) {
                        SyncThread.this.syncPaimentTypeUpToDown();
                    } else {
                        SyncThread.this.consignValidationError();
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncCompactPaiementUpToDown() {
            this.mNetworkDataSource.loadSyncableCompactPaiements(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<CompactPaiement>>() {
                public void onResponse(Call<ArrayList<CompactPaiement>> call, Response<ArrayList<CompactPaiement>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncCheckUpToDown();
                        return;
                    }
                    ArrayList<CompactPaiement> mCompactPaiements = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<CompactPaiement> it = mCompactPaiements.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    WaniSyncHelper.mDataSource.insertCompactPaiementsOrUpdateByRemoteId(lastCandidateDate, mCompactPaiements);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "paiement").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncCheckUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncCheckUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncCheckUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<CompactPaiement>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncCheckUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncCheckUpToDown() {
            this.mNetworkDataSource.loadSyncableChecks(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<Check>>() {
                public void onResponse(Call<ArrayList<Check>> call, Response<ArrayList<Check>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncCompactStatusUpToDown();
                        return;
                    }
                    ArrayList<Check> mChecks = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<Check> it = mChecks.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertChecksOrUpdateByRemoteId(lastCandidateDate, mChecks);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "check").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncCompactStatusUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncCompactStatusUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncCompactStatusUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<Check>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncCompactStatusUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncCompactStatusUpToDown() {
            this.mNetworkDataSource.loadSyncableCompactStatus(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<CompactStatus>>() {
                public void onResponse(Call<ArrayList<CompactStatus>> call, Response<ArrayList<CompactStatus>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncStoryUpToDown();
                        return;
                    }
                    ArrayList<CompactStatus> mCompactStatuss = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<CompactStatus> it = mCompactStatuss.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertCompactStatussOrUpdateByRemoteId(lastCandidateDate, mCompactStatuss);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "carnetstatus").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncStoryUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncStoryUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncStoryUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<CompactStatus>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncStoryUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncStoryUpToDown() {
            this.mNetworkDataSource.loadSyncableStory(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<Story>>() {
                public void onResponse(Call<ArrayList<Story>> call, Response<ArrayList<Story>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncVaccinUpToDown();
                        return;
                    }
                    ArrayList<Story> mStorys = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SyncThread.this.mDataSource.insertStorysOrUpdateByRemoteId(lastCandidateDate, mStorys);
                    Iterator<Story> it = mStorys.iterator();
                    while (it.hasNext()) {
                        Story mStory = it.next();
                        mStory.setSynced(lastCandidateDate);
                        List<Check> mCks = mStory.getCheckStory();
                        if (mCks != null) {
                            for (Check mCk : mCks) {
                                mCk.setLocaleStoryId(mStory.getId());
                            }
                            SyncThread.this.mDataSource.insertChecksOrUpdateByRemoteId(lastCandidateDate, mCks);
                        }
                        List<CompactStatus> mcss = mStory.getStatusStory();
                        if (mcss != null) {
                            for (CompactStatus mCk2 : mcss) {
                                mCk2.setLocaleStoryId(mStory.getId());
                            }
                            SyncThread.this.mDataSource.insertCompactStatussOrUpdateByRemoteId(lastCandidateDate, mcss);
                        }
                    }
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "story").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncVaccinUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncVaccinUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncVaccinUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<Story>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncVaccinUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncVaccinUpToDown() {
            this.vaccinInterations++;
            this.mNetworkDataSource.loadSyncableVaccins(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<Vaccin>>() {
                public void onResponse(Call<ArrayList<Vaccin>> call, Response<ArrayList<Vaccin>> response) {
                    if (response.code() == 200 && response.body() != null && response.body().size() > 0) {
                        ArrayList<Vaccin> mVaccins = response.body();
                        Date lastCandidateDate = null;
                        try {
                            lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Iterator<Vaccin> it = mVaccins.iterator();
                        while (it.hasNext()) {
                            it.next().setSynced(lastCandidateDate);
                        }
                        SyncThread.this.mDataSource.insertVaccinsOrUpdateByRemoteId(lastCandidateDate, mVaccins);
                        if (((long) Integer.parseInt(response.headers().get("count"))) == Vaccin.count(Vaccin.class)) {
                            SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "vaccin").enqueue(new Callback<Object>() {
                                public void onResponse(Call<Object> call, Response<Object> response) {
                                    if (response.code() == 200) {
                                        SyncThread.this.syncVaccinOccurenceUpToDown();
                                        return;
                                    }
                                    if (response.code() != 200) {
                                        SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                                    }
                                    SyncThread.this.syncVaccinOccurenceUpToDown();
                                }

                                public void onFailure(Call<Object> call, Throwable t) {
                                    SyncThread.this.errors.add(t);
                                    SyncThread.this.syncVaccinOccurenceUpToDown();
                                }
                            });
                        } else if (SyncThread.this.vaccinInterations < 3) {
                            SyncThread.this.syncVaccinUpToDown();
                        } else {
                            SyncThread.this.consignValidationError();
                        }
                    } else if (response.code() != 200) {
                        SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        if (!WaniApp.isFirstAccess()) {
                            SyncThread.this.syncVaccinOccurenceUpToDown();
                        } else if (SyncThread.this.vaccinInterations < 3) {
                            SyncThread.this.syncVaccinUpToDown();
                        } else {
                            SyncThread.this.consignValidationError();
                        }
                    } else {
                        SyncThread.this.syncVaccinOccurenceUpToDown();
                    }
                }

                public void onFailure(Call<ArrayList<Vaccin>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    if (!WaniApp.isFirstAccess()) {
                        SyncThread.this.syncVaccinOccurenceUpToDown();
                    } else if (SyncThread.this.vaccinInterations < 3) {
                        SyncThread.this.syncVaccinUpToDown();
                    } else {
                        SyncThread.this.consignValidationError();
                    }
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncVaccinOccurenceUpToDown() {
            this.mNetworkDataSource.loadSyncableVaccinOccurences(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<VaccinOccurence>>() {
                public void onResponse(Call<ArrayList<VaccinOccurence>> call, Response<ArrayList<VaccinOccurence>> response) {
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.syncCompactCarnetUpToDown();
                        return;
                    }
                    ArrayList<VaccinOccurence> mVaccinOccurences = response.body();
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<VaccinOccurence> it = mVaccinOccurences.iterator();
                    while (it.hasNext()) {
                        it.next().setSynced(lastCandidateDate);
                    }
                    SyncThread.this.mDataSource.insertVaccinOccurencesOrUpdateByRemoteId(lastCandidateDate, mVaccinOccurences);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "vaccinoccurence").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.syncCompactCarnetUpToDown();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.syncCompactCarnetUpToDown();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            SyncThread.this.syncCompactCarnetUpToDown();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<VaccinOccurence>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.syncCompactCarnetUpToDown();
                }
            });
        }

        /* access modifiers changed from: private */
        public void syncCompactCarnetUpToDown() {
            this.mNetworkDataSource.loadSyncableCompactCarnets(WaniApp.isFirstAccess()).enqueue(new Callback<ArrayList<CompactCarnet>>() {
                public void onResponse(Call<ArrayList<CompactCarnet>> call, Response<ArrayList<CompactCarnet>> response) {
                    Log.d("zaza", String.format("carnets body %s", new Object[]{response.body().toString()}));
                    if (response.code() != 200 || response.body() == null || response.body().size() <= 0) {
                        if (response.code() != 200) {
                            SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                        }
                        SyncThread.this.consignSynced();
                        return;
                    }
                    ArrayList<CompactCarnet> mCompactCarnets = response.body();
                    Log.d("zaza", String.format("mCompactCarnets %s", new Object[]{Integer.valueOf(mCompactCarnets.size())}));
                    Date lastCandidateDate = null;
                    try {
                        lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Iterator<CompactCarnet> it = mCompactCarnets.iterator();
                    while (it.hasNext()) {
                        CompactCarnet mCompactCarnet = it.next();
                        Picture mPc = SyncThread.this.mDataSource.getPictureByRemoteId(mCompactCarnet.getPictureRawCarnet());
                        if (mPc != null) {
                            mCompactCarnet.setLocalePictureRawCarnet(mPc.getId());
                        }
                    }
                    SyncThread.this.mDataSource.insertCompactCarnetsOrUpdateByRemoteId(lastCandidateDate, mCompactCarnets);
                    SyncThread.this.mNetworkDataSource.validateSync(response.headers().get("lastCandidate"), "carnet").enqueue(new Callback<Object>() {
                        public void onResponse(Call<Object> call, Response<Object> response) {
                            if (response.code() == 200) {
                                SyncThread.this.consignSynced();
                                return;
                            }
                            if (response.code() != 200) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            SyncThread.this.consignSynced();
                        }

                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d("zaza", String.format("sync validation failled because %s", new Object[]{t.getMessage()}));
                            SyncThread.this.errors.add(t);
                            SyncThread.this.consignSynced();
                        }
                    });
                }

                public void onFailure(Call<ArrayList<CompactCarnet>> call, Throwable t) {
                    SyncThread.this.errors.add(t);
                    SyncThread.this.consignSynced();
                }
            });
        }

        private void syncDownToUp() {
            syncLocationDownToUp();
        }

        private void syncLocationDownToUp() {
            final List<Location> syncableLocations = this.mDataSource.fetchSyncableNewLocations();
            if (syncableLocations.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Location mSyncableLocation : syncableLocations) {
                    this.mNetworkDataSource.postSyncableLocation(mSyncableLocation, WaniApp.isFirstAccess()).enqueue(new Callback<Location>() {
                        public void onResponse(Call<Location> call, Response<Location> response) {
                            if (response.code() == 201) {
                                Location synckedLocation = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableLocation.setSynced(syncedDate);
                                mSyncableLocation.set_id(synckedLocation.get_id());
                                mSyncableLocation.update();
                                if (WaniSyncHelper.f88i == syncableLocations.size() - 1) {
                                    SyncThread.this.syncLocationDownToUpOld();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path location"));
                                if (WaniSyncHelper.f88i == syncableLocations.size() - 1) {
                                    SyncThread.this.syncLocationDownToUpOld();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            }
                        }

                        public void onFailure(Call<Location> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableLocations.size() - 1) {
                                SyncThread.this.syncLocationDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncLocationDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncLocationDownToUpOld() {
            final List<Location> syncableOldLocations = this.mDataSource.fetchSyncableOldLocations();
            if (syncableOldLocations.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Location mSyncableLocation : syncableOldLocations) {
                    this.mNetworkDataSource.putSyncableLocation(mSyncableLocation.get_id(), mSyncableLocation, WaniApp.isFirstAccess()).enqueue(new Callback<Location>() {
                        public void onResponse(Call<Location> call, Response<Location> response) {
                            if (response.code() == 202) {
                                Location synckedLocation = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableLocation.setSynced(syncedDate);
                                mSyncableLocation.set_id(synckedLocation.get_id());
                                mSyncableLocation.update();
                                if (WaniSyncHelper.f88i == syncableOldLocations.size() - 1) {
                                    SyncThread.this.syncPictureDownToUp();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path location old"));
                                if (WaniSyncHelper.f88i == syncableOldLocations.size() - 1) {
                                    SyncThread.this.syncPictureDownToUp();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            }
                        }

                        public void onFailure(Call<Location> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldLocations.size() - 1) {
                                SyncThread.this.syncPictureDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPictureDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncPictureDownToUp() {
            final List<Picture> syncablePictures = this.mDataSource.fetchSyncableNewPictures();
            if (syncablePictures.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Picture mSyncablePicture : syncablePictures) {
                    this.mNetworkDataSource.postSyncablePicture(mSyncablePicture, WaniApp.isFirstAccess()).enqueue(new Callback<Picture>() {
                        public void onResponse(Call<Picture> call, Response<Picture> response) {
                            if (response.code() == 201) {
                                Picture synckedPicture = response.body();
                                try {
                                    Date syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePicture.set_id(synckedPicture.get_id());
                                SyncThread.this.mDataSource.bindRemoteProfileToPeopleAndRawCarnet(mSyncablePicture);
                                mSyncablePicture.delete();
                                if (WaniSyncHelper.f88i == syncablePictures.size() - 1) {
                                    SyncThread.this.syncPictureDownToUpOld();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path picture"));
                                if (WaniSyncHelper.f88i == syncablePictures.size() - 1) {
                                    SyncThread.this.syncPictureDownToUpOld();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            }
                        }

                        public void onFailure(Call<Picture> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncablePictures.size() - 1) {
                                SyncThread.this.syncPictureDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPictureDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncPictureDownToUpOld() {
            final List<Picture> syncableOldPictures = this.mDataSource.fetchSyncableOldPictures();
            if (syncableOldPictures.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Picture mSyncablePicture : syncableOldPictures) {
                    this.mNetworkDataSource.putSyncablePicture(mSyncablePicture.get_id(), mSyncablePicture, WaniApp.isFirstAccess()).enqueue(new Callback<Picture>() {
                        public void onResponse(Call<Picture> call, Response<Picture> response) {
                            if (response.code() == 202) {
                                Picture synckedPicture = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePicture.setSynced(syncedDate);
                                mSyncablePicture.set_id(synckedPicture.get_id());
                                SyncThread.this.mDataSource.bindRemoteProfileToPeopleAndRawCarnet(mSyncablePicture);
                                mSyncablePicture.update();
                                if (WaniSyncHelper.f88i == syncableOldPictures.size() - 1) {
                                    SyncThread.this.syncPeopleDownToUp();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path picture old"));
                                if (WaniSyncHelper.f88i == syncableOldPictures.size() - 1) {
                                    SyncThread.this.syncPeopleDownToUp();
                                } else {
                                    WaniSyncHelper.access$2408();
                                }
                            }
                        }

                        public void onFailure(Call<Picture> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldPictures.size() - 1) {
                                SyncThread.this.syncPeopleDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPeopleDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncPeopleDownToUp() {
            final List<People> syncablePeoples = this.mDataSource.fetchSyncableNewPeoples();
            if (syncablePeoples.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final People mSyncablePeople : syncablePeoples) {
                    Picture mPicture = null;
                    if (mSyncablePeople.getLocaleIdProfile() != null) {
                        mPicture = Select.from(Picture.class).where(Condition.prop("ID").eq(mSyncablePeople.getLocaleIdProfile())).first();
                    }
                    if (mPicture != null) {
                        mSyncablePeople.setIdProfile(mPicture.get_id());
                    }
                    this.mNetworkDataSource.postSyncablePeople(mSyncablePeople, WaniApp.isFirstAccess()).enqueue(new Callback<People>() {
                        public void onResponse(Call<People> call, Response<People> response) {
                            if (response.code() == 201) {
                                People synckedPeople = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePeople.setSynced(syncedDate);
                                mSyncablePeople.setBirthDate(synckedPeople.getBirthDate());
                                mSyncablePeople.set_id(synckedPeople.get_id());
                                mSyncablePeople.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Unexpected response code" + response.code()));
                            }
                            if (WaniSyncHelper.f88i == syncablePeoples.size() - 1) {
                                SyncThread.this.syncPeopleDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<People> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncablePeoples.size() - 1) {
                                SyncThread.this.syncPeopleDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPeopleDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncPeopleDownToUpOld() {
            final List<People> syncableOldPeoples = this.mDataSource.fetchSyncableOldPeoples();
            if (syncableOldPeoples.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final People mSyncablePeople : syncableOldPeoples) {
                    Picture mPicture = null;
                    if (mSyncablePeople.getLocaleIdProfile() != null) {
                        mPicture = Select.from(Picture.class).where(Condition.prop("ID").eq(mSyncablePeople.getLocaleIdProfile())).first();
                    }
                    if (mPicture != null) {
                        mSyncablePeople.setIdProfile(mPicture.get_id());
                    }
                    this.mNetworkDataSource.putSyncablePeople(mSyncablePeople.get_id(), mSyncablePeople, WaniApp.isFirstAccess()).enqueue(new Callback<People>() {
                        public void onResponse(Call<People> call, Response<People> response) {
                            if (response.code() == 202) {
                                People synckedPeople = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePeople.setSynced(syncedDate);
                                mSyncablePeople.set_id(synckedPeople.get_id());
                                mSyncablePeople.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path people"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldPeoples.size() - 1) {
                                SyncThread.this.syncCompactUserDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<People> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldPeoples.size() - 1) {
                                SyncThread.this.syncCompactUserDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactUserDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncCompactUserDownToUpOld() {
            final List<CompactUser> syncableOldCompactUsers = this.mDataSource.fetchSyncableOldCompactUsers();
            if (syncableOldCompactUsers.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactUser mSyncableCompactUser : syncableOldCompactUsers) {
                    this.mNetworkDataSource.putSyncableCompactUser(mSyncableCompactUser.get_id(), mSyncableCompactUser, WaniApp.isFirstAccess()).enqueue(new Callback<CompactUser>() {
                        public void onResponse(Call<CompactUser> call, Response<CompactUser> response) {
                            if (response.code() == 202) {
                                CompactUser synckedCompactUser = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactUser.setSynced(syncedDate);
                                mSyncableCompactUser.set_id(synckedCompactUser.get_id());
                                mSyncableCompactUser.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path user"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldCompactUsers.size() - 1) {
                                SyncThread.this.syncPaimentTypeDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactUser> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldCompactUsers.size() - 1) {
                                SyncThread.this.syncPaimentTypeDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPaimentTypeDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncPaimentTypeDownToUp() {
            final List<PaimentType> syncablePaimentTypes = this.mDataSource.fetchSyncableNewPaimentTypes();
            if (syncablePaimentTypes.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final PaimentType mSyncablePaimentType : syncablePaimentTypes) {
                    this.mNetworkDataSource.postSyncablePaimentType(mSyncablePaimentType, WaniApp.isFirstAccess()).enqueue(new Callback<PaimentType>() {
                        public void onResponse(Call<PaimentType> call, Response<PaimentType> response) {
                            if (response.code() == 201) {
                                PaimentType synckedPaimentType = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePaimentType.setSynced(syncedDate);
                                mSyncablePaimentType.set_id(synckedPaimentType.get_id());
                                mSyncablePaimentType.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path paiement type"));
                            }
                            if (WaniSyncHelper.f88i == syncablePaimentTypes.size() - 1) {
                                SyncThread.this.syncPaimentTypeDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<PaimentType> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncablePaimentTypes.size() - 1) {
                                SyncThread.this.syncPaimentTypeDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncPaimentTypeDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncPaimentTypeDownToUpOld() {
            final List<PaimentType> syncableOldPaimentTypes = this.mDataSource.fetchSyncableOldPaimentTypes();
            if (syncableOldPaimentTypes.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final PaimentType mSyncablePaimentType : syncableOldPaimentTypes) {
                    this.mNetworkDataSource.putSyncablePaimentType(mSyncablePaimentType.get_id(), mSyncablePaimentType, WaniApp.isFirstAccess()).enqueue(new Callback<PaimentType>() {
                        public void onResponse(Call<PaimentType> call, Response<PaimentType> response) {
                            if (response.code() == 202) {
                                PaimentType synckedPaimentType = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncablePaimentType.setSynced(syncedDate);
                                mSyncablePaimentType.set_id(synckedPaimentType.get_id());
                                mSyncablePaimentType.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path paiement type old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldPaimentTypes.size() - 1) {
                                SyncThread.this.syncCompactPaiementDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<PaimentType> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldPaimentTypes.size() - 1) {
                                SyncThread.this.syncCompactPaiementDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactPaiementDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncCompactPaiementDownToUp() {
            final List<CompactPaiement> syncableCompactPaiements = this.mDataSource.fetchSyncableNewCompactPaiements();
            if (syncableCompactPaiements.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactPaiement mSyncableCompactPaiement : syncableCompactPaiements) {
                    this.mNetworkDataSource.postSyncableCompactPaiement(mSyncableCompactPaiement, WaniApp.isFirstAccess()).enqueue(new Callback<CompactPaiement>() {
                        public void onResponse(Call<CompactPaiement> call, Response<CompactPaiement> response) {
                            if (response.code() == 201) {
                                CompactPaiement synckedCompactPaiement = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactPaiement.setSynced(syncedDate);
                                mSyncableCompactPaiement.set_id(synckedCompactPaiement.get_id());
                                mSyncableCompactPaiement.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code " + response.code() + " in path paiement"));
                            }
                            if (WaniSyncHelper.f88i == syncableCompactPaiements.size() - 1) {
                                SyncThread.this.syncCompactPaiementDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactPaiement> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableCompactPaiements.size() - 1) {
                                SyncThread.this.syncCompactPaiementDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactPaiementDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncCompactPaiementDownToUpOld() {
            final List<CompactPaiement> syncableOldCompactPaiements = this.mDataSource.fetchSyncableOldCompactPaiements();
            if (syncableOldCompactPaiements.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactPaiement mSyncableCompactPaiement : syncableOldCompactPaiements) {
                    this.mNetworkDataSource.putSyncableCompactPaiement(mSyncableCompactPaiement.get_id(), mSyncableCompactPaiement, WaniApp.isFirstAccess()).enqueue(new Callback<CompactPaiement>() {
                        public void onResponse(Call<CompactPaiement> call, Response<CompactPaiement> response) {
                            if (response.code() == 202) {
                                CompactPaiement synckedCompactPaiement = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactPaiement.setSynced(syncedDate);
                                mSyncableCompactPaiement.set_id(synckedCompactPaiement.get_id());
                                mSyncableCompactPaiement.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path paiement old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldCompactPaiements.size() - 1) {
                                SyncThread.this.syncCheckDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactPaiement> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldCompactPaiements.size() - 1) {
                                SyncThread.this.syncCheckDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCheckDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncCheckDownToUp() {
            final List<Check> syncableChecks = this.mDataSource.fetchSyncableNewChecks();
            if (syncableChecks.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Check mSyncableCheck : syncableChecks) {
                    this.mNetworkDataSource.postSyncableCheck(mSyncableCheck, WaniApp.isFirstAccess()).enqueue(new Callback<Check>() {
                        public void onResponse(Call<Check> call, Response<Check> response) {
                            if (response.code() == 201) {
                                Check synckedCheck = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCheck.setSynced(syncedDate);
                                mSyncableCheck.set_id(synckedCheck.get_id());
                                mSyncableCheck.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path check"));
                            }
                            if (WaniSyncHelper.f88i == syncableChecks.size() - 1) {
                                SyncThread.this.syncCheckDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Check> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableChecks.size() - 1) {
                                SyncThread.this.syncCheckDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCheckDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncCheckDownToUpOld() {
            final List<Check> syncableOldChecks = this.mDataSource.fetchSyncableOldChecks();
            if (syncableOldChecks.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Check mSyncableCheck : syncableOldChecks) {
                    this.mNetworkDataSource.putSyncableCheck(mSyncableCheck.get_id(), mSyncableCheck, WaniApp.isFirstAccess()).enqueue(new Callback<Check>() {
                        public void onResponse(Call<Check> call, Response<Check> response) {
                            if (response.code() == 202) {
                                Check synckedCheck = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCheck.setSynced(syncedDate);
                                mSyncableCheck.set_id(synckedCheck.get_id());
                                mSyncableCheck.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path check old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldChecks.size() - 1) {
                                SyncThread.this.syncCompactStatusDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Check> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldChecks.size() - 1) {
                                SyncThread.this.syncCompactStatusDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactStatusDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncCompactStatusDownToUp() {
            final List<CompactStatus> syncableCompactStatuss = this.mDataSource.fetchSyncableNewCompactStatuss();
            if (syncableCompactStatuss.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactStatus mSyncableCompactStatus : syncableCompactStatuss) {
                    this.mNetworkDataSource.postSyncableCompactStatus(mSyncableCompactStatus, WaniApp.isFirstAccess()).enqueue(new Callback<CompactStatus>() {
                        public void onResponse(Call<CompactStatus> call, Response<CompactStatus> response) {
                            if (response.code() == 201) {
                                CompactStatus synckedCompactStatus = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactStatus.setSynced(syncedDate);
                                mSyncableCompactStatus.set_id(synckedCompactStatus.get_id());
                                mSyncableCompactStatus.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code()));
                            }
                            if (WaniSyncHelper.f88i == syncableCompactStatuss.size() - 1) {
                                SyncThread.this.syncCompactStatusDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactStatus> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableCompactStatuss.size() - 1) {
                                SyncThread.this.syncCompactStatusDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactStatusDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncCompactStatusDownToUpOld() {
            final List<CompactStatus> syncableOldCompactStatuss = this.mDataSource.fetchSyncableOldCompactStatuss();
            if (syncableOldCompactStatuss.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactStatus mSyncableCompactStatus : syncableOldCompactStatuss) {
                    this.mNetworkDataSource.putSyncableCompactStatus(mSyncableCompactStatus.get_id(), mSyncableCompactStatus, WaniApp.isFirstAccess()).enqueue(new Callback<CompactStatus>() {
                        public void onResponse(Call<CompactStatus> call, Response<CompactStatus> response) {
                            if (response.code() == 202) {
                                CompactStatus synckedCompactStatus = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactStatus.setSynced(syncedDate);
                                mSyncableCompactStatus.set_id(synckedCompactStatus.get_id());
                                mSyncableCompactStatus.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path status"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldCompactStatuss.size() - 1) {
                                SyncThread.this.syncStoryDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactStatus> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldCompactStatuss.size() - 1) {
                                SyncThread.this.syncStoryDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncStoryDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncStoryDownToUp() {
            final List<Story> syncableStorys = this.mDataSource.fetchSyncableNewStorys();
            if (syncableStorys.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Story mSyncableStory : syncableStorys) {
                    this.mNetworkDataSource.postSyncableStory(mSyncableStory, WaniApp.isFirstAccess()).enqueue(new Callback<Story>() {
                        public void onResponse(Call<Story> call, Response<Story> response) {
                            if (response.code() == 201) {
                                Story synckedStory = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableStory.setSynced(syncedDate);
                                mSyncableStory.set_id(synckedStory.get_id());
                                mSyncableStory.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path story"));
                            }
                            if (WaniSyncHelper.f88i == syncableStorys.size() - 1) {
                                SyncThread.this.syncStoryDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Story> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableStorys.size() - 1) {
                                SyncThread.this.syncStoryDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncStoryDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncStoryDownToUpOld() {
            final List<Story> syncableOldStorys = this.mDataSource.fetchSyncableOldStorys();
            if (syncableOldStorys.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Story mSyncableStory : syncableOldStorys) {
                    this.mNetworkDataSource.putSyncableStory(mSyncableStory.get_id(), mSyncableStory, WaniApp.isFirstAccess()).enqueue(new Callback<Story>() {
                        public void onResponse(Call<Story> call, Response<Story> response) {
                            if (response.code() == 200) {
                                Story synckedStory = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableStory.setSynced(syncedDate);
                                mSyncableStory.set_id(synckedStory.get_id());
                                mSyncableStory.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path story old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldStorys.size() - 1) {
                                SyncThread.this.syncVaccinDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Story> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldStorys.size() - 1) {
                                SyncThread.this.syncVaccinDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncVaccinDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncVaccinDownToUp() {
            final List<Vaccin> syncableVaccins = this.mDataSource.fetchSyncableNewVaccins();
            if (syncableVaccins.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Vaccin mSyncableVaccin : syncableVaccins) {
                    this.mNetworkDataSource.postSyncableVaccin(mSyncableVaccin, WaniApp.isFirstAccess()).enqueue(new Callback<Vaccin>() {
                        public void onResponse(Call<Vaccin> call, Response<Vaccin> response) {
                            if (response.code() == 201) {
                                Vaccin synckedVaccin = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableVaccin.setSynced(syncedDate);
                                mSyncableVaccin.set_id(synckedVaccin.get_id());
                                mSyncableVaccin.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path vaccin"));
                            }
                            if (WaniSyncHelper.f88i == syncableVaccins.size() - 1) {
                                SyncThread.this.syncVaccinDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Vaccin> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableVaccins.size() - 1) {
                                SyncThread.this.syncVaccinDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncVaccinDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncVaccinDownToUpOld() {
            final List<Vaccin> syncableOldVaccins = this.mDataSource.fetchSyncableOldVaccins();
            if (syncableOldVaccins.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final Vaccin mSyncableVaccin : syncableOldVaccins) {
                    this.mNetworkDataSource.putSyncableVaccin(mSyncableVaccin.get_id(), mSyncableVaccin, WaniApp.isFirstAccess()).enqueue(new Callback<Vaccin>() {
                        public void onResponse(Call<Vaccin> call, Response<Vaccin> response) {
                            if (response.code() == 202) {
                                Vaccin synckedVaccin = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableVaccin.setSynced(syncedDate);
                                mSyncableVaccin.set_id(synckedVaccin.get_id());
                                mSyncableVaccin.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path vaccin old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldVaccins.size() - 1) {
                                SyncThread.this.syncVaccinOccurenceDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<Vaccin> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldVaccins.size() - 1) {
                                SyncThread.this.syncVaccinOccurenceDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncVaccinOccurenceDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncVaccinOccurenceDownToUp() {
            final List<VaccinOccurence> syncableVaccinOccurences = this.mDataSource.fetchSyncableNewVaccinOccurences();
            if (syncableVaccinOccurences.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final VaccinOccurence mSyncableVaccinOccurence : syncableVaccinOccurences) {
                    this.mNetworkDataSource.postSyncableVaccinOccurence(mSyncableVaccinOccurence, WaniApp.isFirstAccess()).enqueue(new Callback<VaccinOccurence>() {
                        public void onResponse(Call<VaccinOccurence> call, Response<VaccinOccurence> response) {
                            if (response.code() == 201) {
                                VaccinOccurence synckedVaccinOccurence = response.body();
                                Log.d("zaza", String.format("%d ime vo synced with _id %s", new Object[]{Integer.valueOf(WaniSyncHelper.f88i), synckedVaccinOccurence.get_id()}));
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableVaccinOccurence.setSynced(syncedDate);
                                mSyncableVaccinOccurence.set_id(synckedVaccinOccurence.get_id());
                                mSyncableVaccinOccurence.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path vaccin occurence"));
                            }
                            if (WaniSyncHelper.f88i == syncableVaccinOccurences.size() - 1) {
                                SyncThread.this.syncVaccinOccurenceDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<VaccinOccurence> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableVaccinOccurences.size() - 1) {
                                SyncThread.this.syncVaccinOccurenceDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncVaccinOccurenceDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncVaccinOccurenceDownToUpOld() {
            final List<VaccinOccurence> syncableOldVaccinOccurences = this.mDataSource.fetchSyncableOldVaccinOccurences();
            if (syncableOldVaccinOccurences.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final VaccinOccurence mSyncableVaccinOccurence : syncableOldVaccinOccurences) {
                    this.mNetworkDataSource.putSyncableVaccinOccurence(mSyncableVaccinOccurence.get_id(), mSyncableVaccinOccurence, WaniApp.isFirstAccess()).enqueue(new Callback<VaccinOccurence>() {
                        public void onResponse(Call<VaccinOccurence> call, Response<VaccinOccurence> response) {
                            if (response.code() == 202) {
                                VaccinOccurence synckedVaccinOccurence = response.body();
                                Date syncedDate = null;
                                try {
                                    syncedDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableVaccinOccurence.setSynced(syncedDate);
                                mSyncableVaccinOccurence.set_id(synckedVaccinOccurence.get_id());
                                mSyncableVaccinOccurence.update();
                            } else {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path vaccin occurence old"));
                            }
                            if (WaniSyncHelper.f88i == syncableOldVaccinOccurences.size() - 1) {
                                SyncThread.this.syncCompactCarnetDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<VaccinOccurence> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableOldVaccinOccurences.size() - 1) {
                                SyncThread.this.syncCompactCarnetDownToUp();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactCarnetDownToUp();
        }

        /* access modifiers changed from: private */
        public void syncCompactCarnetDownToUp() {
            final List<CompactCarnet> syncableCompactCarnets = this.mDataSource.fetchSyncableNewCompactCarnets();
            Log.d("zaza", String.format("Syncable compact carnest size %d", new Object[]{Integer.valueOf(syncableCompactCarnets.size())}));
            if (syncableCompactCarnets.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactCarnet mSyncableCompactCarnet : syncableCompactCarnets) {
                    this.mNetworkDataSource.postSyncableCompactCarnet(mSyncableCompactCarnet, WaniApp.isFirstAccess()).enqueue(new Callback<CompactCarnet>() {
                        public void onResponse(Call<CompactCarnet> call, Response<CompactCarnet> response) {
                            if (response.code() != 201) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path carnet"));
                            } else if (!mSyncableCompactCarnet.getStatus().booleanValue()) {
                                mSyncableCompactCarnet.delete();
                            } else {
                                CompactCarnet synckedCompactCarnet = response.body();
                                Date lastCandidateDate = null;
                                try {
                                    lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactCarnet.setSynced(lastCandidateDate);
                                mSyncableCompactCarnet.set_id(synckedCompactCarnet.get_id());
                                SyncThread.this.mDataSource.insertVaccinOccurencesOrUpdateByRemoteId(lastCandidateDate, synckedCompactCarnet.getVaccins());
                                mSyncableCompactCarnet.update();
                            }
                            if (WaniSyncHelper.f88i == syncableCompactCarnets.size() - 1) {
                                SyncThread.this.syncCompactCarnetDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactCarnet> call, Throwable t) {
                            SyncThread.this.errors.add(t);
                            if (WaniSyncHelper.f88i == syncableCompactCarnets.size() - 1) {
                                SyncThread.this.syncCompactCarnetDownToUpOld();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncCompactCarnetDownToUpOld();
        }

        /* access modifiers changed from: private */
        public void syncCompactCarnetDownToUpOld() {
            final List<CompactCarnet> syncableOldCompactCarnets = this.mDataSource.fetchSyncableOldCompactCarnets();
            if (syncableOldCompactCarnets.size() > 0) {
                int unused = WaniSyncHelper.f88i = 0;
                for (final CompactCarnet mSyncableCompactCarnet : syncableOldCompactCarnets) {
                    this.mNetworkDataSource.putSyncableCompactCarnet(mSyncableCompactCarnet.get_id(), mSyncableCompactCarnet, WaniApp.isFirstAccess()).enqueue(new Callback<CompactCarnet>() {
                        public void onResponse(Call<CompactCarnet> call, Response<CompactCarnet> response) {
                            if (response.code() != 202) {
                                SyncThread.this.errors.add(new Exception("Un expected response code" + response.code() + " in path carnet old"));
                            } else if (!mSyncableCompactCarnet.getStatus().booleanValue()) {
                                Log.d("zaza", "about to delete");
                                mSyncableCompactCarnet.delete();
                                Log.d("zaza", String.format("actual value %s", new Object[]{mSyncableCompactCarnet.get_id()}));
                            } else {
                                CompactCarnet synckedCompactCarnet = response.body();
                                Date lastCandidateDate = null;
                                try {
                                    lastCandidateDate = ISO8601.toCalendar(response.headers().get("lastCandidate")).getTime();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                mSyncableCompactCarnet.setSynced(lastCandidateDate);
                                mSyncableCompactCarnet.set_id(synckedCompactCarnet.get_id());
                                SyncThread.this.mDataSource.insertVaccinOccurencesOrUpdateByRemoteId(lastCandidateDate, synckedCompactCarnet.getVaccins());
                                mSyncableCompactCarnet.update();
                            }
                            if (WaniSyncHelper.f88i == syncableOldCompactCarnets.size() - 1) {
                                SyncThread.this.syncUpToDown();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }

                        public void onFailure(Call<CompactCarnet> call, Throwable t) {
                            if (WaniSyncHelper.f88i == syncableOldCompactCarnets.size() - 1) {
                                SyncThread.this.syncUpToDown();
                            } else {
                                WaniSyncHelper.access$2408();
                            }
                        }
                    });
                }
                return;
            }
            syncUpToDown();
        }

        /* access modifiers changed from: private */
        public void consignSynced() {
            synkRequests.remove(0);
            if(synkRequests.size() > 0) procedSynkRequests(synkRequests);
            else {
                Common.backgroundWork.postValue(false);
                final SharedPreferences mPrefs = WaniSyncHelper.mContext.getSharedPreferences(WaniApp.PREFS_ID, 0);
                Boolean uChanged = Boolean.valueOf(mPrefs.getBoolean(WaniApp.USER_CHANGED, false));
                String newPassword = mPrefs.getString(WaniApp.NEW_PASSWORD, "");
                String currentPassword = mPrefs.getString(WaniApp.LAST_KNOWN_USER_PASSWORD, "");
                if (!uChanged.booleanValue()) {
                    concludeWithMessage();
                } else if (newPassword.compareTo("") != 0) {
                    this.mNetworkDataSource.changePassword(new ChangePasswordBody(currentPassword, newPassword)).enqueue(new Callback<LoginResult>() {
                        public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                            if (response != null && response.code() == 202) {
                                String token = response.body().getToken();
                                mPrefs.edit().putString(WaniApp.LAST_KNOWN_TOKEN_KEY, token).commit();
                                mPrefs.edit().putBoolean(WaniApp.USER_CHANGED, false).commit();
                                WaniApp.setToken(token);
                                WaniRepository.getInstance(WaniSyncHelper.mContext).updateToken(token);
                            }
                            SyncThread.this.concludeWithMessage();
                        }

                        public void onFailure(Call<LoginResult> call, Throwable t) {
                            SyncThread.this.concludeWithMessage();
                        }
                    });
                } else {
                    mPrefs.edit().putBoolean(WaniApp.USER_CHANGED, false).commit();
                    concludeWithMessage();
                }
            }

        }

        /* access modifiers changed from: private */
        public void concludeWithMessage() {
            Message msg = Message.obtain();
            msg.arg1 = 0;
            msg.obj = this.errors;
            this.mHandler.sendMessage(msg);
            interrupt();
        }

        /* access modifiers changed from: private */
        public void consignValidationError() {
            Message msg = Message.obtain();
            msg.arg1 = 1;
            msg.obj = this.errors;
            this.mHandler.sendMessage(msg);
            interrupt();
        }

        public void consignProgress(Integer progress) {
            Message msg = Message.obtain();
            msg.arg1 = 2;
            msg.obj = progress;
            this.mHandler.sendMessage(msg);
            interrupt();
        }
    }

    public static void consignFaillure(ArrayList<Throwable> errors2) {
        Iterator<WaniSynkCallback> it = synkCallbacks.iterator();
        while (it.hasNext()) {
            WaniSynkCallback mcb = it.next();
            if (mcb != null) {
                mcb.onSynkError(errors2);
            }
        }
        errors2.clear();
    }
}
