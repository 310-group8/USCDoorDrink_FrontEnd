package com.example.uscdoordrink_frontend.service;

import android.util.Log;

import androidx.annotation.NonNull;


import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "UserService";

    public void register(User u, final OnSuccessCallBack<Void> successCallBack,
                         final OnFailureCallBack<Exception> failureCallBack) {
        DocumentReference documentReference = db.collection("User").document(u.getUserName());
        documentReference.set(u).addOnSuccessListener(new OnSuccessListener<Void>() {
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


    public String isRegistered(String name) {
        String[] status = {"false"};
        DocumentReference docRef = db.collection("User").document(name);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        status[0] = "true";
                    } else {
                        Log.d(TAG, "No such document");
                        status[0] = "false";
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    status[0] = "failed";
                }
            }
        });
        return status[0];
    }

    public void updateStoreUID(String name, String UID){
        DocumentReference docRef = db.collection("User").document(name);
        docRef.update("storeUID", UID);
    }


}