package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by alexdai on 4/17/16.
 */
public class LiteOrder implements Parcelable {
    private String orderId;
   // private Customer customer;
    private String status;
    private String createdTime;
    private String table;
    private String type;
    private String subId;

    protected LiteOrder(Parcel in) {
        orderId = in.readString();
        status = in.readString();
        createdTime = in.readString();
        table = in.readString();
        type = in.readString();
        subId = in.readString();
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

    public String getType() {
        return type;
    }

    public String getSub_id() {
        return subId;
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
        dest.writeString(type);
        dest.writeString(subId);
    }
}
