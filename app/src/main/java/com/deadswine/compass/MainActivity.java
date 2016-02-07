package com.deadswine.compass;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.deadswine.library.location.ManagerLocation;
import com.deadswine.library.location.ManagerMagnetometer;
import com.deadswine.library.location.Otto.EventLocationChanged;
import com.deadswine.library.location.Otto.EventRequestPermission;
import com.deadswine.library.location.Otto.Otto;
import com.deadswine.library.view.compass.FragmentCompassExtended;
import com.deadswine.library.view.compass.FragmentCompassMap;
import com.deadswine.library.view.compass.Utilities.UtilitiesMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentCompassMap.InterfaceMap {

    private TextView tvHello;


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private FragmentCompassExtended mFragmentCompass;
    private FragmentCompassMap mFragmentMapCompass;
    private String TAG = this.getClass().getSimpleName();

    private final int REQUEST_CODE = 90;


    private Double angleMagnetomere;

    private LatLng mLastTarget;
    private LatLng mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentCompass = new com.deadswine.compass.FragmentCompassExtended();

        mFragmentMapCompass = new FragmentCompassMap();
        mFragmentMapCompass.addInterface(this);

        getAdapter().addFragment(mFragmentCompass, "Compass", false);
        getAdapter().addFragment(mFragmentMapCompass, "Map", true);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(getAdapter());

        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    protected void onPause() {
        super.onPause();

        ManagerMagnetometer.getInstance(getApplicationContext()).stop();

//            Snackbar.make(view, "Location tracking disable", Snackbar.LENGTH_LONG)
//                    //.setAction("Action", null)
//                    .show();

        Otto.getInstance().unregister(this);


    }

    @Override
    protected void onResume() {
        super.onResume();



        Otto.getInstance().register(this);

        ManagerLocation.getInstance(getApplicationContext()).locationToggle() ;

        ManagerMagnetometer.getInstance(getApplicationContext()).start();

        Snackbar.make(findViewById(R.id.toolbar), "Location tracking enabled", Snackbar.LENGTH_LONG)
                //.setAction("Action", null)
                .show();

    }

    @Subscribe
    public void onEventRequestPermission(EventRequestPermission event) {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

    }

    @Subscribe
    public void onEventLocationChanged(EventLocationChanged event) {

        mFragmentMapCompass.setLocation(event.getLocation());

    }

    @Override
    public void onMapTargetChoosen(LatLng targetLatLng) {
        mLastTarget = targetLatLng;

        computeTargetAngle();
    }

    @Override
    public void onMapLocationChanged(LatLng targetLatLng) {
        mLastLocation = targetLatLng;

        computeTargetAngle();
    }

    public void computeTargetAngle(){
        if(mLastLocation ==null || mLastTarget == null)
            return;

        double tmp = UtilitiesMap.calcRotationAngleInDegrees( mLastTarget,mLastLocation);
        float computedAngle = (float)tmp;
        Log.d(TAG, "computedAngle double: " + tmp) ;
        Log.d(TAG, "computedAngle float: " + computedAngle) ;
        mFragmentCompass.getViewCompass().setAngleTarget(computedAngle);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0) {
                    //FIXME probably should check for proper permission
                    ManagerLocation.getInstance(getApplicationContext()).locationToggle();
                    ManagerMagnetometer.getInstance(getApplicationContext()).start();

                }

            }

        }
    }


    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<Boolean> mFabList = new ArrayList<>();

        boolean hasAnyFab;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            hasAnyFab = false;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


        public void addFragment(Fragment fragment, String title, boolean hasFab) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mFabList.add(hasFab);
            hasAnyFab = true;
        }

        public boolean getHasFab(int position) {

            return mFabList.get(position);
        }

    }

    public ViewPagerAdapter getAdapter() {

        if (mAdapter == null) {

            mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        }

        return mAdapter;
    }

}
