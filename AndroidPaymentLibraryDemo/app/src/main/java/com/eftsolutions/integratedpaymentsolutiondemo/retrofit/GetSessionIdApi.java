package com.eftsolutions.integratedpaymentsolutiondemo.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface GetSessionIdApi {
    @GET("create-order.php")
    Call<ResponseBody> getSessionId(@QueryMap Map<String, String> queryParams);
}
