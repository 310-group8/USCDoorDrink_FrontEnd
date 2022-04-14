package com.example.uscdoordrink_frontend.blackboxTest.storeManage;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.awaitility.Awaitility.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.intent.IntentStubberRegistry;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.*;

import android.util.Log;

import com.example.uscdoordrink_frontend.AddStoreActivity;
import com.example.uscdoordrink_frontend.Constants.Constants;
import com.example.uscdoordrink_frontend.R;
import com.example.uscdoordrink_frontend.entity.Drink;
import com.example.uscdoordrink_frontend.entity.Store;
import com.example.uscdoordrink_frontend.entity.User;
import com.example.uscdoordrink_frontend.entity.UserType;
import com.example.uscdoordrink_frontend.fragments.AddStoreName;
import com.example.uscdoordrink_frontend.service.CallBack.OnFailureCallBack;
import com.example.uscdoordrink_frontend.service.CallBack.OnSuccessCallBack;
import com.example.uscdoordrink_frontend.service.StoreService;
import com.example.uscdoordrink_frontend.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/4/12 17:12
 */
@RunWith(AndroidJUnit4.class)
public class AddStoreFragmentsTest{

    private final static String TAG = "AddStoreFragmentsTest";
    private Store localStore;
    private Store remoteStore;
    private NavController navController;

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
    public void init(){
        Constants.currentUser = null;
        navController = null;
        remoteStore = null;
        localStore = new Store();
        localStore.setStoreName("n/naka");
        localStore.setStoreAddress(34.0251, -118.4122);
        localStore.setAddressString("3455 Overland Avenue");
        Drink drink1 = new Drink();
        drink1.setDrinkName("Suntory Hibiki");
        drink1.setPrice(50);
        drink1.setIngredients(Arrays.asList("Malt", "Water"));
        Drink drink2 = new Drink();
        drink2.setDrinkName("Sake");
        drink2.setPrice(30);
        drink2.setDiscount(5);
        List<Drink> menu = new ArrayList<>();
        menu.add(drink1);
        menu.add(drink2);
        localStore.setMenu(menu);
    }

    @Test
    public void testWithEmptyName(){
        try (ActivityScenario<AddStoreActivity> scenario = ActivityScenario.launch(AddStoreActivity.class)){
            onView(withId(R.id.autocomplete_fragment)).perform(click());
            Thread.sleep(1000);
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("input text 3455%sOverland%sAve");
            Thread.sleep(10000);
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("input tap 540 300");
            Thread.sleep(3000);
            onView(withId(R.id.button_confirm_name_and_address)).perform(click());

        }catch (InterruptedException e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testWithNewSeller(){
        User u = new User("Black", "1234", "1234", UserType.SELLER);
        UserService userService = new UserService();
        final Result addUserResult = new Result();
        userService.register(u, new OnSuccessCallBack<Void>() {
            @Override
            public void onSuccess(Void input) {
                addUserResult.complete(true, "add user successful");
            }
        }, new OnFailureCallBack<Exception>() {
            @Override
            public void onFailure(Exception input) {
                addUserResult.complete(false, input.getMessage());
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(addUserResult.hasCompleted());
        assertTrue("addUserResult", addUserResult.success);

        Constants.currentUser = u;
        StoreService storeService = new StoreService();

        try (ActivityScenario<AddStoreActivity> scenario = ActivityScenario.launch(AddStoreActivity.class)){
            //prepare navController
            scenario.onActivity(activity -> {
                Fragment fragment = activity.getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                assertNotNull("NavHostFragment", fragment);
                navController = NavHostFragment.findNavController(fragment);
            });
            //add store name and address
            assertEquals("First nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreName);
            onView(withId(R.id.editTextStoreName)).perform(replaceText(localStore.getStoreName()));
            onView(withId(R.id.autocomplete_fragment)).perform(click());
            Thread.sleep(1000);
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("input text 3455%sOverland%sAve");
            Thread.sleep(10000);
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("input tap 540 300");
            Thread.sleep(3000);
            onView(withId(R.id.button_confirm_name_and_address)).perform(click());

            //add store menu
            assertEquals("Second nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreMenu);
            onView(withId(R.id.button_add_item)).perform(click());

            //add drink one
            assertEquals("Third nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreDrink);
            onView(withId(R.id.editTextAddDrinkName)).perform(replaceText(localStore.getMenu().get(0).getDrinkName()));
            onView(withId(R.id.editTextAddDrinkIngredientOne)).perform(replaceText(localStore.getMenu().get(0).getIngredients().get(0)));
            onView(withId(R.id.editTextAddDrinkIngredientTwo)).perform(replaceText(localStore.getMenu().get(0).getIngredients().get(1)));
            onView(withId(R.id.editTextAddDrinkPrice)).perform(replaceText(String.valueOf(localStore.getMenu().get(0).getPrice())));
            onView(withId(R.id.button_confirm_drink)).perform(click());

            //back to store menu
            assertEquals("Fourth nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreMenu);
            onView(withId(R.id.button_add_item)).perform(click());

            //add drink two
            assertEquals("Fifth nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreDrink);
            onView(withId(R.id.editTextAddDrinkName)).perform(replaceText(localStore.getMenu().get(1).getDrinkName()));
            onView(withId(R.id.editTextAddDrinkPrice)).perform(replaceText(String.valueOf(localStore.getMenu().get(1).getPrice())));
            onView(withId(R.id.editTextAddDrinkDiscount)).perform(replaceText(String.valueOf(localStore.getMenu().get(1).getDiscount())));
            onView(withId(R.id.button_confirm_drink)).perform(click());

            //back to store menu again
            assertEquals("Sixth nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreMenu);
            onView(withId(R.id.button_confirm_menu)).perform(click());

            //to successful page
            Thread.sleep(5000);
            assertEquals("Seventh nav Destination", Objects.requireNonNull(navController.getCurrentDestination()).getId(), R.id.addStoreSuccessful);


            final Result getStoreResult = new Result();
            storeService.getStoreByUID(Constants.currentUser.getStoreUID(),
                    new OnSuccessCallBack<Store>() {
                        @Override
                        public void onSuccess(Store input) {
                            remoteStore = input;
                            getStoreResult.complete(true, "get store successful");
                        }
                    }, new OnFailureCallBack<Exception>() {
                        @Override
                        public void onFailure(Exception input) {
                            getStoreResult.complete(false, input.getMessage());
                        }
                    });
            await().atMost(5, TimeUnit.SECONDS).until(getStoreResult.hasCompleted());
            assertTrue("getStoreResult", getStoreResult.success);
            assertTrue("compare content", localStore.equalContent(remoteStore));
        }catch (InterruptedException e){
            fail(e.getMessage());
        }
        final Result deleteStoreResult = new Result();
        storeService.deleteStoreByUID(Constants.currentUser.getStoreUID(),
                new OnSuccessCallBack<Void>() {
                    @Override
                    public void onSuccess(Void input) {
                        deleteStoreResult.complete(true, "success");
                    }
                }, new OnFailureCallBack<Exception>() {
                    @Override
                    public void onFailure(Exception input) {
                        deleteStoreResult.complete(false, input.getMessage());
                    }
                });
        await().atMost(5, TimeUnit.SECONDS).until(deleteStoreResult.hasCompleted());
        assertTrue("deleteStoreResult", deleteStoreResult.success);



    }
}
