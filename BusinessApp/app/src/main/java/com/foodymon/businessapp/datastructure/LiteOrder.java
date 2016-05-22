package com.foodymon.businessapp.datastructure;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by alexdai on 4/17/16.
 */
public class LiteOrder extends BaseOrder {
    private String subId;

    protected LiteOrder(Parcel in) {
        super(in);
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(subId);

    }

    public String getSubId() {
        return subId;
    }

}
