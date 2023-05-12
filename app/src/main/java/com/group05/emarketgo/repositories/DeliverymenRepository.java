package com.group05.emarketgo.repositories;

import static com.group05.emarketgo.schemas.DeliverymenSchema.COLLECTION_NAME;
import static com.group05.emarketgo.schemas.DeliverymenSchema.EMAIL;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group05.emarketgo.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DeliverymenRepository {

    public DeliverymenRepository() {
    }

    public static void updateUser(User user, String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(EMAIL, user.getEmail());

        userRef.update(userMap);
    }

    public static void createUser(User user, String uuid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(EMAIL, user.getEmail());
        userRef.set(userMap);
    }

    public static CompletableFuture<User> getUser(String uuid) {
        CompletableFuture<User> future = new CompletableFuture<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(COLLECTION_NAME).document(uuid);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().toObject(User.class);
                future.complete(user);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }



    public static DeliverymenRepository getInstance() {
        return new DeliverymenRepository();
    }
}

