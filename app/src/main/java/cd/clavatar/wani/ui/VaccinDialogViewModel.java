package cd.clavatar.wani.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.databinding.DialogVaccinBinding;
import cd.clavatar.wani.vendor.IVaccinDialogHandler;
import cd.clavatar.wani.vendor.VaccinDialogResult;

public class VaccinDialogViewModel{
    public static boolean SELECTED_MANUALLY = false;
    public static boolean SELECTED_HANDLED = false;
    public static boolean SELECTED_INIT = true;
    private static Context mContex;
    private int tag = -1;
    private static DialogVaccinBinding _binding=null;
    private static VaccinDialogViewModel INSTANCE=null;
    private final Context contex;
    private MutableLiveData<Boolean> cancelButtonVisibility=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> extendMode=new MutableLiveData<>(false);
    private IVaccinDialogHandler<VaccinDialogResult> resultHandler;
    private static MutableLiveData<List<Vaccin>> vaccins=new MutableLiveData<>();

    private static MutableLiveData<Vaccin> vaccin=new MutableLiveData<>();
    private MutableLiveData<String> cancelButtonText=new MutableLiveData<>("Annuler"), validateButtonText=new MutableLiveData<>("AJOUTER"), numeroLotVaccin=new MutableLiveData<>();
    private MutableLiveData<Date> receivedDate=new MutableLiveData<>(new Date());

    private View.OnClickListener validateButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(receivedDate.getValue() !=null ){
                VaccinOccurence vo=new VaccinOccurence();
                vo.setIdVaccin(vaccin.getValue());
                vo.setIdLotVaccin(numeroLotVaccin.getValue());
                vo.setReceived(receivedDate.getValue());
                vo.save();
                VaccinDialogResult vdr=new VaccinDialogResult(vo, tag, 1);
                getVaccinResultHandler().onVaccinDialogResult(vdr);
            }
            else Toast.makeText(mContex, "Erreur de format de la date de réception, assurez vous de suivre le modèle JJ/MM/AAA", Toast.LENGTH_LONG).show();

        }
    };

    private View.OnClickListener cancelButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            VaccinOccurence vo=new VaccinOccurence();
            vo.setIdVaccin(vaccin.getValue());
            vo.setIdLotVaccin(numeroLotVaccin.getValue());
            vo.setReceived(receivedDate.getValue());

            VaccinDialogResult vdr=new VaccinDialogResult(vo, tag, 0);
            getVaccinResultHandler().onVaccinDialogResult(vdr);

        }
    };


    public static VaccinDialogViewModel getInstance(Context context, int TAG){
        if(VaccinDialogViewModel.INSTANCE==null){
            synchronized (VaccinDialogViewModel.class){
               VaccinDialogViewModel.INSTANCE=new VaccinDialogViewModel(context);
            }
        }
        mContex=context;
        INSTANCE.setTag(TAG);
        INSTANCE._init();
        return VaccinDialogViewModel.INSTANCE;
    }

    public VaccinDialogViewModel(Context contex) {
        this.contex = contex;

    }

    private void _init() {
        SELECTED_HANDLED=false;
        SELECTED_MANUALLY=false;
        SELECTED_INIT=true;
        vaccins.observe((AppCompatActivity) mContex, new Observer<List<Vaccin>>() {
            @Override
            public void onChanged(List<Vaccin> vaccins) {
                if(vaccins!=null && vaccins.size()!=0) vaccin.setValue(vaccins.get(0));
            }
        });
        receivedDate.setValue(new Date());
        numeroLotVaccin.setValue(null);
        cancelButtonText.setValue("Annuler");
        validateButtonText.setValue("OK");
        cancelButtonVisibility.setValue(false);
        resultHandler=null;
        extendMode.setValue(false);
        vaccin.observe((AppCompatActivity) mContex, new Observer<Vaccin>() {
            @Override
            public void onChanged(Vaccin vaccin) {
//if(vaccin!=null)                // Log.d("zaza",String.format("selected vaccib %s",vaccin.getDesignation()));
            }
        });
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

    public IVaccinDialogHandler<VaccinDialogResult> getVaccinResultHandler() {
        return resultHandler;
    }

    public void setVaccinResultHandler(IVaccinDialogHandler<VaccinDialogResult> resultHandler) {
        this.resultHandler = resultHandler;
    }

    public MutableLiveData<Vaccin> getVaccin() {
        return vaccin;
    }

    public static void setVaccin(Vaccin vaccin) {
        VaccinDialogViewModel.vaccin.setValue(vaccin);
    }

    private AdapterView.OnItemSelectedListener spinnerVaccintemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            vaccin.setValue((Vaccin) parent.getAdapter().getItem(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public static void setVaccin(Vaccin vaccin, boolean updateSelection) {
        if(updateSelection){
            VaccinDialogViewModel.vaccin.setValue(vaccin);
            if(vaccin != null && updateSelection &&_binding!=null){
                // Log.d("zaza","not null");
                int index = vaccins.getValue().indexOf(vaccin);
                // Log.d("zaza",String.format("vc selected index %d",index));
                // Log.d("zaza",String.format("binding spinner %s",_binding.spinnerMonthFrom.getId()));
                _binding.spinnerMonthFrom.setSelection(index);
                // Log.d("zaza",String.format("effectivelly selected %d", _binding.spinnerMonthFrom.getSelectedItemPosition()));
            } else {
                // Log.d("zaza","vaccin is null");
            }
        }

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

    public MutableLiveData<Date> getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(MutableLiveData<Date> receivedDate) {
        this.receivedDate = receivedDate;
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

    public IVaccinDialogHandler<VaccinDialogResult> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(IVaccinDialogHandler<VaccinDialogResult> resultHandler) {
        this.resultHandler = resultHandler;
    }

    public MutableLiveData<List<Vaccin>> getVaccins() {
        return vaccins;
    }

    public void setVaccins(List<Vaccin> vaccins) {
        this.vaccins.setValue(vaccins);
    }

    public static DialogVaccinBinding get_binding() {
        return _binding;
    }

    public static void set_binding(DialogVaccinBinding _binding) {
        VaccinDialogViewModel._binding = _binding;
    }

    public AdapterView.OnItemSelectedListener getSpinnerVaccintemSelectedHandler() {
        return spinnerVaccintemSelectedHandler;
    }

    public void setSpinnerVaccintemSelectedHandler(AdapterView.OnItemSelectedListener spinnerVaccintemSelectedHandler) {
        this.spinnerVaccintemSelectedHandler = spinnerVaccintemSelectedHandler;
    }
}
