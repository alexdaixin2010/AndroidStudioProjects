package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foodymon.businessapp.R;

public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

    private RelativeLayout nav_order;
    private RelativeLayout nav_pay;
    private RelativeLayout nav_takeout;
    private RelativeLayout nav_user;

    private TextView nav_tv_order;
    private TextView nav_tv_pay;
    private TextView nav_tv_takeout;
    private TextView nav_tv_user;

    private OrderFragment orderFragment;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Business View");

        initBottonNavBar();
    }

    private void initBottonNavBar() {
        BottomNavListener navListener = new BottomNavListener();
        nav_order = (RelativeLayout)findViewById(R.id.nav_order);
        nav_pay = (RelativeLayout) findViewById(R.id.nav_pay);
        nav_takeout = (RelativeLayout) findViewById(R.id.nav_takeout);
        nav_user = (RelativeLayout) findViewById(R.id.nav_user);
        nav_order.setOnClickListener(navListener);
        nav_pay.setOnClickListener(navListener);
        nav_takeout.setOnClickListener(navListener);
        nav_user.setOnClickListener(navListener);

        nav_tv_order = (TextView) findViewById(R.id.nav_tv_order);
        nav_tv_pay = (TextView) findViewById(R.id.nav_tv_pay);
        nav_tv_takeout = (TextView) findViewById(R.id.nav_tv_takeout);
        nav_tv_user = (TextView) findViewById(R.id.nav_tv_user);

        //setbadge
        //nav_takeout.setBackground(getResources().getDrawable(R.drawable.badge_circle));

        // set default order fragment
        nav_tv_order.setTextColor(0xff1B940A);
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
        nav_tv_takeout.setTextColor(getResources().getColor(R.color.button_grey));
        nav_tv_user.setTextColor(getResources().getColor(R.color.button_grey));

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
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
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
                    nav_tv_order.setTextColor(0xff1B940A);
                    if(orderFragment == null) {
                        orderFragment = OrderFragment.newInstance();
                    }
                    transaction.replace(R.id.id_content, orderFragment);
                    break;
                case R.id.nav_pay:
                    //    nav_iv_order.setImageResource(R.drawable.nav_pay_pressed);
                    nav_tv_pay.setTextColor(0xff1B940A);
                    transaction.remove(orderFragment);
                    break;
                case R.id.nav_takeout:
                    //    nav_iv_order.setImageResource(R.drawable.nav_takeout_pressed);
                    nav_tv_takeout.setTextColor(0xff1B940A);
                    transaction.remove(orderFragment);
                    break;
                case R.id.nav_user:
                    //    nav_iv_order.setImageResource(R.drawable.nav_user_pressed);
                    nav_tv_user.setTextColor(0xff1B940A);
                    transaction.remove(orderFragment);
                    break;
            }
            transaction.commit();
        }
    }

}
