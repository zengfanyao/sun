package com.frank.lib.utils;

import java.io.Serializable;

public class ACheckUpdateInfo implements Serializable {


    public String status;
    public String msg;
    public String uploadPath;
    public Result result;

    public class Result {
        public String name;
        public String vs;
        public String url;
        public String ud;

    }
}
