package com.frank.lib.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class BannerInfo implements Serializable {


    /**
     * 响应码
     */
    public int code;

    /**
     *  数据
     */
    public BannerData data;

    public class BannerData {
        public String appid;
        public String download_link;
        public boolean active;
        public ArrayList<Images> images;
    }

    public class Images{
        public String banner_url;
        public String down_url;
    }


}
