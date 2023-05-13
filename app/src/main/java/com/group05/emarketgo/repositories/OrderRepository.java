package com.group05.emarketgo.repositories;


import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.group05.emarketgo.models.Address;
import com.group05.emarketgo.models.CartItem;
import com.group05.emarketgo.models.Order;
import com.group05.emarketgo.models.OrderProduct;
import com.group05.emarketgo.models.Product;
import com.group05.emarketgo.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class OrderRepository {
    private static OrderRepository instance;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    private OrderRepository() {
    }

    // get all orders of current user without status
    public CompletableFuture<List<Order>> getAll() {
        CompletableFuture<List<Order>> future = new CompletableFuture<>();

        var user = auth.getCurrentUser();

        if (user == null) {
            future.complete(new ArrayList<>());
            return future;
        }
        db.collection("deliverymen").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var userDoc = task.getResult();
                String email = userDoc.getString("email");
                var userRef = db.collection("deliverymen").document(user.getUid());
                Query query = db.collection("orders").whereEqualTo("deliverymanRef", userRef);

                query.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        var orderDocs = task1.getResult().getDocuments();

                        if (orderDocs.size() == 0) {
                            future.complete(new ArrayList<>());
                            return;
                        }

                        List<Order> orders = new ArrayList<>();
                        for (var doc : orderDocs) {
                            String id = doc.getId();
                            Date updatedAt = doc.getDate("updatedAt");
                            Date createdAt = doc.getDate("createdAt");
                            double totalPrice = 0.0;
                            if (doc.contains("totalPrice") && doc.get("totalPrice") != null) {
                                totalPrice = doc.getDouble("totalPrice");
                            }
                            Order.OrderStatus currentStatus = Order.OrderStatus.valueOf(doc.getString("status"));
                            Order order = new Order(id, currentStatus, updatedAt, createdAt, totalPrice);
                            Query query1 = db.collection("orders").document(id).collection("products");
                            query1.get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    var orderItemDocs = task2.getResult().getDocuments();

                                    if (orderItemDocs.size() == 0) {
                                        future.complete(new ArrayList<>());
                                        return;
                                    }

                                    for (var doc1 : orderItemDocs) {
                                        DocumentReference productRef = doc1.getDocumentReference("productRef");
                                        int quantity = doc1.getLong("quantity").intValue();

                                        productRef.get().addOnCompleteListener(productTask -> {
                                            if (productTask.isSuccessful()) {
                                                Product product = productTask.getResult().toObject(Product.class);
                                                order.addOrderProduct(new OrderProduct(product, quantity, id));
                                            }
                                        });
                                    }
                                }
                            });
                            orders.add(order);
                        }
                        future.complete(orders);
                    }
                });
            }
        });
        return future;
    }

    public CompletableFuture<List<Order>> getOrders(Order.OrderStatus status) {
        CompletableFuture<List<Order>> future = new CompletableFuture<>();

        var user = auth.getCurrentUser();

        if (user == null) {
            future.complete(new ArrayList<>());
            return future;
        }
        Query query = db.collection("orders").whereEqualTo("status", status.toString());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var orderDocs = task.getResult().getDocuments();
                if (orderDocs.size() == 0) {
                    future.complete(new ArrayList<>());
                    return;
                }
                List<Order> orders = new ArrayList<>();
                for (var doc : orderDocs) {
                    String id = doc.getId();
                    Date updatedAt = doc.getDate("updatedAt");
                    Date createdAt = doc.getDate("createdAt");
                    double totalPrice = doc.getDouble("totalPrice");
                    Order.OrderStatus currentStatus = Order.OrderStatus.valueOf(doc.getString("status"));
                    Order order = new Order(id, currentStatus, updatedAt, createdAt, totalPrice);
                    Query query1 = db.collection("orders").document(id).collection("products");
                    query1.get().addOnCompleteListener(task2 -> {
                        if (task2.isSuccessful()) {
                            var orderItemDocs = task2.getResult().getDocuments();

                            if (orderItemDocs.size() == 0) {
                                future.complete(new ArrayList<>());
                                return;
                            }

                            for (var doc1 : orderItemDocs) {
                                DocumentReference productRef = doc1.getDocumentReference("productRef");
                                int quantity = doc1.getLong("quantity").intValue();

                                productRef.get().addOnCompleteListener(productTask -> {
                                    if (productTask.isSuccessful()) {
                                        Product product = productTask.getResult().toObject(Product.class);
                                        order.addOrderProduct(new OrderProduct(product, quantity, id));
                                    }
                                });
                            }
                        }
                    });
                    orders.add(order);
                }
                future.complete(orders);
            }
        });
        return future;
    }

    public CompletableFuture<Void> placeOrder(List<CartItem> cart) throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = new CompletableFuture<>();

        var user = auth.getCurrentUser();

        if (user == null) {
            future.complete(null);
            return future;
        }
        var userRef = db.collection("deliverymen").document(auth.getCurrentUser().getUid());
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("deliverymenRef", userRef);
        orderData.put("createdAt", new Date());
        orderData.put("updatedAt", new Date());
        orderData.put("status", Order.OrderStatus.PENDING);
        //calculate total price
        double totalPrice = 0;
        for (var item : cart) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        orderData.put("totalPrice", totalPrice);
        FirebaseFirestore.getInstance().collection("orders").add(orderData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                addProducts(cart, task.getResult().getId());
                future.complete(null);
            } else {
                throw new IllegalStateException(task.getException());
            }
        });
        return future;
    }

    private void addProducts(List<CartItem> cart, String orderId) {
        var batch = db.batch();
        var orderRef = db.collection("orders").document(orderId).collection("products");
        for (var item : cart) {
            Map<String, Object> data = new HashMap<>();
            DocumentReference productRef = db.collection("products").document(item.getProduct().getId());

            data.put("productRef", productRef);
            data.put("quantity", item.getQuantity());

            batch.set(orderRef.document(), data);
        }
        batch.commit();
    }

    public CompletableFuture<List<OrderProduct>> getOrderDetail(String orderId) {
        Query query = db.collection("orders").document(orderId).collection("products");
        CompletableFuture<List<OrderProduct>> future = new CompletableFuture<>();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var orderItemDocs = task.getResult().getDocuments();

                if (orderItemDocs.size() == 0) {
                    future.complete(new ArrayList<>());
                    return;
                }

                List<OrderProduct> orderProducts = new ArrayList<>();
                for (var doc1 : orderItemDocs) {
                    DocumentReference productRef = doc1.getDocumentReference("productRef");
                    productRef.get().addOnCompleteListener(productTask -> {
                        if (productTask.isSuccessful()) {
                            Product product = productTask.getResult().toObject(Product.class);
                            int quantity = doc1.getLong("quantity").intValue();
                            orderProducts.add(new OrderProduct(product, quantity, orderId));
                            if (orderProducts.size() == orderItemDocs.size()) {
                                future.complete(orderProducts);
                            }
                        }
                    });
                }
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public CompletableFuture<Void> updateDeliverymanRef(String orderId, String deliverymanId) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        DocumentReference deliverymanRef = db.collection("deliverymen").document(deliverymanId);
        db.collection("orders").document(orderId).update("deliverymanRef", deliverymanRef).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                future.complete(null);
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }


    public CompletableFuture<User> getUserByOrderId(String orderId) {
        CompletableFuture<User> future = new CompletableFuture<>();
        db.collection("orders").document(orderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var doc = task.getResult();
                DocumentReference userRef = doc.getDocumentReference("userRef");

                if (userRef != null) {
                    userRef.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            var data = task1.getResult();
                            String userFullName = data.getString("fullName");
                            String userPhoneNumber = data.getString("phoneNumber");
                            User user = new User();
                            user.setFullName(userFullName);
                            user.setPhoneNumber(userPhoneNumber);
                            future.complete(user);
                        } else {
                            future.completeExceptionally(task1.getException());
                        }
                    });
                } else {
                    future.completeExceptionally(new Exception("User reference not found."));
                }
            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }

    public CompletableFuture<Address> getAddressByOrderId(String orderId) {
        CompletableFuture<Address> future = new CompletableFuture<>();
        db.collection("orders").document(orderId).collection("address").get().addOnCompleteListener(task -> {
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

    public CompletableFuture<Order> getOrderById(String id) {
        CompletableFuture<Order> future = new CompletableFuture<>();
        db.collection("orders").document(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                var doc = task.getResult();
                Log.d("Order", doc.toString());

                Date updatedAt = doc.getDate("updatedAt");
                Date createdAt = doc.getDate("createdAt");
                double totalPrice = doc.getDouble("totalPrice");
                Order.OrderStatus currentStatus = Order.OrderStatus.valueOf(doc.getString("status"));
                Order order = new Order(id, currentStatus, updatedAt, createdAt, totalPrice);
                //get all cart items of this order
                Query query1 = db.collection("orders").document(id).collection("products");
                query1.get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        var orderItemDocs = task2.getResult().getDocuments();

                        if (orderItemDocs.size() == 0) {
                            future.complete(new Order());
                            return;
                        }

                        for (var doc1 : orderItemDocs) {
                            DocumentReference productRef = doc1.getDocumentReference("productRef");
                            int quantity = doc1.getLong("quantity").intValue();

                            productRef.get().addOnCompleteListener(productTask -> {
                                if (productTask.isSuccessful()) {
                                    Product product = productTask.getResult().toObject(Product.class);
                                    order.addOrderProduct(new OrderProduct(product, quantity, id));
                                    future.complete(order);
                                }
                            });
                        }
                    }
                });
            }
        });
        return future;
    }

    public CompletableFuture<Void> setOrderStatus(String orderId, Order.OrderStatus status) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        db.collection("orders").document(orderId).update("status", status).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (status == Order.OrderStatus.DELIVERED) {
                    Query query = db.collection("orders").document(orderId).collection("products");
                    query.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            var orderItemDocs = task1.getResult().getDocuments();

                            if (orderItemDocs.size() == 0) {
                                future.complete(null);
                                return;
                            }

                            for (var doc1 : orderItemDocs) {
                                DocumentReference productRef = doc1.getDocumentReference("productRef");
                                Integer quantity = doc1.getLong("quantity").intValue();
                                db.collection("products").document(productRef.getId()).update("sold", FieldValue.increment(quantity));
                            }
                        }
                    });
                    future.complete(null);
                }

            } else {
                future.completeExceptionally(task.getException());
            }
        });
        return future;
    }


}
