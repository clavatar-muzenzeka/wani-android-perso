package cd.clavatar.wani.ui.main;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

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
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.data.model.WaniParameter;
import cd.clavatar.wani.databinding.ActivityMainBinding;
import cd.clavatar.wani.databinding.DialogRawcardCollectorBinding;
import cd.clavatar.wani.ui.RawCarnetIdCollectorViewModel;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.vendor.CompactCarnetViewModel;
import cd.clavatar.wani.vendor.IDialogResultHandler;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle;

/**
 * Created by Cl@v@t@r on 2020-01-30
 */
public class MainViewModel extends ViewModel implements IDialogResultHandler<String>, IInfoDialogResultHandler<InfoDialogResult> {
    public static final Integer HOME = 0;
    public static final Integer STORY = 1;
    public static final Integer PAIEMENT = 2;
    public static final Integer PROFILE_EDITING = 3;
    private static final int TAG_RAW_CARNET_ID_NOT_FOUND = 0;
    private static final int TAG_RAW_CARNET_ID_FOUNDED_AND_RAW = 1;
    private static final int TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW = 2;
    private static final int TAG_CARNET_ID_FOUNDED_AND_NOT_DROPPED = 3;
    private static final int TAG_RAW_CARNET_ID_FOUNDED_AND_RAW_CONTROLLER = 4;
    private static final int TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW_CONTROLLER = 5;
    private static final int TAG_RAW_CARNET_ID_NOT_FOUND_CONTROLLER = 6;
    private static final int TAG_ACCOUNT_LIMIT = 7;
    private static MainViewModel INSTANCE=null;
    private Context mContex;
    private final WaniRepository mRepository;
    private ActivityMainBinding mBinding;
    MutableLiveData<List<CompactCarnet>> rawCarnets = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<List<CompactPaiement>> rawPaiements = new MutableLiveData<>(new ArrayList<>());
    MutableLiveData<String> filterValue=new MutableLiveData<>(null);
    MediatorLiveData<List<CompactCarnet>> carnets = new MediatorLiveData<>();
    MediatorLiveData<List<CompactPaiement>> paiments = new MediatorLiveData<>();
    MutableLiveData<Boolean> serachMode=new MutableLiveData<>(false);
    MutableLiveData<Integer> currentMenu=new MutableLiveData<>(STORY);
    private MutableLiveData<String> selectedId=new MutableLiveData<>(null);
    private MutableLiveData<Boolean> networkError=new MutableLiveData<>(false);
    private MutableLiveData<Boolean> networkOnProgress =new MutableLiveData<>(false);
    Drawable drawableCancelSelection;
    Drawable drawableDuplicata;
    Drawable drawablePreview;
    Drawable drawableAdd;
    MutableLiveData<String> idRawCarnet=new MutableLiveData<>(null);
    MediatorLiveData<Double> account=new MediatorLiveData<>();

