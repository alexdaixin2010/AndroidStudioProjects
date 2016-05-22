package com.foodymon.businessapp.datastructure;

import android.os.Parcel;

/**
 * Created by alexdai on 5/21/16.
 */
public class LitePayment extends BaseOrder{
    private String billId;
    private double totalAmount;
    private double tip;

    protected LitePayment(Parcel in) {
        super(in);
        billId = in.readString();
        totalAmount = in.readDouble();
        tip = in.readDouble();
    }

    public static final Creator<LitePayment> CREATOR = new Creator<LitePayment>() {
        @Override
        public LitePayment createFromParcel(Parcel in) {
            return new LitePayment(in);
        }

        @Override
        public LitePayment[] newArray(int size) {
            return new LitePayment[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(billId);
        dest.writeDouble(totalAmount);
        dest.writeDouble(tip);
    }

    public String getBillId() {
        return billId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public double getTip() {
        return tip;
    }
}
