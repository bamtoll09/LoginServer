package me.bamtoll.lee.loginserver.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.CookieManager;

import me.bamtoll.lee.loginserver.DATA;
import me.bamtoll.lee.loginserver.retrofit.interceptor.AddCookiesInterceptor;
import me.bamtoll.lee.loginserver.retrofit.interceptor.CookiePreference;
import me.bamtoll.lee.loginserver.retrofit.interceptor.ReceiveCookiesInterceptor;
import me.bamtoll.lee.loginserver.retrofit.kook.PersistentCookieStore;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Transceiver {
    private static Transceiver instance = null;

    private static final long CONNECT_TIMEOUT = 100L;
    private static final long WRITE_TIMEOUT = 100L;
    private static final long READ_TIMEOUT = 100L;

    private PersistentCookieStore cookieStore;
    private CookieManager cookieManager;

    private OkHttpClient client;
    public Retrofit retrofit;
    private Gson gson;

    private Transceiver() {
    }

    public static void init(Context context) {
        CookiePreference.create(context);
        // instance.cookieStore = new PersistentCookieStore(context.getApplicationContext());
        // instance.cookieManager = new CookieManager(instance.cookieStore, CookiePolicy.ACCEPT_ALL);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        instance.client = new OkHttpClient.Builder()
                .addInterceptor(new AddCookiesInterceptor())
                // .addInterceptor(interceptor)
                .addInterceptor(new ReceiveCookiesInterceptor())
                .addNetworkInterceptor(new ReceiveCookiesInterceptor())
                .build();
        /*instance.client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(instance.cookieManager))
                .build();*/
        instance.retrofit = new Retrofit.Builder()
                .baseUrl("http://" + DATA.URL + "/")
                .addConverterFactory(GsonConverterFactory.create(instance.gson))
                .client(instance.client)
                .build();
    }

    public static Transceiver getInstance() {
        if (instance == null) {
            instance = new Transceiver();
            instance.gson = new GsonBuilder().setLenient().create();
        }
        return instance;
    }

    public static Transceiver getInstance(Context context) {
        if (instance == null) {
            instance = new Transceiver();
            instance.gson = new GsonBuilder().setLenient().create();

            init(context);
        }
        return instance;
    }
}
