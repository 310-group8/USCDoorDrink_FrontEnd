package com.example.uscdoordrink_frontend;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.UserService;
import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.*;

import android.content.Intent;
import android.util.Log;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SignUpActivityTest {
    @Rule
    public ActivityScenarioRule<SignUpActivity> signUpActivityActivityScenarioRule =
            new ActivityScenarioRule<SignUpActivity>(SignUpActivity.class);

    private String userName = "Testcase3";
    private String password = "12345";
    private String contactInfo = "12345";
    private String sellerName = "Testcase4";
    private String sellerPassword = "23456";
    private String sellerCI = "23456";
    String TAG = "SignUpActivityTest";
    @Before
    public void setUp() throws Exception {
        Intents.init();
    }

    @Test
    public void testCustomerSignUpToLoginCorrectScenario() throws InterruptedException{
        //Input the userName and password
        Espresso.onView(withId(R.id.et_account)).perform(typeText(userName));
        Espresso.onView(withId(R.id.et_password)).perform(typeText(password));
        Espresso.onView(withId(R.id.et_ci)).perform(typeText(contactInfo));
        closeSoftKeyboard();
        Espresso.onView(withId(R.id.customer)).perform(click());

        Espresso.onView(withId(R.id.btn_register)).perform(click());
        //See whether it will login successfully
        Thread.sleep(1000);
        intended(hasComponent(LoginActivity.class.getName()));
        UserService userService = new UserService();
        userService.deleteUserByName(userName, new OnSuccessCallBack<Void>() {
            @Override
            public void onSuccess(Void input) {
                Log.d(TAG, "delete successfully!");
            }
        }, new OnFailureCallBack<Exception>() {
            @Override
            public void onFailure(Exception input) {
                Log.d(TAG, "delete unsuccessfully!");
            }
        });
    }

    @Test
    public void testSellerSignUpToLoginCorrectScenario() throws InterruptedException{
        //Input the userName and password
        Espresso.onView(withId(R.id.et_account)).perform(typeText(sellerName));
        Espresso.onView(withId(R.id.et_password)).perform(typeText(sellerPassword));
        Espresso.onView(withId(R.id.et_ci)).perform(typeText(sellerCI));
        closeSoftKeyboard();
        Espresso.onView(withId(R.id.seller)).perform(click());

        Espresso.onView(withId(R.id.btn_register)).perform(click());
        //See whether it will login successfully
        Thread.sleep(2000);
        intended(hasComponent(LoginActivity.class.getName()));
        UserService userService = new UserService();
        userService.deleteUserByName(sellerName, new OnSuccessCallBack<Void>() {
            @Override
            public void onSuccess(Void input) {
                Log.d(TAG, "delete successfully!");
            }
        }, new OnFailureCallBack<Exception>() {
            @Override
            public void onFailure(Exception input) {
                Log.d(TAG, "delete unsuccessfully!");
            }
        });
    }

    @Test
    public void testUserSignUpToLoginScenario() throws InterruptedException {
        Espresso.onView(withId(R.id.Login)).perform(click());
        Thread.sleep(1000);
        intended(hasComponent(LoginActivity.class.getName()));
    }


    @After
    public void tearDown() throws Exception {
        Intents.release();
    }
}