package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 5/22/16.
 */
public abstract class BaseOrder implements Parcelable {
    private String orderId;
    private Customer customer;
    private String status;
    private String createdTime;
    private String table = "";
    private String orderType;
    private String operator = "";

    protected BaseOrder(Parcel in) {
        orderId = in.readString();
        status = in.readString();
        createdTime = in.readString();
        table = in.readString();
        orderType = in.readString();
        customer = in.readParcelable(Customer.class.getClassLoader());
        operator = in.readString();
    }

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

    public Customer getCustomer () {
        return customer;
    }

    public String getOperator() {
        return operator;
    }
}
