package cd.clavatar.wani.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import cd.clavatar.wani.R;
import cd.clavatar.wani.vendor.nfcutil.PillowNfcManager;

public class NFC2Activity extends AppCompatActivity {

    PillowNfcManager nfcManager;
    Button cancelAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc2);
        cancelAct=(Button)findViewById(R.id.cancel_nfc_write_button);

        nfcManager = new PillowNfcManager(this);
        nfcManager.onActivityCreate();

        nfcManager.setOnTagReadListener(tagRead -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("SCAN_RESULT",tagRead.replace("\n", ""));
            //Log.d("zaza",tagRead);
            setResult(RESULT_OK,returnIntent);
            finish();
            //Toast.makeText(getBaseContext(), "Resultat : "+tagRead, Toast.LENGTH_LONG).show();
        });

        cancelAct.setOnClickListener(goCancel);


    }

    View.OnClickListener goCancel=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("SCAN_RESULT","CANCEL");
            setResult(RESULT_OK,returnIntent);
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        nfcManager.onActivityResume();

    }

    @Override
    protected void onPause() {
        nfcManager.onActivityPause();
        super.onPause();
    }

    @Override
    public void onNewIntent(Intent intent) {
        nfcManager.onActivityNewIntent(intent);
        super.onNewIntent(intent);
    }

}
