package com.example.uscdoordrink_frontend.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.entity.Request;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderNotificationService extends Service {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;

    public OrderNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Request");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Request request = snapshot.getValue(Request.class);
                if(request.getStoreUID().equals(Constants.currentUser.getStoreUID()) && request.getStatus().equals("0")){
                    showNotification(snapshot.getKey(), request);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onStartCommand(intent, flags, startId);
    }


    public void showNotification(String key, Request r){
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

        Intent i = new Intent(OrderNotificationService.this, OrderManagementService.class);
//        i.putExtra("", request.getPhone());
        PendingIntent pendingIntent = PendingIntent.getActivity
                (getBaseContext(), 0,  i, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("EDMT")
                .setContentTitle("USDoorDrink")
                .setContentText("You have a new Order! See in app for more details --->")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        assert notificationManager != null;
        notificationManager.notify(2, builder.build());



    }
}

