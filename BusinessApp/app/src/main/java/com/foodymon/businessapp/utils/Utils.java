package com.foodymon.businessapp.utils;

import android.support.annotation.Nullable;

import com.foodymon.businessapp.datastructure.StoreStaff;

/**
 * Created by alexdai on 4/8/16.
 */
public class Utils {


    public static boolean isValidUser(@Nullable StoreStaff user) {
        if(user != null) {
            return true;
        }
        return false;
    }
}
