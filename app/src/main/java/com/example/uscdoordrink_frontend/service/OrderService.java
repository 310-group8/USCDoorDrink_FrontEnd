package com.example.uscdoordrink_frontend.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class OrderService {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "OrderService";

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
    public void updateRequest(Request r) {
        //update request(status) for both customers and sellers
        DocumentReference customRef = db.collection("Request").document(r.getName());
        DocumentReference sellerRef = db.collection("Request").document(r.getStoreUID());
        customRef.set(r);
        sellerRef.set(r);
    }
}
