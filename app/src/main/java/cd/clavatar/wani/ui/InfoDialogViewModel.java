package cd.clavatar.wani.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import java.net.HttpCookie;

import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.databinding.DialogRawcardCollectorBinding;
import cd.clavatar.wani.vendor.IDialogResultHandler;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.WaniDialogViewModelWrapper;

public class InfoDialogViewModel extends WaniDialogViewModelWrapper<InfoDialogResult> {
    private int tag = -1;
    private DialogRawcardCollectorBinding _binding=null;
    private Dialog selfRef=null;
    private MutableLiveData<String> text=new MutableLiveData<>("Réussite"), cancelButtonText=new MutableLiveData<>("Annuler"), validateButtonText=new MutableLiveData<>("OK");

    private View.OnClickListener validateButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            selfRef.setCancelable(true);
            getInfoDialogResultHandler().onInfoDialogResult(new InfoDialogResult(tag, 1));

        }
    };

    private View.OnClickListener cancelButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getInfoDialogResultHandler().onInfoDialogResult(new InfoDialogResult(tag, 0));

        }
    };

    private static InfoDialogViewModel INSTANCE=null;
    private final Context contex;
    private MutableLiveData<Boolean> cancelButtonVisibility=new MutableLiveData<>(false);
    private IInfoDialogResultHandler<InfoDialogResult> resultHandler;

    public static InfoDialogViewModel getInstance(Context context, int TAG){
        if(InfoDialogViewModel.INSTANCE==null){
            synchronized (InfoDialogViewModel.class){
               InfoDialogViewModel.INSTANCE=new InfoDialogViewModel(context);
            }
        }
        INSTANCE.setTag(TAG);
        INSTANCE._init();
        return InfoDialogViewModel.INSTANCE;
    }

    public InfoDialogViewModel(Context contex) {
        this.contex = contex;

    }

    private void _init() {
        text.setValue("Réussite");
        cancelButtonText.setValue("Annuler");
        validateButtonText.setValue("OK");
        cancelButtonVisibility.setValue(false);
        resultHandler=null;
    }

    public View.OnClickListener getValidateButtonClickHandler() {
        return validateButtonClickHandler;
    }

    public View.OnClickListener getCancelButtonClickHandler() {
        return cancelButtonClickHandler;
    }

    public MutableLiveData<String> getText() {
        return text;
    }

    public void setText(String text) {
        if(text!=null && text!="")this.text.setValue(text);
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

    public IInfoDialogResultHandler<InfoDialogResult> getInfoDialogResultHandler() {
        return this.resultHandler;
    }

    public void setInfoDialogResultHandler(IInfoDialogResultHandler<InfoDialogResult> resultHandler) {
        this.resultHandler=resultHandler;
    }

    public void setSelfRef(Dialog selfRef) {
        this.selfRef = selfRef;
    }
}
