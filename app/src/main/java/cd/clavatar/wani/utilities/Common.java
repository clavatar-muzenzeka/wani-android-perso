package cd.clavatar.wani.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import java.util.Calendar;
import java.util.Date;

import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.CompactPaiement;
import cd.clavatar.wani.data.model.Picture;
import cd.clavatar.wani.databinding.DialogPrintBinding;
import cd.clavatar.wani.databinding.SuperDialogInfosBinding;
import cd.clavatar.wani.ui.InfoDialogViewModel;
import cd.clavatar.wani.ui.carnet.CarnetActivity;
import cd.clavatar.wani.ui.main.MainActivity;
import cd.clavatar.wani.vendor.IInfoDialogResultHandler;
import cd.clavatar.wani.vendor.InfoDialogResult;
import cd.clavatar.wani.vendor.Inventory;
import cd.clavatar.wani.vendor.print.Printer;

/* renamed from: cd.clavatar.wani.utilities.Common */
public class Common {
    private static final String SUNMI_CLASSIC_ADDRESS = "66:11:22:33:44:55";
    private static final String SUNMI_V2_PRO_ADRESS = "00:11:22:33:44:55";
    private static final String SUNMI_V2_PRO_SERIAL_NUMBER = "V2_PRO";
    public static MutableLiveData<Boolean> backgroundWork = new MutableLiveData<>();
    /* access modifiers changed from: private */
    public static Context context;
    public static boolean detached = false;
    public static Dialog infoDialog;
    private static Dialog printDialog;

    public static Date generatedEdidtedFromSynced(Date synced) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(synced);
        cal.add(14, -1);
        return cal.getTime();
    }

    public static String getModelSerial(Context ctx) {
        return Build.MODEL;
    }

    public static String getBluetoothAddress(String deviceSerialNumber) {
        if (((deviceSerialNumber.hashCode() == -1783774614 && deviceSerialNumber.equals(SUNMI_V2_PRO_SERIAL_NUMBER)) ? (char) 0 : 65535) != 0) {
            return SUNMI_CLASSIC_ADDRESS;
        }
        return SUNMI_V2_PRO_ADRESS;
    }

    /* renamed from: cd.clavatar.wani.utilities.Common$PrintThread */
    static class PrintThread extends Thread {
        public static Context tContex;
        private final Inventory inventory;
        public Handler mHandler;
        private CompactPaiement paiement;

        public PrintThread(Context mContext, CompactPaiement mPaiement, Inventory iv, Handler mh) {
            tContex = mContext;
            this.paiement = mPaiement;
            this.mHandler = mh;
            this.inventory = iv;
        }

        public void run() {
            Looper.prepare();
            String text = "     MINISTERE DE LA SANTE\nSERVICE D'HYGIENE AUX FRONTIERES\n________________________________\n\nDate:\n" + ISO8601.getFormateddate(this.paiement.getCreated()) + "\n\nReçu de paiement N°:\n" + WaniApp.getLogedInUser().get_id() + "/" + this.paiement.getId() + "\n\nSujet:\n" + this.paiement.getIdPayer().getName() + " " + this.paiement.getIdPayer().getLastName() + " " + this.paiement.getIdPayer().getFirstName() + "\n\nService:" + this.paiement.getIdPaiementType().getDesignation() + "\n\nNet percu:" + this.paiement.getIdPaiementType().getPaiementPrice() + " USD";
            Inventory inventory2 = this.inventory;
            if (inventory2 != null && inventory2.getIssues().size() > 0) {
                text = text + "\n\n\tNOTA (à régulariser):";
                for (String issue : this.inventory.getIssues()) {
                    text = text + "\n\t* " + issue;
                }
            }
            try {
                Printer.print(Common.context, text + "\n\n________________________________\n              Merci!\n      Copyright VASTEL 2020\n\n\n\n\n");
            } catch (Exception e) {
                Toast.makeText(tContex, "Imprimante non trouvée, assurez vous d'être connecté au bluetooth de l'imprimante embarquée", Toast.LENGTH_LONG).show();
                Message mMs = Message.obtain();
                mMs.obj = true;
                this.mHandler.sendMessage(mMs);
            }
            Message mMs2 = Message.obtain();
            mMs2.obj = true;
            this.mHandler.sendMessage(mMs2);
        }
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= 19) {
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= 12) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    public static Dialog showInfoDialog(Context context2, int TAG, String text, String cancelButtonText, String validateButtonText, Boolean cancelButtonVisibility, IInfoDialogResultHandler<InfoDialogResult> resutHandler) {
        SuperDialogInfosBinding dialogATCDDBinding = (SuperDialogInfosBinding) DataBindingUtil.inflate(LayoutInflater.from(context2), R.layout.super_dialog_infos, (ViewGroup) null, false);
        InfoDialogViewModel idvm = InfoDialogViewModel.getInstance(context2, TAG);
        idvm.setText(text);
        idvm.setCancelButtonText(cancelButtonText);
        idvm.setValidateButtonText(validateButtonText);
        idvm.setCancelButtonVisibility(cancelButtonVisibility);
        idvm.setInfoDialogResultHandler(resutHandler);
        dialogATCDDBinding.setModel(idvm);
        dialogATCDDBinding.setLifecycleOwner((AppCompatActivity) context2);
        View dialogView = dialogATCDDBinding.getRoot();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(context2);
        dBuilder.setCancelable(cancelButtonVisibility.booleanValue());
        dBuilder.setView(dialogView);
        Dialog infoDialog2 = dBuilder.create();
        idvm.setSelfRef(infoDialog2);
        if (MainActivity.RUNNING || CarnetActivity.RUNNING) {
            infoDialog2.show();
        }
        return infoDialog2;
    }

    public static void printReceipt(final Context context2, CompactPaiement paiement, Inventory inventory) {
        detached = false;
        context = context2;
        DialogPrintBinding db = (DialogPrintBinding) DataBindingUtil.inflate(LayoutInflater.from(context2), R.layout.dialog_print, (ViewGroup) null, false);
        db.setLifecycleOwner((AppCompatActivity) context2);
        View v = db.getRoot();
        AlertDialog.Builder dBuilder = new AlertDialog.Builder(context2);
        dBuilder.setCancelable(false);
        dBuilder.setView(v);
        AlertDialog create = dBuilder.create();
        infoDialog = create;
        create.show();
        new PrintThread(context2, paiement, inventory, new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (!Common.detached) {
                    Common.infoDialog.dismiss();
                }
                WaniSyncHelper.getInstance(context2).procedSynk(new WaniRepository.WaniRepositorySynkListener());
            }
        }).start();
        infoDialog.dismiss();
    }

    public static Bitmap loadPictureByLocaleId(Context mct, Long localeId) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(mct.getResources(), R.drawable.ic_broken_image_black_24dp);
        if (localeId == null) {
            return imageBitmap;
        }
        byte[] decodedString = Base64.decode(((Picture) Picture.findById(Picture.class, localeId)).getData(), 0);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public static String getDeviceAdrress(BluetoothAdapter bluetoothAdapter, String deviceName) {
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getName().equals(deviceName)) {
                return device.getAddress();
            }
        }
        return "";
    }

    public static SharedPreferences getSharedPreferences() {
        return new Activity().getSharedPreferences(WaniApp.PREFS_ID, 0);
    }



    public static String getStackTrace(Throwable t) {
        if (t == null) {
            return "Exception not available.";
        } else {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement element : t.getStackTrace()) {
                sb.append(element.toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }
}
