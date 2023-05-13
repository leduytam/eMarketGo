package com.group05.emarketgo.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.group05.emarketgo.models.Address;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.repositories.OrderRepository;

import java.util.List;

public class OrderViewModel extends ViewModel {
    private final OrderRepository orderRepo = OrderRepository.getInstance();
    private final MutableLiveData<List<Order>> orders;
    private final MutableLiveData<Boolean> isLoading;
    private final Order.OrderStatus status;

    public OrderViewModel(Order.OrderStatus status) {
        this.status = status;
        orders = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(true);

        fetch();
    }

    public OrderViewModel() {
        this.status = null;
        orders = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(true);

        fetch();
    }

    public void fetch() {
        isLoading.setValue(true);
        if (status == null) {
            orderRepo.getAll().thenAccept(orders -> {
                Log.d("OrderPendingFragment", "fetch: " + orders);
                this.orders.setValue(orders);
                isLoading.setValue(false);
            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
            return;
        }
        orderRepo.getOrders(status).thenAccept(orders -> {
            this.orders.setValue(orders);
            isLoading.setValue(false);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public void updateStatus(String id, Order.OrderStatus status) {
        orderRepo.setOrderStatus(id, status).thenAccept(isSuccess -> {
            fetch();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public void addDeliverymanAddressByOrderId(String orderId, Address deliverymanAddress) {
        orderRepo.addDeliverymanAddress(orderId, deliverymanAddress);
    }


    public void updateOrderDeliverymanByOrderId(String orderId, String deliverymanId) {
        orderRepo.updateDeliverymanRef(orderId, deliverymanId);
    }

    public MutableLiveData<List<Order>> getOrders() {
        return orders;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Order.OrderStatus status;

        public Factory(Order.OrderStatus status) {
            this.status = status;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new OrderViewModel(status);
        }
    }

}
