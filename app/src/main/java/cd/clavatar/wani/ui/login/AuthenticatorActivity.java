package cd.clavatar.wani.ui.login;

import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.google.android.material.snackbar.Snackbar;
import com.orm.query.Select;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import cd.clavatar.wani.IWaniStartPoint;
import cd.clavatar.wani.R;
import cd.clavatar.wani.Service.SynkService;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.LoginResult;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.data.model.WaniParameter;
import cd.clavatar.wani.databinding.ActivityLoginBinding;
import cd.clavatar.wani.ui.SplashScreenActivity;
import cd.clavatar.wani.ui.main.MainActivity;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.utilities.ILoginFinisher;
import cd.clavatar.wani.utilities.WaniSyncHelper;
import cd.clavatar.wani.vendor.print.Printer;
import br.com.ilhasoft.support.validation.Validator;

/* renamed from: cd.clavatar.wani.ui.login.AuthenticatorActivity */
public class AuthenticatorActivity extends AccountAuthenticatorActivity implements LifecycleOwner, Validator.ValidationListener, ILoginFinisher, IWaniStartPoint {
    public static final String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public static final String ARG_AUTH_TYPE = "AUTH_TYPE";
    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_NEW_ACCOUNT";
    private static final String PARAM_USER_PASS = "USER_PASS";
    private final LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
    private AccountManager mAccountManager;
    private Context mContext;
    public LoginViewModel model;

