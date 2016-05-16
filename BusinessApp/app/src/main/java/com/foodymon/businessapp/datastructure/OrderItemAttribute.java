package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by alexdai on 4/17/16.
 */
public class OrderItemAttribute implements Parcelable {
    String code;
    String name;
    List<String> valueCodes;
    List<String> values;

    protected OrderItemAttribute(Parcel in) {
        code = in.readString();
        name = in.readString();
        valueCodes = in.createStringArrayList();
        values = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
        dest.writeStringList(valueCodes);
        dest.writeStringList(values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderItemAttribute> CREATOR = new Creator<OrderItemAttribute>() {
        @Override
        public OrderItemAttribute createFromParcel(Parcel in) {
            return new OrderItemAttribute(in);
        }

        @Override
        public OrderItemAttribute[] newArray(int size) {
            return new OrderItemAttribute[size];
        }
    };

    public List<String> getValues() {
        return values;
    }

    public List<String> getValueCodes() {
        return valueCodes;
    }

    public String getCode() {
        return code;
    }
    public String getName() {
        return name;
    }
}
