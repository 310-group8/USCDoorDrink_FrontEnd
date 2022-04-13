package com.example.uscdoordrink_frontend.whiteboxTest;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

import android.util.Log;

import com.example.uscdoordrink_frontend.entity.Order;
import com.example.uscdoordrink_frontend.entity.Request;
import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.UserService;
import com.example.uscdoordrink_frontend.whiteboxTest.storeManage.StoreServiceTest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {

    private final String TAG = "UserServiceTest";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String UID = "VNU8salaXDOGcyMIn6o7";
    private UserService userService;
    private User testUser;
    private Request r;
    private Callable<Boolean> hasUser(){
        return () -> testUser != null;
    }
    ArrayList<Request> requests = new ArrayList<Request>();
    ArrayList<Order> orders = new ArrayList<>();


    private static class Result{
        boolean completed = false;
        boolean success;
        String message;
        void complete(boolean result, String inputMsg) {
            completed = true;
            success = result;
            message = inputMsg;
        }
        Callable<Boolean> hasCompleted() {return () -> completed;}
    }

    @Before
    public void setUp() throws Exception {
        userService = new UserService();
        testUser = new User("userTest", "123456", "3333333333", UserType.CUSTOMER);
    }

    @Test
    public void a_registerTest() {
        User u = testUser;
        final Result registerResult = new Result();
        userService.register(u,  new OnSuccessCallBack<Void>() {
            @Override
            public void onSuccess(Void input) {
                Log.d(TAG, "register successful");
                registerResult.complete(true, "register successful");
            }
        }, new OnFailureCallBack<Exception>() {
            @Override
            public void onFailure(Exception input) {
                Log.w(TAG, "failed to register", input);
                registerResult.complete(false, input.getMessage());
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(registerResult.hasCompleted());
        assertTrue("registerResult", registerResult.success);
        assertEquals("registerResult msg", "register successful", registerResult.message);
    }

    @Test
    public void b_updateStoreUIDTest() {
        userService.updateStoreUID("userTest", UID);

        DocumentReference docRef = db.collection("User").document("userTest");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                assertEquals("updateStoreUIDResult ", UID, u.getStoreUID());
            }
        });
    }



    @Test
    public void c_addUserRequestTest() {
        orders.add(new Order("lemonade", "aaaa", 1, 2.0, 0.0));
        r = new Request(Instant.now().toString(), testUser.getUserName(), testUser.getContactInformation(), "44444", UID, 10.0, orders);
        userService.addUserRequest(testUser.getUserName(), r);

        DocumentReference docRef = db.collection("User").document("userTest");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                assertTrue("addUserRequestResult", u.getOrderHistory().contains(r));
            }
        });
    }

    @Test
    public void d_changeUserRequestTest() {
        r.setStatus("4");
        requests.add(r);
        userService.changeUserRequest(testUser.getUserName(), requests);

        DocumentReference docRef = db.collection("User").document("userTest");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User u = documentSnapshot.toObject(User.class);
                assertEquals("changeUserRequestResult", "4", u.getOrderHistory().get(0).getStatus());
            }
        });
    }

    @Test
    public void e_deleteUserByNameTest(){
        final Result removeUserResult = new Result();
        userService.deleteUserByName(testUser.getUserName(),
                new OnSuccessCallBack<Void>() {
                    @Override
                    public void onSuccess(Void input) {
                        Log.d(TAG, "remove user successful");
                        removeUserResult.complete(true, "remove user successful");
                    }
                }, new OnFailureCallBack<Exception>() {
                    @Override
                    public void onFailure(Exception input) {
                        Log.w(TAG, "failed to remove user with name:" + testUser.getUserName(), input);
                        removeUserResult.complete(false, input.getMessage());
                    }
                });
        await().atMost(5, TimeUnit.SECONDS).until(removeUserResult.hasCompleted());
        assertTrue("removeStoreResult", removeUserResult.success);
        assertEquals("removeStoreResultMsg", "remove store successful", removeUserResult.message);
    }
}