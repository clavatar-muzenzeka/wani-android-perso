package cd.clavatar.wani.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import com.orm.query.Select;
import org.json.JSONObject;
import cd.clavatar.wani.IWaniStartPoint;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.data.model.WaniParameter;
import cd.clavatar.wani.utilities.WaniSyncHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* renamed from: cd.clavatar.wani.Service.SynkService */
public class SynkService extends Service implements IWaniStartPoint {
    public static boolean RUNNING = false;
    /* access modifiers changed from: private */
    public static Context STATIC_INSTANCE = null;
    private static SharedPreferences mPref;

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        RUNNING = true;
        initializeWaniApp();
        STATIC_INSTANCE = this;
        mPref = getSharedPreferences(WaniApp.PREFS_ID, 0);
        String lastKnownToken = getSharedPreferences(WaniApp.PREFS_ID, 0).getString(WaniApp.LAST_KNOWN_TOKEN_KEY, (String) null);
        String lastKnownUsername = getSharedPreferences(WaniApp.PREFS_ID, 0).getString(WaniApp.LAST_KNOWN_USER_USERNAME, (String) null);
        WaniApp.setToken(lastKnownToken);
        WaniRepository.getInstance(STATIC_INSTANCE).updateToken(lastKnownToken);
        CompactUser cu = WaniDataSource.getInstance(this).getCompactUserByUsername(lastKnownUsername);
        if (cu != null) {
            WaniApp.setLogedInUser(cu);
            WaniApp.setParameter(Select.from(WaniParameter.class).first());
        }
        WaniApp.setDeviceIdentifier(Settings.Secure.getString(getContentResolver(), "android_id"));
        attemptSynk();
        return super.onStartCommand(intent, flags, 2);
    }

    public static void attemptSynk() {
        final String lastKnownToken = mPref.getString(WaniApp.LAST_KNOWN_TOKEN_KEY, (String) null);
        if (lastKnownToken != null) {
            WaniNetworkAccess.getInstanceForToken(lastKnownToken, STATIC_INSTANCE).checkApi().enqueue(new Callback<JSONObject>() {
                public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                    if (response.code() == 200) {
                        WaniRepository.getInstance(SynkService.STATIC_INSTANCE).updateToken(lastKnownToken);
                        WaniSyncHelper.getInstance(SynkService.STATIC_INSTANCE).procedSynk(new WaniRepository.WaniRepositorySynkListener());
                    }
                }

                public void onFailure(Call<JSONObject> call, Throwable t) {
                }
            });
        }
    }

    public void onDestroy() {
        RUNNING = false;
        super.onDestroy();
    }

    public void initializeWaniApp() {
        String deviceIdentifier = Settings.Secure.getString(getContentResolver(), "android_id");
        boolean alredyAccessed = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.ALREADY_ACCESSED_KEY, false);
        boolean backup = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.BACKUP_KEY, false);
        if (alredyAccessed) {
            WaniApp.setAlradyAccessed();
        }
        WaniParameter parameter = new WaniParameter();
        WaniApp.setDeviceIdentifier(deviceIdentifier);
        WaniApp.setParameter(parameter);
        WaniApp.setBackup(backup);
    }
}
