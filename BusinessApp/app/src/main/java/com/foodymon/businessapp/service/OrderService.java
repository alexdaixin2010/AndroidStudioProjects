package com.foodymon.businessapp.service;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;

import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.StoreStaff;
import com.foodymon.businessapp.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by alexdai on 4/17/16.
 */
public class OrderService {


    public static void getLiteOrderList(String storeId, String status,
                                          final UICallBack<LiteOrderList> callBack) {

        String[] params = new String[]{"storeId", storeId, "status", status, "subStatus","SUBMITTED"};
        new TaskRunner<String, LiteOrderList>(new Task<String, LiteOrderList>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public LiteOrderList doInBackground(String[] params) {
                LiteOrderList orderList = HttpUtils.get("/bp/liteOrderList", params, null, LiteOrderList.class);
                return orderList;
            }

            @Override
            public void onPostExecute(@Nullable LiteOrderList orderList) {
                callBack.onPostExecute(orderList);

            }
        }).execute(params);
    }

    public static void getUnpaidLiteOrderList(String storeId, final UICallBack<LiteOrderList> callBack) {
        getLiteOrderList(storeId, "UNPAID", callBack);
    }

    public static <T> void getOrder(String orderId, String subOrderId,
                                    final UICallBack<Order> callBack) {

        String[] params = new String[]{"orderId", orderId, "subId", subOrderId};
        new TaskRunner<String, Order>(new Task<String, Order>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Order doInBackground(String[] params) {
                Order order = HttpUtils.get("/bp/orderDetails", params, null, Order.class);
                return order;
            }

            @Override
            public void onPostExecute(@Nullable Order order) {
                callBack.onPostExecute(order);

            }
        }).execute(params);
    }

    public static void acceptOrder(final String orderId, final String subId, final String userId, final Order order, final UICallBack<Boolean> callBack) {

        String[] params = new String[]{"operator", userId};
        new TaskRunner<String, Boolean>(new Task<String, Boolean>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Boolean doInBackground(String[] params) {
                byte[] body = new byte[0];

                if(order != null) {
                    try {
                        Gson gson = new Gson();
                        String json = gson.toJson(order);
                        body = json.getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                    }
                }
                Boolean response = HttpUtils.post("/bp/" + orderId + "/accept/" + subId, params,null, body, Boolean.class);
                return response;
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(params);
    }

    public static void rejectOrder(final String orderId, final String subId, final String userId, final UICallBack<Boolean> callBack) {

        String[] params = new String[]{"operator", userId};
        new TaskRunner<String, Boolean>(new Task<String, Boolean>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Boolean doInBackground(String[] params) {
                Boolean response = HttpUtils.post("/bp/" + orderId + "/reject/"+subId, params, null, new byte[0], Boolean.class);
                return response;
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(params);
    }

}
