package com.frank.lib.view;


import android.content.Context;
//import android.support.annotation.NonNull;
import android.view.View;

import androidx.annotation.NonNull;

import com.frank.lib.R;
import com.frank.lib.WebYXActivity;
import com.frank.lib.utils.UserDataCacheManager;


/**
 * 用户使用提示
 */
public class WXUserTipDialogView extends BaseCustomerBottomPopupView {


    private Context mContext;


    public WXUserTipDialogView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_act_user_tip_dialog;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        popupInfo.isDismissOnBackPressed = false;
        popupInfo.isDismissOnTouchOutside =false;
        this.findViewById(R.id.tv_agree).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDataCacheManager.getInstance().saveUserLookYSSTatus(mContext,true);
            }
        });
        this.findViewById(R.id.act_user_tv_ys).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebYXActivity.startActivity(mContext,"隐私策略", "file:///android_asset/privacy.html");
            }
        });
        this.findViewById(R.id.act_user_tv_xy).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                WebYXActivity.startActivity(mContext,"用户协议", "file:///android_asset/userAgreement.html");
            }
        });
    }




}
