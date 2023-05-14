package com.group05.emarketgo.repositories;

import static com.group05.emarketgo.schemas.DeliverymenSchema.COLLECTION_NAME;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group05.emarketgo.models.DeliveryMan;

import java.util.concurrent.CompletableFuture;

public class DeliverymanRepository {

    public DeliverymanRepository() {
    }

//    public static void updateUser(User user, String uuid) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put(EMAIL, user.getEmail());
//        userMap.put(PHONE_NUMBER, user.getPhoneNumber());
//        userMap.put(FULL_NAME, user.getFullName());
//        userMap.put(ADDRESS, user.getAddress());
//        userMap.put(BIRTHDAY, user.getBirthday());
//
//        userRef.update(userMap);
//    }
//
//    public static void createUser(User user, String uuid) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
//        Map<String, Object> userMap = new HashMap<>();
//        userMap.put(EMAIL, user.getEmail());
//        userMap.put(PHONE_NUMBER, user.getPhoneNumber());
//        userMap.put(FULL_NAME, user.getFullName());
//        userMap.put(ADDRESS, user.getAddress());
//        userMap.put(BIRTHDAY, user.getBirthday());
//        userRef.set(userMap);
//    }

    public static CompletableFuture<DeliveryMan> getDelverymanById(String uuid) {
        CompletableFuture<DeliveryMan> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DeliveryMan deliveryMan = task.getResult().toObject(DeliveryMan.class);
                future.complete(deliveryMan);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

//    public static CompletableFuture<Void> setUserAddress(Address address, boolean isDefault) {
//        CompletableFuture<Void> future = new CompletableFuture<>();
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        assert firebaseUser != null;
//        String userUid = firebaseUser.getUid();
//        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(COLLECTION_NAME).document(userUid);
//        if (documentReference != null) {
//            Map<String, Object> addressMap = new HashMap<>();
//            addressMap.put(ADDRESS, address.getAddressLine(0));
//            addressMap.put(CITY, address.getLocality());
//            addressMap.put(DISTRICT, address.getSubAdminArea());
//            addressMap.put(PROVINCE,address.getAdminArea());
//            addressMap.put(WARD, address.getSubLocality());
//            addressMap.put(COUNTRY, address.getCountryName());
//            addressMap.put(POSTAL_CODE, address.getPostalCode());
//            addressMap.put(LATITUDE, address.getLatitude());
//            addressMap.put(LONGITUDE, address.getLongitude());
//            addressMap.put(IS_DEFAULT, isDefault);
//            if (isDefault) {
//                documentReference.collection(ADDRESSES).whereEqualTo(IS_DEFAULT, true).get().addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
//                            document.getReference().update(IS_DEFAULT, false);
//                        }
//                    }
//                });
//            }
//            documentReference.collection(ADDRESSES).add(addressMap).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    String addressId = task.getResult().getId();
//                    Map<String, Object> userMap = new HashMap<>();
//                    userMap.put(ADDRESS, addressId);
//                    documentReference.update(userMap).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                            future.complete(null);
//                        } else {
//                            future.completeExceptionally(task1.getException());
//                        }
//                    });
//                }
//            });
//        }
//        return future;
//    }


    public static DeliverymanRepository getInstance() {
        return new DeliverymanRepository();
    }
}
