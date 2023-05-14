package com.group05.emarketgo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group05.emarketgo.models.DeliveryMan;
import com.group05.emarketgo.repositories.DeliverymanRepository;

public class DeliverymanViewModel extends ViewModel {
    private final DeliverymanRepository deliverymanRepository = DeliverymanRepository.getInstance();
    private final MutableLiveData<DeliveryMan> deliveryManMutableLiveData;


    public DeliverymanViewModel() {
        deliveryManMutableLiveData = new MutableLiveData<>();
    }


    public MutableLiveData<DeliveryMan> getDeliveryMan() {
        return deliveryManMutableLiveData;
    }

    public void getDeliverymanById(String deliverymanId) {
        deliverymanRepository.getDelverymanById(deliverymanId).thenAccept(deliveryMan -> {
            deliveryManMutableLiveData.setValue(deliveryMan);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

}
