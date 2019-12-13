package com.frank.lib.utils;


import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    String WB_A = "back/api.php";


    @GET(WB_A)
    Observable<CheckUpdateInfo> checkUpdate(@Query("app_id") String id);

}
