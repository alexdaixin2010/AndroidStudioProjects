package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 4/8/16.
 */
public class StoreStaff {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private String profilePic;
    private String password;
    private String userType;

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }


}
