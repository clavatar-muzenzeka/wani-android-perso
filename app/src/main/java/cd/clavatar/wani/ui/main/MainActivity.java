package cd.clavatar.wani.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cd.clavatar.wani.R;
import cd.clavatar.wani.ScanConfig;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.WaniBaseActivity;
import cd.clavatar.wani.activity.NFC2Activity;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.CompactCarnet;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.data.model.WaniNetworkAccess;
import cd.clavatar.wani.data.model.WaniParameter;
import cd.clavatar.wani.databinding.ActivityMainBinding;
import cd.clavatar.wani.databinding.DialogUserBinding;
import cd.clavatar.wani.databinding.NavHeaderMainBinding;
import cd.clavatar.wani.ui.DrawerViewModel;
import cd.clavatar.wani.ui.SplashScreenActivity;
import cd.clavatar.wani.ui.UserDialogViewModel;
import cd.clavatar.wani.ui.carnet.CarnetActivity;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.vendor.CompactCarnetAdapter;
import cd.clavatar.wani.vendor.CompactCarnetViewModel;
import cd.clavatar.wani.vendor.CompactPaiementAdapter;
import cd.clavatar.wani.vendor.IDialogResultHandler;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle;

/* renamed from: cd.clavatar.wani.ui.main.MainActivity */
public class MainActivity extends WaniBaseActivity implements CompactCarnetViewModel.CarnetSelectionListener, NavigationView.OnNavigationItemSelectedListener {
    public static final int RC_BARCODE_CAPTURE = 1;
    private static final int RC_HANDLE_CAMERA_PERM = 4;
    private static final int RC_NFC_CAPTURE = 16;
    public static final int REQUEST_IMAGE_CAPTURE = 4;
    public static boolean RUNNING = false;
    public static Context StaticContext = null;
    private static final int TAG_LEAVE_APP = 3;
    private static final int TAG_READ_QR_ECHEC = 2;
    /* access modifiers changed from: private */
    public boolean certifButtonClicked = false;
    private String collectedIdCarnetRaw;
    private boolean creating = false;
    /* access modifiers changed from: private */
    public DrawerLayout drawer;

