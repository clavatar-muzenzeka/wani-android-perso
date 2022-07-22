package cd.clavatar.wani.ui.login;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.LoginResult;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.databinding.DialogServerBinding;
import cd.clavatar.wani.ui.ServerDialogViewModel;
import cd.clavatar.wani.utilities.ILoginFinisher;
import cd.clavatar.wani.vendor.IServerDialogHandler;
import cd.clavatar.wani.vendor.ServerDialogResult;
import br.com.ilhasoft.support.validation.Validator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* renamed from: cd.clavatar.wani.ui.login.LoginViewModel */
public class LoginViewModel extends ViewModel {
    private static LoginViewModel INSTANCE = null;
    private static final int TAG_SERVER_RESULT = 1;
    /* access modifiers changed from: private */
    public Context context;
    private Callback<LoginResult> loginCallBack;
    /* access modifiers changed from: private */
    public ILoginFinisher loginFinisher;
    public MutableLiveData<Boolean> networkOnProgress = new MutableLiveData<Boolean>() {
    };
    public String password;
    private WaniRepository repository = null;
    /* access modifiers changed from: private */
    public AlertDialog serverDialog;
    private View.OnClickListener settingsButonHandler;
    public View.OnClickListener signUpBtnHandler = null;
    public View.OnClickListener submitBtnHandler = null;
    public MutableLiveData<Boolean> syncProgress = new MutableLiveData<Boolean>() {
    };
    public MutableLiveData<Integer> syncProgressValue = new MutableLiveData<Integer>() {
    };
    public String username;
    /* access modifiers changed from: private */
    public Validator validator = null;

    public LoginViewModel(Context context2) {
        synchronized (LoginViewModel.class) {
            if (INSTANCE == null) {
                INSTANCE = this;
            }
        }
        init((Validator) null, context2);
    }

