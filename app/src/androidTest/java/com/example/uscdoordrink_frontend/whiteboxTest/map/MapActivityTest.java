package com.example.uscdoordrink_frontend.whiteboxTest.map;

import android.Manifest;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.example.uscdoordrink_frontend.MapsActivity;
import com.example.uscdoordrink_frontend.utils.DirectionHelper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;
import static org.awaitility.Awaitility.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuxuan Liao
 * @Date: 2022/4/11 12:48
 */
@RunWith(AndroidJUnit4.class)
public class MapActivityTest {

    LatLng origin;
    Polyline polyline;

    @Rule
    public GrantPermissionRule mRuntimeLocationPermission = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public ActivityScenarioRule<MapsActivity> activityScenarioRule = new ActivityScenarioRule<MapsActivity>(MapsActivity.class);

    @Test
    public void locationPermissionTest(){
        ActivityScenario<MapsActivity> scenario = activityScenarioRule.getScenario();
        scenario.moveToState(Lifecycle.State.CREATED);
        scenario.onActivity(activity -> {
            assertTrue("has location permission", activity.isLocationPermissionGranted());
        });
    }

    @Test
    public void testSetUpMarkers(){
        activityScenarioRule.getScenario().onActivity(activity -> {
            Thread testThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    await().atMost(10, TimeUnit.SECONDS)
                            .until(() -> activity.getStoresNearby() != null && !activity.getStoresNearby().isEmpty());
                }
            });
            testThread.start();
        });
    }

    @Test
    public void testDrawPolyline(){
        ActivityScenario<MapsActivity> activityScenario = activityScenarioRule.getScenario();
        LatLng destination = new LatLng(34.0246, -118.2840);
        activityScenario.moveToState(Lifecycle.State.CREATED);
        activityScenario.onActivity(activity -> {
           assertTrue("has service", activity.hasDirectionService());
           origin = new LatLng(activity.getLastKnownLocation().getLatitude(), activity.getLastKnownLocation().getLongitude());
           polyline = activity.getMPolyline();
           activity.drawPoly(origin,
                   destination,
                   DirectionHelper.Modes.driving);
        });
        await().atMost(20, TimeUnit.SECONDS).until(() -> polyline != null);
        List<LatLng> actualList = polyline.getPoints();
/*        assertEquals( "start of the line", origin, actualList.get(0));
        assertEquals("end of the line", destination, actualList.get(-1));*/
    }
}