    /* renamed from: foundedCarnetForNumérisation  reason: contains not printable characters */
    private CompactCarnet f304foundedCarnetForNumrisation;
    /* access modifiers changed from: private */
    public Dialog infoDialog;
    private ActivityMainBinding mBinding;
    public MainToCarnetActivityRoutingBundle mMainToCarnetActivityRoutingBundle = new MainToCarnetActivityRoutingBundle();
    /* access modifiers changed from: private */
    public MainViewModel mModel;
    private boolean makingDuplicate = false;
    private NavigationView navigationView;
    private NavHeaderMainBinding nhmb;
    private boolean swtchingFromRowToNumeric;
    private String tempIdDup = null;
    /* access modifiers changed from: private */
    public AlertDialog userDialog;
    private UserDialogViewModel userModel;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RUNNING = true;
        StaticContext = this;
        //ImageLoader.getInstance().init(new ImageLoaderConfiguration.Builder(this).build());
        ActivityMainBinding activityMainBinding = (ActivityMainBinding) DataBindingUtil.setContentView(this, R.layout.activity_main);
        this.mBinding = activityMainBinding;
        activityMainBinding.setLifecycleOwner(this);
        MainViewModel instance = MainViewModel.getInstance(this, this.mBinding);
        this.mModel = instance;
        this.mBinding.setModel(instance);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.navigationView = this.mBinding.navView;
        this.mBinding.iconMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.drawer.openDrawer((int) GravityCompat.START);
            }
        });
        NavHeaderMainBinding bind = NavHeaderMainBinding.bind(this.navigationView.getHeaderView(0));
        this.nhmb = bind;
        bind.setLifecycleOwner(this);
        this.nhmb.setModel(DrawerViewModel.getInstance(this, 0));
        if (WaniApp.getLogedInUser() !=null && WaniApp.getLogedInUser().getIdPeople()!=null && WaniApp.getLogedInUser().getIdPeople().getLocaleIdProfile() != null && Picture.findById(Picture.class, WaniApp.getLogedInUser().getIdPeople().getLocaleIdProfile()) != null) {
            this.nhmb.imageViewUser.setImageBitmap(Common.loadPictureByLocaleId(this, WaniApp.getLogedInUser().getIdPeople().getLocaleIdProfile()));
        } else if (WaniApp.getLogedInUser() != null && WaniApp.getLogedInUser().getIdPeople() != null && WaniApp.getLogedInUser().getIdPeople().getIdProfile() != null) {
            Picasso.get().load(WaniNetworkAccess.BASE_URL + "/picture/load/" + WaniApp.getLogedInUser().getIdPeople().getIdProfile() + "?static=yes").placeholder((int) R.drawable.ic_user).into((ImageView) this.nhmb.imageViewUser);
        }
        setHeader();
        setNavigationViewListener();
        WaniApp.printableActivity = true;
    }

    /* access modifiers changed from: private */
    public void setHeader() {
        this.nhmb.setModel(DrawerViewModel.getInstance(this, 0));
        CompactUser logedInUser = WaniApp.getLogedInUser();
        this.nhmb.imageViewUser.setImageBitmap(Common.loadPictureByLocaleId(this, WaniApp.getLogedInUser().getIdPeople().getLocaleIdProfile()));
    }

    public void scanCode() {
        NfcAdapter adapter = ((NfcManager) getBaseContext().getSystemService(NFC_SERVICE)).getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            startActivityForResult(new Intent(this, NFC2Activity.class), 16);
        } else if (!isPackageExisted("com.sunmi.codescanner")) {
            Toast.makeText(this, "Outil de lecture QR absent, contacter l'administrateur", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent();
            intent.setAction("com.sunmi.scan");
            intent.setPackage("com.sunmi.codescanner");
            startActivityForResult(intent, 1);
        }
    }

    public boolean isPackageExisted(String targetPackage) {
        for (ApplicationInfo packageInfo : getPackageManager().getInstalledApplications(0)) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
        this.mModel.onActivityResume();
        RUNNING = true;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        RUNNING = false;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        this.mModel.onActivityDestroyed();
        WaniApp.printableActivity = false;
        RUNNING = false;
        super.onDestroy();
    }

    public void onBackPressed() {
        preventDestroy();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.mModel.init();
        this.mBinding.ivScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mMainToCarnetActivityRoutingBundle.setMode(5);
                MainActivity.this.scanCode();
            }
        });
        this.mBinding.ivSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mModel.switchSerch();
            }
        });
        this.mBinding.ivUndo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.mModel.cancelSearch();
            }
        });
        super.onResume();
    }

    public void startCarnetAcivityWithBundle() {
        Intent ca = new Intent(this, CarnetActivity.class);
        ca.putExtra(WaniApp.BUNDLED_MAIN_TO_CARNET, this.mMainToCarnetActivityRoutingBundle);
        startActivity(ca);
    }



    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (data != null) {
                procedCodeHandle((String) ((List<HashMap<String, String>>) data.getExtras().getSerializable("data")).get(0).get(ScanConfig.VALUE));
            } else {
                tryShowToast(this, "Lecture abandonnée");
            }
        } else if (requestCode == 4 && resultCode == -1) {
            this.userModel.uploadImage((Bitmap) data.getExtras().get("data"));
        } else if (requestCode == 16) {
            String scanResult = data.getStringExtra("SCAN_RESULT");
            if (scanResult.compareTo("CANCEL") == 0) {
                Toast.makeText(this, "Lecture abandonnée", Toast.LENGTH_SHORT).show();
            } else {
                procedCodeHandle(scanResult);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /* access modifiers changed from: private */
    public void tryShowToast(final Context ctx, final String messageToShow) {
        new Timer().schedule(new TimerTask() {
            public void run() {
                if (MainActivity.RUNNING) {
                    Log.d("zaza", "running");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, messageToShow, Toast.LENGTH_LONG).show();
                        }
                    });
                    return;
                }
                Log.d("zaza", "not running");
                MainActivity.this.tryShowToast(ctx, messageToShow);
            }
        }, 1000);
    }

    private void procedCodeHandle(String scanResult) {
        CompactCarnet correspondingCC = this.mModel.getCarnetByCarnId(scanResult);
        if (correspondingCC == null) {
            tryShowToast(this, "Ce code est inconnu du système!");
        } else if (correspondingCC.getCarnetStatus() == null || correspondingCC.getCarnetStatus().getDistributionStatus() != 2) {
            if (this.mMainToCarnetActivityRoutingBundle.getMode() == 5) {
                if (correspondingCC.getCarnetStatus() == null || correspondingCC.getCarnetStatus().getDistributionStatus() == 0 || correspondingCC.getCarnetStatus().getDistributionStatus() == 4) {
                    tryShowToast(this, "Ce code est vierge");
                    return;
                }
                this.mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(correspondingCC.getId());
                try {
                    this.mMainToCarnetActivityRoutingBundle.switchToMode(this.mMainToCarnetActivityRoutingBundle.getMode(), scanResult, this.mMainToCarnetActivityRoutingBundle.getRawCarnetId(), this.mMainToCarnetActivityRoutingBundle.getCarnetLocaleId());
                } catch (Exception e) {
                    tryShowToast(this, "Erreur ED001");
                    return;
                }
            }
            if (this.mMainToCarnetActivityRoutingBundle.getMode() == 1 || this.mMainToCarnetActivityRoutingBundle.getMode() == 2 || this.mMainToCarnetActivityRoutingBundle.getMode() == 0 || this.mMainToCarnetActivityRoutingBundle.getMode() == 3) {
                if (correspondingCC != null && correspondingCC.getCarnetStatus() != null && correspondingCC.getCarnetStatus().getDistributionStatus() != 2) {
                    this.mModel.setFoundedCarnetForNumerisation(correspondingCC);
                    this.mModel.showInfoDialogForCarnetWithIdFoundedAndNotRaw(correspondingCC);
                    return;
                } else if (this.mMainToCarnetActivityRoutingBundle.getMode() == 1) {
                    this.mMainToCarnetActivityRoutingBundle.setCarnetLocaleId((Long) null);
                }
            }
            if (this.mMainToCarnetActivityRoutingBundle.getMode() != 5) {
                if (this.certifButtonClicked) {
                    if (correspondingCC.getCarnetPurpose() == 0) {
                        tryShowToast(this, "Veuillez scanner un certificat COVID-19, ceci est un carnet de vaccination");
                        return;
                    }
                } else if (correspondingCC.getCarnetPurpose() == 1) {
                    tryShowToast(this, "Veuillez scanner un carnet de vaccination, ceci est un certificat COVID-19");
                    return;
                }
            }
            this.mMainToCarnetActivityRoutingBundle.setCarnetId(scanResult);
            startCarnetAcivityWithBundle();
        } else {
            tryShowToast(this, "Ce code a été supprimé");
        }
    }

    private void informForQrReadEchec() {
        this.infoDialog = Common.showInfoDialog(this, 2, "La lecture a échouée, vous êtes priés de recommencer", "Retour", "RECOMMENCER", true, new IInfoDialogResultHandler<InfoDialogResult>() {
            public void onInfoDialogResult(InfoDialogResult result) {
                MainActivity.this.infoDialog.dismiss();
                if (result.getResult() == 1) {
                    MainActivity.this.scanCode();
                }
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 3) {
            return super.onKeyDown(keyCode, event);
        }
        preventDestroy();
        return false;
    }

    private void preventDestroy() {
        this.infoDialog = Common.showInfoDialog(this, 3, "Voulez-vous vraiment quitter l'application?", "Oui", "NON", true, new IInfoDialogResultHandler<InfoDialogResult>() {
            public void onInfoDialogResult(InfoDialogResult result) {
                MainActivity.this.infoDialog.dismiss();
                if (result.getResult() == 0) {
                    MainActivity.this.closeApp();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void closeApp() {
        WaniRepository.reinitDatas();
        Intent finishIntent = new Intent(this, SplashScreenActivity.class);
        finishIntent.putExtra(WaniApp.SPLASH_CLOSING_KEY, true);
        startActivity(finishIntent);
    }

    public void handleSelected(String id) {
        this.mModel.select(id);
    }

    public ActivityMainBinding getBinding() {
        return this.mBinding;
    }

    public ActivityMainBinding getBindng() {
        return this.mBinding;
    }

    public void showPopupMenu(FloatingActionButton mBt) {
        PopupMenu mCreate = new PopupMenu(this, mBt);
        mCreate.getMenuInflater().inflate(R.menu.create_carnet_menu, mCreate.getMenu());
        mCreate.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Boolean handeled = false;
                switch (item.getItemId()) {
                    case R.id.menu_item_certif:
                        boolean unused = MainActivity.this.certifButtonClicked = true;
                        MainActivity.this.prepareActivityForCreation();
                        handeled = true;
                        break;
                    case R.id.menu_item_create:
                        boolean unused2 = MainActivity.this.certifButtonClicked = false;
                        MainActivity.this.prepareActivityForCreation();
                        handeled = true;
                        break;
                    case R.id.menu_item_numerise:
                        MainActivity.this.handleNumerisation();
                        handeled = true;
                        break;
                }
                return handeled.booleanValue();
            }
        });
        mCreate.show();
    }

    public void handleNumerisation() {
        this.mModel.prompForRawCarnetId();
    }

    public void prepareActivityForNumerisation(String value) {
        this.mMainToCarnetActivityRoutingBundle.setRawCarnetId(value);
        this.mMainToCarnetActivityRoutingBundle.setMode(0);
        scanCode();
    }

    public void prepareActivityForCreation() {
        this.mMainToCarnetActivityRoutingBundle.setMode(1);
        this.mMainToCarnetActivityRoutingBundle.setCreationMode(MainToCarnetActivityRoutingBundle.CreationBundleExtras.CREATION_MODE_DELIVERY);
        scanCode();
    }

    public void prepareActivityForSwitchFromRowToNumeric(CompactCarnet foundedCarnetForNumerisation) {
        Log.i("zaza", "local id "+foundedCarnetForNumerisation.getId().toString() );
        this.mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(foundedCarnetForNumerisation.getId());
        this.mMainToCarnetActivityRoutingBundle.setRawCarnetId(foundedCarnetForNumerisation.getIdRawCarnet());
        this.mMainToCarnetActivityRoutingBundle.setMode(2);
        scanCode();
    }

    public void prepareActivityForDuplication(CompactCarnet compactCarnet) {
        this.mMainToCarnetActivityRoutingBundle.setMode(3);
        this.mMainToCarnetActivityRoutingBundle.setCarnetLocaleId(compactCarnet.getId());
        scanCode();
    }

    public void prepareActivityForConsignation(String rawCarnetId) {
        this.mMainToCarnetActivityRoutingBundle.setMode(MainToCarnetActivityRoutingBundle.CONSIGNATION);
        this.mMainToCarnetActivityRoutingBundle.setRawCarnetId(rawCarnetId);
        this.mMainToCarnetActivityRoutingBundle.setCarnetLocaleId((Long) null);
        this.mMainToCarnetActivityRoutingBundle.setCarnetId((String) null);
        startCarnetAcivityWithBundle();
    }

    public void prepareActivityForCheck(CompactCarnet cc) {
        scanCode();
    }

    public void prepareActivityForCheck(String ccS) {
        scanCode();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        this.mBinding.getRoot().findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.closeApp();
            }
        });
        return true;
    }

    private void setNavigationViewListener() {
        this.navigationView.setNavigationItemSelectedListener(this);
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                closeApp();
                return false;
            case R.id.nav_paiement:
                this.mModel.getCurrentMenu().setValue(MainViewModel.PAIEMENT);
                this.drawer.closeDrawer((int) GravityCompat.START);
                return false;
            case R.id.nav_stori:
                this.mModel.getCurrentMenu().setValue(MainViewModel.STORY);
                this.drawer.closeDrawer((int) GravityCompat.START);
                return false;
            default:
                return false;
        }
    }

    /* renamed from: cd.clavatar.wani.ui.main.MainActivity$ProfileResultHandler */
    public class ProfileResultHandler implements IDialogResultHandler<Boolean> {
        public ProfileResultHandler() {
        }

        public void onResult(Boolean result) {
            MainActivity.this.userDialog.dismiss();
            MainActivity.this.setHeader();
        }
    }

    private void viewProfileDialog() {
        WaniApp.getLogedInUser();
        DialogUserBinding mBinding2 = DialogUserBinding.inflate(LayoutInflater.from(this));
        UserDialogViewModel instance = UserDialogViewModel.getInstance(this, (CompactUser) CompactUser.findById(CompactUser.class, WaniApp.getLogedInUser().getId()), mBinding2, new ProfileResultHandler());
        this.userModel = instance;
        mBinding2.setModel(instance);
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
        dBuilder.setView(mBinding2.getRoot());
        AlertDialog create = dBuilder.create();
        this.userDialog = create;
        create.show();
    }

    public void startCameraIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 4);
        }
    }

    public void requestCameraPermission() {
        Log.w("zaza", "Camera permission is not granted. Requesting permission");
        final String[] permissions = {"android.permission.CAMERA"};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
            ActivityCompat.requestPermissions(this, permissions, 4);
        } else {
            Snackbar.make(this.mBinding.getRoot(), (int) R.string.permission_camera_rationale, Snackbar.LENGTH_INDEFINITE).setAction((int) R.string.ok, (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(MainActivity.this, permissions, 4);
                }
            }).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 4) {
            if (grantResults[0] == 0) {
                startCameraIntent();
            } else {
                Toast.makeText(this, "Veuillez autoriser l'accès à la caméra pour continuer", Toast.LENGTH_LONG).show();
            }
        }
    }

    @BindingAdapter("dataSource")
    public static void adaptRecyclerViewDataSource(RecyclerView rv, MutableLiveData<List<CompactCarnet>> datas){
        rv.setLayoutManager(new LinearLayoutManager(StaticContext));
        rv.setAdapter(new CompactCarnetAdapter(datas.getValue()));
    }

    @BindingAdapter("dataSourceP")
    public static void adaptRecyclerViewDataSourceP(RecyclerView rv, MutableLiveData<List<CompactPaiement>> datas){
        rv.setLayoutManager(new LinearLayoutManager(StaticContext));
        rv.setAdapter(new CompactPaiementAdapter(datas.getValue()));
    }
}
