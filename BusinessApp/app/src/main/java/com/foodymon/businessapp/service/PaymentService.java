package com.foodymon.businessapp.service;

import android.support.annotation.Nullable;

import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.LitePaymentList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.Payment;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.utils.HttpUtils;

import java.util.HashMap;

/**
 * Created by alexdai on 5/22/16.
 */
public class PaymentService {

    public static void getPaymentList(final String storeId, final String status, final UICallBack<LitePaymentList> callBack, final BusinessApplication app) {

        new TaskRunner<String, LitePaymentList>(new Task<String, LitePaymentList>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public LitePaymentList doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("storeId", storeId);
                paramMap.put("status", status);
                LitePaymentList payList = HttpUtils.get("/bp/paymentList", paramMap, null, LitePaymentList.class, app);
                return payList;
            }

            @Override
            public void onPostExecute(@Nullable LitePaymentList payList) {
                callBack.onPostExecute(payList);

            }
        }).execute(null);
    }

    public static void getPaidRequestList(String storeId, final UICallBack<LitePaymentList> callBack, BusinessApplication app) {
        getPaymentList(storeId, Constants.ORDER_REQUEST_PAY, callBack, app);
    }

    public static void getPaidList(String storeId, final UICallBack<LitePaymentList> callBack, BusinessApplication app) {
        getPaymentList(storeId, Constants.ORDER_PAID, callBack, app);
    }

    public static void getUnPaidList(String storeId, final UICallBack<LitePaymentList> callBack, BusinessApplication app) {
        getPaymentList(storeId, Constants.ORDER_UNPAID, callBack, app);
    }

    public static void getAllLiteOrderList(String storeId, final UICallBack<LitePaymentList> callBack, BusinessApplication app) {
        getPaymentList(storeId, null, callBack, app);
    }

    public static <T> void getPayment(final String paymentId, final UICallBack<Payment> callBack, final BusinessApplication app) {
        new TaskRunner<String, Order>(new Task<String, Payment>() {
            @Override
            public void onPreExecute() {
                callBack.onPreExecute();
            }

            @Override
            @Nullable
            public Payment doInBackground(String[] params) {
                HashMap<String, String> paramMap = new HashMap<>();
                paramMap.put("paymentId", paymentId);
                Payment payment = HttpUtils.get("/bp/paymentDetails", paramMap, null, Payment.class, app);
                return payment;
            }

            @Override
            public void onPostExecute(@Nullable Payment payment) {
                callBack.onPostExecute(payment);
            }
        }).execute(null);
    }
}
