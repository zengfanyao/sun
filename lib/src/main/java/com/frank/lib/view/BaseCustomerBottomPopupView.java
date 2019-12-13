package com.frank.lib.view;

import android.content.Context;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.BottomPopupView;

public class BaseCustomerBottomPopupView extends BottomPopupView {

    public BaseCustomerBottomPopupView(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        popupInfo.enableDrag = false;

    }

    protected int getPaddingSize(){
        return -35;
    }

}
