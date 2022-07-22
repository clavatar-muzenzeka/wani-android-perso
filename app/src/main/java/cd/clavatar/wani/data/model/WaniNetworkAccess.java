package cd.clavatar.wani.data.model;

import android.content.Context;
import com.google.common.net.HttpHeaders;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
//import okhttp3.logging.HttpLoggingInterceptor;
import cd.clavatar.wani.WaniApp;
import cd.clavatar.wani.utils.HttpClientService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/* renamed from: cd.clavatar.wani.data.model.WaniNetworkAccess */
public class WaniNetworkAccess {
    public static String BASE_URL = "https://admin.wani-sante.com";

    //public static String BASE_URL = "http://192.168.43.106:8080";
    //public static String BASE_URL = "http://10.0.2.2:8080";
    //public static String BASE_URL = "http://206.189.184.155:8080";
    private static WaniNetworkDataSource INSTANCE;
    private static WaniNetworkAccess SELF_INSTANCE;
    public static String TOKEN;
    private static Retrofit retrofit;

    public static WaniNetworkDataSource getInstanceForToken(String providedToken, Context context) {
        WaniNetworkAccess waniNetworkAccess = SELF_INSTANCE;
        if (waniNetworkAccess == null || waniNetworkAccess.getToken() == null || !SELF_INSTANCE.getToken().equals(providedToken)) {
            String token = providedToken == null ? TOKEN : providedToken;
            synchronized (WaniNetworkAccess.class) {
                Retrofit build = new Retrofit.Builder().client(_getHttpClient(token, context)).baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
                retrofit = build;
                INSTANCE = (WaniNetworkDataSource) build.create(WaniNetworkDataSource.class);
                WaniNetworkAccess waniNetworkAccess2 = new WaniNetworkAccess();
                SELF_INSTANCE = waniNetworkAccess2;
                waniNetworkAccess2.setToken(providedToken);
            }
        }
        return INSTANCE;
    }

    private Object getToken() {
        return TOKEN;
    }

    private void setToken(String providedToken) {
        TOKEN = providedToken;
    }

    private static OkHttpClient _getHttpClient(final String token, Context context) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);
        //new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
        new OkHttpClient.Builder().connectTimeout(600, TimeUnit.SECONDS).dispatcher(dispatcher).addInterceptor(new Interceptor() {
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                return chain.proceed(original.newBuilder().header(HttpHeaders.USER_AGENT, "cd.clavatar.wani").header(HttpHeaders.ACCEPT, "application/json").header(HttpHeaders.AUTHORIZATION, token).header("device", WaniApp.getDeviceIndentifier()).method(original.method(), original.body()).build());
            }
        });
        return HttpClientService.getUnsafeOkHttpClient(token, context);
    }

    public static void updateToken(String providedToken) {
        TOKEN = providedToken;
    }
}
