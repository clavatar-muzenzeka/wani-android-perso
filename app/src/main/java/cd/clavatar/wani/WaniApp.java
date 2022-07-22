package cd.clavatar.wani;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import cd.clavatar.wani.data.model.CompactUser;
import cd.clavatar.wani.data.model.WaniParameter;

/* renamed from: cd.clavatar.wani.WaniApp */
public class WaniApp extends Application {
    public static String ALREADY_ACCESSED_KEY = "alreadyaccessedkey";
    public static final String BACKUP_KEY = "backup";
    public static String BUNDLED_CARNET_ID_KEY = "bundledcartetidkey";
    public static String BUNDLED_CARNET_ID_RAW_CARNET_KEY = "bundledcarttidrawcarnetkey";
    public static String BUNDLED_CARNET_KEY = "bundledcartetkey";
    public static String BUNDLED_MAIN_TO_CARNET = "bundledmaintoCarnet";
    public static String CARNET_ORDER_KEY = "carnetorder";
    public static String LAST_KNOWN_TOKEN_KEY = "lastknowntoken";
    public static String LAST_KNOWN_USER_PASSWORD = "lastknowntokenuserpassword";
    public static String LAST_KNOWN_USER_USERNAME = "lastknowntokenuserusername";
    public static String NEW_PASSWORD = "newpassword";
    public static String PREFS_ID = "waniprefs";
    public static String SPLASH_CLOSING_KEY = "splashclosingkey";
    public static String USER_CHANGED = "userchanged";
    public static boolean backup = false;
    private static String deviceIdentifier;
    public static boolean firstAccess = true;
    private static CompactUser logedInUser = null;
    private static WaniParameter parameter;
    public static boolean printableActivity = false;
    private static String printerAddress = "";
    private static String token;

    public static Date getCurrentDate() {
        return new Date();
    }

    public static Date getDateMinusOne() {
        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        return cal.getTime();
    }

    public static boolean isFirstAccess() {
        return  firstAccess;
    }

    public static void setAlradyAccessed() {

        firstAccess = false;
    }

    public static String getPrinterAddress() {
        return printerAddress;
    }

    public void onCreate() {
        super.onCreate();
    }

    public static CompactUser getLogedInUser() {
        return logedInUser;
    }

    public static void setLogedInUser(CompactUser logedInUser2) {
        logedInUser = logedInUser2;
    }

    public String getToken() {
        return token;
    }

    public static void setToken(String token2) {
        token = token2;
    }

    public static WaniParameter getParameter() {
        return parameter;
    }

    public static String getDeviceIndentifier() {
        return deviceIdentifier;
    }

    public static void setParameter(WaniParameter parameter2) {
        parameter = parameter2;
    }

    public static void setDeviceIdentifier(String deviceIdentifier2) {
        deviceIdentifier = deviceIdentifier2;
    }

    public static void setPrinterAddress(String printerAddress2) {
        printerAddress = printerAddress2;
    }

    public static boolean isBackup() {
        return backup;
    }

    public static void setBackup(boolean backup2) {
        backup = backup2;
    }
}
