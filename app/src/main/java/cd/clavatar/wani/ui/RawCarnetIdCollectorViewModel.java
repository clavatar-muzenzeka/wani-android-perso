package cd.clavatar.wani.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.databinding.DialogRawcardCollectorBinding;
import cd.clavatar.wani.vendor.WaniDialogViewModelWrapper;

public class RawCarnetIdCollectorViewModel extends WaniDialogViewModelWrapper<String> {
    private int selection = -1;
    private MediatorLiveData<Boolean> valid= new MediatorLiveData<>();
    private DialogRawcardCollectorBinding _binding=null;
    //private JSONObject literal=new JSONObject();

    private View.OnClickListener validateButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Log.d("zaza","Clicked");
            String val=idRawCarnet.getValue();
            idRawCarnet.setValue(null);
            getResultHandler().onResult(val);

        }
    };
    private MutableLiveData<String> idRawCarnet=new MutableLiveData<>();

    private static RawCarnetIdCollectorViewModel INSTANCE=null;
    private final Context contex;

    public static RawCarnetIdCollectorViewModel getInstance(Context context){
        if(RawCarnetIdCollectorViewModel.INSTANCE==null){
            synchronized (RawCarnetIdCollectorViewModel.class){
               RawCarnetIdCollectorViewModel.INSTANCE=new RawCarnetIdCollectorViewModel(context);
            }
        }
        return RawCarnetIdCollectorViewModel.INSTANCE;
    }

    public RawCarnetIdCollectorViewModel(Context contex) {
        this.contex = contex;
        this._init();
    }

    private void _init() {

    }

    public View.OnClickListener getValidateButtonClickHandler() {
        return validateButtonClickHandler;
    }

    public MutableLiveData<String> getIdRawCarnet() {
        return idRawCarnet;
    }


}
