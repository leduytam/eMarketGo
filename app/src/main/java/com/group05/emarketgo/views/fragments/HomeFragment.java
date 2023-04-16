package com.group05.emarketgo.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;

public class HomeFragment extends Fragment {

    private Context context;
    private static FirebaseAuth mAuth;

    private LinearLayout orderDetailLayout;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();

        orderDetailLayout = view.findViewById(R.id.order_detail);
        orderDetailLayout.setOnClickListener(v -> onClickOrderDetail());

        return view;
    }

    private void onClickOrderDetail() {
        int fragmentId = this.getId();
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentId, orderDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
