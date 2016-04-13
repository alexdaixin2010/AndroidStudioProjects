// ILoginService.aidl
package com.foodymon.businessapp.service;

import com.foodymon.businessapp.service.ILoginCallBack;


// Declare any non-default types here with import statements

interface ILoginService {

    void authenticate(String storeId, String userName, String password, in ILoginCallBack callback);

}
