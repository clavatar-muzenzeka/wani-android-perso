package cd.clavatar.wani.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.HttpCookie;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.People;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.data.model.WaniNetworkDataSource;
import cd.clavatar.wani.databinding.DialogUserBinding;
import cd.clavatar.wani.databinding.DialogVaccinBinding;
import cd.clavatar.wani.ui.carnet.CarnetViewModel;
import cd.clavatar.wani.ui.main.MainActivity;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.utilities.WaniSyncHelper;
import cd.clavatar.wani.vendor.IDialogResultHandler;
import cd.clavatar.wani.vendor.IVaccinDialogHandler;
import cd.clavatar.wani.vendor.VaccinDialogResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDialogViewModel {
    private static UserDialogViewModel INSTANCE = null;
    private static Context mContex;
    private DialogUserBinding _binding;
    private CompactUser user=null;
    private MutableLiveData<String> username=new MutableLiveData<>("");
    private MutableLiveData<String> password=new MutableLiveData<>("");
    private MutableLiveData<String> confirmPassword=new MutableLiveData<>("");
    private MutableLiveData<String> name=new MutableLiveData<>("");
    private MutableLiveData<String> lastname=new MutableLiveData<>("");
    private MutableLiveData<String> firstname=new MutableLiveData<>("");
    private MutableLiveData<String> tel=new MutableLiveData<>("");
    private MutableLiveData<String> email=new MutableLiveData<>("");
    private MutableLiveData<String> cancelButtonText=new MutableLiveData<>("Annuler"),
            validateButtonText=new MutableLiveData<>("AJOUTER"),
            numeroLotVaccin=new MutableLiveData<>();
    private IDialogResultHandler<Boolean> resultHandeler=null;

    private View.OnClickListener takePictureClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
requestCapture();
        }
    };
    private Bitmap bufferedImage;

    public void requestCapture(){
        int rc = ActivityCompat.checkSelfPermission(mContex, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED)
            ((MainActivity) mContex).requestCameraPermission();
        else {
            ((MainActivity)mContex).startCameraIntent();
        }
    }





    private View.OnClickListener validateButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUser();
        }
    };

    private void updateUser() {
        if(password.getValue().compareTo(confirmPassword.getValue())!=0) Toast.makeText(mContex, "Les deux mots de passe sont différents, recommencer!", Toast.LENGTH_LONG).show();
        else {
            _bindViewToUser();

        }


    }

    private void _bindViewToUser() {
        String token=mContex.getSharedPreferences(WaniApp.PREFS_ID, Context.MODE_PRIVATE).getString(WaniApp.LAST_KNOWN_TOKEN_KEY, "");
        WaniNetworkAccess.getInstanceForToken(token,mContex).checkApi().enqueue(
                new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        if(response!=null && response.code()==200){
                            procedDataHandling();
                            Toast.makeText(mContex, "Reussite!", Toast.LENGTH_LONG).show();
                            resultHandeler.onResult(true);
                        }
                        else {
                            promptForNetworkNeed();
                            resultHandeler.onResult(true);
                        }
                    }

                    private void promptForNetworkNeed() {
                        Toast.makeText(mContex, "Vous devez avoir une connexion internet pour actualiser vos informations de connextion", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        promptForNetworkNeed();
                        resultHandeler.onResult(true);
                    }
                }
        );



    }

    private void procedDataHandling(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        People mPeople= this.user.getIdPeople();
        if(bufferedImage!=null){
            bufferedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String mData= Base64.encodeToString(byteArray, Base64.DEFAULT);


            Picture userPicture=Picture.findById(Picture.class,
                    user.getIdPeople().getLocaleIdProfile());
            if(userPicture==null){
                userPicture=new Picture();
                userPicture.save();
                mPeople.setLocaleIdProfile(userPicture.getId());

            }
            userPicture.setData(mData);
            userPicture.setEdited(WaniApp.getCurrentDate());
            userPicture.update();
        }
        this.user.setUsername(username.getValue());

        mPeople.setName(name.getValue());
        mPeople.setLastName(lastname.getValue());
        mPeople.setFirstName(firstname.getValue());
        mPeople.setTel(tel.getValue());
        mPeople.setEmail(email.getValue());
        mPeople.setEdited(WaniApp.getCurrentDate());
        SharedPreferences mPrefs=mContex.getSharedPreferences(WaniApp.PREFS_ID, Context.MODE_PRIVATE);
        mPrefs.edit().putString(WaniApp.LAST_KNOWN_USER_USERNAME, username.getValue()).commit();

        if(password.getValue().length()>=4){
            mPrefs.edit().putString(WaniApp.NEW_PASSWORD, password.getValue()).commit();
            mPrefs.edit().putBoolean(WaniApp.USER_CHANGED, true).commit();
        }

        mPeople.update();
        Log.d("zaza",String.format("name %s",People.findById(People.class, mPeople.getId()).getName()));
        this.user.setEdited(WaniApp.getCurrentDate());
        this.user.update();
        WaniApp.setLogedInUser(this.user);
        WaniSyncHelper.getInstance(_binding.getRoot().getContext()).procedSynk(new WaniRepository.WaniRepositorySynkListener());
    }

    private View.OnClickListener cancelButtonClickHandler= new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            resultHandeler.onResult(false);
        }
    };


    public static UserDialogViewModel getInstance(Context context, CompactUser user, DialogUserBinding mBinding, MainActivity.ProfileResultHandler profileResultHandler){
        if(UserDialogViewModel.INSTANCE==null){
            synchronized (UserDialogViewModel.class){
               UserDialogViewModel.INSTANCE=new UserDialogViewModel(context);
            }
        }
        mContex=context;
        INSTANCE._binding=mBinding;
        INSTANCE.resultHandeler=profileResultHandler;
        INSTANCE.user=user;
        mContex.getSharedPreferences(WaniApp.PREFS_ID, Context.MODE_PRIVATE).edit().putBoolean(WaniApp.USER_CHANGED, false).commit();
        INSTANCE._init();
        return UserDialogViewModel.INSTANCE;
    }

    public UserDialogViewModel(Context contex) {
        this.mContex = contex;

    }

    private void _init() {
        cancelButtonText.setValue("Annuler");
        validateButtonText.setValue("OK");
        bufferedImage=null;
        _bindUserToView(user);
        _reinitializePassword();
    }

    private void _reinitializePassword() {
        password.setValue("");
        confirmPassword.setValue("");
    }

    private void _bindUserToView(CompactUser user) {
        People mPeople=user.getIdPeople();
        this.username.setValue(user.getUsername());
        this.name.setValue(mPeople.getName());
        this.lastname.setValue(mPeople.getLastName());
        this.firstname.setValue(mPeople.getFirstName());
        this.tel.setValue(mPeople.getTel());
        this.email.setValue(mPeople.getEmail());
        _binding.ivProfile.setImageBitmap(Common.loadPictureByLocaleId(_binding.getRoot().getContext(), mPeople.getLocaleIdProfile()));
    }

    public void uploadImage(Bitmap imageBitmap) {
        _binding.ivProfile.setImageBitmap(imageBitmap);
        bufferedImage= imageBitmap;

    }

    public View.OnClickListener getValidateButtonClickHandler() {
        return validateButtonClickHandler;
    }

    public View.OnClickListener getCancelButtonClickHandler() {
        return cancelButtonClickHandler;
    }


    public void setValidateButtonClickHandler(View.OnClickListener validateButtonClickHandler) {
        this.validateButtonClickHandler = validateButtonClickHandler;
    }

    public void setCancelButtonClickHandler(View.OnClickListener cancelButtonClickHandler) {
        this.cancelButtonClickHandler = cancelButtonClickHandler;
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

    /*

    public IVaccinDialogHandler<VaccinDialogResult> getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(IVaccinDialogHandler<VaccinDialogResult> resultHandler) {
        this.resultHandler = resultHandler;
    }


     */

    public DialogUserBinding get_binding() {
        return _binding;
    }

    public  void set_binding(DialogUserBinding _binding) {
        this._binding = _binding;
    }

    public IDialogResultHandler<Boolean> getResultHandeler() {
        return resultHandeler;
    }

    public void setResultHandeler(IDialogResultHandler<Boolean> resultHandeler) {
        this.resultHandeler = resultHandeler;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public void setUsername(MutableLiveData<String> username) {
        this.username = username;
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(MutableLiveData<String> confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(MutableLiveData<String> name) {
        this.name = name;
    }

    public MutableLiveData<String> getLastname() {
        return lastname;
    }

    public void setLastname(MutableLiveData<String> lastname) {
        this.lastname = lastname;
    }

    public MutableLiveData<String> getFirstname() {
        return firstname;
    }

    public void setFirstname(MutableLiveData<String> firstname) {
        this.firstname = firstname;
    }

    public MutableLiveData<String> getTel() {
        return tel;
    }

    public void setTel(MutableLiveData<String> tel) {
        this.tel = tel;
    }

    public MutableLiveData<String> getEmail() {
        return email;
    }

    public void setEmail(MutableLiveData<String> email) {
        this.email = email;
    }

    public View.OnClickListener getTakePictureClickHandler() {
        return takePictureClickHandler;
    }
}
