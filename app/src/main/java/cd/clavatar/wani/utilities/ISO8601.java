package cd.clavatar.wani.utilities;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Helper class for handling ISO 8601 strings of the following format:
 * "2008-03-01T13:00:00+01:00". It also supports parsing the "Z" timezone.
 */
public final class ISO8601 {
    /** Transform Calendar to ISO 8601 string. */
    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
            .format(date);
        return formatted.substring(0, 26) + ":" + formatted.substring(26);
    }

    /** Get current date and time formatted as ISO 8601 string. */
    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

    /** Transform ISO 8601 string to Calendar. 
     * @throws ParseException */
    public static Calendar toCalendar(final String iso8601string)
            throws ParseException {
        Calendar calendar = GregorianCalendar.getInstance();
        String p=iso8601string.substring(0,19)+"Z";
        String s = p.replace("Z", "+00:00");
        try {
            s = s.substring(0, 22) + s.substring(23);  // to get rid of the ":"
        } catch (IndexOutOfBoundsException e) {

        }
        Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).parse(s);
        calendar.setTime(date);
        return calendar;
    }

    public static String getFormateddate(Date date){
        String fd=null;
        if(date!=null){
            Log.d("zaza",String.format("date %s",date));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy  HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Africa/Kinshasa"));
            fd=sdf.format(date);
        }

        //

        return fd;
        //return "yess";
    }

}