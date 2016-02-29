package com.example.vitalykulyk.kpischedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vitaly Kulyk on 28.02.2016.
 */
public class Monday extends Fragment {

    ArrayAdapter<Lesson> mScheduleAdapter;

    static ListView mListView;

    List<Lesson> testSchedule;

    static ScheduleFragment.ScheduleTask.LessonAdapter mLessonAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mListView = (ListView) rootView.findViewById(R.id.listview_forecast);

        ArrayList<Lesson> testSchedule = new ArrayList<>();

        mLessonAdapter = new ScheduleFragment.ScheduleTask.LessonAdapter(getActivity(), R.layout.list_item, testSchedule);

        return rootView;
    }
}
