package com.group05.emarketgo.views.dialogs;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.group05.emarketgo.R;
import com.group05.emarketgo.databinding.BottomSheetLocationBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.viewmodels.OrderViewModel;
import com.group05.emarketgo.views.activities.MapActivity;


@ExperimentalBadgeUtils
public class LocationBottomSheetDialog extends BottomSheetDialog {

    private Address deliverymanAddress;
    private Address orderAddress;

    private String customerName;

    private String customerPhone;

    private OrderViewModel orderViewModel;

    private String deliverymanRef;

    private String orderId;

    private BottomSheetLocationBinding binding;

    private MapActivity mapActivity;


    public LocationBottomSheetDialog(Context context) {
        super(context);
    }

    public void setDeliverymanAddress(Address deliverymanAddress) {
        this.deliverymanAddress = deliverymanAddress;
        if (deliverymanAddress != null) {
            updateAddress();
        }
    }

    public void setDeliverymanRef(String deliverymanRef) {
        this.deliverymanRef = deliverymanRef;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
        if (customerName != null) {
            updateCustomerName();
        }
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
        if (customerPhone != null) {
            updateCustomerPhone();
        }
    }

    private void updateCustomerName() {
        if (binding != null) {
            binding.tvCustomerName.setText(customerName);
        }
    }

    private void updateCustomerPhone() {
        if (binding != null) {
            binding.tvCustomerPhone.setText(customerPhone);
        }
    }

    public void setOrderAddress(Address orderAddress) {
        this.orderAddress = orderAddress;
        if (orderAddress != null) {
            updateAddress();
        }
    }

    private void updateAddress() {
        if (binding != null) {
            binding.tvDeliverymanLocation.setText(deliverymanAddress.getAddressLine(0));
            binding.tvOrderLocation.setText(orderAddress.getAddressLine(0));
        }
    }


    @Nullable
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BottomSheetLocationBinding.bind(getLayoutInflater().inflate(R.layout.bottom_sheet_location, null));

        this.setContentView(binding.getRoot());
        this.setCanceledOnTouchOutside(true);
        this.setCancelable(true);
        this.setOnShowListener(dialog -> {
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((android.view.View) this.findViewById(com.google.android.material.R.id.design_bottom_sheet));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        binding.btnCloseBottomSheet.setOnClickListener(v -> dismiss());
        if (orderAddress != null && deliverymanAddress != null && customerName != null && customerPhone != null) {
            binding.tvDeliverymanLocation.setText(deliverymanAddress.getAddressLine(0));
            binding.tvOrderLocation.setText(orderAddress.getAddressLine(0));
            binding.tvCustomerName.setText(customerName);
            binding.tvCustomerPhone.setText(customerPhone);
        }

        binding.btnConfirmLocation.setOnClickListener(v -> {
            orderViewModel = new OrderViewModel();
            orderViewModel.updateOrderDeliverymanByOrderId(orderId, deliverymanRef);
            orderViewModel.updateStatus(orderId, Order.OrderStatus.DELIVERING);
            dismiss();
        });

    }
}
