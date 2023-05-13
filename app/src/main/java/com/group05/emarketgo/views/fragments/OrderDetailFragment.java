package com.group05.emarketgo.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.databinding.FragmentOrderDetailBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.models.OrderProduct;
import com.group05.emarketgo.viewmodels.AddressViewModel;
import com.group05.emarketgo.viewmodels.OrderViewModel;
import com.group05.emarketgo.views.activities.MapActivity;
import com.group05.emarketgo.views.adapters.ProductItemAdapter;

import java.util.List;

public class OrderDetailFragment extends Fragment {

    private Context context;
    private static FirebaseAuth mAuth;

    private RadioButton rbPending;
    private RadioButton rbOnProcess;
    private RadioButton rbDelivered;

    private Order order;

    private OrderViewModel orderViewModel;

    private AddressViewModel addressViewModel;

    private FragmentOrderDetailBinding binding;

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
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        Context context = binding.getRoot().getContext();

        MaterialToolbar topBar = binding.topBar;
        topBar.setNavigationOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        orderViewModel = new OrderViewModel();

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
