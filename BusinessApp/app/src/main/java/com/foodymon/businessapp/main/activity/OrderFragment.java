package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constant;
import com.foodymon.businessapp.datastructure.LiteOrder;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class OrderFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private LiteOrderList mOrderList;
    private long currentTimeStamp;

    private BusinessApplication app;

    // TODO: Rename and change types and number of parameters
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
        app = (BusinessApplication)getActivity().getApplication();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshListener());


        mRecyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.order_span_count));
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.setBackgroundColor(Color.argb(230, 255, 255, 255));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LiteOrderList liteOrderList = getActivity().getIntent()
            .getParcelableExtra(LiteOrderList.BUNDLE_NAME);
        mAdapter = new RecyclerViewAdapter(liteOrderList);
        mRecyclerView.setAdapter(mAdapter);

        currentTimeStamp = System.currentTimeMillis();
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

    public void refreshOrderList() {
        OrderService.getUnpaidLiteOrderList(app.getStoreId(),  new UICallBack<LiteOrderList>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(LiteOrderList liteOrderList) {
                // refresh time stamp
                currentTimeStamp = System.currentTimeMillis();

                // set order list
                OrderFragment.this.mAdapter.updateOrder(liteOrderList);
                mSwipeRefreshLayout.setRefreshing(false);
                notifyActivity(Constant.ORDER_REFRESH_DONE);
            }
        });
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
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
            intent.putExtra(Constant.INTENT_FROM, Constant.ORDER_FRAGMENT);
            intent.putExtra(Constant.INTENT_ACTION, action);
            mListener.onFragmentInteraction(intent);
        }
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter {
        private List<LiteOrder> orders = new ArrayList<>();

        private class OrderViewHolder extends RecyclerView.ViewHolder {
            View card;
            TextView orderId;
            TextView table;
            TextView time;
            TextView status;
            public OrderViewHolder(View v) {
                super(v);
                card = v;
                orderId = (TextView)v.findViewById(R.id.order_id);
                table = (TextView)v.findViewById(R.id.order_table);
                time = (TextView)v.findViewById(R.id.order_time);
                status = (TextView)v.findViewById(R.id.order_status);
            }
        }

        RecyclerViewAdapter(LiteOrderList listOrderList) {
            orders.addAll(listOrderList.getOrders());
        }

        void updateOrder(LiteOrderList listOrderList) {
            orders.clear();
            if(!Utils.isNullOrEmpty(listOrderList.getOrders())) {
                orders.addAll(listOrderList.getOrders());
            }
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
            OrderViewHolder viewHolder = (OrderViewHolder) holder;
            viewHolder.orderId.setText(order.getOrderId()+"/"+ order.getSub_id());
            viewHolder.table.setText(order.getTable());


            String waitingTimeString;
            try {
                Date date = Utils.converterISO8601StringToDate(order.getCreatedTime());
            long millis = currentTimeStamp - date.getTime();
            waitingTimeString = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
            );
            } catch (ParseException e) {
                waitingTimeString = "N/A";
            }

            viewHolder.time.setText("11:20");
            viewHolder.status.setText(order.getStatus());

            viewHolder.card.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    loadOrderDetails(order.getOrderId(), order.getSub_id());

                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }

    private void startOrderDetailsActivity(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra(Order.BUNDLE_NAME, order);
        getActivity().startActivityForResult(intent, Constant.ORDER_DETAIL_REQUEST);
    }


    private void loadOrderDetails(String orderId, String subOrderId) {
        OrderService.getOrder(orderId, subOrderId, new UICallBack<Order>() {
            @Override
            public void onPreExecute() {
                notifyActivity(Constant.SHOW_LOADING);
            }

            @Override
            public void onPostExecute(Order order) {
                notifyActivity(Constant.HIDE_LOADING);
                startOrderDetailsActivity(order);
            }
        });
    }

    class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            refreshOrderList();
        }
    }


}
