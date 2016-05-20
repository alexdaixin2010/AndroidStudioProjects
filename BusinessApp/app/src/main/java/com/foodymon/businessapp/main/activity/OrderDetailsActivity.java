package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.datastructure.Customer;
import com.foodymon.businessapp.datastructure.LiteOrder;
import com.foodymon.businessapp.datastructure.Order;
import com.foodymon.businessapp.datastructure.OrderItem;
import com.foodymon.businessapp.datastructure.OrderItemAttribute;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.main.view.CircleImageView;
import com.foodymon.businessapp.service.OrderService;
import com.foodymon.businessapp.service.UICallBack;
import com.foodymon.businessapp.utils.ImageLoader;
import com.foodymon.businessapp.utils.Utils;

import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private Order order;
    private ProgressBar loadding;
    private BusinessApplication app;
    private boolean isOrderChange =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (BusinessApplication) getApplication();
        order = getIntent().getParcelableExtra(Order.BUNDLE_NAME);

        setContentView(R.layout.activity_order_detail);

        this.setTitle("Order Details");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        loadding = (ProgressBar)findViewById(R.id.order_detail_loading);

        TextView orderId = (TextView)findViewById(R.id.order_detail_id);
        TextView table = (TextView)findViewById(R.id.order_detail_table);
        TextView time = (TextView)findViewById(R.id.order_detail_time);
        TextView status = (TextView)findViewById(R.id.order_detail_status);
        TextView name = (TextView)findViewById(R.id.order_detail_customer_name);
        CircleImageView imageView = (CircleImageView)findViewById(R.id.order_detail_user_img);

        LiteOrder liteOrder = order.getLiteOrder();
        Customer customer  = liteOrder.getCustomer();

        orderId.setText(liteOrder.getOrderId()+"/"+ liteOrder.getSubId());
        table.setText(liteOrder.getTable());
        time.setText("11:20");
        status.setText(liteOrder.getStatus());
        int id = getResources().getIdentifier("monkey", "drawable", getPackageName());
        if(customer != null) {
            name.setText(customer.getFirstName() + " " + customer.getLastName());
            if (!TextUtils.isEmpty(customer.getProfilePic())) {
                ImageLoader.loadImage(imageView, customer.getProfilePic());
            } else {
                imageView.setImageResource(id);
            }
        } else{
            name.setText("Anonymous");
            imageView.setImageResource(id);
        }

        CircleImageView confirmBtn = (CircleImageView)findViewById(R.id.order_detail_accept_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
            }
        });

        CircleImageView rejectBtn = (CircleImageView)findViewById(R.id.order_detail_reject_button);
        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // rejectOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSub_id());
            }
        });

        final LinearLayout itemsView = (LinearLayout)findViewById(R.id.order_detail_items);

        for ( OrderItem item: order.getItems()) {
            final View itemCard = getLayoutInflater().inflate(R.layout.order_item_card, null);
            final OrderItem orderItem = item;

            final LinearLayout itemView= (LinearLayout) itemCard.findViewById(R.id.order_detail_item);

            TextView itemName = (TextView)itemCard.findViewById(R.id.order_detail_item_name);
            itemName.setText(item.getItemName() + " X" + item.getQuantity());

            TextView itemPrice = (TextView)itemCard.findViewById(R.id.order_detail_item_price);
            itemPrice.setText("$ "+String.valueOf(item.getPrice()));

            ImageButton itemRemoveBtn = (ImageButton)itemCard.findViewById(R.id.order_detail_remove_item_button);
            itemRemoveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemsView.removeView(itemCard);
                    order.getItems().remove(orderItem);
                    isOrderChange = true;
                }
            });

            if(!Utils.isEmpty(item.getAttributes())) {
                final List<OrderItemAttribute> attribueList = item.getAttributes();
                for (int i = 0 ; i < attribueList.size(); ++i) {
                    final OrderItemAttribute itemAttribute = attribueList.get(i);
                    final LinearLayout attributeView = (LinearLayout)getLayoutInflater().inflate(R.layout.order_item_attribute_name, null);
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
                        final LinearLayout valueView = (LinearLayout)getLayoutInflater().inflate(R.layout.order_item_attribute, null);
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

            itemsView.addView(itemCard);

        }

    }

    public void showLoadding(boolean show) {
        if(show) {
            loadding.setVisibility(View.VISIBLE);
        } else {
            loadding.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                if(order.getLiteOrder().getStatus().equals(Constants.SUB_ORDER_IN_PROCESS)) {
                    unLockOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
                } else {
                    finishActivity(true);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed () {
        unLockOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
    }

    private void unLockOrder(String orderId, String subOrderId) {
        OrderService.lockOrder(orderId, subOrderId, app.getUser().getUserId(), false, new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Boolean response) {
                if(response != null && response == Boolean.TRUE) {
                    finishActivity(true);
                }else {
                    Toast.makeText(OrderDetailsActivity.this.getApplicationContext(), "Cannot unlock this order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, app);
    }

    private void acceptOrder(String orderId, String subOrderId) {
        OrderService.acceptOrder(orderId, subOrderId, app.getUser().getUserId(), isOrderChange?order:null, new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Boolean response) {
                if (response != null && response == Boolean.TRUE) {
                    Toast.makeText(getApplicationContext(), "Order Accepted",
                        Toast.LENGTH_SHORT).show();
                    finishActivity(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Fail accept Order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, app);
    }

    private void rejectOrder(String orderId, String subOrderId) {
        OrderService.rejectOrder(orderId, subOrderId,app.getUser().getUserId(), new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(Boolean response) {
                if (response != null && response == Boolean.TRUE) {
                    Toast.makeText(getApplicationContext(), "Order Rejected",
                        Toast.LENGTH_SHORT).show();
                    finishActivity(true);
                } else {
                    Toast.makeText(getApplicationContext(), "Fail reject Order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, app);
    }

    private void finishActivity(boolean refreshOrder) {
        if (refreshOrder) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", "refresh");
            setResult(Activity.RESULT_OK, returnIntent);
        }
        finish();
    }
}
