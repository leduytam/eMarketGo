package com.group05.emarketgo.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group05.emarketgo.R;
import com.group05.emarketgo.databinding.ActivityMapBinding;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.repositories.AddressRepository;
import com.group05.emarketgo.viewmodels.OrderDetailViewModel;
import com.group05.emarketgo.viewmodels.OrderViewModel;
import com.group05.emarketgo.views.dialogs.LocationBottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@ExperimentalBadgeUtils
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_CODE_GPS_PERMISSION = 100;
    private static final int MAP_ZOOM = 1000;

    private OrderDetailViewModel orderDetailViewModel;

    private OrderViewModel orderViewModel;
    private static FirebaseAuth mAuth;

    private Geocoder geocoder;
    private ActivityMapBinding binding;
    private LocationBottomSheetDialog locationBottomSheetDialog;

    private String orderId;

    private Boolean isDeliveringOrder = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        mAuth = FirebaseAuth.getInstance();
        binding = ActivityMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        orderDetailViewModel = new OrderDetailViewModel(orderId);
        orderViewModel = new OrderViewModel();
        locationBottomSheetDialog = new LocationBottomSheetDialog(this, orderDetailViewModel);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_app);
        geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
        binding.topBar.setNavigationOnClickListener(v -> finish());
        binding.topBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_map_refresh) {
                getCurrentLocation();
            }
            return true;
        });
        mapFragment.getMapAsync(this);

        orderDetailViewModel.getStatus(orderId).thenAccept(status -> {
            if (status == Order.OrderStatus.PENDING) {
                binding.btnDeliveringOrder.setVisibility(View.VISIBLE);
                binding.btnDeliveredOrder.setVisibility(View.GONE);
            } else if (status == Order.OrderStatus.DELIVERING) {
                binding.btnDeliveredOrder.setVisibility(View.VISIBLE);
                binding.btnDeliveringOrder.setVisibility(View.GONE);
            } else {
                binding.btnDeliveredOrder.setVisibility(View.GONE);
                binding.btnDeliveringOrder.setVisibility(View.GONE);
            }
        });

        orderDetailViewModel.isDeliveringOrder().observe(this, isDeliveringOrder -> {
            setIsDeliveredOrder(isDeliveringOrder);
            if (isDeliveringOrder) {
                binding.btnDeliveringOrder.setVisibility(View.GONE);
                binding.btnDeliveredOrder.setVisibility(View.VISIBLE);
            } else {
                binding.btnDeliveringOrder.setVisibility(View.VISIBLE);
                binding.btnDeliveredOrder.setVisibility(View.GONE);
            }
        });

        binding.btnDeliveredOrder.setOnClickListener(v -> {
            orderViewModel.updateStatus(orderId, Order.OrderStatus.DELIVERED);
            finish();
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS_PERMISSION);
        }
    }

    public void setIsDeliveredOrder(Boolean isDeliveringOrder) {
        this.isDeliveringOrder = isDeliveringOrder;
    }

    private void getFromLocationLongLat(double latitude, double longitude) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(latitude, longitude, 1, addresses -> {
                    setCurrentAddress(addresses.get(0));
                });
            } else {
                List<Address> addressList = new ArrayList<>();
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
                setCurrentAddress(addressList.get(0));
            }
        } catch (IOException e) {
            Log.e("getFromLocationLongLat", e.getMessage());
            e.printStackTrace();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        AddressRepository addressRepository = AddressRepository.getInstance();
        addressRepository.getUserAddress().thenAccept(addressModel -> {
            if (addressModel != null) {
                double latitude = addressModel.getLatitude();
                double longitude = addressModel.getLongitude();
                getFromLocationLongLat(latitude, longitude);
            } else {
                FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.getCurrentLocation(100, null).addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            getFromLocationLongLat(location.getLatitude(), location.getLongitude());
                        }
                    }
                });
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_GPS_PERMISSION:
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        orderDetailViewModel = new OrderDetailViewModel(orderId);
        orderDetailViewModel.getUser(orderId).thenAccept(userModel -> {
            if (userModel != null) {
                locationBottomSheetDialog.setCustomerName(userModel.getFullName());
                locationBottomSheetDialog.setCustomerPhone(userModel.getPhoneNumber());
            }
        });
        orderDetailViewModel.getAddress(orderId).thenAccept(addressModel -> {
            if (addressModel != null) {
                try {
                    String userAddress = addressModel.getAddress();
                    List<Address> addresses = geocoder.getFromLocationName(userAddress, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        double latitude = address.getLatitude();
                        double longitude = address.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(address.getAddressLine(0));
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        var locationMarker = mMap.addMarker(markerOptions);
                        if (locationMarker != null) {
                            locationMarker.showInfoWindow();
                            locationMarker.setDraggable(true);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM));
                        locationBottomSheetDialog.setOrderAddress(address);
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String deliverymanId = user.getUid();
                        locationBottomSheetDialog.setDeliverymanRef(deliverymanId);
                        locationBottomSheetDialog.setOrderId(orderId);
                    } else {
                        Toast.makeText(this, "Address not found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        binding.btnDeliveringOrder.setOnClickListener(v -> {
            locationBottomSheetDialog.show();
        });
    }

    void setCurrentAddress(Address address) {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(address.getAddressLine(0));
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        var locationMarker = mMap.addMarker(markerOptions);
        if (locationMarker != null) {
            locationMarker.showInfoWindow();
            locationMarker.setDraggable(true);
        }
        mMap.setOnMarkerClickListener(marker -> {
            marker.showInfoWindow();
            return true;
        });
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM));
        locationBottomSheetDialog.setDeliverymanAddress(address);
    }

}