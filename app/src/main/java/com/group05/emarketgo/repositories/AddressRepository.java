package com.group05.emarketgo.repositories;

import static com.group05.emarketgo.schemas.DeliverymenSchema.ADDRESSES;
import static com.group05.emarketgo.schemas.DeliverymenSchema.IS_DEFAULT;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group05.emarketgo.models.Address;
import com.group05.emarketgo.schemas.DeliverymenSchema;

import java.util.concurrent.CompletableFuture;

public class AddressRepository {
    private static AddressRepository instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static AddressRepository getInstance() {
        if (instance == null) {
            instance = new AddressRepository();
        }
        return instance;
    }

    private AddressRepository() {
    }

    public CompletableFuture<Address> getUserAddress() {
        CompletableFuture<Address> future = new CompletableFuture<>();

        var user = auth.getCurrentUser();

        if (user == null) {
            future.complete(null);
            return future;
        }

        Query query = db.collection(DeliverymenSchema.COLLECTION_NAME).document(user.getUid()).collection(DeliverymenSchema.ADDRESSES);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var addressDocs = task.getResult().getDocuments();
                if (addressDocs.size() == 0) {
                    future.complete(null);
                    return;
                }
                var defaultAddress = addressDocs.stream().filter(addressDoc -> {
                    var address = addressDoc.toObject(Address.class);
                    return address.getIsDefault();
                }).findFirst();
                var addressDoc = addressDocs.get(0);
                if (defaultAddress.isPresent()) {
                    addressDoc = defaultAddress.get();
                }
                var address = addressDoc.toObject(Address.class);
                future.complete(address);
            } else {
                future.completeExceptionally(task.getException());
            }
        });

        return future;
    }

    public CompletableFuture<Void> addAddress(Address address) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        var user = auth.getCurrentUser();

        if (user == null) {
            future.complete(null);
            return future;
        }

        var addressMap = address.toMap();
        addressMap.put(IS_DEFAULT, address.getIsDefault());

        var batch = db.batch();
        var userRef = db.collection(DeliverymenSchema.COLLECTION_NAME).document(user.getUid());
        var addressRef = userRef.collection(ADDRESSES).document();
        batch.set(addressRef, addressMap);
        if (address.getIsDefault()) {
            var query = userRef.collection(ADDRESSES).whereEqualTo(IS_DEFAULT, true);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    var addressDocs = task.getResult().getDocuments();
                    for (var addressDoc : addressDocs) {
                        batch.update(addressDoc.getReference(), IS_DEFAULT, false);
                    }
                    batch.commit().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            future.complete(null);
                        } else {
                            future.completeExceptionally(task1.getException());
                        }
                    });
                } else {
                    future.completeExceptionally(task.getException());
                }
            });
        } else {
            batch.commit().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(task.getException());
                }
            });
        }
        return future;
    }

}
