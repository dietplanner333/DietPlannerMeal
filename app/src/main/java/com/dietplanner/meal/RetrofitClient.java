package com.dietplanner.meal;

import java.security.cert.CertificateException;
import javax.net.ssl.*;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit restRetrofit = null;
    private static Retrofit rpcRetrofit = null;

    public static Retrofit getRestClient() {
        if (restRetrofit == null) {
            restRetrofit = createClient(Constants.SUPABASE_REST);
        }
        return restRetrofit;
    }

    public static Retrofit getRpcClient() {
        if (rpcRetrofit == null) {
            rpcRetrofit = createClient(Constants.SUPABASE_RPC);
        }
        return rpcRetrofit;
    }

    private static Retrofit createClient(String baseUrl) {
        try {
            // Trust all SSL certificates
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}
                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                    }
            };
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Logging
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Header interceptor
            Interceptor headerInterceptor = chain -> {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder()
                        .header("apikey", Constants.SUPABASE_ANON_KEY)
                        .header("Authorization", "Bearer " + Constants.SUPABASE_ANON_KEY)
                        .method(original.method(), original.body());
                return chain.proceed(builder.build());
            };

            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .addInterceptor(headerInterceptor)
                    .addInterceptor(logging)
                    .build();

            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
