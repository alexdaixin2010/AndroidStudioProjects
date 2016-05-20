package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 4/17/16.
 */
public class Customer implements Parcelable {
    private String userId;
    private String firstName;
    private String lastName;
    private String profilePic;

    protected Customer(Parcel in) {
        userId = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        profilePic = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(profilePic);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
