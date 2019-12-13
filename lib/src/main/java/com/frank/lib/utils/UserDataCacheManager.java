package com.frank.lib.utils;


import android.content.Context;

public class UserDataCacheManager {

    private UserDataCacheManager() {
    }

    public static class CacheHolder {
        private static UserDataCacheManager mInstance = new UserDataCacheManager();

        public static UserDataCacheManager getInstance() {
            return mInstance;
        }
    }

    public static UserDataCacheManager getInstance() {
        return CacheHolder.getInstance();
    }


    public void saveUserLookYSSTatus(Context context,boolean isLook) {
        SpManager.getInstance(context).put("islook", isLook);
}

    public boolean getUserLookYSSTatus(Context context) {
        return SpManager.getInstance(context).get("islook",false);
    }


}
