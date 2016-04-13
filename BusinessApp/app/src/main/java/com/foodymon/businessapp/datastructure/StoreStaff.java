package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 4/8/16.
 */
public class StoreStaff implements Parcelable {
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

    public StoreStaff() {
    }

    protected StoreStaff(Parcel in) {
        userId = in.readString();
        fistName = in.readString();
        lastName = in.readString();
        email = in.readString();
        telephone = in.readInt();
        profilePic = in.readString();
        password = in.readString();
        dob = in.readString();
        status = in.readString();
        userType = in.readString();
    }

    public static final Creator<StoreStaff> CREATOR = new Creator<StoreStaff>() {
        @Override
        public StoreStaff createFromParcel(Parcel in) {
            return new StoreStaff(in);
        }

        @Override
        public StoreStaff[] newArray(int size) {
            return new StoreStaff[size];
        }
    };

    public String toString() {
        return this.userId + ", "
            + this.fistName + ", "
            + this.lastName + ", "
            + this.status+ ", "
            + this.userType+ ".";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(fistName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeInt(telephone);
        dest.writeString(profilePic);
        dest.writeString(password);
        dest.writeString(dob);
        dest.writeString(status.toString());
        dest.writeString(userType.toString());
    }
}
