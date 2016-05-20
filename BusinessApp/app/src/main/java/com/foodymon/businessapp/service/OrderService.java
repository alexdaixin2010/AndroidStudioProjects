package com.foodymon.businessapp.service;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.utils.HttpUtils;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by alexdai on 4/17/16.
 */
public class OrderService {



    public static void getLiteOrderList(final String storeId, final String status, final String subStatus,
                                        final UICallBack<LiteOrderList> callBack, final BusinessApplication app) {

        new TaskRunner<String, LiteOrderList>(new Task<String, LiteOrderList>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public LiteOrderList doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("storeId", storeId);
                paramMap.put("status", status);
                paramMap.put("subStatus",subStatus);
                LiteOrderList orderList = HttpUtils.get("/bp/liteOrderList", paramMap, null, LiteOrderList.class, app);
                return orderList;
            }

            @Override
            public void onPostExecute(@Nullable LiteOrderList orderList) {
                callBack.onPostExecute(orderList);

            }
        }).execute(null);
    }

    public static void getSubmittedLiteOrderList(String storeId, final UICallBack<LiteOrderList> callBack, BusinessApplication app) {
        getLiteOrderList(storeId, Constants.ORDER_UNPAID, Constants.SUB_ORDER_SUBMITTED, callBack, app);
    }

    public static void getIPLiteOrderList(String storeId, final UICallBack<LiteOrderList> callBack, BusinessApplication app) {
        getLiteOrderList(storeId, Constants.ORDER_UNPAID, Constants.SUB_ORDER_IN_PROCESS, callBack, app);
    }

    public static void getAllLiteOrderList(String storeId, final UICallBack<LiteOrderList> callBack, BusinessApplication app) {
        getLiteOrderList(storeId, Constants.ORDER_UNPAID, null, callBack, app);
    }

    public static <T> void getOrder(final String orderId, final String subOrderId,
                                    final UICallBack<Order> callBack, final BusinessApplication app) {
        new TaskRunner<String, Order>(new Task<String, Order>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Order doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("orderId", orderId);
                paramMap.put("subId",  subOrderId);
                Order order = HttpUtils.get("/bp/orderDetails", paramMap, null, Order.class, app);
                return order;
            }

            @Override
            public void onPostExecute(@Nullable Order order) {
                callBack.onPostExecute(order);

            }
        }).execute(null);
    }

    public static void acceptOrder(final String orderId, final String subId, final String userId, final Order order, final UICallBack<Boolean> callBack, final BusinessApplication app) {

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
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("operator", userId);
                Boolean response = HttpUtils.post("/bp/" + orderId + "/accept/" + subId, paramMap,null, null, body, Boolean.class, app);
                return response;
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(null);
    }

    public static void rejectOrder(final String orderId, final String subId, final String userId, final UICallBack<Boolean> callBack, final BusinessApplication app) {

        new TaskRunner<String, Boolean>(new Task<String, Boolean>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Boolean doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("operator", userId);
                Boolean response = HttpUtils.post("/bp/" + orderId + "/reject/"+subId, paramMap, null, null, new byte[0],Boolean.class, app);
                return response;
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(null);
    }

    public static void lockOrder(final String orderId, final String subId, final String userId, final boolean isLock, final UICallBack<Boolean> callBack, final BusinessApplication app) {

        new TaskRunner<String, Boolean>(new Task<String, Boolean>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Boolean doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("operator", userId);
                paramMap.put("lock", isLock?"true":"false");
                Boolean response = HttpUtils.post("/bp/"+orderId+"/lock/"+subId, paramMap, null, null, new byte[0],Boolean.class, app);
                return response;
            }

            @Override
            public void onPostExecute(Boolean response) {
                callBack.onPostExecute(response);

            }
        }).execute(null);
    }

}
