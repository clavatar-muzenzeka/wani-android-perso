package cd.clavatar.wani.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cd.clavatar.wani.utilities.WaniAccountAuthenticator;

public class WaniAuthenticatorService extends Service {
    public WaniAuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new WaniAccountAuthenticator(this).getIBinder();
    }
}
