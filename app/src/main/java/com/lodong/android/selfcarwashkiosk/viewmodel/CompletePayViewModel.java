package com.lodong.android.selfcarwashkiosk.viewmodel;

import android.app.Activity;

import com.lodong.android.selfcarwashkiosk.serial.SerialManager;
import com.lodong.android.selfcarwashkiosk.view.CompletePayActivity;

import java.lang.ref.WeakReference;

public class CompletePayViewModel {

    private WeakReference<Activity> mActivity;

    public CompletePayViewModel(){}

    public void setParent(Activity parent){
        this.mActivity = new WeakReference<>(parent);
    }



}
