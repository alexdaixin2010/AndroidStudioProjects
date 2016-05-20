package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.foodymon.businessapp.R;
import com.foodymon.businessapp.constant.Constants;
import com.foodymon.businessapp.main.BusinessApplication;
import com.foodymon.businessapp.service.LoginService;
import com.foodymon.businessapp.service.TopicRegistrationService;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener, OrderFragment.OnFragmentInteractionListener {

    private ViewGroup nav_order;
    private ViewGroup nav_pay;
    private ViewGroup nav_customer;
    private ViewGroup nav_dashboard;

    private ViewGroup selectedTab;

    private TextView nav_tv_order;
    private TextView nav_tv_pay;
    private TextView nav_tv_customer;
    private TextView nav_tv_dashboard;

    private TextView order_badge;

    private ProgressBar loading;

    private OrderFragment orderFragment;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private BusinessApplication app;

    private static int TAB_SELECTED = Color.argb(18, 0,0,0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Business View");

        initBottonNavBar();

        getSupportFragmentManager().beginTransaction().commit();

        app = (BusinessApplication)this.getApplication();

        loading = (ProgressBar)findViewById(R.id.activity_main_loading);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Constants.ORDER_UPDATE)) {
                    order_badge.setVisibility(View.VISIBLE);
                }
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
            new IntentFilter(Constants.ORDER_UPDATE));
    }
    private void initBottonNavBar() {
        BottomNavListener navListener = new BottomNavListener();
        nav_order = (ViewGroup)findViewById(R.id.nav_order);

        order_badge = (TextView)findViewById(R.id.order_badge);

        nav_pay = (ViewGroup) findViewById(R.id.nav_pay);
        nav_customer = (ViewGroup) findViewById(R.id.nav_customer);
        nav_dashboard = (ViewGroup) findViewById(R.id.nav_dashboard);

        nav_order.setOnClickListener(navListener);
        nav_pay.setOnClickListener(navListener);
        nav_customer.setOnClickListener(navListener);
        nav_dashboard.setOnClickListener(navListener);

        nav_tv_order = (TextView) findViewById(R.id.nav_tv_order);
        nav_tv_pay = (TextView) findViewById(R.id.nav_tv_pay);
        nav_tv_customer = (TextView) findViewById(R.id.nav_tv_customer);
        nav_tv_dashboard = (TextView) findViewById(R.id.nav_tv_dashboard);

        //setbadge
        //nav_customer.setBackground(getResources().getDrawable(R.drawable.badge_circle));

        // set default order fragment
        nav_order.setBackgroundColor(TAB_SELECTED);
        selectedTab = nav_order;
        if(orderFragment == null) {
            orderFragment = OrderFragment.newInstance();
        }
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, orderFragment);
        transaction.commit();

    }

    private void restartBottonNavTextColor() {
        nav_tv_order.setTextColor(getResources().getColor(R.color.button_grey));
        nav_tv_pay.setTextColor(getResources().getColor(R.color.button_grey));
        nav_tv_customer.setTextColor(getResources().getColor(R.color.button_grey));
        nav_tv_dashboard.setTextColor(getResources().getColor(R.color.button_grey));

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);

            LoginService.logout(this);

            Intent unSuscribe = new Intent(MainActivity.this, TopicRegistrationService.class);
            unSuscribe.putExtra(Constants.KEY, Constants.UNSUBSCRIBE);
            unSuscribe.putExtra(Constants.TOPIC, app.getStoreId()+"-order");
            startService(unSuscribe);

            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Intent intent) {
        String from = intent.getStringExtra(Constants.INTENT_FROM);
        String action = intent.getStringExtra(Constants.INTENT_ACTION);
        switch (from) {
            case Constants.ORDER_FRAGMENT:
                if(action.equals(Constants.ORDER_REFRESH_DONE)) {
                    this.order_badge.setVisibility(View.INVISIBLE);
                }
                break;
            default:
        }

        switch (action) {
            case Constants.SHOW_LOADING:
                loading.setVisibility(View.VISIBLE);
                break;
            case Constants.HIDE_LOADING:
                loading.setVisibility(View.INVISIBLE);
            default:
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Constants.ORDER_DETAIL_REQUEST) {
            if(resultCode == Activity.RESULT_OK){
                if ("refresh".equals(data.getStringExtra("result"))){
                    orderFragment.refreshOrderList(false);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    class BottomNavListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            restartBottonNavTextColor();

            FragmentManager fm = getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();

            switch (v.getId()) {
                case R.id.nav_order:
                    //    nav_iv_order.setImageResource(R.drawable.nav_order_pressed);
                    selectedTab.setBackground(null);
                    nav_order.setBackgroundColor(TAB_SELECTED);
                    selectedTab = nav_order;
                    if(orderFragment == null) {
                        orderFragment = OrderFragment.newInstance();
                    }
                    transaction.replace(R.id.id_content, orderFragment);
                    break;
                case R.id.nav_pay:
                    //    nav_iv_order.setImageResource(R.drawable.nav_pay_pressed);
                    selectedTab.setBackground(null);
                    nav_pay.setBackgroundColor(TAB_SELECTED);
                    selectedTab = nav_pay;
                    transaction.remove(orderFragment);
                    break;
                case R.id.nav_customer:
                    //    nav_iv_order.setImageResource(R.drawable.nav_takeout_pressed);
                    selectedTab.setBackground(null);
                    nav_customer.setBackgroundColor(TAB_SELECTED);
                    selectedTab = nav_customer;
                    transaction.remove(orderFragment);
                    break;
                case R.id.nav_dashboard:
                    //    nav_iv_order.setImageResource(R.drawable.nav_user_pressed);
                    selectedTab.setBackground(null);
                    nav_dashboard.setBackgroundColor(TAB_SELECTED);
                    selectedTab = nav_dashboard;
                    transaction.remove(orderFragment);
                    break;
            }
            transaction.commit();
        }
    }

}
