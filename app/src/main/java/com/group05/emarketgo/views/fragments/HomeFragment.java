package com.group05.emarketgo.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.MockData;
import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.views.adapters.OrderItemAdapter;

import java.util.List;

public class HomeFragment extends Fragment implements OrderItemAdapter.OnItemClickListener {

    private Context context;
    private static FirebaseAuth mAuth;

    private LinearLayout orderDetailLayout;

    private TextView _tvMyBalance;



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

        List<Order> pendingOrders;
        pendingOrders = MockData.getOrders(Order.OrderStatus.PENDING);
        RecyclerView recyclerOrdersView = view.findViewById(R.id.ll_orders_container).findViewById(R.id.rv_orders);
        recyclerOrdersView.setAdapter(new OrderItemAdapter(getContext(), pendingOrders, this));
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Order> deliveredOrders;
        deliveredOrders = MockData.getOrders(Order.OrderStatus.DELIVERED);

        float shipCost = 0;
        for (Order order : deliveredOrders) {
            shipCost += order.getShipCost();
        }

        _tvMyBalance = view.findViewById(R.id.tv_my_balance);
        _tvMyBalance.setText(String.valueOf(shipCost));

        return view;
    }

    @Override
    public void onItemClick(Order order) {
        int fragmentId = this.getId();
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment(order);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(fragmentId, orderDetailFragment)
                .addToBackStack(null)
                .commit();
    }
}
