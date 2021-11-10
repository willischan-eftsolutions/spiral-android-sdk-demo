package com.eftsolutions.integratedpaymentsolutiondemo.util;

import com.eftsolutions.integratedpaymentsolutiondemo.payment.PaymentMethod;
import com.eftsolutions.integratedpaymentsolutiondemo.retrofit.GetSessionIdApi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SessionIdUtils {

    public interface ResultCallback {

        void onSuccess(String sessionID);

        void onFailure(String errMsg);
    }

    public static void getSessionID(PaymentMethod paymentMethod, String amount, ResultCallback resultCallback) {
        Retrofit retrofit = RetrofitUtils.getRetrofit("http://xmo.exw.mybluehost.me/samples/");
        GetSessionIdApi getSessionIdApi = retrofit.create(GetSessionIdApi.class);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("type", paymentMethod.getType());
        queryParams.put("amount", amount);
        queryParams.put("channel", "APP");
        Call<ResponseBody> getSessionIdApiCall = getSessionIdApi.getSessionId(queryParams);
        getSessionIdApiCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        resultCallback.onSuccess(response.body().string());
                    } else {
                        resultCallback.onFailure("Get Session ID Failure: HTTP " + response.code());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                resultCallback.onFailure("Get Session ID Failure: " + RetrofitUtils.getErrMsg(t));
            }
        });
    }
}
