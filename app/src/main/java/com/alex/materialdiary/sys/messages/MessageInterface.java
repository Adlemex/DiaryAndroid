package com.alex.materialdiary.sys.messages;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MessageInterface {
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    @POST("direct")
    Call<ResponseBody> getDirect(@Body RequestBody params);
}