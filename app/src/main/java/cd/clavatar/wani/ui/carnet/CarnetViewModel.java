package cd.clavatar.wani.ui.carnet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.Check;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.CompactStatus;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.PaimentType;
import cd.clavatar.wani.data.model.People;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.Story;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.databinding.DialogIssuesBinding;
import cd.clavatar.wani.databinding.DialogVaccinBinding;
import cd.clavatar.wani.ui.IssuedDialogViewModel;
import cd.clavatar.wani.ui.VaccinDialogViewModel;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.utilities.ISO8601;
import cd.clavatar.wani.utilities.WaniSyncHelper;
import cd.clavatar.wani.vendor.CompactCarnetViewModel;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.IVaccinDialogHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.Inventory;
import cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle;
import cd.clavatar.wani.vendor.VaccinDialogResult;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class CarnetViewModel extends ViewModel {
    private static final int TAG_PAIEMENT_PROMPT = 0;
    private static final int TAG_PAIEMENT_INTERACT_PROMPT = 1;
    private static final int TAG_PROMPT_FOR_DEFAULT_VACCIN_ASSIGNATION = 2;
    private static final int TAG_PREVENT_ERRASING_BEFORE_DUPLICATION = 3;
    private static final int ISSUE_DIALOG = 4;
    private static CarnetViewModel INSTANCE=null;
    private Context mContex;
    private final WaniRepository mRepository;


    MutableLiveData<CompactCarnet> carnet = new MutableLiveData<>(null);
    private MutableLiveData<List<Vaccin>> vaccinsList=new MutableLiveData<>(null);
    private MutableLiveData  <List<PaimentType>> paiementTypesList =new MutableLiveData<>(null);
    private MediatorLiveData<Inventory> inventory= new MediatorLiveData<Inventory>();


    MutableLiveData<String>
            name=new MutableLiveData<>(""),
            lastName=new MutableLiveData<>(""),
            firstName=new MutableLiveData<>(""),
            sex=new MutableLiveData<>("")  ,
            adressProvince=new MutableLiveData<>(""),
            adressTownship=new MutableLiveData<>(""),
            adressCity=new MutableLiveData<>(""),
            adressLocality=new MutableLiveData<>(""),
            adressRoad=new MutableLiveData<>(""),
            adressNumber=new MutableLiveData<>(""),
            tel=new MutableLiveData<>(""),
            email=new MutableLiveData<>("");




    private MutableLiveData<Long> bufferedProfileId =new MutableLiveData<Long>(null);


    MutableLiveData<Date> birthDate=new MutableLiveData<>(null);
    MutableLiveData<Boolean> valid=new MutableLiveData<>(false);
    MutableLiveData<Boolean> uploadingPicture=new MutableLiveData<>(false);
    MutableLiveData<Boolean> networkCall=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> readOnly=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> pictureAvaillable=new MutableLiveData<>(false), pictureUploaded=new MutableLiveData<>(false), preserveReceived= new MutableLiveData<>(false);
    private MutableLiveData<String> carnetId=new MutableLiveData<>(null);
    private MutableLiveData<String> textInfo=new MutableLiveData<>(null);
    private int dialogResulType=0;
    MediatorLiveData<Boolean> preprocessed=new MediatorLiveData<>();
    MutableLiveData<Boolean> vaccinLoaded=new MutableLiveData<>(false);
    MutableLiveData<Boolean> paiementTypeLoaded=new MutableLiveData<>(false);
    MutableLiveData<Boolean> numerisation= new MutableLiveData<>(false);
    MutableLiveData<Boolean> consigningRaw= new MutableLiveData<>(false);
    MutableLiveData<Date> newVaccinDate= new MutableLiveData<>(new Date());
    private MutableLiveData<String> rawCarnetId=new MutableLiveData<>();
    private MutableLiveData<Boolean> lockRawCarnetId=new MutableLiveData<>(false);
    private MutableLiveData<String> passportNumber=new MutableLiveData<>();
    private MutableLiveData<String> nationality=new MutableLiveData<>();



    private FloatingActionButton fabValidate;
    private FloatingActionButton fabPreview;

    private Dialog vaccinDialog;
    private int currRequiredVaccinIndex=0;
    private Drawable drawableUploading, drawablePictureUnavaillable, drawableUploadToServer, drawableReadOnly, drawablePreview, drawableUserPlaceholder, drawableRawcarnetPlaceholder, drawableDuplicate, drawableSwipe, drawableCheck;
    CircleImageView mIv;
    private Vaccin selectedVaccin=null;
    private Dialog atcdDialog;
    public Dialog infoDialog;
    public CompactCarnet interactingCarnet;
    private Dialog issuesDialog;









    View.OnClickListener validateButonClickHandler = new View.OnClickListener() {
        public void onClick(View v) {

            if (((Boolean) CarnetViewModel.this.readOnly.getValue()).booleanValue()) {
                if (((Integer) CarnetViewModel.this.mode.getValue()).intValue() == 5 && WaniApp.getLogedInUser().getRole().compareTo("Controler") == 0) {
                    if (CarnetViewModel.this.carnet.getValue().getCarnetPurpose() == 0) {
                        CarnetViewModel.this.preventIssues();
                    } else {
                        ((CarnetActivity) CarnetViewModel.this.mContex).finish();
                    }
                } else if (((Integer) CarnetViewModel.this.mode.getValue()).intValue() != 3 && ((Integer) CarnetViewModel.this.mode.getValue()).intValue() != 2) {
                    Toast.makeText(CarnetViewModel.this.mContex, "Mode lecture seule", Toast.LENGTH_LONG).show();
                } else if (((Integer) CarnetViewModel.this.mode.getValue()).intValue() == 3) {
                    CarnetViewModel.this.preventErrasingBeforeDuplication();
                } else if (!((Boolean) CarnetViewModel.this.pictureAvaillable.getValue()).booleanValue()) {
                    Toast.makeText(CarnetViewModel.this.mContex, "Veuillez prendre la photo du porteur avant", Toast.LENGTH_LONG).show();
                } else if (CarnetViewModel.this.uploadingPicture.getValue().booleanValue()) {
                    Toast.makeText(CarnetViewModel.this.mContex, "Patientez le temps du téléversement de la photo", Toast.LENGTH_LONG).show();
                } else {
                    CarnetViewModel.this.informSwipeCarnet();
                }
            } else if (CarnetViewModel.this.uploadingPicture.getValue().booleanValue()) {
                Toast.makeText(CarnetViewModel.this.mContex, "Patientez le temps du téléversement de la photo", Toast.LENGTH_LONG).show();
            } else if (!CarnetViewModel.this.valid.getValue().booleanValue()) {
                Toast.makeText(CarnetViewModel.this.mContex, "Formulaire incomplet, les champs marqués d'un (*) sont obligatoires, les groupes des champs marqués d'un (*) exigent le remplissage d'au moins un champ", Toast.LENGTH_LONG).show();
            }
                else if (!((Boolean) CarnetViewModel.this.pictureAvaillable.getValue()).booleanValue()) {
                    Toast.makeText(CarnetViewModel.this.mContex, "Veuillez prendre la photo du porteur avant", Toast.LENGTH_LONG).show();
                }
             else {
                CompactCarnet mCompactCarnet = CarnetViewModel.this.getBaseCarnet();
                if (mCompactCarnet.getCarnetPurpose() != 0 || CarnetViewModel.this.getMode().getValue().intValue() == 4) {
                    CarnetViewModel.this.interactingCarnet = mCompactCarnet;
                    CarnetViewModel.this.informPaiementCarnet();
                    return;
                }
                CarnetViewModel.this.promptForDefaultVaccinsAssignation(mCompactCarnet);
            }
        }
    };





    private View.OnClickListener issuesClickhandler=new View.OnClickListener() {
        class ShowIssueDialogClickHandler implements IInfoDialogResultHandler<InfoDialogResult>{
            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                CarnetViewModel.this.issuesDialog.dismiss();
                //preventCheckPaiement();
                return;

            }
        }



        @Override
        public void onClick(View v) {

if(inventory.getValue() != null && !inventory.getValue().isValid())
            CarnetViewModel.this.issuesDialog  = showInventoryDialog(CarnetViewModel.this.inventory, new ShowIssueDialogClickHandler());
        }
    };




    private void preventIssues(){

        class IssueDialogResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                issuesDialog.dismiss();
                if(result.getResult()==1 && result.getTag()==ISSUE_DIALOG){
                    preventCheckPaiement();
                }
            }
        }

        if(!inventory.getValue().isValid()){
            CarnetViewModel.this.issuesDialog = showInventoryDialog(inventory, new IssueDialogResultHandler());
        }
        else preventCheckPaiement();
    }

    private Dialog showInventoryDialog(MediatorLiveData<Inventory> inventory, IInfoDialogResultHandler<InfoDialogResult> resutHandler) {
        DialogIssuesBinding dialogATCDDBinding = DialogIssuesBinding.inflate(LayoutInflater.from(mContex));


        IssuedDialogViewModel idvm= IssuedDialogViewModel.getInstance(mContex, ISSUE_DIALOG);
        idvm.setInventory(inventory);
        idvm.setCancelButtonVisibility(false);
        idvm.setInfoDialogResultHandler(resutHandler);
        dialogATCDDBinding.setModel(
                idvm
        );

        dialogATCDDBinding.setLifecycleOwner((AppCompatActivity)mContex);
        View dialogView = dialogATCDDBinding.getRoot();

        AlertDialog.Builder dBuilder = new AlertDialog.Builder(mContex);
        dBuilder.setCancelable(false);
        dBuilder.setView(dialogView);
        Dialog infoDialog = dBuilder.create();
        idvm.setSelfRef(infoDialog);
        infoDialog.show();
        return infoDialog;
    }

