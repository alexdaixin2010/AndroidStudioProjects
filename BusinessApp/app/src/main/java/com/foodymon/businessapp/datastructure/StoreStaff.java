package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 4/8/16.
 */
public class StoreStaff {
    private String userId;
    private String fistName;
    private String lastName;
    private String email;
    private int telephone;
    private String profilePic;
    private String password;
    private String dob;
    private String status;
    private String userType;


    public String toString() {
        return this.userId + ", "
            + this.fistName + ", "
            + this.lastName + ", "
            + this.status+ ", "
            + this.userType+ ".";
    }

}
