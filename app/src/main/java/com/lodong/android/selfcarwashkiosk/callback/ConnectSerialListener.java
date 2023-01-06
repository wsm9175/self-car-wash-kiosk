package com.lodong.android.selfcarwashkiosk.callback;

import android.system.ErrnoException;

public interface ConnectSerialListener {
    public void onSuccess() throws ErrnoException;
    public void onFailed() throws ErrnoException;
}
