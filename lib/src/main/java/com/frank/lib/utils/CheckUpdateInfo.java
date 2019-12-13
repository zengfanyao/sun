package com.frank.lib.utils;

import java.io.Serializable;

public class CheckUpdateInfo implements Serializable {


    /**
     * 是否需要跳转
     * 0或者1，0代表true ，1代表false
     */
    public String is_wap;

    /**
     * 跳转网页地址
     */
    public String wap_url;

    /**
     * 是否需要更新
     * 0或者1，0代表true ，1代表false
     */
    public String is_update;

    /**
     * 更新地址
     */
    public String update_url;

    /**
     * 响应码
     */
    public int code;

    /**
     * 错误提示
     */
    public String msg;

}
