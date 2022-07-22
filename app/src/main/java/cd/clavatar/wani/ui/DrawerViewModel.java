package cd.clavatar.wani.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.databinding.DialogRawcardCollectorBinding;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.WaniDialogViewModelWrapper;

public class DrawerViewModel extends WaniDialogViewModelWrapper<InfoDialogResult> {

    private static DrawerViewModel INSTANCE=null;
    private  Context contex;
    MutableLiveData<CompactUser> logedIdUser=new MutableLiveData<>();
    public static DrawerViewModel getInstance(Context context, int TAG){
        if(DrawerViewModel.INSTANCE==null){
            synchronized (DrawerViewModel.class){
               DrawerViewModel.INSTANCE=new DrawerViewModel(context);
            }
        }
        INSTANCE._init();
        return DrawerViewModel.INSTANCE;
    }

    public DrawerViewModel(Context contex) {
        this.contex = contex;

    }

    private void _init() {
        this.logedIdUser.setValue(WaniApp.getLogedInUser());
    }

    public MutableLiveData<CompactUser> getLogedIdUser() {
        return logedIdUser;
    }

    public void setLogedIdUser(MutableLiveData<CompactUser> logedIdUser) {
        this.logedIdUser = logedIdUser;
    }
}
