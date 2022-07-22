package cd.clavatar.wani.ui.carnet;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.databinding.InverseMethod;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.WaniBaseActivity;
import cd.clavatar.wani.data.model.Vaccin;
import cd.clavatar.wani.data.model.VaccinOccurence;
import cd.clavatar.wani.databinding.LayoutActivityCarnetBinding;
import cd.clavatar.wani.utilities.Common;
import cd.clavatar.wani.utilities.ISO8601;
import cd.clavatar.wani.vendor.IssueAdapter;
import cd.clavatar.wani.vendor.MainToCarnetActivityRoutingBundle;
import cd.clavatar.wani.vendor.VaccinAdapter;

/* renamed from: cd.clavatar.wani.ui.carnet.CarnetActivity */
public class CarnetActivity extends WaniBaseActivity {
    private static CarnetActivity CONSTANT_CONTEX = null;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static boolean RUNNING = false;
    private LayoutActivityCarnetBinding binding;
    private Bitmap bitmap;
    private CarnetViewModel carnetModel;
    private MainToCarnetActivityRoutingBundle mMainToCarnetActivityRoutingBundle;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RUNNING = true;
        CONSTANT_CONTEX = this;
        LayoutActivityCarnetBinding layoutActivityCarnetBinding = (LayoutActivityCarnetBinding) DataBindingUtil.setContentView(this, R.layout.layout_activity_carnet);
        this.binding = layoutActivityCarnetBinding;
        layoutActivityCarnetBinding.setLifecycleOwner(this);
        if (getIntent().getExtras() != null) {
            MainToCarnetActivityRoutingBundle mainToCarnetActivityRoutingBundle = (MainToCarnetActivityRoutingBundle) getIntent().getExtras().getParcelable(WaniApp.BUNDLED_MAIN_TO_CARNET);
            this.mMainToCarnetActivityRoutingBundle = mainToCarnetActivityRoutingBundle;
            CarnetViewModel instance = CarnetViewModel.getInstance(this, mainToCarnetActivityRoutingBundle);
            this.carnetModel = instance;
            this.binding.setModel(instance);
        } else {
            Toast.makeText(this, "Erreur ED001", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(1);
        }
        this.binding.getRoot().findViewById(R.id.icon_back).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CarnetActivity.this.goBack();
            }
        });
        WaniApp.printableActivity = true;
    }

    /* access modifiers changed from: private */
    public void goBack() {
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void onPostResume() {
        super.onPostResume();
    }

    public void requestCapture() {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            requestCameraPermission();
        } else {
            startCameraIntent();
        }
    }

    private void startCameraIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            this.carnetModel.uploadImage((Bitmap) data.getExtras().get("data"));
        }
    }

    public LayoutActivityCarnetBinding getCarnetBinding() {
        return this.binding;
    }

    /* renamed from: cd.clavatar.wani.ui.carnet.CarnetActivity$DateISOConverter */
    public static class DateISOConverter {
        private static String lastString = "";

        public static Date rawToDate(String value) {
            String[] valParams = value.split("/");
            if (valParams.length == 3 && valParams[0].length() == 2 && valParams[1].length() == 2 && valParams[2].length() == 4) {
                try {
                    return ISO8601.toCalendar(valParams[2] + "-" + valParams[1] + "-" + valParams[0] + "T00:00:00.000Z").getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                    return null;
                }
            } else {
                lastString = value;
                return null;
            }
        }

        @InverseMethod("rawToDate")
        public static String isoStringToRaw(Date value) {
            return value != null ? new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Long.valueOf(value.getTime())) : lastString;
        }
    }

    private void requestCameraPermission() {
        Log.w("zaza", "Camera permission is not granted. Requesting permission");
        final String[] permissions = {"android.permission.CAMERA"};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.CAMERA")) {
            ActivityCompat.requestPermissions(this, permissions, 2);
        } else {
            Snackbar.make(this.binding.getRoot(), (int) R.string.permission_camera_rationale, BaseTransientBottomBar.LENGTH_INDEFINITE).setAction((int) R.string.ok, (View.OnClickListener) new View.OnClickListener() {
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(CarnetActivity.this, permissions, 2);
                }
            }).show();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 2) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (grantResults.length == 0 || grantResults[0] != 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Permission not granted: results len = ");
            sb.append(grantResults.length);
            sb.append(" Result code = ");
            sb.append(grantResults.length > 0 ? Integer.valueOf(grantResults[0]) : "(empty)");
            Log.e("zaza", sb.toString());
            new AlertDialog.Builder(this).setTitle("Multitracker sample").setMessage(R.string.no_camera_permission).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    CarnetActivity.this.finish();
                }
            }).show();
        } else {
            startCameraIntent();
        }
    }

    @BindingAdapter("chipsSource")
    public static void bindChipsSource(LinearLayout ll, List<VaccinOccurence> vacs) {
        ll.removeAllViews();
        if (vacs != null) {
            for (VaccinOccurence vac : vacs) {
                LinearLayout llParent = (LinearLayout) LayoutInflater.from(ll.getContext()).inflate(R.layout.layout_vaccin_chips, (ViewGroup) null);
                Chip mChip = (Chip) llParent.findViewById(R.id.inner_chip);
                mChip.setText(generateTextFromVaccin(vac));
                mChip.setBackgroundColor(getBackgroundColorFromVaccinationDate(vac));
                llParent.removeView(mChip);
                ll.addView(mChip);
            }
        }
    }

    private static int getBackgroundColorFromVaccinationDate(VaccinOccurence vac) {
        if (vac.getIdVaccin() == null || vac.getReceived() == null) {
            return R.color.secondaryLightColor;
        }
        Date currDate = WaniApp.getCurrentDate();
        Calendar todayCal = Calendar.getInstance();
        todayCal.setTime(vac.getReceived());
        todayCal.add(5, vac.getIdVaccin().getExpiration());
        if (currDate.after(todayCal.getTime())) {
            return R.color.secondaryTextColorLight;
        }
        return R.color.secondaryTextColorLight;
    }

    private static String generateTextFromVaccin(VaccinOccurence vac) {
        String str;
        if (vac.getIdVaccin() == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(vac.getIdVaccin().getDesignation());
        if (vac.getReceived() != null) {
            str = " - (" + ISO8601.getFormateddate(vac.getReceived()) + ")";
        } else {
            str = "(...)";
        }
        sb.append(str);
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        WaniApp.printableActivity = false;
        RUNNING = false;
        this.carnetModel.resetCarnId();
        this.carnetModel.carnet.setValue(null);
        this.carnetModel.getRawCarnetId().setValue(null);
        this.carnetModel.getLockRawCarnetId().setValue(false);
        this.carnetModel.interactingCarnet = null;
        this.carnetModel.initAfterMainvieDestroyed();
        Common.detached = true;
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.carnetModel.initAfterMainviePaused();
        if (this.carnetModel.infoDialog != null) {
            this.carnetModel.infoDialog.dismiss();
        }
    }

    @BindingAdapter(
            {"bind:spinnerCriteria"})
    public static void setSpinnerCriteria(Spinner spinner, List<Vaccin> cc) {
        spinner.setAdapter(new VaccinAdapter(spinner.getContext(), cc));
    }

    @BindingAdapter(value = {"selectedSpinnerCriteria", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(AppCompatSpinner pAppCompatSpinner, Vaccin newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        pAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (newSelectedValue != null) {
            pAppCompatSpinner.setSelection(((VaccinAdapter) pAppCompatSpinner.getAdapter()).getPosition(newSelectedValue), true);
        }
    }

    @InverseBindingAdapter(attribute = "selectedSpinnerCriteria", event = "selectedValueAttrChanged")
    public static Vaccin captureSelectedValue(AppCompatSpinner pAppCompatSpinner) {
        return (Vaccin) pAppCompatSpinner.getSelectedItem();
    }

    @BindingAdapter("customSelectionChanged")
    public static void setCustomSelectionChanged(AppCompatSpinner spinner, AdapterView.OnItemSelectedListener listener) {
        spinner.setOnItemSelectedListener(listener);
    }
    @BindingAdapter("dataSourceIssues")
    public static void adaptRecyclerViewDataSource(RecyclerView rv, List<String> datas) {
        rv.setLayoutManager(new LinearLayoutManager(rv.getContext()));
        rv.setAdapter(new IssueAdapter(datas));
    }
    @BindingAdapter(value = {"selectedNationalityValue", "selectedValueAttrChanged"}, requireAll = false)
    public static void bindSpinnerData(AppCompatSpinner pAppCompatSpinner, String newSelectedValue, final InverseBindingListener newTextAttrChanged) {
        pAppCompatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                newTextAttrChanged.onChange();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (newSelectedValue != null) {
            pAppCompatSpinner.setSelection(((ArrayAdapter) pAppCompatSpinner.getAdapter()).getPosition(newSelectedValue), true);
        }
    }
    @InverseBindingAdapter(attribute = "selectedNationalityValue", event = "selectedValueAttrChanged")
    public static String captureNatSelectedValue(AppCompatSpinner pAppCompatSpinner) {
        return (String) pAppCompatSpinner.getSelectedItem();
    }
}
