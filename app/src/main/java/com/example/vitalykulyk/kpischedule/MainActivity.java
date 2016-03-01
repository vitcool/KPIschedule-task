package com.example.vitalykulyk.kpischedule;


import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.design.widget.TabLayout;

import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;

    //tabs
    private TabLayout tabLayout;
    private ViewPager viewPager;

    String day;
    EditText search_query;
    android.support.v4.app.FragmentTransaction mFragmentTransaction;
    android.support.v4.app.FragmentManager mFragmentManager;
    boolean isPressed = false;
    boolean isPressedSearch = false;
    boolean pressedButton = false;

    private Bundle savedState = null;

    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPressedSearch = false;

//        day = getNextTodayDay();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);


        search_query = (EditText) findViewById(R.id.editText);
        search_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPressedSearch == false){
                    search_query.setText("");
                }
                isPressedSearch = true;
            }
        });

        mFragmentManager = getSupportFragmentManager();

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

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

        if (id == R.id.action_search){

            ScheduleFragment.ScheduleTask scheduleTask = new ScheduleFragment.ScheduleTask();
            String query = String.valueOf(search_query.getText());



            switch (adapter.getRegisteredFragment(viewPager.getCurrentItem())){
                case "0": {
                    scheduleTask.execute(query, "TUESDAY");
                    break;
                }
                case "1": {
                    scheduleTask.execute(query, "WEDNESDAY");
                    break;
                }
                case "2": {
                    scheduleTask.execute(query, "THURSDAY");
                    break;
                }
                case "3": {
                    scheduleTask.execute(query, "FRIDAY");
                    break;
                }
            }

            Log.w("GET FRAGMENT _", adapter.getRegisteredFragment(viewPager.getCurrentItem()));
            if (!isPressed) {
                if (id == R.id.action_search) {
                    pressedButton = true;
                }
                isPressed = true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {

        if (!pressedButton) {
            ScheduleFragment monday = new ScheduleFragment();
            ScheduleFragment tuersday = new ScheduleFragment();
            ScheduleFragment wednesday = new ScheduleFragment();
            ScheduleFragment thursday = new ScheduleFragment();
            ScheduleFragment friday = new ScheduleFragment();
            ScheduleFragment saturday = new ScheduleFragment();
            ScheduleFragment sunday = new ScheduleFragment();

            adapter = new ViewPagerAdapter(getSupportFragmentManager());

            adapter.addFragment(monday, "ПН", "monday");
            adapter.addFragment(tuersday, "ВТ", "tuesday");
            adapter.addFragment(wednesday, "СР", "wednesday");
            adapter.addFragment(thursday, "ЧТ", "thursday");
            adapter.addFragment(friday, "ПТ", "friday");
            adapter.addFragment(saturday, "СБ", "suterday");
            adapter.addFragment(sunday, "НД", "sunday");
        }

        viewPager.setAdapter(adapter);

    }


    public String getTodayDay(){
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case (Calendar.TUESDAY) : {
                return "TUESDAY";
            }
            case (Calendar.WEDNESDAY) : {
                return "WEDNESDAY";
            }
            case (Calendar.THURSDAY) : {
                return "THURSDAY";
            }
            case (Calendar.FRIDAY) :{
                return "FRIDAY";
            }
            default:{
                return "TUESDAY";
            }
        }
    }



    public String getNextTodayDay(){
        String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        switch (tag){
            case ("TUESDAY") : {
                return "WEDNESDAY";
            }
            case ("WEDNESDAY") : {
                return "THURSDAY";
            }
            case ( "THURSDAY") : {
                return "FRIDAY";
            }
            default:{
                return "TUESDAY";
            }
        }
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        ScheduleFragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new ScheduleFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new ScheduleFragment();
                title = getString(R.string.title_friends);
                break;
            case 2:
                fragment = new ScheduleFragment();
                title = getString(R.string.title_messages);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            //FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.container_body, fragment);
            //fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        private final List<String> mTagsList = new ArrayList<>();

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title, String tag) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            mTagsList.add(tag);
        }

        public int getPositionTitle(String tag){
            return mTagsList.indexOf(tag);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public String getRegisteredFragment(int position) {
            return registeredFragments.get(position).getTag().toString().substring(28);
        }

    }
}