/*
    public boolean isCertifiedCovid(){
        boolean isCertifiedCovid=false;
        if(this.carnet.getValue() != null){

            for(CompactStatus status:this.carnet.getValue().getStory().getStatusStory()){
                if(status.getIdPaiement().getIdPaiementType().getDesignation().compareTo("Certificat COVID-19") ==0) {
                    isCertifiedCovid=true;
                    break;
                }
            }
        }
        return isCertifiedCovid;
    }


 */
    private void preventCheckPaiement() {
        PaimentType mPaimentType=getPaimentTypeDesignation("Check point");

        class CheckPaiementResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                if(infoDialog !=null)infoDialog.dismiss();
                if(result.getResult()==1){
                    procedCheck();
                }
            }
        }

        prepareAndPromptForPaiement(mContex, mPaimentType,  new CheckPaiementResultHandler());
    }








    private void procedCheck() {
        CompactPaiement mCompactPaiement= new CompactPaiement();
        PaimentType checkPaiementType= getPaimentTypeDesignation("Check point");
        if(checkPaiementType !=null) mCompactPaiement.setIdPaiementType(checkPaiementType);
        mCompactPaiement.setIdPayer(this.carnet.getValue().getIdPeople());
        mCompactPaiement.setIdReceiver(WaniApp.getLogedInUser());
        mCompactPaiement.save();
        Check mCheck=new Check();
        mCheck.setIdPaiement(mCompactPaiement);
        CompactCarnet mcc=carnet.getValue();
        Story ms=mcc.getStory();

        if(ms==null){
            ms=new Story();
            ms.save();
        }
        mCheck.setLocaleStoryId(ms.getId());
        mCheck.save();
        ms.setEdited(WaniApp.getCurrentDate());
        ms.update();
        List<Check> checks=ms.getCheckStory();
        checks.add(mCheck);
        mcc.setStory(ms);
        mcc.setEdited(WaniApp.getCurrentDate());
        mcc.update();
        carnet.setValue(mcc);
        Toast.makeText(mContex, "Reussite", Toast.LENGTH_LONG).show();
        Common.printReceipt(CarnetViewModel.this.mContex, mCompactPaiement, CarnetViewModel.this.getInventory().getValue());
    }



    private AdapterView.OnItemSelectedListener spinnerNationalityItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            nationality.setValue(parent.getAdapter().getItem(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private MainToCarnetActivityRoutingBundle bundle;
    private MutableLiveData<Integer> mode=new MutableLiveData<>(-1);



    public void resetCarnId() {
        this.carnetId.setValue(null);
    }








    public void initAfterMainviePaused() {
        //this.readOnly.setValue(false);
    }









    public void initAfterMainvieDestroyed() {
        this.readOnly.setValue(false);
        this.carnet.setValue(null);
        this.carnetId.setValue(null);
        this.mode.setValue(-1);
        this.rawCarnetId.setValue(null);
        this.nationality.setValue(null);
        this.passportNumber.setValue(null);
        this.carnetId.setValue(null);
        this.inventory.setValue(null);
        CompactCarnetViewModel.select(null);
        if(infoDialog!=null)infoDialog.dismiss();
        if(vaccinDialog!=null) vaccinDialog.dismiss();
        //TODO
    }






    public AdapterView.OnItemSelectedListener getSpinnerNationalityItemSelectedHandler() {
        return spinnerNationalityItemSelectedHandler;
    }






    public void setSpinnerNationalityItemSelectedHandler(AdapterView.OnItemSelectedListener spinnerNationalityItemSelectedHandler) {
        this.spinnerNationalityItemSelectedHandler = spinnerNationalityItemSelectedHandler;
    }







    private View.OnClickListener dialogInfoClickHandler=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(dialogResulType==0) {
                dialogResulType=0;
                //postCarnet();
            }
        }
    };






    private Observer<? super Boolean> observerPreprocessor=new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {

            // Log.d("zaza",String.format("processor oberver trigered with %b",aBoolean));

            Boolean val=paiementTypeLoaded.getValue()&&vaccinLoaded.getValue();
            preprocessed.setValue(val);

            networkCall.setValue(false);
        }
    };








    private AdapterView.OnItemSelectedListener tabagFromMonthSelectionHandler=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            parent.getAdapter().getItem(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };







    private View.OnClickListener newVaccinClickhandler=new View.OnClickListener() {
        class AddVaccinOccurenceVaccinDialogResulHandler implements IVaccinDialogHandler<VaccinDialogResult>{

            @Override
            public void onVaccinDialogResult(VaccinDialogResult result) {
                vaccinDialog.dismiss();
                if(result.getResult()==1){
                    VaccinOccurence vo=result.getVaccinOccurence();
                    String paiementTypeStr="Vaccin "+result.getVaccinOccurence().getIdVaccin().getDesignation();


                    PaimentType mPt=CarnetViewModel.this.getPaimentTypeDesignation(paiementTypeStr);
                    // Log.d("zaza",String.format("vaccin des selected %s",result.getVaccinOccurence().getIdVaccin().getDesignation()));

                    if(mPt!=null) {
                        // Log.d("zaza",String.format("vaccin des %s",mPt.getDesignation()));
                    }

                    else{
                        // Log.d("zaza","vaccin des is null");
                    }


                    class PaiementVaccinResponseHandler implements IInfoDialogResultHandler<InfoDialogResult>{

                        @Override
                        public void onInfoDialogResult(InfoDialogResult result) {
                            CarnetViewModel.this.infoDialog.dismiss();
                            if(result.getResult()==1){
                                procedVaccinationWithVaccinAndPaiement(vo, mPt);
                            }else return;
                        }
                    }

                    if(mPt!=null && mPt.getPaiementPrice()!=0){
                        prepareAndPromptForPaiement(mContex, mPt, new PaiementVaccinResponseHandler());
                    }else procedVaccinationWithVaccinAndPaiement(vo, null);
                }

            }
        }

        @Override
        public void onClick(View v) {
            if(WaniApp.getLogedInUser().getRole().compareTo("Controler")==0)
                Toast.makeText(mContex, "Un controlleur ne peut pas vacciner", Toast.LENGTH_SHORT).show();
            else
            showVaccinDialog(mContex, 2, vaccinsList.getValue(), null, false,  new AddVaccinOccurenceVaccinDialogResulHandler());
        }
    };



    private void procedVaccinationWithVaccinAndPaiement(VaccinOccurence result, PaimentType mPt) {
        //GreenDao.getSession().getVaccinOccurenceDao().insert(result);
        if(mPt!=null){

            CompactPaiement paiementVaccin= new CompactPaiement();
            paiementVaccin.setIdPaiementType(mPt);
            paiementVaccin.setIdPayer(this.carnet.getValue().getIdPeople());
            paiementVaccin.setIdReceiver(WaniApp.getLogedInUser());
            paiementVaccin.save();
            result.setIdPaiement(paiementVaccin);
            result.setEdited(WaniApp.getCurrentDate());
            result.update();
            Common.printReceipt(mContex, paiementVaccin, CarnetViewModel.this.getInventory().getValue());
        }
        CompactCarnet mcc=this.carnet.getValue();
        result.setLocaleCarnetId(carnet.getValue().getId());
        result.setEdited(WaniApp.getCurrentDate());
        result.update();
        mcc.setEdited(WaniApp.getCurrentDate());
        mcc.save();
        mcc.getVaccins().add(0, result);
        CarnetViewModel.this.carnet.setValue(mcc);
        Toast.makeText(mContex, "Reussite", Toast.LENGTH_SHORT).show();
        WaniSyncHelper.getInstance(mContex).procedSynk(new WaniRepository.WaniRepositorySynkListener());

    }


    Observer formFieldsObserver=new Observer() {
        @Override
        public void onChanged(Object o) {
            // Log.d("zaza","validity observer fired");
            Boolean mValue=
                    (name.getValue()!=null && name.getValue().compareTo("")!=0) &&
                            sex.getValue()!=null && sex.getValue().compareTo("")!=0 &&
                            ((adressProvince.getValue()!=null && adressProvince.getValue().compareTo("")!=0) ||
                                    (adressTownship.getValue()!=null && adressTownship.getValue().compareTo("")!=0) ||
                                    (adressCity.getValue()!=null && adressCity.getValue().compareTo("")!=0) ||
                                    (adressLocality.getValue()!=null && adressLocality.getValue().compareTo("")!=0) ||
                                    (adressRoad.getValue()!=null && adressRoad.getValue().compareTo("")!=0) ||
                                    (adressNumber.getValue()!=null && adressNumber.getValue().compareTo("")!=0)) &&
                            birthDate.getValue()!=null && birthDate.getValue().toGMTString().compareTo("")!=0;
            valid.setValue(mValue);
        }
    };






    Observer sendIconStatusObserver=new Observer() {
        @Override
        public void onChanged(Object o) {
            // Log.d("zaza","____________________________________________________________");
            // Log.d("zaza",String.format("\tvalid status %b", valid.getValue()));
            // Log.d("zaza",String.format("\tpictAv status %b", pictureAvaillable.getValue()));
            // Log.d("zaza",String.format("\tpictureUploaded %b", pictureUploaded.getValue()));
            // Log.d("zaza",String.format("\treadonly %b", readOnly.getValue()));
            if(readOnly.getValue()){
                if(WaniApp.getLogedInUser().getRole().compareTo("Controler")==0 && mode.getValue()==MainToCarnetActivityRoutingBundle.CHECK){
                    setfabStyle(fabValidate, drawableCheck, 1);
                }else
                if(mode.getValue()==MainToCarnetActivityRoutingBundle.CHECK)
                    setfabStyle(fabValidate, drawableReadOnly, 0);
                else if (mode.getValue()==MainToCarnetActivityRoutingBundle.DUPLICATION)setfabStyle(fabValidate, drawableDuplicate, 1);
                else {
                    if( pictureUploaded.getValue()){
                        setfabStyle(fabValidate, drawableSwipe, 1);
                    }else setfabStyle(fabValidate, drawableSwipe, 0);
                }
            }
            else{
                if(!CarnetViewModel.this.pictureAvaillable.getValue()){
                    setfabStyle(fabValidate, drawablePictureUnavaillable, 0);
                }else{
                    if(!pictureUploaded.getValue()){
                        if(valid.getValue()){
                            setfabStyle(fabValidate, drawableUploading, 0);
                        }else {
                            setfabStyle(fabValidate, drawableUploadToServer, 0);
                        }
                    }
                    else{
                        if(!valid.getValue()){
                            setfabStyle(fabValidate, drawableUploadToServer, 0);
                        }
                        else {
                            setfabStyle(fabValidate, drawableUploadToServer, 1);
                        }
                    }
                }
            }

        }
    };















    private void preventErrasingBeforeDuplication() {
        class PreventErrasingBeforeDuplicationResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                // Log.d("zaza",String.format("Carnet People after prevent %d",carnet.getValue().getIdPeople().getId()));
                if(result.getResult()==1)informPaiementDupication();
            }
        }

        String text="En continuant, l'actuelle carnet sera supprimé du système et toutes les informations seront transférées sur celui que vous venez de scanner";

        infoDialog=Common.showInfoDialog(mContex, TAG_PREVENT_ERRASING_BEFORE_DUPLICATION, text, null, null, true, new PreventErrasingBeforeDuplicationResultHandler());
    }










    private void promptForDefaultVaccinsAssignation(CompactCarnet mCarnet) {
        class NumerisationCarnetResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                infoDialog.dismiss();
                if(result.getResult()==1){
                    if(result.getTag()==TAG_PROMPT_FOR_DEFAULT_VACCIN_ASSIGNATION){

                        interactForVaccinsOccurenceCreation(mCarnet, new ArrayList<VaccinOccurence>(), getRequiredVaccins(), -1 );
                    }
                }

            }
        }

        String text="Pour chaque vaccin ci-dessous remplissez le numéro de lot du vaccin et la date de réception (Informations à lire sur l'ancien carnet que vous numérisez ou que vous fournirez si c'est un carnet que vous émettez";
        infoDialog=Common.showInfoDialog(mContex, TAG_PROMPT_FOR_DEFAULT_VACCIN_ASSIGNATION, text, null, null, false, new NumerisationCarnetResultHandler());
    }










    public void informPaiementCarnet(){
        PaimentType numPaiementType;
        this.dialogResulType = 0;
        if (this.interactingCarnet.getCarnetPurpose() == 0) {
            if (this.mode.getValue().intValue() == 4) {
                numPaiementType = getPaimentTypeDesignation("Non numerised");
            } else if (this.bundle.getMode() == 1 && this.bundle.getCreationMode() == MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_DELIVERY) {
                numPaiementType = getPaimentTypeDesignation("Delivery");
            } else {
                numPaiementType = getPaimentTypeDesignation("Numerisation");
            }
            prepareAndPromptForPaiement(this.mContex, numPaiementType, new IInfoDialogResultHandler<InfoDialogResult>() {
                public void onInfoDialogResult(InfoDialogResult result) {
                    if (CarnetViewModel.this.infoDialog != null) {
                        CarnetViewModel.this.infoDialog.dismiss();
                    }
                    Log.d("zaza", String.format("result %d", new Object[]{Integer.valueOf(result.getResult())}));
                    if (result.getResult() == 1 && result.getTag() == 0) {
                        CarnetViewModel carnetViewModel = CarnetViewModel.this;
                        carnetViewModel.postCarnet(carnetViewModel.interactingCarnet);
                    }
                }
            });
            return;
        }
        prepareAndPromptForPaiement(this.mContex, getPaimentTypeDesignation("Certificat COVID-19"), new IInfoDialogResultHandler<InfoDialogResult>() {
            public void onInfoDialogResult(InfoDialogResult result) {
                if (CarnetViewModel.this.infoDialog != null) {
                    CarnetViewModel.this.infoDialog.dismiss();
                }
                Log.d("zaza", String.format("result %d", new Object[]{Integer.valueOf(result.getResult())}));
                if (result.getResult() == 1 && result.getTag() == 0) {
                    CarnetViewModel carnetViewModel = CarnetViewModel.this;
                    carnetViewModel.postCarnet(carnetViewModel.interactingCarnet);
                }
            }
        });
    }

    public void informSwipeCarnet(){
        this.dialogResulType=0;

        class SwipeCarnetResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                infoDialog.dismiss();
                if(result.getResult()==1){
                    if(result.getTag()==TAG_PAIEMENT_PROMPT){
                        // Log.d("zaza","within swipe");
                        swipeCarnet(CarnetViewModel.this.carnet.getValue());
                    }
                }

            }
        }


        prepareAndPromptForPaiement(mContex, getPaimentTypeDesignation("Numerisation"), new SwipeCarnetResultHandler());
    }












    public void informPaiementDupication(){
        this.dialogResulType=0;

        class DuplicationCarnetResultHandler implements IInfoDialogResultHandler<InfoDialogResult>{

            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                infoDialog.dismiss();
                if(result.getResult()==1){
                    // Log.d("zaza",String.format("Carnet People after prevent paiement %d",carnet.getValue().getIdPeople().getId()));

                    procedDuplication(carnet.getValue(), carnetId.getValue());


                }

            }
        }


        prepareAndPromptForPaiement(mContex, getPaimentTypeDesignation("Duplicata"), new DuplicationCarnetResultHandler());
    }






    private CompactCarnet getBaseCarnet(){
        CompactCarnet carn=null;
        if(CarnetViewModel.this.carnetId.getValue()!=null){
            carn=WaniDataSource.getInstance(mContex).getCompactCarnetByCarnetId(CarnetViewModel.this.carnetId.getValue());

        }else{
            carn=new CompactCarnet();
            carn.setLocalePictureRawCarnet(CarnetViewModel.this.bufferedProfileId.getValue());
        }

        carn.setIdRawCarnet(rawCarnetId.getValue());
        return carn;
    }





    private void postCarnet(final CompactCarnet carn){
        this.networkCall.setValue(true);
        People mPeople = getPeopleByMappingLiveDatas();
        mPeople.setCreated(WaniApp.getCurrentDate());
        mPeople.save();
        PaimentType paiementNumerisation = getPaimentTypeDesignation("Non numerised");
        if (getMode().getValue().intValue() == 1 || getMode().getValue().intValue() == 2) {
            if (carn.getCarnetPurpose() == 0) {
                if (this.bundle.getCreationMode() == MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_DELIVERY) {
                    paiementNumerisation = getPaimentTypeDesignation("Delivery");
                } else {
                    paiementNumerisation = getPaimentTypeDesignation("Numerisation");
                }
                if (getMode().getValue().intValue() == 2) {
                    paiementNumerisation = getPaimentTypeDesignation("Numerisation");
                }
            } else {
                getPaimentTypeDesignation("Certificat COVID-19");
            }
        }
        CompactPaiement mPaiement = new CompactPaiement();
        mPaiement.setCreated(WaniApp.getCurrentDate());
        mPaiement.setIdPaiementType(paiementNumerisation);
        mPaiement.setIdPayer(mPeople);
        mPaiement.setIdReceiver(WaniApp.getLogedInUser());
        mPaiement.save();
        postCarnetWithPaiement(carn, mPeople, mPaiement);
    }

    private void postCarnetWithPaiement(CompactCarnet carn, People freshPeople, CompactPaiement mPaiement) {
        carn.setCreated(WaniApp.getCurrentDate());
        carn.setIdPeople(freshPeople);
        Story cst = carn.getStory();
        if (cst == null) {
            cst = new Story();
        }
        if (cst.getId() == null) {
            cst.save();
        }
        CompactStatus cs = new CompactStatus();
        cs.setCreated(WaniApp.getCurrentDate());
        cs.setDistributionStatus(1);
        cs.setAuthor(WaniApp.getLogedInUser());
        if (getMode().getValue().intValue() == 4) {
            cs.setTypeStatus(2);
        } else {
            cs.setTypeStatus(0);
        }
        if (mPaiement != null) {
            cs.setIdPaiement(mPaiement);
        }
        cs.setLocaleStoryId(cst.getId());
        cs.save();
        carn.setCarnetStatus(cs);
        carn.setStory(cst);
        if (carn.getId() == null) {
            carn.save();
        } else {
            carn.setEdited(WaniApp.getCurrentDate());
            carn.update();
        }
        Toast.makeText(this.mContex, "Réussite", Toast.LENGTH_LONG).show();
        if (mPaiement != null) {
            Common.printReceipt(this.mContex, mPaiement, getInventory().getValue());
        } else {
            WaniSyncHelper.getInstance(this.mContex).procedSynk(new WaniRepository.WaniRepositorySynkListener());
        }
        ((CarnetActivity) this.mContex).finish();
    }











    private void swipeCarnet(CompactCarnet rawCarn){
        CarnetViewModel.this.networkCall.setValue(true);
        PaimentType paiementNumerisation=getPaimentTypeDesignation("Numerisation");
        CompactPaiement mPaiement=new CompactPaiement();
        mPaiement.setIdPaiementType(paiementNumerisation);
        mPaiement.setIdPayer(rawCarn.getIdPeople());
        mPaiement.setIdReceiver(WaniApp.getLogedInUser());
        mPaiement.save();

        CompactCarnet carn=WaniDataSource.getInstance(mContex).getCompactCarnetByCarnetId(carnetId.getValue());
        carn.setIdPeople(rawCarn.getIdPeople());
        carn.setIdRawCarnet(rawCarn.getIdRawCarnet());
        carn.setLocalePictureRawCarnet(rawCarn.getLocalePictureRawCarnet());
        carn.setPictureRawCarnet(rawCarn.getPictureRawCarnet());
        carn.setStory(rawCarn.getStory());

        for (VaccinOccurence mvo:
                rawCarn.getVaccins()
             ) {
            mvo.setLocaleCarnetId(carn.getId());
            mvo.setEdited(WaniApp.getCurrentDate());
            mvo.update();
        }

        carn.setVaccins(rawCarn.getVaccins());


        Story cst=carn.getStory();
        if(cst==null){
            cst=new Story();
            cst.save();
        }
        //StatusStory mss=new StatusStory();
        CompactStatus cs=new CompactStatus();
        cs.setDistributionStatus(1);
        cs.setTypeStatus(0);
        cs.setAuthor(WaniApp.getLogedInUser());
        cs.setLocaleStoryId(cst.getId());
        if(mPaiement!=null)cs.setIdPaiement(mPaiement);

        cs.save();
        cst.setEdited(WaniApp.getCurrentDate());
        cst.update();
        carn.setCarnetStatus(cs);

        //mss.setCarnetStatus(cs);



                /*
                List<StatusStory> foundedStatusStory=new ArrayList<>();
                if(cst.getStatusStory()!=null) foundedStatusStory=cst.getStatusStory();
                foundedStatusStory.add(mss);


                 */
        // TODO to many
        carn.setStory(cst);
        People cp=carn.getIdPeople();
        cp.setLocaleIdProfile(bufferedProfileId.getValue());
        cp.setEdited(WaniApp.getCurrentDate());
        cp.update();
        carn.setEdited(WaniApp.getCurrentDate());
        carn.update();
        Toast.makeText(mContex, "Réussite", Toast.LENGTH_SHORT).show();
        rawCarn.setStatus(false);
        CompactStatus mcsss=rawCarn.getCarnetStatus();
        mcsss.setDistributionStatus(2);
        mcsss.setEdited(WaniApp.getCurrentDate());
        mcsss.update();
        rawCarn.setEdited(WaniApp.getCurrentDate());
        rawCarn.update();
        Common.printReceipt(mContex, mPaiement, CarnetViewModel.this.getInventory().getValue());
        ((CarnetActivity)CarnetViewModel.this.mContex).finish();
    }










    public void procedDuplication(CompactCarnet ncc, String newId){

        // Log.d("zaza",String.format("People name %d",ncc.getIdPeople().getId()));
        CompactStatus mCompactCarneetStatus=new CompactStatus();
        mCompactCarneetStatus.setIdPaiement(null);
        mCompactCarneetStatus.setIdLocation(ncc.getCarnetStatus().getIdLocation());
        mCompactCarneetStatus.setTypeStatus(ncc.getCarnetStatus().getTypeStatus());
        mCompactCarneetStatus.setAuthor(WaniApp.getLogedInUser());
        mCompactCarneetStatus.setDistributionStatus(2);
        mCompactCarneetStatus.setLocaleStoryId(ncc.getStory().getId());
        mCompactCarneetStatus.save();
        
        ncc.setCarnetStatus(mCompactCarneetStatus);
        ncc.setEdited(WaniApp.getCurrentDate());
        ncc.update();
        
        
        PaimentType dupPaiementType=getPaimentTypeDesignation("Duplicata");
        CompactPaiement mCompactPaiement = new CompactPaiement();
        mCompactPaiement.setIdPaiementType(dupPaiementType);
        mCompactPaiement.setIdReceiver(WaniApp.getLogedInUser());
        mCompactPaiement.setIdPayer(ncc.getIdPeople());
        mCompactPaiement.save();
        CompactStatus nCs=new CompactStatus();
        nCs.setAuthor(WaniApp.getLogedInUser());
        nCs.setTypeStatus(1);
        nCs.setIdLocation(WaniApp.getLogedInUser().getIdLocation());
        nCs.setDistributionStatus(1);
        nCs.setIdPaiement(mCompactPaiement);
        nCs.save();


        CompactCarnet mcc= WaniDataSource.getInstance(mContex).getCompactCarnetByCarnetId(newId);

        mcc.setPictureRawCarnet(ncc.getPictureRawCarnet());
        mcc.setCarnetStatus(nCs);
        mcc.setIdPeople(ncc.getIdPeople());

        Story ms= new Story();
        ms.save();
        if(ncc.getStory() !=null){
            /*
            List<Check> mcks=ncc.getStory().getCheckStory();
            List<CompactStatus> mcss=ncc.getStory().getStatusStory();

            for (Check mck: mcks) {
                mck.setCarnetLocaleId(ms.getLocaleId());
                GreenDao.getSession().getCheckDao().update(mck);
            }

            for (CompactStatus mcs: mcss) {
                mcs.setCarnetLocaleId(ms.getLocaleId());
                GreenDao.getSession().getCompactStatusDao().update(mcs);
            }
            */
            for (Check mc: ncc.getStory().getCheckStory()
                 ) {
                mc.setLocaleStoryId(ms.getId());
                mc.setId(null);
                mc.save();
            }

            for (CompactStatus mc: ncc.getStory().getStatusStory()
            ) {
                mc.setLocaleStoryId(ms.getId());
                mc.setId(null);
                mc.save();
            }

            for(VaccinOccurence mvo: ncc.getVaccins()){
                mvo.setLocaleCarnetId(mcc.getId());
                mvo.setEdited(WaniApp.getCurrentDate());
                mvo.update();
            }
        }
        if(ncc.getVaccins() !=null){
            for (VaccinOccurence mc: ncc.getVaccins()
            ) {
                mc.setLocaleCarnetId(mcc.getId());
                mc.setId(null);
                mc.save();
            }
        }
        mcc.setStory(ms);

        mcc.setEdited(WaniApp.getCurrentDate());
        mcc.update();
        Toast.makeText(mContex, "Reussite", Toast.LENGTH_LONG).show();
        Common.printReceipt(mContex, mCompactPaiement, CarnetViewModel.this.getInventory().getValue());
        ((CarnetActivity) mContex).finish();

    }












    private void interactForVaccinsOccurenceCreation(CompactCarnet carn, List<VaccinOccurence> vo, List<Vaccin> requiredVaccins, int index) {
        this.interactingCarnet=carn;
        this.interactingCarnet.save();
        //CompactCarnet handledCarnet=new CompactCarnet();
        List<VaccinOccurence> completedVaccinOccurences=vo;
        if(index ==-1)currRequiredVaccinIndex=0;
        else currRequiredVaccinIndex=index;
        class InteractForVaccinOccurenceVaccinDialogHandler implements IVaccinDialogHandler<VaccinDialogResult>{

            public InteractForVaccinOccurenceVaccinDialogHandler() {
            }

            @Override
            public void onVaccinDialogResult(VaccinDialogResult result) {
                CarnetViewModel.this.closeVassinDialog();
                if(result.getResult()==1){
                    // Log.d("zaza",String.format("result sent with vaccin %s",result.getVaccinOccurence().getIdVaccin().getDesignation()));
                    VaccinOccurence mVo=result.getVaccinOccurence();
                    mVo.setLocaleCarnetId(CarnetViewModel.this.interactingCarnet.getId());
                    mVo.setEdited(WaniApp.getCurrentDate());
                    mVo.update();

                    if(CarnetViewModel.this.interactingCarnet.getVaccins() !=null)CarnetViewModel.this.interactingCarnet.getVaccins().add(mVo);
                    else {
                        List<VaccinOccurence> mVos=new ArrayList<>();
                        mVos.add(mVo);
                        CarnetViewModel.this.interactingCarnet.setVaccins(mVos);
                        // Log.d("zaza",String.format("Number of vaccins %d", CarnetViewModel.this.interactingCarnet.getVaccins().size()));
                    }
                    //GreenDao.getSession().getCompactCarnetDao().update(CarnetViewModel.this.interactingCarnet);
                   // mVo.setCarnetLocaleId(CarnetViewModel.this.interactingCarnet.getLocaleId());
                    //GreenDao.getSession().getVaccinOccurenceDao().update(mVo);
                    completedVaccinOccurences.add(mVo);

                }

                currRequiredVaccinIndex++;
                // Log.d("zaza",String.format("curr vaccin index %d",currRequiredVaccinIndex));
                if(requiredVaccins.size()>currRequiredVaccinIndex) interactForVaccinsOccurenceCreation(CarnetViewModel.this.interactingCarnet, completedVaccinOccurences, requiredVaccins, currRequiredVaccinIndex);
                else{



                    /*
                    // Log.d("zaza",String.format("completed vaccin lengt %d",completedVaccinOccurences.size()));
                    List<VaccinOccurence> carnetVaccinOccurence=CarnetViewModel.this.interactingCarnet.getVaccins();
                    if(carnetVaccinOccurence==null) carnetVaccinOccurence=new ArrayList<>();
                    carnetVaccinOccurence.addAll(completedVaccinOccurences);

                    //TODO to many
                    CarnetViewModel.this.interactingCarnet.setVaccins(carnetVaccinOccurence);*/
                    informPaiementCarnet();
                }

            }


        }



        if(requiredVaccins.size()>currRequiredVaccinIndex) showVaccinDialog(mContex, 1, vaccinsList.getValue(), requiredVaccins.get(currRequiredVaccinIndex), true, new InteractForVaccinOccurenceVaccinDialogHandler());
        else{
            List<VaccinOccurence> carnetVaccinOccurence=this.interactingCarnet.getVaccins();
            if(carnetVaccinOccurence==null) carnetVaccinOccurence=new ArrayList<>();
            carnetVaccinOccurence.addAll(completedVaccinOccurences);

            // TODO to many
            //this.interactingCarnet.setVaccins(carnetVaccinOccurence);
            informPaiementCarnet();

        }


    }







    private void closeVassinDialog() {
        if(this.vaccinDialog!=null)vaccinDialog.dismiss();
    }










    public void prepareAndPromptForPaiement(Context context, PaimentType mPaimentType, IInfoDialogResultHandler<InfoDialogResult> mResultHandler){
        if(mPaimentType.getPaiementPrice()==0) {
            mResultHandler.onInfoDialogResult(new InfoDialogResult(TAG_PAIEMENT_PROMPT, 1));
            return;
        }

        String text="Avant de continuer, assurez-vous d'avoir percu la somme de "+mPaimentType.getPaiementPrice()+" USD";

        this.infoDialog= Common.showInfoDialog(mContex, TAG_PAIEMENT_PROMPT, text, null, null, true, mResultHandler);
    }











    public void showVaccinDialog(Context context, int TAG, List<Vaccin> vaccins, Vaccin vaccin, Boolean extendMode, IVaccinDialogHandler resultHandler){
        DialogVaccinBinding dialogATCDDBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_vaccin, null, false);


        dialogATCDDBinding.setLifecycleOwner((CarnetActivity)this.mContex);

        VaccinDialogViewModel idvm=VaccinDialogViewModel.getInstance(mContex, TAG);
        VaccinDialogViewModel.set_binding(dialogATCDDBinding);
        idvm.setVaccins(vaccins);
        if(vaccin!= null)idvm.setVaccin(vaccin, true);
        idvm.setExtendMode(extendMode);
        idvm.setResultHandler(resultHandler);
        dialogATCDDBinding.setModel(
                idvm
        );
        View dialogView = dialogATCDDBinding.getRoot();

        AlertDialog.Builder dBuilder = new AlertDialog.Builder(context);
        dBuilder.setView(dialogView);
        this.vaccinDialog = dBuilder.create();

        this.vaccinDialog.show();
    }










    private List<Vaccin> getRequiredVaccins() {
        List<Vaccin> foundedVacs=new ArrayList<>();
        for(Vaccin vac: vaccinsList.getValue()){
            if(vac.getRequired()) foundedVacs.add(vac);

        }
        return foundedVacs;
    }









    View.OnClickListener btnCaptureClickHandler=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!readOnly.getValue() || mode.getValue()==MainToCarnetActivityRoutingBundle.SWITCH_RAW_TO_NUMERIC)
                ((CarnetActivity)mContex).requestCapture();
        }
    };








    public static CarnetViewModel getInstance(Context ct, MainToCarnetActivityRoutingBundle mMainToCarnetActivityRoutingBundle){
        if(INSTANCE==null){
            synchronized (CarnetViewModel.class){
                // Log.d("zaza","creating new instance of the viewholder");
                INSTANCE = new CarnetViewModel(ct);
            }
        }
        INSTANCE.setContext(ct);
        INSTANCE.init();
        INSTANCE.setBundle(mMainToCarnetActivityRoutingBundle);
        return INSTANCE;
    }













    private void setBundle(MainToCarnetActivityRoutingBundle mMainToCarnetActivityRoutingBundle) {
        this.bundle = mMainToCarnetActivityRoutingBundle;
        this.carnetId.setValue(mMainToCarnetActivityRoutingBundle.getCarnetId());
        this.carnet.setValue(
                CompactCarnet.findById(CompactCarnet.class, mMainToCarnetActivityRoutingBundle.getCarnetLocaleId())
                //Select.from(CompactCarnet.class).where(Condition.prop("CARNET_STATUS").eq(mMainToCarnetActivityRoutingBundle.getCarnetLocaleId())).first()
                );
        this.carnet.observe((CarnetActivity) this.mContex, new Observer<CompactCarnet>() {
            public void onChanged(CompactCarnet compactCarnet) {
            }
        });
        CompactCarnet providedCompactCarnet=CompactCarnet.findById(CompactCarnet.class, mMainToCarnetActivityRoutingBundle.getCarnetLocaleId());
        // CompactCarnet providedCompactCarnet=Select.from(CompactCarnet.class).where(Condition.prop("CARNET_STATUS").eq(mMainToCarnetActivityRoutingBundle.getCarnetLocaleId())).first();
        setCarnet(providedCompactCarnet);
        if (mMainToCarnetActivityRoutingBundle.getRawCarnetId() != null) {
            this.rawCarnetId.setValue(mMainToCarnetActivityRoutingBundle.getRawCarnetId());
        }
        setMode(mMainToCarnetActivityRoutingBundle.getMode());
        this.readOnly.setValue(Boolean.valueOf(this.bundle.getMode() == 5 || this.bundle.getMode() == 3));
        if (mMainToCarnetActivityRoutingBundle.getMode() == 4 || (mMainToCarnetActivityRoutingBundle.getMode() == 1 && mMainToCarnetActivityRoutingBundle.getCreationMode() == MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_NUMERISATION)) {
            this.lockRawCarnetId.setValue(true);
            if (this.rawCarnetId.getValue().compareTo("000000") == 0) {
                this.nationality.setValue("Expatrié");
            }
        }
    }













    private CarnetViewModel(Context mContext){
        this.mContex= mContext;
        this.mRepository= WaniRepository.getInstance(this.mContex);
    }









    public void init(){
        initPreprocessIndependant();
        newVaccinDate.setValue(new Date());
        mode.setValue(-1);
        this.preprocessed.addSource(this.vaccinLoaded, this.observerPreprocessor);
        this.preprocessed.addSource(this.paiementTypeLoaded, this.observerPreprocessor);
        this.preprocessed.observe((CarnetActivity) this.mContex, new Observer<Boolean>() {
            public void onChanged(Boolean aBoolean) {
                if (aBoolean.booleanValue()) {
                    CarnetViewModel.this.achieveInit();
                }
            }
        });
        this.paiementTypesList.observe((CarnetActivity) this.mContex, new Observer<List<PaimentType>>() {
            public void onChanged(List<PaimentType> list) {
            }
        });
        this.networkCall.setValue(true);
        this.vaccinsList.setValue(Vaccin.listAll(Vaccin.class));
        this.vaccinLoaded.setValue(true);
        this.paiementTypesList.setValue(PaimentType.listAll(PaimentType.class));
        this.paiementTypeLoaded.setValue(true);
        this.networkCall.setValue(false);
    }








    private void initPreprocessIndependant() {
        drawableUploading=mContex.getResources().getDrawable(R.drawable.ic_more_horiz_black_24dp);
        drawablePictureUnavaillable=mContex.getResources().getDrawable(R.drawable.ic_no_cameras);
        drawableUploadToServer=mContex.getResources().getDrawable(R.drawable.ic_save_black_24dp);
        drawableReadOnly= mContex.getResources().getDrawable(R.drawable.ic_lock_black_24dp);
        drawablePreview= mContex.getResources().getDrawable(R.drawable.ic_visibility_black_24dp);
        drawableDuplicate= mContex.getResources().getDrawable(R.drawable.ic_content_copy_black_24dp);
        drawableSwipe=mContex.getResources().getDrawable(R.drawable.ic_call_to_action_black_24dp);
        drawableCheck=mContex.getResources().getDrawable(R.drawable.ic_done_black_24dp);
        drawableUserPlaceholder = mContex.getResources().getDrawable(R.drawable.ic_user);
        drawableRawcarnetPlaceholder = mContex.getResources().getDrawable(R.drawable.ic_attach);
        mIv=((CarnetActivity)(CarnetViewModel.this.mContex)).getCarnetBinding().getRoot().findViewById(R.id.picture);
        // Log.d("zaza",String.format("miv %s",mIv.toString()));
        fabValidate=  ((CarnetActivity)mContex).getCarnetBinding().getRoot().findViewById(R.id.fab_validate);
        fabPreview=  ((CarnetActivity)mContex).getCarnetBinding().getRoot().findViewById(R.id.fab_preview);

        setfabStyle(fabPreview, drawablePreview, 1);
    }






    Observer<Object> inventoryListener=new Observer<Object>() {
        @Override
        public void onChanged(Object inventory) {
            if(carnet.getValue()!=null){
                Inventory iv=getInventoryForCarn(carnet.getValue(), getRequiredVaccins());
                CarnetViewModel.this.inventory.setValue(iv);
            }

        }
    };








    private void achieveInit(){
        // Log.d("zaza","initcalled");

        //valid.setValue(false);
        //CompactCarnetViewModel.selectedId.setValue(null);
        initAutomateFlowListening();

        initFormObserver();

        iniFlowTriggers();

        if(this.carnet.getValue()==null){
            generateDefaultCarnet();
            this.readOnly.setValue(false);
        }else {
            mapCarnetToLiveData();
            this.readOnly.setValue(true);
        }

       if(this.mode.getValue()==MainToCarnetActivityRoutingBundle.CHECK && WaniApp.getLogedInUser().getRole().compareTo("Controler")==0){
           this.inventory.addSource(this.carnet, inventoryListener);


       }
    }









    private void iniFlowTriggers() {
        // Log.d("zaza","binding valid");
        valid.setValue(false);

        // Log.d("zaza","binding pictureAvaillable");
        pictureAvaillable.setValue(false);

        // Log.d("zaza","binding pictureUploaded");
        pictureUploaded.setValue(false);

        // Log.d("zaza","binding readOnly");
        readOnly.setValue(false);

    }








    private void initFormObserver() {
        // Log.d("zaza","--> binding name");
        name.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding lastName");
        lastName.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding firstName");
        firstName.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding sex");
        sex.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressProvince");
        adressProvince.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressTownship");
        adressTownship.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressCity");
        adressCity.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressLocality");
        adressLocality.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressRoad");
        adressRoad.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding adressNumber");
        adressNumber.observe((CarnetActivity)this.mContex, formFieldsObserver);

        // Log.d("zaza","--> binding birthDate");
        birthDate.observe((CarnetActivity)this.mContex, formFieldsObserver);


    }








    private void initAutomateFlowListening() {
        // Log.d("zaza","binding valid");
        valid.observe((CarnetActivity)this.mContex, sendIconStatusObserver);

        // Log.d("zaza","binding pictureAvaillable");
        pictureAvaillable.observe((CarnetActivity)this.mContex, sendIconStatusObserver);

        // Log.d("zaza","binding pictureUploaded");
        pictureUploaded.observe((CarnetActivity)this.mContex, sendIconStatusObserver);

        // Log.d("zaza","binding readOnly");
        readOnly.observe((CarnetActivity)this.mContex, sendIconStatusObserver);

        carnetId.observe((CarnetActivity) this.mContex, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                positionPlaceHolder();
            }
        });

    }










    public static Inventory getInventoryForCarn(CompactCarnet carn, List<Vaccin> requiredVaccins){

        for (Vaccin rv:
             requiredVaccins) {
            // Log.d("zaza",String.format("current required vaccin for issue: %s",rv.getDesignation()));
        }

        Inventory iv=new Inventory();
      if( carn.getCarnetStatus().getTypeStatus()==2 && carn.getIdPeople().getNationality().compareTo("National")==0) iv.addIssue("Carnet non numérisé");

        List<VaccinOccurence> vos=carn.getVaccins();
        for(Vaccin v: requiredVaccins){
            boolean founded=false;
            for(VaccinOccurence vo: vos){
                if(vo.getIdVaccin().getDesignation().compareTo( v.getDesignation())==0) {
                    founded=true;
                }
            }
            if(!founded) {
                iv.addIssue("Vaccin "+v.getDesignation()+" manquant");
                // Log.d("zaza",String.format("issue added %s",iv.getIssues().toString()));
            }
        }

        List<Check> checks=carn.getStory().getCheckStory();

        Collections.sort(checks, new Comparator<Check>() {
            @Override
            public int compare(Check o1, Check o2) {
                return o1.getCreated().after(o2.getCreated())?1:-1;
            }
        });

        if(checks.size()>0)iv.setLastCheck(checks.get(checks.size()-1));

        iv.setValid(!(iv.getIssues().size() > 0));

        return iv;

    }










    private void generateDefaultCarnet() {

        // Log.d("zaza",String.format("preserve %b",preserveReceived.getValue()));
        positionPlaceHolder();

        // Log.d("zaza","--> initiarting name");
        name.setValue("");

        // Log.d("zaza","--> initiarting lastName");
        lastName.setValue("");

        // Log.d("zaza","--> initiarting firstName");
        firstName.setValue("");

        // Log.d("zaza","--> initiarting sex");
        sex.setValue("M");

        // Log.d("zaza","--> initiarting adressProvince");
        adressProvince.setValue("Kinshasa");

        // Log.d("zaza","--> initiarting adressTownship");
        adressTownship.setValue("");

        // Log.d("zaza","--> initiarting adressCity");
        adressCity.setValue("");

        // Log.d("zaza","--> initiarting adressLocality");
        adressLocality.setValue("");

        // Log.d("zaza","--> initiarting adressRoad");
        adressRoad.setValue("");

        // Log.d("zaza","--> initiarting adressNumber");
        adressNumber.setValue("");

        // Log.d("zaza","--> initiarting birthDate");
        tel.setValue("");


        email.setValue("");

        try {
            birthDate.setValue(ISO8601.toCalendar("1991-09-11T00:00:00.000Z").getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }






    private void positionPlaceHolder() {
        // Log.d("zaza",String.format("mode %d",getMode().getValue()));
        if(getMode().getValue()==MainToCarnetActivityRoutingBundle.CREATION || getMode().getValue()==MainToCarnetActivityRoutingBundle.NUMERISATION || getMode().getValue()==MainToCarnetActivityRoutingBundle.SWITCH_RAW_TO_NUMERIC){
            mIv.setImageDrawable(drawableUserPlaceholder);
        }else mIv.setImageDrawable(drawableRawcarnetPlaceholder);
    }








    public void setfabStyle(FloatingActionButton fab, Drawable dr, int style){
        dr.setColorFilter(mContex.getResources().getColor( style == 0 ?R.color.primaryLightColor:R.color.primaryDarkColor), PorterDuff.Mode.SRC_IN);//setTint(getContext().getResources().getColor(R.color.colorSecondaryDark));
        fab.setImageDrawable(dr);

    }








    public void setCarnet(CompactCarnet parcelable) {
        if (parcelable == null) {
            iniFlowTriggers();
            this.carnet.setValue(null);
            if (this.mIv != null) {
                generateDefaultCarnet();
                return;
            }
            return;
        }
        this.readOnly.setValue(true);
          this.pictureAvaillable.setValue(true);
        this.networkCall.setValue(false);
        this.carnet.setValue(parcelable);
        this.nationality.setValue(parcelable.getIdPeople().getNationality());
        mapCarnetToLiveData();
        if (this.bundle.getMode() == 1 && this.bundle.getCreationMode() == MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_NUMERISATION) {
            this.rawCarnetId.setValue(this.bundle.getRawCarnetId());
        }
        this.rawCarnetId.setValue(this.carnet.getValue().getIdRawCarnet());
        this.passportNumber.setValue(this.carnet.getValue().getIdPeople().getPassportNumber());
    }

















    private void mapCarnetToLiveData() {CompactCarnet carnetValue = this.carnet.getValue();
        this.name.setValue(carnetValue.getIdPeople().getName());
        this.lastName.setValue(carnetValue.getIdPeople().getLastName());
        this.firstName.setValue(carnetValue.getIdPeople().getFirstName());
        this.sex.setValue(carnetValue.getIdPeople().getSex());
        this.birthDate.setValue(carnetValue.getIdPeople().getBirthDate());
        this.tel.setValue(carnetValue.getIdPeople().getTel());
        this.email.setValue(carnetValue.getIdPeople().getEmail());
        String[] addressFreatures = carnetValue.getIdPeople().getAdress().split(",");
        this.adressProvince.setValue(addressFreatures[5].trim());
        this.adressTownship.setValue(addressFreatures[4].trim());
        this.adressCity.setValue(addressFreatures[3].trim());
        this.adressLocality.setValue(addressFreatures[2].trim());
        this.adressRoad.setValue(addressFreatures[1].trim());
        this.adressNumber.setValue(addressFreatures[0].trim());
        CircleImageView iv = (CircleImageView) ((CarnetActivity) this.mContex).getCarnetBinding().getRoot().findViewById(R.id.picture);
        Long id = carnetValue.getIdPeople().getLocaleIdProfile();
        if (carnetValue.getCarnetStatus().getTypeStatus() == 2) {
            id = carnetValue.getLocalePictureRawCarnet();
        }
        this.bufferedProfileId.setValue(id);
        if (this.mode.getValue().intValue() == 2) {
            return;
        }
        if (id == null || Picture.findById(Picture.class, id) == null) {
            String stringFeature = carnetValue.getCarnetStatus().getTypeStatus() == 2 ? carnetValue.getPictureRawCarnet() : carnetValue.getIdPeople().getIdProfile();
            if (stringFeature != null) {
                String path = WaniNetworkAccess.BASE_URL + "/picture/load/" + stringFeature + "?static=yes";
                Log.d("zaza", String.format("path %s", new Object[]{path}));
                Picasso.get().load(path).placeholder((int) R.drawable.ic_user).into((ImageView) iv);
                return;
            }
            return;
        }
        iv.setImageBitmap(Common.loadPictureByLocaleId(this.mContex, id));
    }










    private People getPeopleByMappingLiveDatas() {
        People mPeople = new People();
        if (this.carnetId.getValue() != null) {
            mPeople.setLocaleIdProfile(this.bufferedProfileId.getValue());
        }
        mPeople.setName(this.name.getValue());
        mPeople.setFirstName(this.firstName.getValue());
        mPeople.setLastName(this.lastName.getValue());
        mPeople.setSex(this.sex.getValue());
        mPeople.setTel(this.tel.getValue());
        mPeople.setEmail(this.email.getValue());
        mPeople.setBirthDate(this.birthDate.getValue());
        mPeople.setAdress(this.adressNumber.getValue().concat(", ") + this.adressRoad.getValue().concat(", ") + this.adressLocality.getValue().concat(", ") + this.adressTownship.getValue().concat(", ") + this.adressCity.getValue().concat(", ") + this.adressProvince.getValue());
        mPeople.setNationality(this.nationality.getValue());
        mPeople.setPassportNumber(this.passportNumber.getValue());
        return mPeople;
    }










    private String generatedcarnId(String id) {
        SharedPreferences mPrefs= mContex.getSharedPreferences(WaniApp.PREFS_ID, Context.MODE_MULTI_PROCESS);
        int carnOrder = mPrefs.getInt(WaniApp.CARNET_ORDER_KEY, 0);
        carnOrder++;

        CompactUser us=WaniApp.getLogedInUser();
        String carnId=
                "CAR-"+
                        us.getIdPeople().getName().substring(0, 3).toUpperCase()+"-"+
                        us.get_id().substring(us.get_id().length()-4, us.get_id().length()-1).toUpperCase()+ "-"+
                        String.format("%03d", carnOrder)+"-"+
                        id.substring(id.length()-4, id.length()-1).toUpperCase();

        mPrefs.edit().putInt(WaniApp.CARNET_ORDER_KEY, carnOrder).commit();

        return carnId;
    }










    public void uploadImage(Bitmap imageBitmap) {
        //preserveReceived.setValue(true);
        mIv.setImageBitmap(imageBitmap);
        // Log.d("zaza",String.format("miv %s",mIv.toString()));
        this.pictureAvaillable.setValue(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String mData= Base64.encodeToString(byteArray, Base64.DEFAULT);
        Picture mPicture=new Picture();
        mPicture.setData(mData);
        mPicture.save();

        // Log.d("zaza","consiging picture availlability");

        // Log.d("zaza","consiging upload");
        this.uploadingPicture.setValue(false);

        ///GreenDao.getSession().getPictureDao().insert(mPicture);

        CarnetViewModel.this.bufferedProfileId.setValue(mPicture.getId());
        CarnetViewModel.this.pictureUploaded.setValue(true);

        // Log.d("zaza","consiging picture uploading...");
        CarnetViewModel.this.uploadingPicture.setValue(false);
    }










    private PaimentType getPaimentTypeDesignation(String designation){
        PaimentType founded=null;
        for(PaimentType mPt: paiementTypesList.getValue()){
            if(mPt.getDesignation().compareTo(designation)==0){
                founded=mPt;
                break;
            }
        }

        return founded;
    }


















    private void setContext(Context mContext){
        this.mContex=mContext;
    }

    public MutableLiveData<CompactCarnet> getCarnet() {
        return carnet;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public MutableLiveData<String> getLastName() {
        return lastName;
    }

    public MutableLiveData<String> getFirstName() {
        return firstName;
    }

    public MutableLiveData<String> getSex() {
        return sex;
    }

    public MutableLiveData<String> getAdressProvince() {
        return adressProvince;
    }

    public MutableLiveData<String> getAdressTownship() {
        return adressTownship;
    }

    public MutableLiveData<String> getAdressCity() {
        return adressCity;
    }

    public MutableLiveData<String> getAdressLocality() {
        return adressLocality;
    }

    public MutableLiveData<String> getAdressRoad() {
        return adressRoad;
    }

    public MutableLiveData<String> getAdressNumber() {
        return adressNumber;
    }

    public MutableLiveData<String> getTel() {
        return tel;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public MutableLiveData<Date> getBirthDate() {
        return birthDate;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public void setLastName(MutableLiveData<String> lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(MutableLiveData<String> firstName) {
        this.firstName = firstName;
    }

    public void setSex(MutableLiveData<String> sex) {
        this.sex = sex;
    }

    public void setAdressProvince(MutableLiveData<String> adressProvince) {
        this.adressProvince = adressProvince;
    }

    public void setAdressTownship(MutableLiveData<String> adressTownship) {
        this.adressTownship = adressTownship;
    }

    public void setAdressCity(MutableLiveData<String> adressCity) {
        this.adressCity = adressCity;
    }

    public void setAdressLocality(MutableLiveData<String> adressLocality) {
        this.adressLocality = adressLocality;
    }

    public void setAdressRoad(MutableLiveData<String> adressRoad) {
        this.adressRoad = adressRoad;
    }

    public void setAdressNumber(MutableLiveData<String> adressNumber) {
        this.adressNumber = adressNumber;
    }

    public void setTel(MutableLiveData<String> tel) {
        this.tel = tel;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public void setBirthDate(MutableLiveData<Date> birthDate) {
        this.birthDate = birthDate;
    }

    public MutableLiveData<Boolean> getValid() {
        return valid;
    }

    public View.OnClickListener getValidateButonClickHandler() {
        return validateButonClickHandler;
    }

    public View.OnClickListener getBtnCaptureClickHandler() {
        return btnCaptureClickHandler;
    }

    public MutableLiveData<Boolean> getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(MutableLiveData<Boolean> readOnly) {
        this.readOnly = readOnly;
    }

    public MutableLiveData<Boolean> getUploadingPicture() {
        return uploadingPicture;
    }

    public void setUploadingPicture(MutableLiveData<Boolean> uploadingPicture) {
        this.uploadingPicture = uploadingPicture;
    }

    public MutableLiveData<Boolean> getPictureAvaillable() {
        return pictureAvaillable;
    }

    public void setPictureAvaillable(MutableLiveData<Boolean> pictureAvaillable) {
        this.pictureAvaillable = pictureAvaillable;
    }

    public MutableLiveData<Boolean> getPictureUploaded() {
        return pictureUploaded;
    }

    public void setPictureUploaded(MutableLiveData<Boolean> pictureUploaded) {
        this.pictureUploaded = pictureUploaded;
    }

    public void setCarnetId(String carnId) {
        // Log.d("zaza",String.format("ID seted %s",carnId));
        this.carnetId.setValue(carnId);
    }

    public void setSelectedVaccin(Vaccin vaccin) {
        this.selectedVaccin=vaccin;
    }

    public MutableLiveData<List<Vaccin>> getVaccinsList() {
        return vaccinsList;
    }

    public View.OnClickListener getNewVaccinClickhandler() {
        return newVaccinClickhandler;
    }

    public MutableLiveData<String> getTextInfo() {
        return textInfo;
    }

    public View.OnClickListener getDialogInfoClickHandler() {
        return dialogInfoClickHandler;
    }

    public MutableLiveData<Boolean> getNumerisation() {
        return numerisation;
    }

    public void setNumerisation(MutableLiveData<Boolean> numerisation) {
        this.numerisation = numerisation;
    }

    public MutableLiveData<Date> getNewVaccinDate() {
        return newVaccinDate;
    }

    public void setNewVaccinDate(MutableLiveData<Date> newVaccinDate) {
        this.newVaccinDate = newVaccinDate;
    }

    public void setRawCarnetId(String rawCarnetId) {
        this.rawCarnetId.setValue(rawCarnetId);
    }

    public MutableLiveData<String> getRawCarnetId() {
        return rawCarnetId;
    }

    public void setCarnetIdFromActivity(String carnId) {
        this.lockRawCarnetId.setValue(false);
        this.rawCarnetId.setValue(carnId);
    }

    public MutableLiveData<Boolean> getLockRawCarnetId() {
        return lockRawCarnetId;
    }

    public void setLockRawCarnetId(MutableLiveData<Boolean> lockRawCarnetId) {
        this.lockRawCarnetId = lockRawCarnetId;
    }

    public MutableLiveData<String> getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(MutableLiveData<String> passportNumber) {
        this.passportNumber = passportNumber;
    }

    public MutableLiveData<String> getNationality() {
        return nationality;
    }

    public void setNationality(MutableLiveData<String> nationality) {
        this.nationality = nationality;
    }

    public MutableLiveData<Boolean> getConsigningRaw() {
        return consigningRaw;
    }

    public void setConsigningRaw(MutableLiveData<Boolean> consigningRaw) {
        this.consigningRaw = consigningRaw;
    }

    public void setMode(int mode) {
        this.mode.setValue(mode);
    }

    public MutableLiveData<Integer> getMode() {
        return mode;
    }

    public MediatorLiveData<Inventory> getInventory() {
        return inventory;
    }

    public void setInventory(MediatorLiveData<Inventory> inventory) {
        this.inventory = inventory;
    }

    public View.OnClickListener getIssuesClickhandler() {
        return issuesClickhandler;
    }

    public void setIssuesClickhandler(View.OnClickListener issuesClickhandler) {
        this.issuesClickhandler = issuesClickhandler;
    }
}