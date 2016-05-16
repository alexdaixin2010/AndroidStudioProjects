package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexdai on 4/17/16.
 */
public class Order implements Parcelable{

    public final static String BUNDLE_NAME = "OrderBundle";

    private LiteOrder liteOrder;

    private List<OrderItem> items;

    protected Order(Parcel in) {
        liteOrder = in.readParcelable(LiteOrder.class.getClassLoader());
        items = in.createTypedArrayList(OrderItem.CREATOR);
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
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
        dest.writeParcelable(liteOrder, flags);
        dest.writeTypedList(items);
    }

    public LiteOrder getLiteOrder(){
        return liteOrder;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
