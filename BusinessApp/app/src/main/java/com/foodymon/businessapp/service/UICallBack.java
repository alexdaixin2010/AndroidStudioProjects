package com.foodymon.businessapp.service;

import com.foodymon.businessapp.datastructure.StoreStaff;

/**
 * Created by alexdai on 4/17/16.
 */
public interface UICallBack<T> {

    void onPreExecute();

    void onPostExecute(T t);
}
