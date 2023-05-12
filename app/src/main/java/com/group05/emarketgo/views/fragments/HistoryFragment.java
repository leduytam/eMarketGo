package com.group05.emarketgo.views.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.group05.emarketgo.R;
import com.group05.emarketgo.databinding.FragmentHistoryBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.viewmodels.OrderViewModel;
import com.group05.emarketgo.views.adapters.HistoryItemAdapter;

public class HistoryFragment extends Fragment implements HistoryItemAdapter.OnItemClickListener {

    private Context context;
    private static FirebaseAuth mAuth;

    private OrderViewModel orderViewModel;

    private FragmentHistoryBinding binding;

    private TextView tvSearchResult;


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
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        tvSearchResult = binding.tvHistorySearchResults;
        orderViewModel = new OrderViewModel();
        orderViewModel.fetch();
        RecyclerView recyclerOrdersView = binding.llOrdersHistoryContainer.findViewById(R.id.ll_orders_history_container).findViewById(R.id.rv_orders_history);
        recyclerOrdersView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            tvSearchResult.setText(orders.isEmpty() ? "No orders found" : orders.size() + " Results");
            recyclerOrdersView.setAdapter(new HistoryItemAdapter(getContext(), orders, this));
            binding.llOrdersHistoryContainer.setVisibility(orders.isEmpty() ? View.GONE : View.VISIBLE );
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
