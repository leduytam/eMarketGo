package com.group05.emarketgo.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Order;

import java.util.List;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Order> orders;

    private OnItemClickListener onItemClickListener;

    public HistoryItemAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    public HistoryItemAdapter(Context context, List<Order> orders, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.orders = orders;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        var orderId = String.valueOf(order.getId());
        var orderStatus = order.getStatus();

        if (orderStatus == Order.OrderStatus.DELIVERED) {
            holder._tvOrderDescription.setText("Done");
            holder._tvOrderStatus.setTextColor(Color.parseColor("#FD683D"));
            holder._ivOrderImage.setImageResource(R.drawable.ic_order_delivered);
        } else if (orderStatus == Order.OrderStatus.DELIVERING) {
            holder._tvOrderStatus.setTextColor(Color.parseColor("#B473FE"));
            holder._tvOrderDescription.setText("Shipping");
            holder._ivOrderImage.setImageResource(R.drawable.ic_order_delivering);
        } else if (orderStatus == Order.OrderStatus.PENDING) {
            holder._tvOrderStatus.setTextColor(Color.parseColor("#FBBC05"));
            holder._tvOrderDescription.setText("Waiting for confirmation");
            holder._ivOrderImage.setImageResource(R.drawable.ic_order_tracking);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(order);
            }
        });

        holder._tvOrderId.setText(orderId);
        holder._tvOrderStatus.setText(orderStatus.toString());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView _ivOrderImage;

        private final TextView _tvOrderDescription;

        private final TextView _tvOrderId;

        private final TextView _tvOrderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _ivOrderImage = itemView.findViewById(R.id.iv_order_image);
            _tvOrderDescription = itemView.findViewById(R.id.tv_order_description);
            _tvOrderId = itemView.findViewById(R.id.tv_order_id);
            _tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(Order order);
    }
}
