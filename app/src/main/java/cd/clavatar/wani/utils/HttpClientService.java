package cd.clavatar.wani.utils;

import android.content.Context;

import com.google.common.net.HttpHeaders;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cd.clavatar.wani.WaniApp;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//import okhttp3.logging.HttpLoggingInterceptor;

/* renamed from: cd.clavatar.wani.utils.HttpClientService */
public class HttpClientService {
    public static OkHttpClient getUnsafeOkHttpClient(final String token, Context context) {
        try {
            TrustManager[] trustAllCerts = {new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init((KeyManager[]) null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            Dispatcher dispatcher = new Dispatcher();
            dispatcher.setMaxRequests(100);
            dispatcher.setMaxRequestsPerHost(10);
            //.addInterceptor(new HttpLoggingInterceptor())
            return new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory).connectTimeout(60, TimeUnit.SECONDS).readTimeout(1800, TimeUnit.SECONDS).retryOnConnectionFailure(true).dispatcher(dispatcher).hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).addInterceptor(new Interceptor() {
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    return chain.proceed(original.newBuilder().header(HttpHeaders.CONNECTION, "close").header(HttpHeaders.USER_AGENT, "cd.clavatar.wani").header(HttpHeaders.ACCEPT, "application/json").header(HttpHeaders.AUTHORIZATION, token).header("device", WaniApp.getDeviceIndentifier()).method(original.method(), original.body()).build());
                }
            }).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
