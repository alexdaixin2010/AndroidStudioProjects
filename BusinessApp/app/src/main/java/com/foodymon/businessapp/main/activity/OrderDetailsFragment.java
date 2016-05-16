package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constant;
import com.foodymon.businessapp.datastructure.LiteOrderList;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.OrderItem;
import com.foodymon.businessapp.datastructure.OrderItemAttribute;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class OrderDetailsFragment extends Fragment {
    private Order order;
    TextView orderId;
    TextView table;
    TextView time;
    TextView status;


    public static OrderDetailsFragment newInstance() {
        return new OrderDetailsFragment();
    }

    public OrderDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        order = getActivity().getIntent().getParcelableExtra(Order.BUNDLE_NAME);

        orderId = (TextView)getView().findViewById(R.id.order_detail_id);
        table = (TextView)getView().findViewById(R.id.order_detail_table);
        time = (TextView)getView().findViewById(R.id.order_detail_time);
        status = (TextView)getView().findViewById(R.id.order_detail_status);

        orderId.setText(order.getLiteOrder().getOrderId()+"/"+ order.getLiteOrder().getSub_id());
        table.setText(order.getLiteOrder().getTable());
        time.setText("11:20");
        status.setText(order.getLiteOrder().getStatus());



        final LinearLayout linearLayout = (LinearLayout)getView().findViewById(R.id.order_detail_items);

        for ( OrderItem item: order.getItems()) {
            final View itemCard = getActivity().getLayoutInflater().inflate(R.layout.order_item_card, null);

            GridLayout itemView= (GridLayout) itemCard.findViewById(R.id.order_detail_item);

            TextView itemName = (TextView)itemCard.findViewById(R.id.order_detail_item_name);
            itemName.setText(item.getItemName());

            TextView itemPrice = (TextView)itemCard.findViewById(R.id.order_detail_item_price);
            itemPrice.setText("$ "+String.valueOf(item.getPrice()));

            ImageButton removeBtn = (ImageButton)itemCard.findViewById(R.id.order_detail_remove_item_button);
            removeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.removeView(itemCard);
                }
            });


            //TODO: set quantity

            if(!Utils.isNullOrEmpty(item.getAttributes())) {
                for (OrderItemAttribute attribute : item.getAttributes()) {
                    View attributeView = getActivity().getLayoutInflater().inflate(R.layout.order_item_attribute, null);
                    TextView attributeName = (TextView) attributeView.findViewById(R.id.order_detail_item_attribute_name);
                    attributeName.setText(attribute.getName());

                    TextView attributeValue = (TextView) attributeView.findViewById(R.id.order_detail_item_attribute_value);
                    attributeValue.setText(Utils.StringJoin(",", attribute.getValues().toArray(new String[0])));

                    itemView.addView(attributeView);

                }
            }

            linearLayout.addView(itemCard);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order_details, container, false);

        Button confirmBtn = (Button)view.findViewById(R.id.accept_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSub_id());
            }
        });

        Button rejectBtn = (Button)view.findViewById(R.id.reject_button);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSub_id());
            }
        });

        return view;
    }

    private void acceptOrder(String orderId, String subOrderId) {
        OrderService.acceptOrder(orderId, subOrderId, new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Boolean response) {
                if (response != null && response == Boolean.TRUE) {
                    Toast.makeText(getActivity().getApplicationContext(), "Order Accepted",
                        Toast.LENGTH_SHORT).show();
                    finishActivity(true);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Fail accept Order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void rejectOrder(String orderId, String subOrderId) {
        OrderService.rejectOrder(orderId, subOrderId, new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Boolean response) {
                if (response != null && response == Boolean.TRUE) {
                    Toast.makeText(getActivity().getApplicationContext(), "Order Rejected",
                        Toast.LENGTH_SHORT).show();
                    finishActivity(true);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Fail reject Order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void finishActivity(boolean refreshOrder) {
        if (refreshOrder) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "refresh");
            getActivity().setResult(Activity.RESULT_OK, returnIntent);
        }
        getActivity().finish();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
           // mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
