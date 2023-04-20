package com.group05.emarketgo.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.models.Product;
import com.group05.emarketgo.views.adapters.ProductItemAdapter;

import java.util.List;

public class OrderDetailFragment extends Fragment {

    private Context context;
    private static FirebaseAuth mAuth;

    private RadioButton rbPending;
    private RadioButton rbOnProcess;
    private RadioButton rbDelivered;

    private Order order;

    private TextView textView;

    public OrderDetailFragment() {
    }

    public OrderDetailFragment(Order order) {
        this.order = order;
    }

    public static OrderDetailFragment newInstance() {
        return new OrderDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        context = getContext();

        MaterialToolbar topBar = view.findViewById(R.id.top_bar);
        topBar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        rbPending = view.findViewById(R.id.rb_pending);
        rbOnProcess = view.findViewById(R.id.rb_on_process);
        rbDelivered = view.findViewById(R.id.rb_delivered);

        rbPending.setOnClickListener(v -> {
            order.setStatus(Order.OrderStatus.PENDING);
        });

        rbOnProcess.setOnClickListener(v -> {
            order.setStatus(Order.OrderStatus.DELIVERING);
        });

        rbDelivered.setOnClickListener(v -> {
            order.setStatus(Order.OrderStatus.DELIVERED);
        });

        List<Product> products = order.getProducts();

        var orderStatus = order.getStatus();

        if (orderStatus == Order.OrderStatus.PENDING) {
            rbPending.setChecked(true);
        } else if (orderStatus == Order.OrderStatus.DELIVERING) {
            rbOnProcess.setChecked(true);
        } else if (orderStatus == Order.OrderStatus.DELIVERED) {
            rbDelivered.setChecked(true);
        }

        RecyclerView recyclerOrdersView = view.findViewById(R.id.ll_products_container).findViewById(R.id.rv_products);
        recyclerOrdersView.setAdapter(new ProductItemAdapter(getContext(), products));
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
