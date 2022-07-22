package cd.clavatar.wani.ui;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.databinding.DialogServerBinding;
import cd.clavatar.wani.vendor.IServerDialogHandler;
import cd.clavatar.wani.vendor.ServerDialogResult;

public class ServerDialogViewModel {

    private static Context mContex;
    private int tag = -1;
    private static DialogServerBinding _binding=null;
    private static ServerDialogViewModel INSTANCE=null;
    private final Context contex;
    //private MutableLiveDataString deviceId;
    private MutableLiveData<Boolean> cancelButtonVisibility=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> extendMode=new MutableLiveData<>(false);
    private IServerDialogHandler<ServerDialogResult> resultHandler;
    private static MutableLiveData<Boolean> backup=new MutableLiveData<>(false);

    private MutableLiveData<Integer> checkedButtonId=new MutableLiveData<>(R.id.prim);

    private MutableLiveData<String> cancelButtonText=new MutableLiveData<>("Annuler"), validateButtonText=new MutableLiveData<>("AJOUTER"), numeroLotVaccin=new MutableLiveData<>();
    private View.OnClickListener validateButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ServerDialogResult vdr=new ServerDialogResult(checkedButtonId.getValue()==R.id.sec, tag, 1);
            getServerResultHandler().onServerDialogResult(vdr);
        }
    };

    private View.OnClickListener cancelButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ServerDialogResult vdr=new ServerDialogResult(checkedButtonId.getValue()==R.id.sec, tag, 0);
            getServerResultHandler().onServerDialogResult(vdr);

        }
    };

    private View.OnClickListener radioButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ServerDialogResult vdr=new ServerDialogResult(false, tag, 0);
            getServerResultHandler().onServerDialogResult(vdr);

        }
    };


    public static ServerDialogViewModel getInstance(Context context, int TAG){
        if(ServerDialogViewModel.INSTANCE==null){
            synchronized (ServerDialogViewModel.class){
               ServerDialogViewModel.INSTANCE=new ServerDialogViewModel(context);
            }
        }
        mContex=context;
        INSTANCE.setTag(TAG);
        INSTANCE._init();
        return ServerDialogViewModel.INSTANCE;
    }

    public ServerDialogViewModel(Context contex) {
        this.contex = contex;

    }

    private void _init() {

        this.checkedButtonId.setValue( WaniApp.isBackup()? R.id.sec: R.id.prim);
        //this.deviceId

    }

    public View.OnClickListener getValidateButtonClickHandler() {
        return validateButtonClickHandler;
    }

    public View.OnClickListener getCancelButtonClickHandler() {
        return cancelButtonClickHandler;
    }

    public void setCancelButtonVisibility(MutableLiveData<Boolean> cancelButtonVisibility) {
        this.cancelButtonVisibility = cancelButtonVisibility;
    }

    public IServerDialogHandler<ServerDialogResult> getServerResultHandler() {
        return resultHandler;
    }

    public void setVaccinResultHandler(IServerDialogHandler<ServerDialogResult> resultHandler) {
        this.resultHandler = resultHandler;
    }



    public void setCancelButtonText(MutableLiveData<String> cancelButtonText) {
        this.cancelButtonText = cancelButtonText;
    }

    public void setValidateButtonText(MutableLiveData<String> validateButtonText) {
        this.validateButtonText = validateButtonText;
    }

    public MutableLiveData<String> getNumeroLotVaccin() {
        return numeroLotVaccin;
    }

    public void setNumeroLotVaccin(MutableLiveData<String> numeroLotVaccin) {
        this.numeroLotVaccin = numeroLotVaccin;
    }

    public void setValidateButtonClickHandler(View.OnClickListener validateButtonClickHandler) {
        this.validateButtonClickHandler = validateButtonClickHandler;
    }

    public void setCancelButtonClickHandler(View.OnClickListener cancelButtonClickHandler) {
        this.cancelButtonClickHandler = cancelButtonClickHandler;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public MutableLiveData<String> getCancelButtonText() {
        return cancelButtonText;
    }

    public MutableLiveData<String> getValidateButtonText() {
        return validateButtonText;
    }

    public void setCancelButtonText(String cancelButtonText) {
        if(cancelButtonText!=null && cancelButtonText!="")this.cancelButtonText.setValue(cancelButtonText);
    }

    public void setValidateButtonText(String validateButtonText) {
        if(validateButtonText!=null && validateButtonText!="")this.validateButtonText.setValue(validateButtonText);
    }

    public void setCancelButtonVisibility(Boolean cancelButtonVisibility) {
        this.cancelButtonVisibility.setValue(cancelButtonVisibility);
    }

    public MutableLiveData<Boolean> getCancelButtonVisibility() {
        return cancelButtonVisibility;
    }

    public MutableLiveData<Boolean> getExtendMode() {
        return extendMode;
    }

    public void setExtendMode(Boolean extendMode) {
        this.extendMode.setValue(extendMode);
    }

    public IServerDialogHandler<ServerDialogResult> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(IServerDialogHandler<ServerDialogResult> resultHandler) {
        this.resultHandler = resultHandler;
    }

    public static DialogServerBinding get_binding() {
        return _binding;
    }

    public static void set_binding(DialogServerBinding _binding) {
        ServerDialogViewModel._binding = _binding;
    }

    public static MutableLiveData<Boolean> getBackup() {
        return backup;
    }

    public static void setBackup(Boolean backup) {
        ServerDialogViewModel.backup.setValue(backup);
    }

    public MutableLiveData<Integer> getCheckedButtonId() {
        return checkedButtonId;
    }

    public void setCheckedButtonId(MutableLiveData<Integer> checkedButtonId) {
        this.checkedButtonId = checkedButtonId;
    }
}
