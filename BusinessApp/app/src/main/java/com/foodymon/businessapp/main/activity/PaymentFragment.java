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

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.Customer;
import com.foodymon.businessapp.datastructure.LitePayment;
import com.foodymon.businessapp.datastructure.LitePaymentList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.Payment;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.main.view.CircleImageView;
import com.foodymon.businessapp.service.PaymentService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.ImageLoader;
import com.foodymon.businessapp.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alexdai on 5/21/16.
 */
public class PaymentFragment extends Fragment  {

    private PaymentFragment.OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private TextView mAllBtn;
    private TextView mPaidBtn;
    private TextView mRequestPayBtn;
    private LitePaymentList mPaymentList;

    int mUnSelectBtnColor;
    int mSelectedBtnColor;
    boolean cardClickable = true;

    enum PAY_MODE {
        ALL,
        PAID,
        REQUEST,
    }
    private PAY_MODE mPayMode = PAY_MODE.REQUEST;

    private BusinessApplication app;

    public static PaymentFragment newInstance() {
        PaymentFragment fragment = new PaymentFragment();
        return fragment;
    }

    public PaymentFragment() {
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
            mPaymentList = savedInstanceState.getParcelable(LitePaymentList.BUNDLE_NAME);
            mPayMode = PAY_MODE.valueOf(savedInstanceState.getString("payMode"));
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.pay_swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());


        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.order_span_count));
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setBackgroundColor(Color.argb(230, 255, 255, 255));

        mAllBtn = (TextView)view.findViewById(R.id.pay_btn_all);
        mPaidBtn = (TextView)view.findViewById(R.id.pay_btn_paid);
        mRequestPayBtn = (TextView)view.findViewById(R.id.pay_btn_request);

        setPayBtnColor();

        mAllBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mPayMode == PAY_MODE.ALL){
                    return false;
                }
                mPayMode = PAY_MODE.ALL;
                setPayBtnColor();
                refreshPaymentList(false);
                return true;
            }
        });

        mPaidBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mPayMode == PAY_MODE.PAID){
                    return false;
                }
                mPayMode = PAY_MODE.PAID;
                setPayBtnColor();
                refreshPaymentList(false);
                return true;
            }
        });
        mRequestPayBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mPayMode == PAY_MODE.REQUEST){
                    return false;
                }
                mPayMode = PAY_MODE.REQUEST;
                setPayBtnColor();
                refreshPaymentList(false);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mPaymentList == null ) {
            mPaymentList = getActivity().getIntent()
                .getParcelableExtra(LitePaymentList.BUNDLE_NAME);
        }
        mAdapter = new RecyclerViewAdapter(mPaymentList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("payMode", mPayMode.toString());
        state.putParcelable(LitePaymentList.BUNDLE_NAME, mPaymentList);
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

    public void refreshPaymentList(final boolean isSwipe) {
        switch (mPayMode) {
            case ALL:
                PaymentService.getAllLiteOrderList(app.getStoreId(), new UICallBack<LitePaymentList>() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(LitePaymentList paymentList) {
                        mPaymentList = paymentList;
                        // set order list
                        PaymentFragment.this.mAdapter.updateOrder(paymentList);
                        if (isSwipe) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        notifyActivity(Constants.ORDER_REFRESH_DONE);
                    }
                }, app);
                break;
            case PAID:
                PaymentService.getPaidList(app.getStoreId(), new UICallBack<LitePaymentList>() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(LitePaymentList paymentList) {
                        // refresh time stamp
                        mPaymentList = paymentList;

                        // set order list
                        PaymentFragment.this.mAdapter.updateOrder(paymentList);
                        if (isSwipe) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                        notifyActivity(Constants.ORDER_REFRESH_DONE);
                    }
                }, app);
                break;
            case REQUEST:
                PaymentService.getUnPaidList(app.getStoreId(), new UICallBack<LitePaymentList>() {
                    @Override
                    public void onPreExecute() {
                    }

                    @Override
                    public void onPostExecute(LitePaymentList paymentList) {
                        mPaymentList = paymentList;
                        // set order list
                        PaymentFragment.this.mAdapter.updateOrder(paymentList);
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
        private List<LitePayment> payments = new ArrayList<>();
        private Drawable submit_bg;
        private Drawable accept_bg;
        private Drawable ip_bg;
        private int monkey;
        private long currentTimeStamp;

        private class OrderViewHolder extends RecyclerView.ViewHolder {
            View card;
            LinearLayout orderStatusSection;
            TextView customerName;
            TextView billId;
            TextView table;
            TextView time;
            TextView status;
            CircleImageView img;
            TextView total;
            TextView tip;

            public OrderViewHolder(View v) {
                super(v);
                card = v;
                orderStatusSection = (LinearLayout) v.findViewById(R.id.pay_status_section);
                customerName = (TextView) v.findViewById(R.id.pay_customer_name);
                billId = (TextView) v.findViewById(R.id.pay_id);
                table = (TextView) v.findViewById(R.id.pay_table);
                time = (TextView) v.findViewById(R.id.pay_time);
                status = (TextView) v.findViewById(R.id.pay_status);
                img = (CircleImageView) v.findViewById(R.id.pay_user_img);
                total = (TextView) v.findViewById(R.id.pay_total);
                tip = (TextView) v.findViewById(R.id.pay_tip);
            }
        }

        RecyclerViewAdapter(LitePaymentList litePaymentList) {
            if(litePaymentList != null && !Utils.isEmpty(litePaymentList.getPayments())) {
                payments.addAll(litePaymentList.getPayments());
            }
            Resources res = getResources();
            submit_bg = res.getDrawable(R.drawable.state_new_background_480);
            accept_bg = res.getDrawable(R.drawable.state_completed_background_480);
            ip_bg = res.getDrawable(R.drawable.state_accepted_background_480);
            monkey = res.getIdentifier("monkey", "drawable", getActivity().getPackageName());
            currentTimeStamp = System.currentTimeMillis();
        }

        void updateOrder(LitePaymentList litePaymentList) {
            payments.clear();
            if (litePaymentList != null && !Utils.isEmpty(litePaymentList.getPayments())) {
                payments.addAll(litePaymentList.getPayments());
            }
            currentTimeStamp = System.currentTimeMillis();
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pay_card, parent, false);
            OrderViewHolder vh = new OrderViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final LitePayment pay = payments.get(position);
            Customer customer = pay.getCustomer();

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
            Drawable statusBg = null;
            if (pay.getStatus().equals(Constants.ORDER_UNPAID)) {
                statusBg = submit_bg;
            } else if (pay.getStatus().equals(Constants.ORDER_PAID)) {
                statusBg = ip_bg;
            }
            if (statusBg != null) {
                viewHolder.orderStatusSection.setBackground(statusBg);
            }

            viewHolder.billId.setText(pay.getBillId());
            viewHolder.table.setText(pay.getTable());
            viewHolder.total.setText("$"+pay.getTotalAmount());
            viewHolder.tip.setText("$"+pay.getTip());

            String waitingTimeString;
            try {
                Date date = Utils.converterISO8601StringToDate(pay.getCreatedTime());
                long millis = currentTimeStamp - date.getTime();
                waitingTimeString = Utils.formatMillis(millis);
            } catch (ParseException e) {
                waitingTimeString = "N/A";
            }

            viewHolder.time.setText(waitingTimeString);
            viewHolder.status.setText(pay.getStatus());

            viewHolder.card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cardClickable) {
                        cardClickable = false;
                        loadPaymentDetails(pay.getBillId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return payments.size();
        }
    }

    private void setPayBtnColor() {
        ((GradientDrawable) mAllBtn.getBackground()).setColor(mUnSelectBtnColor);
        ((GradientDrawable) mRequestPayBtn.getBackground()).setColor(mUnSelectBtnColor);
        ((GradientDrawable) mPaidBtn.getBackground()).setColor(mUnSelectBtnColor);
        switch (mPayMode) {
            case ALL:
                ((GradientDrawable) mAllBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
            case PAID:
                ((GradientDrawable) mPaidBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
            case REQUEST:
                ((GradientDrawable) mRequestPayBtn.getBackground()).setColor(mSelectedBtnColor);
                break;
        }
    }

    private void startOrderDetailsActivity(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra(Order.BUNDLE_NAME, order);
        getActivity().startActivityForResult(intent, Constants.ORDER_DETAIL_REQUEST);
    }


    private void loadPaymentDetails(final String paymentId) {
        PaymentService.getPayment(paymentId, new UICallBack<Payment>() {
            @Override
            public void onPreExecute() {
                //notifyActivity(Constants.SHOW_LOADING);
            }

            @Override
            public void onPostExecute(Payment payment) {
                /*
                    notifyActivity(Constants.HIDE_LOADING);
                    startOrderDetailsActivity(order);
                    cardClickable = true;
                    */
            }
        }, app);
    }


    class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshPaymentList(true);
        }
    }
}