    public void init(Validator pValidator, final Context context2) {
        setContext(context2);
        if (pValidator != null) {
            setValidator(pValidator);
        }
        this.repository = WaniRepository.getInstanceWhithoutInit(context2);
        this.syncProgressValue.postValue(0);
        this.submitBtnHandler = new View.OnClickListener() {
            public void onClick(View v) {
                if (LoginViewModel.this.validator == null) {
                    LoginViewModel loginViewModel = LoginViewModel.this;
                    loginViewModel._loginToServer(loginViewModel.username, LoginViewModel.this.password);
                } else if (LoginViewModel.this.validator.validate()) {
                    LoginViewModel loginViewModel2 = LoginViewModel.this;
                    loginViewModel2._loginToServer(loginViewModel2.username, LoginViewModel.this.password);
                }
            }
        };
        this.signUpBtnHandler = new View.OnClickListener() {
            public void onClick(View v) {
                ((AuthenticatorActivity) LoginViewModel.this.context).startSignUpActivity();
            }
        };
        this.settingsButonHandler = new View.OnClickListener() {
            public void onClick(View v) {
                LoginViewModel.this.showServerDialog(context2, 1, Boolean.valueOf(WaniApp.isBackup()), (Vaccin) null, false, new IServerDialogHandler<ServerDialogResult>() {
                    public void onServerDialogResult(ServerDialogResult result) {
                        if (result.getResult() == 1) {
                            context2.getSharedPreferences(WaniApp.PREFS_ID, 0).edit().putBoolean(WaniApp.BACKUP_KEY, result.isBackup()).commit();
                            WaniApp.setBackup(result.isBackup());
                            LoginViewModel.this.serverDialog.dismiss();
                            return;
                        }
                        LoginViewModel.this.serverDialog.dismiss();
                    }
                });
            }
        };
        this.loginCallBack = new Callback<LoginResult>() {
            public void onResponse(Call<LoginResult> call, Response<LoginResult> response) {
                if (response.code() == 200) {
                    LoginViewModel.this.loginFinisher.finishLogin(response.body());
                } else {
                    int code = response.code();
                    if (code == 401) {
                        Toast.makeText(LoginViewModel.this.context, R.string.prompt_wrong_password_simple, Toast.LENGTH_LONG).show();
                    } else if (code == 501) {
                        Toast.makeText(LoginViewModel.this.context, R.string.prompt_network_error, Toast.LENGTH_LONG).show();
                    } else if (code == 403) {
                        Toast.makeText(LoginViewModel.this.context, R.string.prompt_wrong_password_overshooted, Toast.LENGTH_LONG).show();
                    } else if (code == 404) {
                        Toast.makeText(LoginViewModel.this.context, R.string.prompt_user_not_found, Toast.LENGTH_LONG).show();
                    }
                }
                LoginViewModel.this.networkOnProgress.setValue(false);

            }

            public void onFailure(Call<LoginResult> call, Throwable t) {
                LoginViewModel.this.networkOnProgress.setValue(false);
                tryAuthentOfline();
            }



            private void tryAuthentOfline() {
                String lu = context2.getSharedPreferences(WaniApp.PREFS_ID, 0).getString(WaniApp.LAST_KNOWN_USER_USERNAME, (String) null);
                if (lu == null) {
                    Toast.makeText(context2, "Impossible d'accéder en mode offline, aucun utilisateur n'a jamais accédé à cette application", Toast.LENGTH_LONG).show();
                } else if (lu.compareTo(LoginViewModel.this.username) == 0) {
                    String pw = context2.getSharedPreferences(WaniApp.PREFS_ID, 0).getString(WaniApp.LAST_KNOWN_USER_PASSWORD, (String) null);
                    if (pw == null || pw.compareTo(LoginViewModel.this.password) != 0) {
                        Toast.makeText(context2, "Le mot de passe est différent du dernier mot de passe vous ayant donné accès!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    ((AuthenticatorActivity) context2).finishLoginOfline(LoginViewModel.this.username);
                    Toast.makeText(context2, "Vous accedez en mode ofline", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context2, "Connexion non disponible et utilisateur jamais enregistré sur ce mobile!", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public void showServerDialog(Context context2, int TAG, Boolean backup, Vaccin vaccin, Boolean extendMode, IServerDialogHandler resultHandler) {
        DialogServerBinding dialogATCDDBinding = (DialogServerBinding) DataBindingUtil.inflate(LayoutInflater.from(context2), R.layout.dialog_server, (ViewGroup) null, false);
        dialogATCDDBinding.setLifecycleOwner((AuthenticatorActivity) this.context);
        ServerDialogViewModel idvm = ServerDialogViewModel.getInstance(context2, TAG);
        ServerDialogViewModel.set_binding(dialogATCDDBinding);
        ServerDialogViewModel.setBackup(backup);
        idvm.setExtendMode(extendMode);
        idvm.setCancelButtonVisibility((Boolean) true);
        idvm.setCancelButtonText("Retour");
        idvm.setValidateButtonText("Valider");
        idvm.setResultHandler(resultHandler);
        dialogATCDDBinding.setModel(idvm);
        View dialogView = dialogATCDDBinding.getRoot();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(context2);
        dBuilder.setView(dialogView);
        AlertDialog create = dBuilder.create();
        this.serverDialog = create;
        create.show();
    }

    private void setContext(Context context2) {
        this.context = context2;
    }

    public void _loginToServer(String username2, String password2) {
        this.networkOnProgress.setValue(true);
        this.repository.login(username2, password2).enqueue(this.loginCallBack);
    }

    public void setValidator(Validator validator2) {
        this.validator = validator2;
    }

    public void setLoginFinisher(ILoginFinisher loginFinisher2) {
        this.loginFinisher = loginFinisher2;
    }

    public MutableLiveData<Boolean> getSyncProgress() {
        return this.syncProgress;
    }

    public View.OnClickListener getSettingsButonHandler() {
        return this.settingsButonHandler;
    }

    public void setSettingsButonHandler(View.OnClickListener settingsButonHandler2) {
        this.settingsButonHandler = settingsButonHandler2;
    }

    public void setSyncProgressValue(Integer value){
        this.syncProgressValue.postValue(value);
    }

    public MutableLiveData<Integer> getSyncProgressValue(){
        return this.syncProgressValue;
    }
}
