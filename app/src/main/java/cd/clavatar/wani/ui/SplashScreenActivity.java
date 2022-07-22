package cd.clavatar.wani.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;

import cd.clavatar.wani.IWaniStartPoint;
import cd.clavatar.wani.R;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.WaniBaseActivity;
import cd.clavatar.wani.data.model.WaniParameter;
import cd.clavatar.wani.ui.login.AuthenticatorActivity;
import cd.clavatar.wani.utilities.Common;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends WaniBaseActivity implements IWaniStartPoint {
    private static final String NOT_FOUND_ACCOUNT_NAME = "Not found";
    private View mContentView;
    private View splashView;
    private boolean closing=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(getIntent().getExtras()!=null){
            this.closing=getIntent().getExtras().getBoolean(WaniApp.SPLASH_CLOSING_KEY);
        }
        //discoverDivices();
        super.onCreate(savedInstanceState);
        this.initializeWaniApp();
        splashView= LayoutInflater.from(this).inflate(R.layout.activity_splash_screen, null);
        setContentView(splashView);
        LottieAnimationView annimationSpalsh = (LottieAnimationView) splashView.findViewById(R.id.annim_splash);

        annimationSpalsh.setVisibility(View.INVISIBLE);



        long delay=1500;
        long endDelay=2500;
        if(closing){
            delay=0;
            endDelay=1000;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                annimationSpalsh.setVisibility(View.VISIBLE);
                if(!closing)annimationSpalsh.reverseAnimationSpeed();
                annimationSpalsh.playAnimation();
            }
        }, delay);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(closing) {
                    finishAffinity();
                    System.exit(0);
                }
                else {
                    Intent i=new Intent(SplashScreenActivity.this, AuthenticatorActivity.class);
                    startActivity(i);
                }
            }
        }, endDelay);



    }

    public void initializeWaniApp() {
        String deviceIdentifier = Settings.Secure.getString(getContentResolver(), "android_id");
        boolean alredyAccessed = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.ALREADY_ACCESSED_KEY, false);
        boolean backup = getSharedPreferences(WaniApp.PREFS_ID, 0).getBoolean(WaniApp.BACKUP_KEY, false);
        if (alredyAccessed) {
            WaniApp.setAlradyAccessed();
        }
        WaniParameter parameter = new WaniParameter();
        WaniApp.setDeviceIdentifier(deviceIdentifier);
        Log.d("zaza", "cacthed device id "+WaniApp.getDeviceIndentifier());
        WaniApp.setParameter(parameter);
        WaniApp.setBackup(backup);
        String serial = Common.getModelSerial(this);
        Log.d("zaza","serial "+ serial);
        String bluetoothAdresse = Common.getBluetoothAddress(serial);
        Log.d("zaza", "bt adresse "+bluetoothAdresse);
        WaniApp.setPrinterAddress(bluetoothAdresse);
    }

    private void discoverDivices(){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            Log.d("zaza", device.getName()+" : "+device.getAddress());
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
