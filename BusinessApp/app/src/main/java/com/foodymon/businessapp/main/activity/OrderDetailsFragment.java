package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.OrderItem;
import com.foodymon.businessapp.datastructure.OrderItemAttribute;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.main.view.CircleImageView;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.Utils;

import java.util.List;


public class OrderDetailsFragment extends Fragment {
    private Order order;
    private boolean isOrderChange =false;
    private BusinessApplication app;

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
        app = (BusinessApplication)getActivity().getApplication();

        TextView orderId = (TextView)getView().findViewById(R.id.order_detail_id);
        TextView table = (TextView)getView().findViewById(R.id.order_detail_table);
        TextView time = (TextView)getView().findViewById(R.id.order_detail_time);
        TextView status = (TextView)getView().findViewById(R.id.order_detail_status);

        orderId.setText(order.getLiteOrder().getOrderId()+"/"+ order.getLiteOrder().getSub_id());
        table.setText(order.getLiteOrder().getTable());
        time.setText("11:20");
        status.setText(order.getLiteOrder().getStatus());



        final LinearLayout linearLayout = (LinearLayout)getView().findViewById(R.id.order_detail_items);

        for ( OrderItem item: order.getItems()) {
            final View itemCard = getActivity().getLayoutInflater().inflate(R.layout.order_item_card, null);
            final OrderItem orderItem = item;

            final LinearLayout itemView= (LinearLayout) itemCard.findViewById(R.id.order_detail_item);

            TextView itemName = (TextView)itemCard.findViewById(R.id.order_detail_item_name);
            itemName.setText(item.getItemName());

            TextView itemPrice = (TextView)itemCard.findViewById(R.id.order_detail_item_price);
            itemPrice.setText("$ "+String.valueOf(item.getPrice()));

            ImageButton itemRemoveBtn = (ImageButton)itemCard.findViewById(R.id.order_detail_remove_item_button);
            itemRemoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    linearLayout.removeView(itemCard);
                    order.getItems().remove(orderItem);
                    isOrderChange = true;
                }
            });

            //TODO: set quantity

            if(!Utils.isNullOrEmpty(item.getAttributes())) {
                final List<OrderItemAttribute> attribueList = item.getAttributes();
                for (int i = 0 ; i < attribueList.size(); ++i) {
                    final OrderItemAttribute itemAttribute = attribueList.get(i);
                    final LinearLayout attributeView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.order_item_attribute_name, null);
                    TextView attributeName = (TextView) attributeView.findViewById(R.id.order_detail_item_attribute_name);
                    attributeName.setText(itemAttribute.getName());

                    ImageButton attrRemoveBtn = (ImageButton)attributeView.findViewById(R.id.order_detail_remove_attribute_button);
                    attrRemoveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemView.removeView(attributeView);
                            attribueList.remove(itemAttribute);
                            isOrderChange = true;

                        }
                    });

                    final List<String> values = itemAttribute.getValues();
                    for(int j = 0; j < values.size(); ++j) {
                        final int index = j;
                        String value = values.get(j);
                        final LinearLayout valueView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.order_item_attribute, null);
                        TextView valueTextView = (TextView) valueView.findViewById(R.id.order_detail_item_attribute_value);
                        valueTextView.setText(value);
                        attributeView.addView(valueView);

                        ImageButton attrValueRemoveBtn = (ImageButton)valueView.findViewById(R.id.order_detail_remove_attribute_value_button);
                        attrValueRemoveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                attributeView.removeView(valueView);
                                values.remove(index);
                                itemAttribute.getValueCodes().remove(index);
                                isOrderChange = true;
                            }
                        });
                    }
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

        CircleImageView confirmBtn = (CircleImageView)view.findViewById(R.id.accept_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSub_id());
            }
        });

        CircleImageView rejectBtn = (CircleImageView)view.findViewById(R.id.reject_button);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // rejectOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSub_id());
            }
        });

        return view;
    }

    private void acceptOrder(String orderId, String subOrderId) {
        OrderService.acceptOrder(orderId, subOrderId, app.getUserId(), isOrderChange?order:null, new UICallBack<Boolean>() {
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
        OrderService.rejectOrder(orderId, subOrderId,app.getUserId(), new UICallBack<Boolean>() {
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
