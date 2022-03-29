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
import com.example.uscdoordrink_frontend.R;
import com.example.uscdoordrink_frontend.ViewOrderActivity;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OrderNotificationService extends Service {

    private ListenerRegistration registration;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "OrderNotificationService";
    String path;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public OrderNotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
                path = intent.getStringExtra("path");
                orderListener(path);
        return super.onStartCommand(intent, flags, startId);

    }


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
                (getBaseContext(), 0,  i, PendingIntent.FLAG_IMMUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("USCDoorDrink")
                .setContentTitle("USDoorDrink")
                .setContentText(msg)
                .setSmallIcon(R.drawable.icon_cart)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        assert notificationManager != null;
        notificationManager.notify(2, builder.build());
    }


//listen to database request changes and update current request
    public void orderListener(String path){
        final DocumentReference docRef = db.collection("Request").document(path);
        registration = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException ex) {
                if (ex != null) {
                    Log.w(TAG, "Listen failed.", ex);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    Request r = snapshot.toObject(Request.class);;
                    Constants.currentRequest = r;

                    // seller receive order
                    if((r.getStatus().equals("0")) && (Constants.currentUser.getUserType() == UserType.SELLER)) {
                        // Current order for seller is to manage order, current order for user is cart.
                        // When user submit current order to firebase, current order will be reset, and add request to order history.
                        // Seller's request will be added to order history when completing order.
                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!");
                        Constants.currentUser.setCurrentOrder(r.getOrders());
                        showNotification("You have a new order! See in app for more details ---->", r.getStatus());
                    }

                    // customer get notified when drinks are on the way
                    if((r.getStatus().equals("1")) && (Constants.currentUser.getUserType() == UserType.CUSTOMER)){
                        showNotification("Your drinks are one the way ---->", r.getStatus());
                    }

                    // notify customer drinks have arrived
                    if((r.getStatus().equals("2"))  && (Constants.currentUser.getUserType() == UserType.CUSTOMER)) {
                        String s = r.getStart();
                        String e = r.getEnd();
                        Instant instant_s = Instant.parse(s);
                        Instant instant_e = Instant.parse(e);
                        Duration timeElapsed = Duration.between(instant_s, instant_e);
                        showNotification("Your drinks have arrived at "+ e.toString()  + " Your delivery takes " + timeElapsed.toMinutes() + "minutes.", r.getStatus());
                    }

                    if((r.getStatus().equals("3"))  && (Constants.currentUser.getUserType() == UserType.CUSTOMER)){
                        Constants.currentRequest.setStatus("3");
                        showNotification("Cannot track your order ---->", r.getStatus());
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        registration.remove();
    }
}

