package com.example.vitalykulyk.kpischedule;


import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    android.app.FragmentTransaction mFragmentTransaction;
    android.app.FragmentManager mFragmentManager;
    boolean isPressed = false;
    boolean isPressedSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPressedSearch = false;

        day = getTodayDay();

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

        mFragmentManager = getFragmentManager();

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
            scheduleTask.execute(query, day);
            if (!isPressed) {
                mFragmentTransaction = mFragmentManager.beginTransaction();
                if (id == R.id.action_search) {
                    ScheduleFragment schFragment = new ScheduleFragment();
                    mFragmentTransaction.add(R.id.schedule_fragment, schFragment);//(R.id.schedule_fragment, schFragment);
                    mFragmentTransaction.commit();
                }
                isPressed = true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new Tworagment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    public String getTodayDay(){
        Calendar calendar = Calendar.getInstance();
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
//                case (Calendar.MONDAY) : {
//                    return 1;
//                }
            case (Calendar.TUESDAY) : {
                return "Tuersday";
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
                return "Tuersday";
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
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
