package com.foodymon.businessapp.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.foodymon.businessapp.R;


public class OrderDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types and number of parameters
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_order_details, container, false);

        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.order_items);
        String[] values = new String[] { "Item1, Black Sea Osetra",
            "Item 2, Lobster Salad",
            "Item 3, Savory Tart",
            "Item 4, Pan Seared Bass",
            "Item 5, Roast Maine Lobster",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast","Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast","Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 6, Lemon Pepper Duck Breast",
            "Item 7, French Macaron Ice Cream Sandwiches with Three Sauces"
        };
        for(int i = 0; i<values.length; ++i) {
            TextView tv = new TextView(view.getContext());
            tv.setText(values[i]);
            tv.setPadding(20,4,20,4);
            linearLayout.addView(tv);
        }

        Button confrimBtn = (Button)view.findViewById(R.id.confirm_button);
        confrimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return view;
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
