package com.example.uscdoordrink_frontend.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.OrderManagementActivity;
import com.example.uscdoordrink_frontend.ViewOrderActivity;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.Duration;
import java.time.Instant;

public class OrderNotificationService extends Service{
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "UserService";

    public OrderNotificationService() {
    }

    public void addRequest(@NonNull Request r, final OnSuccessCallBack<Void> successCallBack,
                           final OnFailureCallBack<Exception> failureCallBack) {
        DocumentReference customerRef = db.collection("Request").document(r.getName());
        customerRef.set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot added");
                successCallBack.onSuccess(unused);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                failureCallBack.onFailure(e);
            }
        });

        DocumentReference sellerRef = db.collection("Request").document(r.getStoreUID());
        sellerRef.set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot added");
                successCallBack.onSuccess(unused);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                failureCallBack.onFailure(e);
            }
        });
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        orderListener();
//        return super.onStartCommand(intent, flags, startId);
//    }



    public void showNotification(String msg, String status){
        Intent i;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "1011001010";
        CharSequence channelName = "USCDoorDrink";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(notificationChannel);

        if(status.equals("0")){
            i = new Intent(OrderNotificationService.this, OrderManagementActivity.class);
        }else{
            i = new Intent(OrderNotificationService.this, ViewOrderActivity.class);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity
                (getBaseContext(), 0,  i, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("EDMT")
                .setContentTitle("USDoorDrink")
                .setContentText(msg)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        assert notificationManager != null;
        notificationManager.notify(2, builder.build());
    }

    public void updateRequest(Request r) {
        DocumentReference ref = db.collection("Request").document();
        ref.update(r.getName(), r);
        ref.update(r.getStoreUID(), r);
    }

    public void orderListener(){
        final DocumentReference docRef = db.collection("Request").document();
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException ex) {
                if (ex != null) {
                    Log.w(TAG, "Listen failed.", ex);
                    return;
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, source + " data: " + snapshot.getData());
                    Request r = snapshot.toObject(Request.class);

                    // seller receive order
                    if(r.getStatus() == "0" && (r.getStoreUID().equals(Constants.currentUser.getStoreUID()))) {
                        Constants.currentRequest = r;
                        Constants.currentUser.setCurrentOrder(r.getOrders());
                        showNotification("You have a new order! See in app for more details ---->", r.getStatus());
                    }

                    // customer get notified when drinks are on the way
                    if(r.getStatus() == "1" && (r.getName().equals(Constants.currentUser.getUserName()))){
                        showNotification("Your drinks are one the way ---->", r.getStatus());
                    }

                    // notify customer drinks have arrived
                    if(r.getStatus() == "2" && (r.getName().equals(Constants.currentUser.getUserName()))) {
                        Instant s = r.getStart();
                        Instant e = r.getEnd();
                        Duration timeElapsed = Duration.between(s, e);
                        showNotification("Your drinks have arrived at"+ e.toString() +"! ---->\n" + "Your delivery takes " + timeElapsed.toMinutes() + "minutes.", r.getStatus());
                    }
                } else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });
    }
}

