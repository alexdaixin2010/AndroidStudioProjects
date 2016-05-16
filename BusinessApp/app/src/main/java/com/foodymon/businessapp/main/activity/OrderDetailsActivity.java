package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.foodymon.businessapp.R;

public class OrderDetailsActivity extends AppCompatActivity {

    private ProgressBar loadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detials);

        this.setTitle("Order Details");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        loadding = (ProgressBar)findViewById(R.id.order_details_loading);

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
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
