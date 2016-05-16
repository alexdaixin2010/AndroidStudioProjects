package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexdai on 4/17/16.
 */
public class LiteOrderList implements Parcelable {

    public final static String BUNDLE_NAME = "LiteOrderListBundle";

    List<LiteOrder> orders;

    protected LiteOrderList(Parcel in) {
        orders = new ArrayList<>();
        in.readTypedList(orders, LiteOrder.CREATOR);
    }

    public static final Creator<LiteOrderList> CREATOR = new Creator<LiteOrderList>() {
        @Override
        public LiteOrderList createFromParcel(Parcel in) {
            return new LiteOrderList(in);
        }

        @Override
        public LiteOrderList[] newArray(int size) {
            return new LiteOrderList[size];
        }
    };

    public List<LiteOrder> getOrders() {
        return orders;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(orders);
    }
}
