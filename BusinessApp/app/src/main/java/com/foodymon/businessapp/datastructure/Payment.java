package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by alexdai on 5/22/16.
 */
public class Payment implements Parcelable{

    public final static String BUNDLE_NAME = "PaymentBundle";

    private LitePayment litePayment;

    private List<OrderItem> items;

    protected Payment(Parcel in) {
        litePayment = in.readParcelable(LitePayment.class.getClassLoader());
        items = in.createTypedArrayList(OrderItem.CREATOR);
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(litePayment, flags);
        dest.writeTypedList(items);
    }

    public LitePayment getLitePayment(){
        return litePayment;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
