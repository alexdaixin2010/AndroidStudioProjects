package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by alexdai on 4/17/16.
 */
public class OrderItem implements Parcelable{

    String itemCode;
    //i18n name;
    String itemName;

    int quantity;

    double price;

    List<OrderItemAttribute> attributes;

    //Additional request;
    String request;

    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public List<OrderItemAttribute> getAttributes() {
        return attributes;
    }

    protected OrderItem(Parcel in) {
        itemCode = in.readString();
        itemName = in.readString();
        quantity = in.readInt();
        price = in.readDouble();
        attributes = in.createTypedArrayList(OrderItemAttribute.CREATOR);
        request = in.readString();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemCode);
        dest.writeString(itemName);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeTypedList(attributes);
        dest.writeString(request);
    }
}
