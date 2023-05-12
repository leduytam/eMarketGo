package com.group05.emarketgo.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;
import com.group05.emarketgo.databinding.FragmentHomeBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.viewmodels.OrderViewModel;
import com.group05.emarketgo.views.adapters.OrderItemAdapter;

public class HomeFragment extends Fragment implements OrderItemAdapter.OnItemClickListener {

    private Context context;
    private static FirebaseAuth mAuth;

    private LinearLayout orderDetailLayout;

    private TextView _tvMyBalance;

    private OrderViewModel orderViewModel;

    private FragmentHomeBinding binding;



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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        orderViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new OrderViewModel.Factory(Order.OrderStatus.PENDING)).get(OrderViewModel.class);
        orderViewModel.fetch();
        RecyclerView recyclerOrdersView = binding.llOrdersContainer.findViewById(R.id.ll_orders_container).findViewById(R.id.rv_orders);
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            recyclerOrdersView.setAdapter(new OrderItemAdapter(getContext(), orders, this));
            binding.llOrdersContainer.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE );
        });
        return binding.getRoot();
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
