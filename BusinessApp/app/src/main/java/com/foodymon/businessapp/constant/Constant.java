package com.foodymon.businessapp.constant;

/**
 * Created by alexdai on 4/8/16.
 */
public class Constant {

    public static class User {
        public enum USERTYPE {
            STORE_STAFF,
            MANAGE,
            OWNER,
        }

        public enum STATUS {
            ACTIVE,
            INACTIVE
        }
    }

    public static final String SENDER_ID = "1003628376366";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String ORDER_UPDATE = "orderUpdateNotification";

    public static final String ORDER_REFRESH_DONE = "orderRefreshDone";

    public static final String KEY = "key";
    public static final String TOPIC = "topic";
    public static final String SUBSCRIBE = "subscribe";
    public static final String UNSUBSCRIBE = "unsubscribe";

    public static final String INTENT_FROM = "intentFrom";
    public static final String INTENT_ACTION = "intentAction";

    public static final String ORDER_FRAGMENT = "orderFragment";

    public static final String SHOW_LOADING = "showLoading";
    public static final String HIDE_LOADING = "hideLoading";

    public static final int ORDER_DETAIL_REQUEST = 1;


}
