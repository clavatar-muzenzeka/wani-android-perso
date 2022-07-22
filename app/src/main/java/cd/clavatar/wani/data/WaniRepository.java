package cd.clavatar.wani.data;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.HttpCookie;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.lifecycle.MutableLiveData;

import javax.xml.transform.Result;

import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.model.Check;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.GlobalState;
import cd.clavatar.wani.data.model.LoginBody;
import cd.clavatar.wani.data.model.LoginResult;
import cd.clavatar.wani.data.model.PaimentType;
import cd.clavatar.wani.data.model.People;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.User;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.data.model.WaniNetworkDataSource;
import cd.clavatar.wani.ui.main.MainViewModel;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.utilities.FetchCarnetsTask;
import cd.clavatar.wani.utilities.FetchPaiementsTask;
import cd.clavatar.wani.utilities.ISO8601;
import cd.clavatar.wani.utilities.WaniSyncHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaniRepository {
    private static WaniRepository INSTANCE =null ;
    private static Context context;
    private static MutableLiveData<List<CompactCarnet>> rawCarnets=new MutableLiveData<>();
    private static MutableLiveData<List<CompactPaiement>> rawPaiements=new MutableLiveData<>();
    private static boolean inited=false;
    WaniNetworkDataSource networkDataSource =null;
    private static boolean  requested;

    public static WaniRepository getInstance(Context context2) {
        context = context2;
        synchronized (WaniRepository.class) {
            if (INSTANCE == null) {
                WaniRepository waniRepository = new WaniRepository();
                INSTANCE = waniRepository;
                waniRepository.networkDataSource = WaniNetworkAccess.getInstanceForToken("", context2);
                init();
            }
        }
        if(!inited) init();
        return INSTANCE;
    }

    public static WaniRepository getInstanceWhithoutInit(Context context2) {
        context = context2;
        synchronized (WaniRepository.class) {
            if (INSTANCE == null) {
                WaniRepository waniRepository = new WaniRepository();
                INSTANCE = waniRepository;
                waniRepository.networkDataSource = WaniNetworkAccess.getInstanceForToken("", context2);
            }
        }
        return INSTANCE;
    }

    public static void init() {
        inited=true;
        if (rawCarnets.getValue() == null) {
            rawCarnets.postValue(new ArrayList());
        }
        if (rawPaiements.getValue() == null) {
            rawPaiements.postValue(new ArrayList());
        }

        fetchAndSortCarnetsAndPaiements();
    }

    private static void fetchAndSortCarnetsAndPaiements() {

        CompactUser lu=WaniApp.getLogedInUser();
        if ( lu!= null) {
            Common.backgroundWork.postValue(true);
            new FetchCarnetsTask(getRawCarnets()).doInBackground(lu);
            new FetchPaiementsTask(getRawPaiements()).doInBackground(lu);
        }
    }

    public static void reinitDatas() {
        rawCarnets=null;
        rawPaiements=null;
    }

    public void updateToken(String token) {
        INSTANCE.networkDataSource = WaniNetworkAccess.getInstanceForToken(token, context);
    }

    public Call<LoginResult> login(String username, String password) {
        return this.networkDataSource.login(new LoginBody(username, password));
    }

    public Call<Boolean> validateUsername(String username){
        JSONObject body = null;
        HashMap<String, String> bodyMap=new HashMap<>();
        bodyMap.put("username", username);

        return this.networkDataSource.validateUsername(bodyMap);
    }

    public Call<User> signUp(User incompleteUser, String password) {
        HashMap<String, Object> body =new HashMap<>();
        body.put("user", incompleteUser);
        body.put("password", password);
        return this.networkDataSource.signUp(body);
    }

    public Call<List<CompactCarnet>> loadCarnets() {
        return this.networkDataSource.loadCarnets();
    }

    public Call<People> postPeopleNetwork(People mPeople) {
        return this.networkDataSource.postPeople(mPeople);
    }

    public Call<Picture> postPicture(Picture mPicture) {
        return this.networkDataSource.postPicture(mPicture);
    }

    public Call<CompactCarnet> postCarnet(CompactCarnet carn) {
        return this.networkDataSource.postCarnet(carn);
    }


    public Call<CompactCarnet> putCarnet(CompactCarnet carn){
        String id=carn.get_id();
        return this.networkDataSource.putCarnet(id, prepareForPut(carn));
    }

    public Call<List<Vaccin>> loadVaccins() {
        return this.networkDataSource.loadVaccins();
    }

    private CompactCarnet prepareForPut(CompactCarnet mCarnet){

        mCarnet.set_id(null);
        return mCarnet;
    }

    public Call<CompactPaiement> postPaiement(CompactPaiement mPaiement) {
        return networkDataSource.postPaiement(mPaiement);
    }

    public Call<List<PaimentType>> loadPaiementTypes() {
        return networkDataSource.loadPaiementTypes();
    }

    public Call<List<CompactPaiement>> loadPaiements() {
        return networkDataSource.loaPaiments();
    }

    public Call<People> putPeople(People cp) {
        return networkDataSource.putPeople(cp.get_id(), cp);
    }

    public Call<Check> postCheck(Check mCheck) {
        return networkDataSource.postCheck(mCheck);
    }

    public static MutableLiveData<List<CompactCarnet>> getRawCarnets() {
return rawCarnets;
    }

    public static MutableLiveData<List<CompactPaiement>> getRawPaiements() {
        return rawPaiements;
    }


    public static class WaniRepositorySynkListener implements WaniSyncHelper.WaniSynkCallback {

        TimerTask mtt=new TimerTask() {
            @Override
            public void run() {
                requested=false;
                if (INSTANCE != null) init();
            }
        };

        Timer timer=new Timer();


        @Override
        public void onSynkStarted() {

        }

        @Override
        public void onSynkError(ArrayList<Throwable> errors) {
            if (INSTANCE != null) init();
        }

        @Override
        public void onSynced(ArrayList<Throwable> errors) {
            WaniRepository.init();
        }

        @Override
        public void onValidationError(ArrayList<Throwable> errors) {

        }

        @Override
        public void onProgress(Integer progressValue) {

        }
    }
}
