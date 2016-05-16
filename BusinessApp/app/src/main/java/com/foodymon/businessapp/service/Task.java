package com.foodymon.businessapp.service;

import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.concurrent.Callable;

/**
 * Created by alexdai on 4/8/16.
 */
public interface Task<T, V> {

    // UI thread
    void onPreExecute();

    //Non UI thread
    @Nullable
    V doInBackground(T[] params);

    //UI thread
    void onPostExecute(@Nullable V v);
}
