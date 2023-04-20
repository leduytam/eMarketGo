package com.group05.emarketgo.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.MockData;
import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.views.adapters.HistoryItemAdapter;

import java.util.List;

public class HistoryFragment extends Fragment implements HistoryItemAdapter.OnItemClickListener {

    private Context context;
    private static FirebaseAuth mAuth;

    public HistoryFragment() {
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        context = getContext();

        List<Order> orders;
        orders = MockData.getOrders();
        RecyclerView recyclerOrdersView = view.findViewById(R.id.ll_orders_history_container).findViewById(R.id.rv_orders_history);
        recyclerOrdersView.setAdapter(new HistoryItemAdapter(getContext(), orders, this));
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));


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
