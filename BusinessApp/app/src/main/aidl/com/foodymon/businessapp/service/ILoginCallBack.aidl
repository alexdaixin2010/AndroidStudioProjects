// UICallBack.aidl
package com.foodymon.businessapp.service;

import com.foodymon.businessapp.datastructure.StoreStaff;

// Declare any non-default types here with import statements

interface ILoginCallBack {
    void preCallBack();

    void postCallBack(in StoreStaff user);
}
