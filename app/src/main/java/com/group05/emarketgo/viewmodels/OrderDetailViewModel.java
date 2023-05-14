package com.group05.emarketgo.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.group05.emarketgo.models.Address;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.models.OrderProduct;
import com.group05.emarketgo.models.User;
import com.group05.emarketgo.repositories.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class OrderDetailViewModel extends ViewModel {
    private static OrderRepository orderRepository = OrderRepository.getInstance();
    private final MutableLiveData<List<OrderProduct>> products;
    private final MutableLiveData<Boolean> isLoading;

    private final String orderId;

    private final MutableLiveData<Boolean> isDeliveringOrder;

    public OrderDetailViewModel(String orderId) {
        products = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        isDeliveringOrder = new MutableLiveData<>(false);
        this.orderId = orderId;

        fetchProducts();
    }

    public OrderDetailViewModel() {
        products = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        isDeliveringOrder = new MutableLiveData<>(false);
        this.orderId = "";
    }

    public LiveData<Boolean> isDeliveringOrder() {
        return isDeliveringOrder;
    }

    public void setIsDeliveringOrder(boolean isDeliveringOrder) {
        this.isDeliveringOrder.setValue(isDeliveringOrder);
    }

    public LiveData<List<OrderProduct>> getProducts() {
        return products;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void fetchProducts() {
        isLoading.setValue(true);
        orderRepository.getOrderDetail(orderId).thenAccept(products -> {
            this.products.setValue(products);
            isLoading.setValue(false);
        }).exceptionally(throwable -> {
            isLoading.setValue(false);
            return null;
        });
    }

    public CompletableFuture<Address> getAddress(String orderId) {
        return orderRepository.getAddressByOrderId(orderId);
    }

    public CompletableFuture<User> getUser(String orderId) {
        return orderRepository.getUserByOrderId(orderId);
    }

    public CompletableFuture<Order.OrderStatus> getStatus(String orderId) {
        return orderRepository.getOrderStatus(orderId);
    }


    public static class Factory implements ViewModelProvider.Factory {
        private final String orderId;

        public Factory(String orderId) {
            this.orderId = orderId;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new OrderDetailViewModel(orderId);
        }
    }

    public CompletableFuture<Void> cancelOrder() {
        return orderRepository.setOrderStatus(orderId, Order.OrderStatus.CANCELLED);
    }
}
