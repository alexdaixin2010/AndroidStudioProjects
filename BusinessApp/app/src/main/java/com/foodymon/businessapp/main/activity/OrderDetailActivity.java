package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    private Order order;
    private ProgressBar loadding;
    private BusinessApplication app;
    private boolean isOrderChange = false;

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

        loadding = (ProgressBar) findViewById(R.id.order_detail_loading);

        TextView orderId = (TextView) findViewById(R.id.order_detail_id);
        TextView table = (TextView) findViewById(R.id.order_detail_table);
        TextView time = (TextView) findViewById(R.id.order_detail_time);
        TextView status = (TextView) findViewById(R.id.order_detail_status);
        TextView name = (TextView) findViewById(R.id.order_detail_customer_name);
        TextView type = (TextView) findViewById(R.id.order_detail_order_type) ;
        LinearLayout orderStatusSection = (LinearLayout) findViewById(R.id.order_detail_status_section);
        CircleImageView imageView = (CircleImageView) findViewById(R.id.order_detail_user_img);

        LiteOrder liteOrder = order.getLiteOrder();
        Customer customer = liteOrder.getCustomer();

        type.setText(liteOrder.getOrderType());
        orderId.setText(liteOrder.getOrderId() + "/" + liteOrder.getSubId());
        table.setText(liteOrder.getTable());

        String waitingTimeString;
        try {
            Date date = Utils.converterISO8601StringToDate(liteOrder.getCreatedTime());
            DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm:ss");
            waitingTimeString = formatter.format(date);
        } catch (ParseException e) {
            waitingTimeString = "N/A";
        }
        time.setText(waitingTimeString);
        status.setText(liteOrder.getStatus());
        int id = getResources().getIdentifier("monkey", "drawable", getPackageName());
        if (customer != null) {
            name.setText(customer.getFirstName() + " " + customer.getLastName());
            if (!TextUtils.isEmpty(customer.getProfilePic())) {
                ImageLoader.loadImage(imageView, customer.getProfilePic());
            } else {
                imageView.setImageResource(id);
            }
        } else {
            name.setText("Anonymous");
            imageView.setImageResource(id);
        }

        Resources res = getResources();
        Drawable statusBg = null;
        if (liteOrder.getStatus().equals(Constants.SUB_ORDER_SUBMITTED)) {
            statusBg = res.getDrawable(R.drawable.state_new_background_480);
        } else if (liteOrder.getStatus().equals(Constants.SUB_ORDER_IN_PROCESS)) {
            statusBg = res.getDrawable(R.drawable.state_accepted_background_480);
        } else if (liteOrder.getStatus().equals(Constants.SUB_ORDER_ACCEPTED)) {
            statusBg = res.getDrawable(R.drawable.state_completed_background_480);
        }
        if (statusBg != null) {
            orderStatusSection.setBackground(statusBg);
        }
        if (isOrderEditable()) {
            CircleImageView confirmBtn = (CircleImageView) findViewById(R.id.order_detail_accept_button);
            CircleImageView rejectBtn = (CircleImageView) findViewById(R.id.order_detail_reject_button);
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
                }
            });
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     rejectOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
                }
            });
        } else {
            LinearLayout btnSection = (LinearLayout) findViewById(R.id.order_detail_btn_section);
            btnSection.setVisibility(View.GONE);
        }


        final LinearLayout itemsView = (LinearLayout) findViewById(R.id.order_detail_items);

        for (OrderItem item : order.getItems()) {
            final View itemCard = getLayoutInflater().inflate(R.layout.order_item_card, null);
            final OrderItem orderItem = item;

            final LinearLayout itemView = (LinearLayout) itemCard.findViewById(R.id.order_detail_item);

            TextView itemName = (TextView) itemCard.findViewById(R.id.order_detail_item_name);
            itemName.setText(item.getItemName());

            TextView itemqty = (TextView)itemCard.findViewById(R.id.order_detail_item_quantity);
            itemqty.setText("X" + item.getQuantity());

            TextView itemPrice = (TextView) itemCard.findViewById(R.id.order_detail_item_price);
            itemPrice.setText("$" + String.valueOf(item.getPrice()));

            ImageButton itemRemoveBtn = (ImageButton) itemCard.findViewById(R.id.order_detail_remove_item_button);
            if (isOrderEditable()) {
                itemRemoveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemsView.removeView(itemCard);
                        order.getItems().remove(orderItem);
                        isOrderChange = true;
                    }
                });
            } else {
                itemRemoveBtn.setVisibility(View.GONE);
            }

            if (!Utils.isEmpty(item.getAttributes())) {
                final List<OrderItemAttribute> attribueList = item.getAttributes();
                for (int i = 0; i < attribueList.size(); ++i) {
                    final OrderItemAttribute itemAttribute = attribueList.get(i);
                    final LinearLayout attributeView = (LinearLayout) getLayoutInflater().inflate(R.layout.order_item_attribute_name, null);
                    TextView attributeName = (TextView) attributeView.findViewById(R.id.order_detail_item_attribute_name);
                    attributeName.setText(itemAttribute.getName());

                    ImageButton attrRemoveBtn = (ImageButton) attributeView.findViewById(R.id.order_detail_remove_attribute_button);
                    if (isOrderEditable()) {
                        attrRemoveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemView.removeView(attributeView);
                                attribueList.remove(itemAttribute);
                                isOrderChange = true;

                            }
                        });
                    } else {
                        attrRemoveBtn.setVisibility(View.GONE);
                    }

                    final List<String> values = itemAttribute.getValues();
                    for (int j = 0; j < values.size(); ++j) {
                        final int index = j;
                        String value = values.get(j);
                        final LinearLayout valueView = (LinearLayout) getLayoutInflater().inflate(R.layout.order_item_attribute, null);
                        TextView valueTextView = (TextView) valueView.findViewById(R.id.order_detail_item_attribute_value);
                        valueTextView.setText(value);
                        attributeView.addView(valueView);

                        ImageButton attrValueRemoveBtn = (ImageButton) valueView.findViewById(R.id.order_detail_remove_attribute_value_button);
                        if (isOrderEditable()) {
                            attrValueRemoveBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    attributeView.removeView(valueView);
                                    values.remove(index);
                                    itemAttribute.getValueCodes().remove(index);
                                    isOrderChange = true;
                                }
                            });
                        } else {
                            attrValueRemoveBtn.setVisibility(View.GONE);
                        }
                    }
                    itemView.addView(attributeView);

                }
            }
            itemsView.addView(itemCard);
        }
    }

    private boolean isOrderEditable() {
        return order.getLiteOrder().getStatus().equals(Constants.SUB_ORDER_IN_PROCESS);
    }

    public void showLoadding(boolean show) {
        if (show) {
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
                if (isOrderEditable()) {
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
    public void onBackPressed() {
        if(isOrderEditable()) {
            unLockOrder(order.getLiteOrder().getOrderId(), order.getLiteOrder().getSubId());
        }
        super.onBackPressed();
    }

    private void unLockOrder(String orderId, String subOrderId) {
        OrderService.unLockOrder(orderId, subOrderId, app.getUser().getUserId(), new UICallBack<Boolean>() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(Boolean response) {
                if (response != null && response == Boolean.TRUE) {
                    finishActivity(true);
                } else {
                    Toast.makeText(OrderDetailActivity.this.getApplicationContext(), "Cannot unlock this order",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }, app);
    }

    private void acceptOrder(String orderId, String subOrderId) {
        OrderService.acceptOrder(orderId, subOrderId, app.getUser().getUserId(), isOrderChange ? order : null, new UICallBack<Boolean>() {
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
        OrderService.cancelOrder(orderId, subOrderId, app.getUser().getUserId(), new UICallBack<Boolean>() {
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