    /* renamed from: mv */
    private View f87mv;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding authenticatorActivityViewBinder = (ActivityLoginBinding) DataBindingUtil.setContentView(this, R.layout.activity_login);
        this.f87mv = authenticatorActivityViewBinder.getRoot();
        authenticatorActivityViewBinder.setLifecycleOwner(this);
        LoginViewModel loginViewModel = new LoginViewModel(this);
        this.model = loginViewModel;
        loginViewModel.setValidator(new Validator(authenticatorActivityViewBinder));
        this.model.setLoginFinisher(this);
        authenticatorActivityViewBinder.setModel(this.model);
        this.mAccountManager = AccountManager.get(this);
        String modelSerial = Common.getModelSerial(this);
        Log.d("zaza", String.format("mn %s", new Object[]{Build.MODEL}));
        initializeWaniApp();
        Snackbar.make(this.f87mv, (CharSequence) "Bienvenue, l'imprimante va être testée, si vous ne voyez pas d'impression de test prière de vérifier que le bluetooth du téléphone est activé et qu'il y a du papier dans l'imprimante", Snackbar.LENGTH_INDEFINITE).setAction((CharSequence) "TESTER", (View.OnClickListener) new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("zaza", "adress before test "+WaniApp.getPrinterAddress());
                Printer.print(AuthenticatorActivity.this, "*****************\nTest imprimante \n*****************\n\n\n");
            }
        }).setActionTextColor(getResources().getColor(R.color.secondaryLightColor)).show();
        new ArrayList<String>(Arrays.asList(new String[]{"Mar. 12h30-16:00", "Jeu. 10h30-17h30"})) {
        };
    }

    public void initializeWaniApp() {
        String deviceIdentifier = Settings.Secure.getString(getContentResolver(), "android_id");
        boolean alredyAccessed = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.ALREADY_ACCESSED_KEY, false);
        boolean backup = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.BACKUP_KEY, false);
        if (alredyAccessed) {
            WaniApp.setAlradyAccessed();
        }
        String serial = Common.getModelSerial(this);
        Log.d("zaza","serial "+ serial);
        String bluetoothAdresse = Common.getBluetoothAddress(serial);
        Log.d("zaza", "bt adresse "+bluetoothAdresse);
        WaniApp.setPrinterAddress(bluetoothAdresse);
        WaniApp.setBackup(backup);
        WaniParameter parameter = new WaniParameter();
        WaniApp.setDeviceIdentifier(deviceIdentifier);
        WaniApp.setParameter(parameter);
    }

    public void onBackPressed() {
        closeApp();
    }

    /* access modifiers changed from: private */
    public void closeApp() {
        Intent finishIntent = new Intent(this, SplashScreenActivity.class);
        finishIntent.putExtra(WaniApp.SPLASH_CLOSING_KEY, true);
        startActivity(finishIntent);
    }

    public void sendMail(String errorDetails){
        BackgroundMail.newBuilder(this)
                .withUsername("vastel.user.2@gmail.com")
                .withPassword("vastel243!")
                //.withAttachments(Environment.getExternalStorageDirectory().getPath() + "/Prime.jpg")
                .withMailto("vastel.user.1@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Bug rapport "+WaniApp.getDeviceIndentifier()+" "+WaniApp.LAST_KNOWN_USER_USERNAME)
                .withBody(errorDetails)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }

    public void finishLogin(LoginResult result) {
        getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putString(WaniApp.LAST_KNOWN_TOKEN_KEY, result.getToken()).commit();

        if (result.getUser().getRole().compareTo("Agent") == 0 || result.getUser().getRole().compareTo("Controler") == 0) {

            WaniApp.setToken(result.getToken());
            final CompactUser mu = result.getUser();
            ArrayList<CompactUser> mul = new ArrayList<>();
            mul.add(mu);
            WaniDataSource.getInstance(this).insertCompactUsersOrUpdateByRemoteId(WaniApp.getCurrentDate(), mul);
            WaniApp.setLogedInUser(WaniDataSource.getInstance(this).getCompactUserByRemoteId(mu.get_id()));
            ArrayList<WaniParameter> mp = new ArrayList<>();
            WaniParameter mwp = result.getParameter();
            mp.add(mwp);
            WaniDataSource.getInstance(this).insertWaniParametersOrUpdateByRemoteId(WaniApp.getCurrentDate(), mp);
            WaniApp.setParameter(mwp);
            getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putString(WaniApp.LAST_KNOWN_USER_USERNAME, result.getUser().getUsername()).commit();
            getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putString(WaniApp.LAST_KNOWN_USER_PASSWORD, result.getPassword()).commit();
            if (result.getToken() != null) {
                WaniRepository.getInstanceWhithoutInit(this.mContext).updateToken(result.getToken());
            }
            WaniRepository.getInstanceWhithoutInit(this.mContext);
            if (!SynkService.RUNNING) {
                Log.d("zaza", "Sync service started from login activity");
                startService(new Intent(this, SynkService.class));
            }
            procedSynk(new WaniSyncHelper.WaniSynkCallback() {
                public void onSynkStarted() {
                }

                public void onSynkError(ArrayList<Throwable> errors) {
                    AuthenticatorActivity.this.model.syncProgress.setValue(false);
                    Toast.makeText(AuthenticatorActivity.this, "Quelques problèmes sont survenus lors de la synchronisation, certaines données risquent d'être indisponibles", Toast.LENGTH_LONG).show();
                    String em = "";
                    for (Throwable error: errors
                         ) {
                        em+= " "+error.getMessage();
                    }
                    sendMail("Synchronisation error "+em);
                    if (WaniApp.isFirstAccess()) {
                        Iterator<Throwable> it = errors.iterator();
                        while (it.hasNext()) {
                            Throwable err = it.next();
                            Log.d("zaza", "tryng to print error");
                            if (err.getMessage() != null) {
                                Log.d("zaza", String.format("message %s", new Object[]{err.getMessage()}));
                            }
                        }
                        exitForFatalError();
                        return;
                    }
                    AuthenticatorActivity.this.startMainActivity();
                }



                public void onSynced(ArrayList<Throwable> errors) {
                    AuthenticatorActivity.this.model.syncProgress.setValue(false);
                    WaniApp.setLogedInUser(WaniDataSource.getInstance(AuthenticatorActivity.this).getCompactUserByRemoteId(mu.get_id()));
                    if (errors.size() > 0) {
                        //Toast.makeText(AuthenticatorActivity.this, "Quelques problèmes sont survenus lors de la synchronisation, certaines données risquent d'être indisponibles", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(AuthenticatorActivity.this, "Synchronisé avec sucès", Toast.LENGTH_LONG).show();
                    }
                    if (WaniApp.isFirstAccess()) {
                        AuthenticatorActivity.this.getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putBoolean(WaniApp.ALREADY_ACCESSED_KEY, true).commit();
                        WaniApp.setAlradyAccessed();
                    }
                    AuthenticatorActivity.this.startMainActivity();
                }

                public void onValidationError(ArrayList<Throwable> arrayList) {
                    exitForFatalError();
                }

                @Override
                public void onProgress(Integer progressValue) {
                    AuthenticatorActivity.this.model.setSyncProgressValue(progressValue);
                }

                private void exitForFatalError() {
                    Toast.makeText(AuthenticatorActivity.this, "Certaines données indispensables n'ont pas pu être téléchargées, les système va s'arrêter", Toast.LENGTH_LONG).show();
                    new Timer().schedule(new TimerTask() {
                        public void run() {
                            AuthenticatorActivity.this.closeApp();
                        }
                    }, 5000);
                }
            });
            return;
        }
        Toast.makeText(this, "Accès reservé aux agents et controlleurs seulement", Toast.LENGTH_LONG).show();
    }

    private void procedSynk(WaniSyncHelper.WaniSynkCallback fromLoginSynkCallback) {
        this.model.syncProgress.setValue(true);
        WaniSyncHelper.getInstance(this).procedSynk(fromLoginSynkCallback);
    }

    /* access modifiers changed from: private */
    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    public LoginViewModel getModel() {
        return this.model;
    }

    public void setModel(LoginViewModel model2) {
        this.model = model2;
    }

    public void onValidationSuccess() {
        Toast.makeText(this, "Valid", Toast.LENGTH_LONG).show();
    }

    public void onValidationError() {
    }

    public void startSignUpActivity() {
    }

    public void finishLoginOfline(String username) {
        if (!SynkService.RUNNING) {
            startService(new Intent(this, SynkService.class));
        }
        String lastKnownToken = getSharedPreferences(WaniApp.PREFS_ID, 0).getString(WaniApp.LAST_KNOWN_TOKEN_KEY, (String) null);
        WaniApp.setToken(lastKnownToken);
        WaniRepository.getInstanceWhithoutInit(this.mContext).updateToken(lastKnownToken);
        CompactUser cu = WaniDataSource.getInstance(this).getCompactUserByUsername(username);
        if (cu != null) {
            WaniApp.setLogedInUser(cu);
            WaniApp.setParameter(Select.from(WaniParameter.class).first());
            startMainActivity();
            return;
        }
        Toast.makeText(this, "Impossible de demarrer l'application en mode offline, utilisateur non trouvé!", Toast.LENGTH_LONG).show();
        closeApp();
    }
}