    private View.OnClickListener refreshListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadCarnets();
        }
    };

    private View.OnClickListener validateButonClickHandler=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(CompactCarnetViewModel.getSelectedId().getValue()!=null){
                //CompactCarnet sc=getCarnetById(CompactCarnetViewModel.getSelectedId().getValue());
                CompactCarnet sc=getCarnetByLocaleId(CompactCarnetViewModel.getSelectedId().getValue());
                ((MainActivity) mContex).mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CHECK);
                //GreenDao.getSession().getCompactCarnetDao().loadDeep(sc.getLocaleId());
                /*
                for (VaccinOccurence mvo: sc.getVaccins()
                     ) {
                    GreenDao.getSession().getVaccinOccurenceDao().loadDeep(mvo.getLocaleId());
                    GreenDao.getSession().getVaccinDao().loadDeep(mvo.getIdVaccin().getLocaleId());
                    // Log.d("zaza",String.format("##############Current vaccin des %s",mvo.getIdVaccin().getDesignation()));
                }
                */
                ((MainActivity) mContex).mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(sc.getId());
                //// Log.d("zaza",String.format("######## carn vac size %d", sc.getVaccins().size()));
                ((MainActivity) mContex).startCarnetAcivityWithBundle();

            }else {
                if(WaniApp.getLogedInUser().getRole().compareTo("Agent")==0)
                ((MainActivity) mContex).showPopupMenu(MainViewModel.this.mBinding.fabValidate);
                else
                {
                    prompForRawCarnetId();
                }
            }
        }
    };

    public CompactCarnet getCarnetByLocaleId(Long value) {
        CompactCarnet foundedCarnet = null;
        if(value!= null){
            foundedCarnet = Select.from(CompactCarnet.class).where(Condition.prop("CARNET_STATUS").eq(value)).first();
        }
        return foundedCarnet;
    }


    private Dialog rawCarnetIdCollectorDialog;
    private Dialog infoDialog;
    private CompactCarnet foundedCarnetForNumerisation;


    public void showRawCardCollectorDialog(Context context, IDialogResultHandler mResultHandler){
        DialogRawcardCollectorBinding dialogATCDDBinding = (DialogRawcardCollectorBinding) DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_rawcard_collector, (ViewGroup) null, false);
        RawCarnetIdCollectorViewModel rcicVM = RawCarnetIdCollectorViewModel.getInstance(this.mContex);
        rcicVM.setResultHandler(mResultHandler);
        dialogATCDDBinding.setModel(rcicVM);
        dialogATCDDBinding.setLifecycleOwner((MainActivity) this.mContex);
        View dialogView = dialogATCDDBinding.getRoot();
        dialogView.findViewById(R.id.cancel_add_program).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainViewModel.this.rawCarnetIdCollectorDialog.dismiss();
            }
        });
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(context);
        dBuilder.setView(dialogView);
        AlertDialog create = dBuilder.create();
        this.rawCarnetIdCollectorDialog = create;
        create.show();
    }


    public CompactCarnet getCarnetById(String value) {

        CompactCarnet foundedCarnet=null;
        if(value!=null){
            for(CompactCarnet cc:this.rawCarnets.getValue()){
                if(cc.get_id()!=null&&cc.get_id().compareTo(value)==0){
                    foundedCarnet=cc;
                }
            }
        }
        if(foundedCarnet!=null){
            // Log.d("zaza",String.format("ft %d",foundedCarnet.getCarnetStatus().getTypeStatus()));
        }
        return foundedCarnet;
    }

    public CompactCarnet getCarnetByCarnId(String value) {
        // Log.d("zaza",String.format("provided id %s",value));
        CompactCarnet foundedCarnet=null;
        if(value!=null){
            foundedCarnet = Select.from(CompactCarnet.class).where(Condition.prop("IDI_CARNET").eq(value)).first();
            // Log.d("zaza",String.format("provided founded before check %s",foundedCarnet));
        }
        if(foundedCarnet!=null && foundedCarnet.getCarnetStatus()!=null){
            // Log.d("zaza",String.format("ft %d",foundedCarnet.getCarnetStatus().getTypeStatus()));
        }

        return foundedCarnet;
    }

    private View.OnClickListener cancelSelectViewListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainViewModel.this.cancelSelected();
            CompactCarnetViewModel.select(null);
        }
    };
    private Observer<? super Long> selectedIdObserver=new Observer<Long>() {
        @Override
        public void onChanged(Long s) {
            if(s==null)
                setMainButtonNewStyle();
             else setMainButtonPreviewStyle();
        }
    };

    View.OnClickListener btnDuplicataClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //CompactCarnet mCarn=getCarnetById(CompactCarnetViewModel.getSelectedId().getValue());
            CompactCarnet mCarn=getCarnetByLocaleId(CompactCarnetViewModel.getSelectedId().getValue());
            ((MainActivity)mContex).prepareActivityForDuplication(mCarn);
        }
    };

    public void procedDuplication(CompactCarnet ncc, String newId){
        /*
        CompactStatus cs=ncc.getCarnetStatus();
        CompactStatus mCompactStatus=new CompactStatus();
        mCompactStatus.setAuthor(WaniApp.getLogedInUser());
        mCompactStatus.setIdLocation(WaniApp.getLogedInUser().getIdLocation());
        mCompactStatus.setLocaleStoryId(ncc.getStory().getId());
        mCompactStatus.setDistributionStatus(2);
        mCompactStatus.setTypeStatus(cs.getTypeStatus());
        mCompactStatus.save();
        ncc.setCarnetStatus(mCompactStatus);
        ncc.setEdited(WaniApp.getCurrentDate());
        ncc.update();

        CompactCarnet dup=new CompactCarnet();
        dup.setIdPeople(ncc.getIdPeople());
        dup.setIdRawCarnet(ncc.getIdRawCarnet());
        dup.setLocalePictureRawCarnet(ncc.getLocalePictureRawCarnet());
        dup.setPictureRawCarnet(ncc.getPictureRawCarnet());
        dup.setStory(ncc.getStory());
        dup.setIdiCarnet(newId);
        CompactStatus nCs=new CompactStatus();
        nCs.setAuthor(WaniApp.getLogedInUser());
        nCs.setIdLocation(WaniApp.getLogedInUser().getIdLocation());
        nCs.setLocaleStoryId(ncc.getStory().getId());
        nCs.setIdPaiement();
        nCs.setTypeStatus(1);
        nCs.setDistributionStatus(1);
        dup.setCarnetStatus(nCs);
        dup.set_id(null);

        mRepository.putCarnet(ncc).enqueue(new Callback<CompactCarnet>() {
            @Override
            public void onResponse(Call<CompactCarnet> call, Response<CompactCarnet> response) {
                if(response.body()!=null){
                    // Log.d("zaza",String.format("response body not null %s",response.body().getIdPeople()));
                    CompactCarnet dup=response.body();
                    dup.setIdiCarnet(newId);
                    CompactStatus nCs=dup.getCarnetStatus();
                    nCs.setTypeStatus(1);
                    nCs.setDistributionStatus(1);
                    dup.setCarnetStatus(nCs);
                    dup.set_id(null);
                    mRepository.postCarnet(dup).enqueue(
                            new Callback<CompactCarnet>() {
                                @Override
                                public void onResponse(Call<CompactCarnet> call, Response<CompactCarnet> response) {
                                    loadCarnets();
                                }

                                @Override
                                public void onFailure(Call<CompactCarnet> call, Throwable t) {
                                    loadCarnets();
                                }
                            }
                    );
                }else {
                    // Log.d("zaza",String.format("response body null %s",response.body().toString()));
                }
            }

            @Override
            public void onFailure(Call<CompactCarnet> call, Throwable t) {
                // Log.d("zaza",String.format("failled for %s", t.getMessage()));
            }
        });

         */
    }

    private void setMainButtonPreviewStyle() {
        mBinding.fabValidate.setImageDrawable(drawablePreview);
    }

    private void setMainButtonNewStyle() {
        mBinding.fabValidate.setImageDrawable(drawableAdd);
    }

    public void cancelSelected() {
        CompactCarnetViewModel.selectedId.setValue(null);
    }

    public static MainViewModel getInstance(Context ct, ActivityMainBinding mBinding){
        if(INSTANCE==null){
            synchronized (MainViewModel.class){

                INSTANCE = new MainViewModel(ct, mBinding);

            }
        }
        INSTANCE.setContext(ct);
        INSTANCE.setBinding(((MainActivity)ct).getBindng());
        INSTANCE.init();
        return INSTANCE;
    }

    private void setBinding(ActivityMainBinding bd){
        this.mBinding=bd;
    }


    private void setContext(Context ct) {
        this.mContex=ct;
    }

    private MainViewModel(Context mContext, ActivityMainBinding mBinding){
        CompactCarnetViewModel.setSelectionListener((CompactCarnetViewModel.CarnetSelectionListener) mContext);
        this.mContex= mContext;
        this.mBinding=mBinding;
        this.mRepository=  WaniRepository.getInstance(mContext);
    }

    private Observer<Object> mOb=new Observer<Object>() {
        @Override
        public void onChanged(Object o) {
            filterCompactCarnets();
        }
    };

    private Observer<Object> mObP=new Observer<Object>() {
        @Override
        public void onChanged(Object o) {
            filterCompactPaiments();
        }
    };

    private Observer<Object> mObAc=new Observer<Object>() {
        @Override
        public void onChanged(Object o) {
            double tot=0;
            for(CompactPaiement cp:paiments.getValue()){
                try{
                    if((cp.getEncaissed()!=null && cp.getEncaissed().getIdEncaisser() ==null) || cp.getEncaissed() == null)tot+=cp.getIdPaiementType().getPaiementPrice();
                }catch (Exception e){
                    Log.d("zaza", "Exeption caused by cp with remote id %s"+cp.get_id());
                }

            }
            account.setValue(tot);
        }
    };

    private Observer<Double> mObAcBloc=new Observer<Double>() {
        @Override
        public void onChanged(Double aDouble) {

            if(aDouble>=WaniApp.getParameter().getMaxAccount())
            showInfoDialogForAccountLimit();
        }
    };

    public void init(){
        CompactCarnetViewModel.selectedId.observeForever( selectedIdObserver);
        initFabSkins();
        this.filterValue.setValue(null);
        carnets.addSource(this.rawCarnets, mOb);
        carnets.addSource(this.filterValue, mOb);
        paiments.addSource(this.rawPaiements, mObP);
        account.addSource(this.paiments, mObAc);

        account.observeForever(mObAcBloc);
        loadCarnets();
    }



    private void filterCompactCarnets() {
        /*
        CompactCarnetViewModel.select(null);
        List<CompactCarnet> founded=new ArrayList<>();
        String fc=this.filterValue.getValue();
        if (fc!=null) fc=fc.toLowerCase();
        if(fc!=null){
            String[] fcf=fc.split(" ");
        for(CompactCarnet cc: rawCarnets.getValue()){



                for (int ind=0; ind<fcf.length; ind++){
                    String pff=fcf[ind].trim();
                    if(((cc.getIdPeople()!=null && cc.getIdPeople().getName() != null && cc.getIdPeople().getName().toLowerCase().indexOf(pff) !=-1) ||
                            (cc.getIdPeople()!=null &&cc.getIdPeople().getFirstName() != null && cc.getIdPeople().getFirstName().toLowerCase().indexOf(pff) !=-1) ||
                            (cc.getIdPeople()!=null &&cc.getIdPeople().getLastName() != null && cc.getIdPeople().getLastName().toLowerCase().indexOf(pff) !=-1) ||
                            (cc.getIdPeople()!=null &&cc.getIdPeople().getTel() != null && cc.getIdPeople().getTel().indexOf(pff) !=-1)
                            ||
                            (cc.getIdRawCarnet()!=null && cc.getIdRawCarnet().indexOf(pff) !=-1)
                            ||
                            (cc.getIdiCarnet()!=null && cc.getIdiCarnet().indexOf(pff) !=-1)
                    )
                    ){
                        founded.add(cc);
                        break;
                    }
                }
            }


        }else founded=rawCarnets.getValue();
        List<CompactCarnet> retaineds=new ArrayList<>();
        for(CompactCarnet mC: founded){
            if(mC.getCarnetStatus() !=null && mC.getCarnetStatus().getDistributionStatus()==1 && (getForUsername(mC)
                    || getForChecks(mC) || getForVaccins(mC))) retaineds.add(mC);

        }
        this.carnets.setValue(retaineds);

         */
        this.carnets.setValue(this.rawCarnets.getValue());
    }

    private boolean getForUsername(CompactCarnet cc){
       // Log.d("zaza",String.format("current carn un %s",cc.getCarnetStatus().getAuthor().getUsername()));
        return cc.getCarnetStatus().getAuthor().getUsername().compareTo(WaniApp.getLogedInUser().getUsername())==0;
    }

    private boolean getForChecks(CompactCarnet cc){
        Boolean valid=false;
        for(Check mc: cc.getStory().getCheckStory()){
            if(mc.getIdPaiement().getIdReceiver().getUsername().compareTo(WaniApp.getLogedInUser().getUsername())==0) {
                valid=true;
                break;
            }
        }
        return valid;
    }

    private boolean getForVaccins(CompactCarnet cc){
        Boolean valid=false;
        for(VaccinOccurence mc: cc.getVaccins()){
            if(mc.getIdPaiement()!=null){
                if(mc.getIdPaiement().getIdReceiver().getUsername().compareTo(WaniApp.getLogedInUser().getUsername())==0) {
                   // Log.d("zaza",String.format("current receiver %s", mc.getIdPaiement().getIdReceiver().getUsername()));
                    valid=true;
                    break;
                }
            }

        }
        return valid;
    }



    private void filterCompactPaiments() {
        this.paiments.setValue(this.rawPaiements.getValue());
    }

    private void initFabSkins() {
        // Log.d("zaza","inited");
        // Log.d("zaza",mContex.toString());
        // Log.d("zaza",mBinding.toString());
        drawableCancelSelection=mContex.getResources().getDrawable(R.drawable.ic_clear_black_24dp);
        drawableCancelSelection.setColorFilter(new PorterDuffColorFilter(mContex.getResources().getColor(R.color.primaryLightColor), PorterDuff.Mode.SRC_IN));
        drawableDuplicata=mContex.getResources().getDrawable(R.drawable.ic_content_copy_black_24dp);
        drawableDuplicata.setColorFilter(new PorterDuffColorFilter(mContex.getResources().getColor(R.color.primaryLightColor), PorterDuff.Mode.SRC_IN));
        drawablePreview=mContex.getResources().getDrawable(R.drawable.ic_visibility_black_24dp);
        drawablePreview.setColorFilter(new PorterDuffColorFilter(mContex.getResources().getColor(R.color.primaryDarkColor), PorterDuff.Mode.SRC_IN));
        drawableAdd=mContex.getResources().getDrawable(R.drawable.ic_add_black_24dp);
        drawableAdd.setColorFilter(new PorterDuffColorFilter(mContex.getResources().getColor(R.color.primaryDarkColor), PorterDuff.Mode.SRC_IN));

        mBinding.fabCancel.setImageDrawable(drawableCancelSelection);
        mBinding.fabDuplicate.setImageDrawable(drawableDuplicata);
    }



    public void loadCarnets(){
        Common.backgroundWork.setValue(true);
        this.networkError.setValue(false);
        this.rawCarnets = this.mRepository.getRawCarnets();
        this.rawPaiements = this.mRepository.getRawPaiements();
        Common.backgroundWork.setValue(false);
    }

    public MutableLiveData<List<CompactCarnet>> getCarnets() {
        return carnets;
    }

    public MutableLiveData<Boolean> getSerachMode() {
        return serachMode;
    }

    public void switchSerch(){
        serachMode.setValue(true);
    }

    public void cancelSearch(){
        serachMode.setValue(false);
        filterValue.setValue(null);
    }

    public void select(String id) {
        this.selectedId.setValue(id);
    }

    public MutableLiveData<Boolean> getNetworkError() {
        return networkError;
    }

    public View.OnClickListener getRefreshListener() {
        return refreshListener;
    }

    public MutableLiveData<String> getSelectedId() {
        return selectedId;
    }

    public View.OnClickListener getCancelSelectViewListener() {
        return cancelSelectViewListener;
    }

    public View.OnClickListener getValidateButonClickHandler() {
        return validateButonClickHandler;
    }

    public View.OnClickListener getBtnDuplicataClickListener() {
        return btnDuplicataClickListener;
    }

    public MutableLiveData<String> getIdRawCarnet() {
        return idRawCarnet;
    }

    public CompactCarnet getFoundedCarnetForNumerisation() {
        return foundedCarnetForNumerisation;
    }

    public MutableLiveData<Integer> getCurrentMenu() {
        return currentMenu;
    }

    public void setCurrentMenu(MutableLiveData<Integer> currentMenu) {
        this.currentMenu = currentMenu;
    }

    public MediatorLiveData<Double> getAccount() {
        return account;
    }

    public void setAccount(MediatorLiveData<Double> account) {
        this.account = account;
    }

    public MediatorLiveData<List<CompactPaiement>> getPaiments() {
        return paiments;
    }

    public void setPaiments(MediatorLiveData<List<CompactPaiement>> paiments) {
        this.paiments = paiments;
    }

    public void setFoundedCarnetForNumerisation(CompactCarnet foundedCarnetForNumérisation) {
        this.foundedCarnetForNumerisation = foundedCarnetForNumérisation;
    }

    public void setIdRawCarnet(MutableLiveData<String> idRawCarnet) {
        this.idRawCarnet = idRawCarnet;
    }

    public void catchIdForDuplication(String id, String scanResult) {
        CompactCarnet mCC=getCarnetById(id);
        procedDuplication(mCC, scanResult);
    }

    public MutableLiveData<String> getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(MutableLiveData<String> filterValue) {
        this.filterValue = filterValue;
    }

    public void searchRawIdBeforeCreation(String result) {CompactCarnet fcc = null;
        if (result.compareTo("000000") != 0) {
            fcc = getRawCarnetByRawCarnetId(result);
        }
        if (fcc != null) {
            if (fcc.getCarnetStatus().getDistributionStatus() == 2) {
                if (WaniApp.getLogedInUser().getRole().compareTo("Agent") == 0) {
                    showInfoDialogForCarnetWithRawIdNotFounded(result);
                } else {
                    showInfoDialogForCarnetWithRawIdNotFoundedController(result);
                }
            } else if (fcc.getCarnetStatus().getTypeStatus() == 2) {
                if (WaniApp.getLogedInUser().getRole().compareTo("Agent") == 0) {
                    showInfoDialogForCarnetWithRawIdFoundedAndRaw(result, fcc);
                } else {
                    showInfoDialogForCarnetWithRawIdFoundedAndRawControler(result, fcc);
                }
            } else if (WaniApp.getLogedInUser().getRole().compareTo("Agent") == 0) {
                showInfoDialogForCarnetWithRawIdFoundedAndNotRaw(result, fcc);
            } else {
                showInfoDialogForCarnetWithRawIdFoundedAndNotRawController(result, fcc);
            }
        } else if (WaniApp.getLogedInUser().getRole().compareTo("Agent") == 0) {
            ((MainActivity) this.mContex).mMainToCarnetActivityRoutingBundle.setMode(1);
            ((MainActivity) this.mContex).mMainToCarnetActivityRoutingBundle.setCreationMode(MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_NUMERISATION);
            ((MainActivity) this.mContex).mMainToCarnetActivityRoutingBundle.setRawCarnetId(result);
            ((MainActivity) this.mContex).scanCode();
        } else {
            showInfoDialogForCarnetWithRawIdNotFoundedController(result);
        }
    }







    private void showInfoDialogForAccountLimit(){



        class AccountLimitResutHandler implements IInfoDialogResultHandler<InfoDialogResult>{
            @Override
            public void onInfoDialogResult(InfoDialogResult result) {
                ((MainActivity)mContex).finishAffinity();
            }
        }


        this.infoDialog=Common.showInfoDialog(mContex, TAG_ACCOUNT_LIMIT, "Vous avez atteint la limite du compte en itinérance, vous êtes priés de verser l'argent à l'administration afin de recouvrer l'accès à l'application", "Quitter", "QUITTER", false, new AccountLimitResutHandler());
    }








    private void showInfoDialogForCarnetWithRawIdNotFoundedController(String result) {
        idRawCarnet.setValue(result);
        this.infoDialog=Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_NOT_FOUND_CONTROLLER, "Consignez ce carnet dans le système comme obsolète. Vous prendrez la photo du carnet plustôt que celle du porteur. Vous accorderez une attention particulière à la liste déroulante nationalité", "Quitter", null, false, this);
    }











    private void showInfoDialogForCarnetWithRawIdFoundedAndNotRawController(String result, CompactCarnet fcc) {
        idRawCarnet.setValue(result);
        this.infoDialog=
                Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW_CONTROLLER, "Un carnet numérisé à partir de ce numéro de carnet papier existe déjà, cliquez sur DETAILS ci dessous pour voir les détails du carnet et procéder au check", "Quitter", "DETAILS", true, this);
        this.foundedCarnetForNumerisation =fcc;
    }










    private void showInfoDialogForCarnetWithRawIdFoundedAndRawControler(String result, CompactCarnet fcc) {
        idRawCarnet.setValue(result);
        this.infoDialog=Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_FOUNDED_AND_RAW_CONTROLLER, "La trace de ce carnet papier existe dans le système, appuyez sur DETAILS pour l'ouvir et proceder au check", "Quitter", "DETAILS", true, this);
        this.foundedCarnetForNumerisation =fcc;
    }









    public CompactCarnet getRawCarnetByRawCarnetId(String value) {
        CompactCarnet foundedCarnet = null;
        if (value != null) {
            foundedCarnet = Select.from(CompactCarnet.class).where(Condition.prop("ID_RAW_CARNET").eq(value)).first();
        }
        if (foundedCarnet != null) {
            //Log.d("zaza", String.format("ft %d", new Object[]{foundedCarnet.getId()}));
        }
        return foundedCarnet;
    }









    public void showInfoDialogForCarnetWithIdFoundedAndNotRaw(CompactCarnet fcc) {
        this.infoDialog=
                Common.showInfoDialog(mContex, TAG_CARNET_ID_FOUNDED_AND_NOT_DROPPED, "Ce carnet n'est pas vierge, cliquez sur DETAILS ci dessous pour voir les détails du carnet et vous assurer qu'il s'agit bien du même propriétaire", "Quitter", "DETAILS", true, this);
        this.foundedCarnetForNumerisation =fcc;
    }









    public void showInfoDialogForCarnetWithRawIdFoundedAndNotRaw(String result, CompactCarnet fcc) {
        idRawCarnet.setValue(result);
        this.infoDialog=
        Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW, "Un carnet numérisé à partir de ce numéro de carnet papier existe déjà, cliquez sur DETAILS ci dessous pour voir les détails du carnet et vous assurer qu'il s'agit bien du même propriétaire", "Quitter", "DETAILS", true, this);
        this.foundedCarnetForNumerisation =fcc;
    }










    public void showInfoDialogForCarnetWithRawIdFoundedAndRaw(String result, CompactCarnet fcc) {
        idRawCarnet.setValue(result);
        this.infoDialog=Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_FOUNDED_AND_RAW, "La trace de ce carnet papier existe dans le système, scannez un QR code vierge pour affectation et par la suite ajoutez simplement la photo", "Quitter", null, true, this);
        this.foundedCarnetForNumerisation =fcc;
    }














    public void showInfoDialogForCarnetWithRawIdNotFounded(String result) {
        idRawCarnet.setValue(result);
        this.infoDialog=Common.showInfoDialog(mContex, TAG_RAW_CARNET_ID_NOT_FOUND, "Ce carnet peut être numérisé, scannez un QR code vierge pour affectation", "Quitter", null, true, this);

    }














    public void prompForRawCarnetId() {
        showRawCardCollectorDialog(mContex,this);
    }













    @Override
    public void onResult(String result) {
        rawCarnetIdCollectorDialog.dismiss();
        searchRawIdBeforeCreation(result);
    }











    @Override
    public void onInfoDialogResult(InfoDialogResult result) {
        infoDialog.dismiss();
        if(result.getResult()==1){
            switch (result.getTag()){
                case TAG_RAW_CARNET_ID_NOT_FOUND:
                    ((MainActivity)mContex).prepareActivityForNumerisation(idRawCarnet.getValue());
                    break;
                case TAG_RAW_CARNET_ID_FOUNDED_AND_RAW:
                    ((MainActivity)mContex).prepareActivityForSwitchFromRowToNumeric(foundedCarnetForNumerisation);
                    break;
                case TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW:
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CHECK);
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(foundedCarnetForNumerisation.getId());
                    ((MainActivity)mContex).startCarnetAcivityWithBundle();
                    break;
                case TAG_CARNET_ID_FOUNDED_AND_NOT_DROPPED:
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CHECK);
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(foundedCarnetForNumerisation.getId());
                    ((MainActivity)mContex).startCarnetAcivityWithBundle();
                    break;

                case TAG_RAW_CARNET_ID_FOUNDED_AND_RAW_CONTROLLER:
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CHECK);
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(foundedCarnetForNumerisation.getId());
                    ((MainActivity)mContex).startCarnetAcivityWithBundle();
                    break;

                case TAG_RAW_CARNET_ID_FOUNDED_AND_NOT_RAW_CONTROLLER:
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CHECK);
                    ((MainActivity)mContex).mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(foundedCarnetForNumerisation.getId());
                    ((MainActivity)mContex).startCarnetAcivityWithBundle();
                    break;

                case TAG_RAW_CARNET_ID_NOT_FOUND_CONTROLLER:
                    ((MainActivity)mContex).prepareActivityForConsignation(idRawCarnet.getValue());
                    break;
            }
        }else {
            infoDialog.dismiss();
        }

    }

    public void onActivityDestroyed() {
        CompactCarnetViewModel.selectedId.setValue(null);
        //selectedIdObserver=null;
    }

    public void onActivityResume() {
        CompactCarnetViewModel.selectedId.setValue(null);
    }

    public MutableLiveData<Boolean> getNetworkOnProgress() {
        return networkOnProgress;
    }

    public MutableLiveData<List<CompactPaiement>> getRawPaiements() {
        return rawPaiements;
    }
}
