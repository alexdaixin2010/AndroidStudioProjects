package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by alexdai on 4/17/16.
 */
public class LiteOrder implements Parcelable {
    private String orderId;
    private Customer customer;
    private String status;
    private String createdTime;
    private String table = "N/A";
    private String orderType;
    private String subId;
    private String operator = "N/A";

    protected LiteOrder(Parcel in) {
        orderId = in.readString();
        status = in.readString();
        createdTime = in.readString();
        table = in.readString();
        subId = in.readString();
        orderType = in.readString();
        customer = in.readParcelable(Customer.class.getClassLoader());
        operator = in.readString();
    }

    public static final Creator<LiteOrder> CREATOR = new Creator<LiteOrder>() {
        @Override
        public LiteOrder createFromParcel(Parcel in) {
            return new LiteOrder(in);
        }

        @Override
        public LiteOrder[] newArray(int size) {
            return new LiteOrder[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(status);
        dest.writeString(createdTime);
        dest.writeString(table);
        dest.writeString(subId);
        dest.writeString(orderType);
        dest.writeParcelable(customer, flags);
        dest.writeString(operator);
    }


    public String getStatus(){
        return this.status;
    }

    public String getOrderId() {
        return orderId;
    }


    public String getCreatedTime() {
        return createdTime;
    }

    public String getTable() {
        return table;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getSubId() {
        return subId;
    }

    public Customer getCustomer () {
        return customer;
    }

    public String getOperator() {
        return operator;
    }
}
