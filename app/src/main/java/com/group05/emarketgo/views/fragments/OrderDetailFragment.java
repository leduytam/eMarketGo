package com.group05.emarketgo.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;
import com.group05.emarketgo.databinding.FragmentOrderDetailBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.models.OrderProduct;
import com.group05.emarketgo.viewmodels.AddressViewModel;
import com.group05.emarketgo.viewmodels.OrderDetailViewModel;
import com.group05.emarketgo.views.activities.MapActivity;
import com.group05.emarketgo.views.adapters.ProductItemAdapter;

import java.util.List;

public class OrderDetailFragment extends Fragment {


    private static FirebaseAuth mAuth;


    private OrderDetailViewModel orderDetailViewModel;

    private Order order;


    private AddressViewModel addressViewModel;

    private FragmentOrderDetailBinding binding;


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
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        Context context = binding.getRoot().getContext();

        MaterialToolbar topBar = binding.topBar;
        topBar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        orderDetailViewModel = new OrderDetailViewModel(order.getId());
        orderDetailViewModel.getStatus(order.getId()).thenAccept(status -> {
            if (status == Order.OrderStatus.PENDING) {
                binding.btnTakeItem.setText(R.string.fragment_order_details_button_take_item);
            } else {
                binding.btnTakeItem.setText(R.string.fragment_order_details_button_check_order);
            }
        });

        List<OrderProduct> products = order.getProducts();

        addressViewModel = new ViewModelProvider(requireActivity()).get(AddressViewModel.class);
        addressViewModel.getUserAddress().observe(getViewLifecycleOwner(), userAddress -> {
        });



        binding.btnTakeItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, MapActivity.class);
            intent.putExtra("isHavingDefaultAddress", addressViewModel.getUserAddress().getValue() != null);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });


        RecyclerView recyclerOrdersView = binding.rvProducts;
        recyclerOrdersView.setAdapter(new ProductItemAdapter(getContext(), products));
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));

        return binding.getRoot();
    }
}
