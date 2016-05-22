package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexdai on 5/21/16.
 */
public class LitePaymentList implements Parcelable{

    public final static String BUNDLE_NAME = "LitePayListBundle";

    List<LitePayment> payments;

    protected LitePaymentList(Parcel in) {
        payments = new ArrayList<>();
        in.readTypedList(payments, LitePayment.CREATOR);
    }

    public static final Parcelable.Creator<LitePaymentList> CREATOR = new Parcelable.Creator<LitePaymentList>() {
        @Override
        public LitePaymentList createFromParcel(Parcel in) {
            return new LitePaymentList(in);
        }

        @Override
        public LitePaymentList[] newArray(int size) {
            return new LitePaymentList[size];
        }
    };

    public List<LitePayment> getPayments() {
        return payments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(payments);
    }
}
