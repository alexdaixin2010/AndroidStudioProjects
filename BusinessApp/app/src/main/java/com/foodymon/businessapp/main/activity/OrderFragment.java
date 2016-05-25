package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.Customer;
import com.foodymon.businessapp.datastructure.LiteOrder;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.main.view.CircleImageView;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.ImageLoader;
import com.foodymon.businessapp.utils.Utils;
//import com.squareup.leakcanary.RefWatcher;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OrderFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private TextView mOrderAllBtn;
    private TextView mOrderSubmittedBtn;
    private TextView mOrderIPBtn;
    private LiteOrderList mLiteOrderList;

    int mUnSelectBtnColor;
    int mSelectedBtnColor;
    boolean orderClickable = true;

    private ORDER_MODE mOrderMode = ORDER_MODE.SUBMITTED;

    enum ORDER_MODE {
        ALL,
        SUBMITTED,
        INPROCESS,
    }

    private BusinessApplication app;

    public static OrderFragment newInstance() {
        OrderFragment fragment = new OrderFragment();
        return fragment;
    }

    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        app = (BusinessApplication) getActivity().getApplication();

        loadResource();

        if(savedInstanceState != null) {
            mLiteOrderList = savedInstanceState.getParcelable(LiteOrderList.BUNDLE_NAME);
            mOrderMode = ORDER_MODE.valueOf(savedInstanceState.getString("orderMode"));
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.order_swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.order_span_count));
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setBackgroundColor(Color.argb(230, 255, 255, 255));

        mOrderAllBtn = (TextView)view.findViewById(R.id.order_btn_all);
        mOrderSubmittedBtn = (TextView)view.findViewById(R.id.order_btn_submit);
        mOrderIPBtn = (TextView)view.findViewById(R.id.order_btn_ip);

        setOrderBtnColor();

        mOrderAllBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mOrderMode == ORDER_MODE.ALL){
                    return false;
                }
                mOrderMode = ORDER_MODE.ALL;
                setOrderBtnColor();
                refreshOrderList(false);
                return true;
            }
        });

        mOrderSubmittedBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mOrderMode == ORDER_MODE.SUBMITTED){
                    return false;
                }
                mOrderMode = ORDER_MODE.SUBMITTED;
                setOrderBtnColor();
                refreshOrderList(false);
                return true;
            }
        });
        mOrderIPBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mOrderMode == ORDER_MODE.INPROCESS){
                    return false;
                }
                mOrderMode = ORDER_MODE.INPROCESS;
                setOrderBtnColor();
                refreshOrderList(false);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mLiteOrderList == null ) {
            Bundle bundle = getActivity().getIntent().getExtras();
            mLiteOrderList = bundle.getParcelable(LiteOrderList.BUNDLE_NAME);
            bundle.remove(LiteOrderList.BUNDLE_NAME);
        }
        mAdapter = new RecyclerViewAdapter(mLiteOrderList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("orderMode", mOrderMode.toString());
        state.putParcelable(LiteOrderList.BUNDLE_NAME, mLiteOrderList);
    }

    @Override
    public void onAttach(Activity activiy) {
        super.onAttach(activiy);
        if (activiy != null) {
            mListener = (OnFragmentInteractionListener) activiy;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRecyclerView.setAdapter(null);
    //    RefWatcher refWatcher = BusinessApplication.getRefWatcher(getActivity());
     //   refWatcher.watch(this);
    }

    private void loadResource() {
        Resources res = getActivity().getResources();
        mUnSelectBtnColor = res.getColor(R.color.light_grey_10);
        mSelectedBtnColor = res.getColor(R.color.light_grey_38);
    }

    public void refreshOrderList(final boolean isSwipe) {
        switch (mOrderMode) {
            case ALL:
                OrderService.getAllLiteOrderList(app.getStoreId(), new UICallBack<LiteOrderList>() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(LiteOrderList liteOrderList) {
                        mLiteOrderList = liteOrderList;
                        // set order list
                        OrderFragment.this.mAdapter.updateOrder(liteOrderList);
                        if (isSwipe) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        notifyActivity(Constants.ORDER_REFRESH_DONE);
                    }
                }, app);
                break;
            case SUBMITTED:
                OrderService.getSubmittedLiteOrderList(app.getStoreId(), new UICallBack<LiteOrderList>() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(LiteOrderList liteOrderList) {
                        // refresh time stamp
                        mLiteOrderList = liteOrderList;

                        // set order list
                        OrderFragment.this.mAdapter.updateOrder(liteOrderList);
                        if (isSwipe) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        notifyActivity(Constants.ORDER_REFRESH_DONE);
                    }
                }, app);
                break;
            case INPROCESS:
                OrderService.getIPLiteOrderList(app.getStoreId(), new UICallBack<LiteOrderList>() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(LiteOrderList liteOrderList) {
                        mLiteOrderList = liteOrderList;
                        // set order list
                        OrderFragment.this.mAdapter.updateOrder(liteOrderList);
                        if (isSwipe) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        notifyActivity(Constants.ORDER_REFRESH_DONE);
                    }
                }, app);
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Intent intent);
    }

    public void notifyActivity(String action) {
        if (mListener != null) {
            Intent intent = new Intent();
            intent.putExtra(Constants.INTENT_FROM, Constants.ORDER_FRAGMENT);
            intent.putExtra(Constants.INTENT_ACTION, action);
            mListener.onFragmentInteraction(intent);
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter {
        private List<LiteOrder> orders = new ArrayList<>();
        private Drawable submit_bg;
        private Drawable accept_bg;
        private Drawable ip_bg;
        private int monkey;
        private long currentTimeStamp;

        private class OrderViewHolder extends RecyclerView.ViewHolder {
            View card;
            LinearLayout orderStatusSection;
            TextView customerName;
            TextView orderType;
            TextView orderId;
            TextView table;
            TextView time;
            TextView status;
            CircleImageView img;

            public OrderViewHolder(View v) {
                super(v);
                card = v;
                orderStatusSection = (LinearLayout) v.findViewById(R.id.order_status_section);
                customerName = (TextView) v.findViewById(R.id.order_customer_name);
                orderType = (TextView) v.findViewById(R.id.order_order_type);
                orderId = (TextView) v.findViewById(R.id.order_id);
                table = (TextView) v.findViewById(R.id.order_table);
                time = (TextView) v.findViewById(R.id.order_time);
                status = (TextView) v.findViewById(R.id.order_status);
                img = (CircleImageView) v.findViewById(R.id.order_user_img);
            }
        }

        RecyclerViewAdapter(LiteOrderList liteOrderList) {
            if(liteOrderList != null && !Utils.isEmpty(liteOrderList.getOrders())) {
                orders.addAll(liteOrderList.getOrders());
            }
            Resources res = getResources();
            submit_bg = res.getDrawable(R.drawable.state_new_background_480);
            accept_bg = res.getDrawable(R.drawable.state_completed_background_480);
            ip_bg = res.getDrawable(R.drawable.state_accepted_background_480);
            monkey = res.getIdentifier("monkey", "drawable", getActivity().getPackageName());
            currentTimeStamp = System.currentTimeMillis();
        }

        void updateOrder(LiteOrderList listOrderList) {
            orders.clear();
            if (listOrderList != null && !Utils.isEmpty(listOrderList.getOrders())) {
                orders.addAll(listOrderList.getOrders());
            }
            currentTimeStamp = System.currentTimeMillis();
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card, parent, false);
            OrderViewHolder vh = new OrderViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final LiteOrder order = orders.get(position);
            Customer customer = order.getCustomer();

            OrderViewHolder viewHolder = (OrderViewHolder) holder;
            if (customer != null) {
                viewHolder.customerName.setText(customer.getFirstName() + " " + customer.getLastName());
                if (!TextUtils.isEmpty(customer.getProfilePic())) {
                    ImageLoader.loadImage(viewHolder.img, customer.getProfilePic());
                } else {
                    viewHolder.img.setImageResource(monkey);
                }
            } else {
                viewHolder.customerName.setText("Anonymous");
                viewHolder.img.setImageResource(monkey);
            }
            viewHolder.orderType.setText(order.getOrderType());

            Drawable statusBg = null;
            if (order.getStatus().equals(Constants.SUB_ORDER_SUBMITTED)) {
                statusBg = submit_bg;
            } else if (order.getStatus().equals(Constants.SUB_ORDER_IN_PROCESS)) {
                statusBg = ip_bg;
            } else if (order.getStatus().equals(Constants.SUB_ORDER_ACCEPTED)) {
                statusBg = accept_bg;
            }
            if (statusBg != null) {
                viewHolder.orderStatusSection.setBackground(statusBg);
            }

            viewHolder.orderId.setText(order.getOrderId() + "/" + order.getSubId());
            viewHolder.table.setText(order.getTable());


            String waitingTimeString;
            try {
                Date date = Utils.converterISO8601StringToDate(order.getCreatedTime());
                long millis = currentTimeStamp - date.getTime();
                waitingTimeString = Utils.formatMillis(millis);
            } catch (ParseException e) {
                waitingTimeString = "N/A";
            }

            viewHolder.time.setText(waitingTimeString);
            viewHolder.status.setText(order.getStatus());

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderClickable) {
                        orderClickable = false;
                        loadOrderDetails(order.getOrderId(), order.getSubId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }

    private void setOrderBtnColor() {
        ((GradientDrawable) mOrderAllBtn.getBackground()).setColor(mUnSelectBtnColor);
        ((GradientDrawable) mOrderIPBtn.getBackground()).setColor(mUnSelectBtnColor);
        ((GradientDrawable) mOrderSubmittedBtn.getBackground()).setColor(mUnSelectBtnColor);
        switch (mOrderMode) {
            case ALL:
                ((GradientDrawable) mOrderAllBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
            case SUBMITTED:
                ((GradientDrawable) mOrderSubmittedBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
            case INPROCESS:
                ((GradientDrawable) mOrderIPBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
        }
    }

    private void startOrderDetailsActivity(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra(Order.BUNDLE_NAME, order);
        getActivity().startActivityForResult(intent, Constants.ORDER_DETAIL_REQUEST);
    }


    private void loadOrderDetails(final String orderId, final String subOrderId) {
        OrderService.lockOrder(orderId, subOrderId, app.getUser().getUserId(), new UICallBack<Order>() {
            @Override
            public void onPreExecute() {
                notifyActivity(Constants.SHOW_LOADING);
            }

            @Override
            public void onPostExecute(Order order) {
                if (order != null) {
                    startOrderDetailsActivity(order);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Cannot lock this order",
                        Toast.LENGTH_SHORT).show();
                }
                notifyActivity(Constants.HIDE_LOADING);
                orderClickable = true;
            }
        }, app);
    }

    class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshOrderList(true);
        }
    }
}