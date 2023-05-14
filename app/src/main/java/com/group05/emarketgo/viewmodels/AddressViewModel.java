package com.group05.emarketgo.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.group05.emarketgo.models.Address;
import com.group05.emarketgo.repositories.AddressRepository;

public class AddressViewModel extends ViewModel {
    AddressRepository addressRepository = AddressRepository.getInstance();

    private final MutableLiveData<Address> userAddress;

    public AddressViewModel() {
        userAddress = new MutableLiveData<>();
        fetch();
    }

    public void fetch() {
        addressRepository.getUserAddress().thenAccept(address -> {
            userAddress.setValue(address);
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }

    public MutableLiveData<Address> getUserAddress() {
        return userAddress;
    }
}
