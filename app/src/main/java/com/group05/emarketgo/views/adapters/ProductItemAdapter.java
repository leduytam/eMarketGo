package com.group05.emarketgo.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group05.emarketgo.R;
import com.group05.emarketgo.models.Product;

import java.util.List;

public class ProductItemAdapter extends RecyclerView.Adapter<ProductItemAdapter.ViewHolder> {
    private final Context context;
    private final List<Product> products;

    public ProductItemAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        var productName = product.getName();
        var productQuantity = 2;

        holder._etName.setText(productName);
        holder._etQuantity.setText(String.valueOf(productQuantity));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final EditText _etName;

        private final EditText _etQuantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            _etName = itemView.findViewById(R.id.et_product_name);
            _etQuantity = itemView.findViewById(R.id.et_product_quantity);
        }
    }
}
