package com.deadswine.compass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.deadswine.library.location.ManagerLocation;
import com.deadswine.library.location.ManagerMagnetometer;
import com.deadswine.library.location.Otto.EventLocationChanged;
import com.deadswine.library.location.Otto.Otto;
import com.deadswine.library.view.compass.FragmentCompass;
import com.deadswine.library.view.compass.FragmentCompassMap;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentCompassMap.InterfaceMap {

    private TextView tvHello;


    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private FragmentCompass mFragmentCompass;
    private FragmentCompassMap mFragmentMapCompass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvHello = (TextView) findViewById(R.id.main_tv_hello);

        mFragmentCompass = new FragmentCompass();

        mFragmentMapCompass = new FragmentCompassMap();
        mFragmentMapCompass.addInterface(this);

        getAdapter().addFragment(mFragmentCompass, "Compass", false);
        getAdapter().addFragment(mFragmentMapCompass, "Map", true);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(getAdapter());


        // mViewPager.setOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(2);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ManagerLocation.getInstance(getApplicationContext()).locationToggle()) {
                    ManagerMagnetometer.getInstance(getApplicationContext()).start();

                    Snackbar.make(view, "Location tracking enabled", Snackbar.LENGTH_LONG)
                            //.setAction("Action", null)
                            .show();

                } else {
                    ManagerMagnetometer.getInstance(getApplicationContext()).stop();

                    Snackbar.make(view, "Location tracking disable", Snackbar.LENGTH_LONG)
                            //.setAction("Action", null)
                            .show();
                }

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        Otto.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Otto.getInstance().register(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public ViewPagerAdapter getAdapter() {

        if (mAdapter == null) {

            mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        }

        return mAdapter;
    }


    @Subscribe
    public void onEventLocationChanged(EventLocationChanged event) {

        tvHello.setText("Location: " + event.getLocation().toString());

        mFragmentMapCompass.setLocation(event.getLocation());
    }

    @Override
    public void onMapTargetChoosen(LatLng targetLatLng) {

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


}
