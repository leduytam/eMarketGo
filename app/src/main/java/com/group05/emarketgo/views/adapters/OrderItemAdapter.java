package com.group05.emarketgo.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Order;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Order> orders;
    private OnItemClickListener onItemClickListener;

    public OrderItemAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public OrderItemAdapter(Context context, List<Order> orders, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.orders = orders;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Order order = orders.get(position);
        var orderId = String.valueOf(order.getId());
        var orderStatus = order.getStatus();
        var orderDate = order.getCreated_at();

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(order);
            }
        });



            holder._tvOrderId.setText(orderId);
            holder._tvOrderDate.setText(orderDate);
            holder._tvOrderStatus.setText(orderStatus.toString());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView _tvOrderId;

        private LinearLayout _llOrderItem;

        private final TextView _tvOrderStatus;

        private final TextView _tvOrderDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _tvOrderId = itemView.findViewById(R.id.order_id);
            _tvOrderStatus = itemView.findViewById(R.id.order_status);
            _tvOrderDate = itemView.findViewById(R.id.order_time);
            _llOrderItem = itemView.findViewById(R.id.ll_order_item);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(Order order);
    }
}
