package com.example.uscdoordrink_frontend.service;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/3/23 20:30
 */

import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

public class StoreService {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final double searchRadius = 3218.69;//2 miles
    private static final String TAG = "StoreService";

    public static class NoSuchStoreException extends Exception{
        public NoSuchStoreException(String errorMessage){
            super(errorMessage);
        }
    }

    /**
     * @param newStore, the new Store
     * @param successCallBack, an OnSuccessCallBack that contains a onSuccess call back
     *                         function. The onSuccess method will be called when the add is
     *                         completed successfully.
     * @param failureCallBack, an OnFailureCallBack that contains a onFailure call back
     *      *                  function. The onFailure method will be called when the add is
     *      *                  completed with failure.
     * @return the UID of the newStore
     *
     * Add the new Store to the database. Returns the UID of the newStore and automatically set the
     * storeUID of all the drinks its menu.
     *
     * Example to use:
     *
     * In your Activity:
     *
     *  addStore(yourNewStore,
     *           new OnSuccessCallBack<Void>() {
     *               @Override
     *               public void OnSuccess(Void unused){
     *                   // the code that you want your activity to do on success. For example, jump
     *                   // to another page. This method, as a method of an anonymous class, have
     *                   // access to all the member variables of your Activity class and local
     *                   // variables that are final.
     *               }
     *           },
     *           new OnFailureCallBack<Exception>(){
     *               @Override
     *               public void OnFailure(Exception e){
     *                   // the code you want your activity to do on failure.
     *               }
     *           })
     */
    public String addStore(Store newStore, final OnSuccessCallBack<Void> successCallBack,
                           final OnFailureCallBack<Exception> failureCallBack){
        DocumentReference documentReference = db.collection("Store").document();
        final String UID = documentReference.getId();
        newStore.setStoreUID(UID);
        for (Drink drink : newStore.getMenu()){
            drink.setStoreUID(UID);
        }
        documentReference.set(newStore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + UID);
                successCallBack.onSuccess(unused);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                failureCallBack.onFailure(e);
            }
        });
        return UID;
    }

    public void getStoreByUID(String storeUID,
                              final OnSuccessCallBack<Store> successCallBack,
                              final OnFailureCallBack<Exception> failureCallBack){
        DocumentReference docRef = db.collection("Store").document(storeUID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        successCallBack.onSuccess(document.toObject(Store.class));
                    } else {
                        Log.d(TAG, "No such document");
                        failureCallBack.onFailure(new NoSuchStoreException("No such store"));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    failureCallBack.onFailure(task.getException());
                }
            }
        });
    }

    public void getNearbyStore(Pair<Double, Double> position,
                               final OnSuccessCallBack<List<Store>> successCallBack,
                               final OnFailureCallBack<Exception> failureCallBack){
        final GeoLocation center = new GeoLocation(position.first, position.second);
        List<GeoQueryBounds> bounds = GeoFireUtils.getGeoHashQueryBounds(center, searchRadius);
        final List<Task<QuerySnapshot>> tasks = new ArrayList<>();
        for (GeoQueryBounds b : bounds) {
            Query q = db.collection("Store")
                    .orderBy("hashLocation")
                    .startAt(b.startHash)
                    .endAt(b.endHash);

            tasks.add(q.get());
        }

        // Collect all the query results together into a single list
        Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<Task<?>>> t) {
                        List<DocumentSnapshot> matchingDocs = new ArrayList<>();

                        try{
                            for (Task<QuerySnapshot> task : tasks) {
                                QuerySnapshot snap = task.getResult();
                                for (DocumentSnapshot doc : snap.getDocuments()) {
                                    Store store = doc.toObject(Store.class);
                                    if (store == null){
                                        throw new NullPointerException("The Store equals null");
                                    }
                                    double lat = store.getStoreAddress().first;
                                    double lng = store.getStoreAddress().second;
                                    // We have to filter out a few false positives due to GeoHash
                                    // accuracy, but most will match
                                    GeoLocation docLocation = new GeoLocation(lat, lng);
                                    double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                    if (distanceInM <= searchRadius) {
                                        matchingDocs.add(doc);
                                    }
                                }
                            }

                            List<Store> matchingStores = new ArrayList<>();
                            for (DocumentSnapshot doc : matchingDocs){
                                matchingStores.add(doc.toObject(Store.class));
                            }

                            Log.d(TAG, "Search Stores successful");
                            successCallBack.onSuccess(matchingStores);
                        }catch (NullPointerException e){
                            Log.w(TAG, "The query fails with a null store:", e);
                            failureCallBack.onFailure(e);
                        }catch (Exception e){
                            Log.w(TAG, "The query fails with", e);
                            failureCallBack.onFailure(e);
                        }
                    }
                });
    }
}