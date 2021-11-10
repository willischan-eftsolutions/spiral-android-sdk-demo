package com.eftsolutions.integratedpaymentsolutiondemo.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitUtils {

    private static OkHttpClient okHttpClient;

    public static Retrofit getRetrofit(String url) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);

            okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(httpLoggingInterceptor)
                    .addNetworkInterceptor(new Interceptor() {
                        @NotNull
                        @Override
                        public Response intercept(@NotNull Chain chain) throws IOException {
                            Request request = chain.request().newBuilder().addHeader("Cache-Control", "no-cache")
                                    .removeHeader("Connection")
                                    .addHeader("Connection", "close").build();
                            return chain.proceed(request);
                        }
                    })
                    .build();
        }
        return okHttpClient;
    }

    public static String getErrMsg(Throwable throwable) {
        if (throwable == null) {
            return "Null Throwable";
        } else if (throwable instanceof SocketTimeoutException) {
            return "Socket Timeout";
        } else if (throwable instanceof UnknownHostException) {
            return "Unknown Host";
        } else if (throwable instanceof UnknownServiceException) {
            return "Unknown Service";
        } else if (throwable instanceof IOException) {
            return "Network Error";
        } else {
            return throwable.toString();
        }
    }
}
