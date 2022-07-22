package cd.clavatar.wani;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import cd.clavatar.wani.Service.SynkService;
import cd.clavatar.wani.data.WaniRepository;
import cd.clavatar.wani.data.model.WaniDataSource;
import cd.clavatar.wani.ui.main.MainActivity;
import cd.clavatar.wani.utilities.Common;

import static cd.clavatar.wani.data.model.WaniDataSource.getInstance;

/**
 * Created by Cl@v@t@r on 21/03/2020
 */
public abstract class WaniBaseActivity extends AppCompatActivity {

    private Throwable pendingBug;

    public void sendMail(String errorDetails){
        String username = WaniApp.getLogedInUser() != null ?WaniApp.getLogedInUser().getUsername():"No loged in user";
        BackgroundMail.newBuilder(this)
                .withUsername("vastel.user.2@gmail.com")
                .withPassword("vastel243!")
                //.withAttachments(Environment.getExternalStorageDirectory().getPath() + "/Prime.jpg")
                .withMailto("vastel.user.1@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("Bug rapport "+WaniApp.getDeviceIndentifier()+" "+username)
                .withBody(errorDetails)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)   {



                } else {


                }
                sendBugMail();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void sendBugMail() {
        if (ContextCompat.checkSelfPermission(WaniBaseActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //request for the permission
            ActivityCompat.requestPermissions(WaniBaseActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    10);

            // MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        }else {
            WaniBaseActivity.this.sendMail("Message "+this.pendingBug.getMessage()+"\nStack Trace: "+Common.getStackTrace(this.pendingBug));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

/*
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {


                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        WaniBaseActivity.this.pendingBug = paramThrowable;
                        WaniBaseActivity.this.sendMail("Message "+paramThrowable.getMessage()+"\nStack Trace: "+Common.getStackTrace(paramThrowable));
                        Toast.makeText(WaniBaseActivity.this,"Il semble y avoir une erreur dans les données, l'application va redemarrer", Toast.LENGTH_LONG).show();

                        //Toast.makeText(WaniBaseActivity.this,paramThrowable.getMessage(), Toast.LENGTH_LONG).show();

                        Log.d("zaza",String.format("Gobally caughtched error %s", paramThrowable.getMessage()));
                        paramThrowable.printStackTrace();
                        Looper.loop();
                    }
                }.start();
                try
                {
                    Thread.sleep(5000); // Let the Toast display before app will get shutdown
                }
                catch (InterruptedException e) {    }
                System.exit(2);
            }
        });


*/



    }

}
