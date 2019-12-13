package com.frank.lib.utils;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CApiService {

    String WB_A = "web/user/getAppMsg/{id}";


    @GET(WB_A)
    Observable<ACheckUpdateInfo> checkUpdate(@Path("id") String id);
}
