package com.frank.lib.utils;


import android.util.Base64;

import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 默认Base64响应转换器
 */
public class CObservableTransformer implements ObservableTransformer<String, CheckUpdateInfo> {
    @Override
    public ObservableSource apply(Observable observable) {
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<String,ObservableSource<CheckUpdateInfo>>() {
                    @Override
                    public ObservableSource<CheckUpdateInfo> apply(String str) throws Exception {
                        CheckUpdateInfo updateInfo = new Gson().fromJson(String.valueOf(Base64.decode(str.getBytes(),0)),CheckUpdateInfo.class);
                        if(updateInfo.code==200){
                            return Observable.just(updateInfo);
                        }else{
                            return Observable.error(new Throwable(updateInfo.msg));
                        }
                    }

                }).onErrorResumeNext(new Function<Throwable, ObservableSource<CheckUpdateInfo>>() {
                    @Override
                    public ObservableSource<CheckUpdateInfo> apply(Throwable throwable) throws Exception {
                        if (throwable.getMessage().equals("HTTP 401 Unauthorized")) {
                            return Observable.empty();
                        }
                        return Observable.error(throwable);
                    }
                });
    }
}
