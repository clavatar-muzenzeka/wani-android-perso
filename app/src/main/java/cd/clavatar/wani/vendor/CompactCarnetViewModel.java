package cd.clavatar.wani.vendor;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.ui.carnet.CarnetViewModel;

/**
 * Created by Cl@v@t@r on 2020-02-17
 */
public class CompactCarnetViewModel {

    private static CarnetSelectionListener mListener=null;

    public static MutableLiveData<Long> selectedId=new MutableLiveData<>(null);

    public MutableLiveData<CompactCarnet> carnet=new MutableLiveData<>(null);

    public CompactCarnetViewModel(CompactCarnet cc){
        this.carnet.setValue(cc);
    }

    public static MutableLiveData<Long> getSelectedId() {
        return selectedId;
    }

    public MutableLiveData<CompactCarnet> getCarnet() {
        return carnet;
    }

    public static void select(Long id){
        selectedId.setValue(id);
    }

    public static void setSelectionListener(CarnetSelectionListener mListener) {
        CompactCarnetViewModel.mListener = mListener;
    }

    public interface CarnetSelectionListener{
        public void handleSelected(String id);
    }
}
