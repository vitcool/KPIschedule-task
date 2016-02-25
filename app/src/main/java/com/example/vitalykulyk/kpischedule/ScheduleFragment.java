package com.example.vitalykulyk.kpischedule;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by Vitaly Kulyk on 23.02.2016.
 */
public class ScheduleFragment extends Fragment {

    //Array adapter for
    ArrayAdapter<Lesson> mScheduleAdapter;

    ListView mListView;

    List<Lesson> testSchedule;

    static LessonAdapter mLessonAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.schedule_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh){
            ScheduleTask scheduleTask = new ScheduleTask();
            scheduleTask.execute("io-32");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        ScheduleTask scheduleTask = new ScheduleTask();
//        scheduleTask.execute("io-32");

//        Lesson[] scheduleArray = {
//                new Lesson("1.","512-18","PPKS","LAB","KORO"),
//                new Lesson("2.","513-18","KM","LEKT","MARK") ,
//                new Lesson("3.","511-18","OOP","LAB","SIMO") ,
//                new Lesson("4.","515-18","KS","LAB","KULA")  ,
//
//        };



        ArrayList<Lesson> testSchedule = new ArrayList<>();
        testSchedule.add(new Lesson("1.","512-18","PPKS","LAB","KORO"));
        testSchedule.add(new Lesson("2.","513-18","KM","LEKT","MARK") );
        testSchedule.add(new Lesson("3.","511-18","OOP","LAB","SIMO") );
        testSchedule.add(new Lesson("4.","515-18","KS","LAB","KULA")  );


//        mScheduleAdapter = new ArrayAdapter<Lesson>(
//                //current context
//                getActivity(),
//                //ID of list item
//                R.layout.list_item_schedule,
//                //forecast data
//                R.id.list_item_forecast_textview,
//                //forecast data
//                testSchedule);

        mListView = (ListView)rootView.findViewById(R.id.listview_forecast);

        //mListView.setAdapter(mScheduleAdapter);

        mLessonAdapter = new LessonAdapter(getActivity(), R.layout.list_item, testSchedule);

        mListView.setAdapter(mLessonAdapter);

        return rootView;
    }

    public static class ScheduleTask extends AsyncTask<String, Void, Lesson[]> {

        private final String LOG_CAT = ScheduleTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private Lesson[] getScheduleDataFromJson(String scheduleJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String API_DATA = "data";
            final String API_LESSONS_NUMBER = "lesson_number";
            final String API_LESSONS_ROOM = "lesson_room";
            final String API_LESSONS_NAME = "lesson_name";
            final String API_LESSONS_TYPE = "lesson_type";
            final String API_LESSONS_TEACHER = "teacher_name";

            JSONObject scheduleJson = new JSONObject(scheduleJsonStr);
            JSONArray dataArray = scheduleJson.getJSONArray(API_DATA);

            Lesson[] resultStrs = new Lesson[dataArray.length()];
            for(int i = 0; i < dataArray.length(); i++) {
                // For now, using the format "number, description, teacher,room, type"
                String number;
                String name;
                String teacher;
                String room;
                String type;

                // Get the JSON object representing the lesson
                JSONObject lesson = dataArray.getJSONObject(i);

                // description is in a string field "name",
                // string field "lesson_number",
                // string field "lesson_room",
                // string field "lesson_type",
                // string field "lesson_teacher"
                name = lesson.getString(API_LESSONS_NAME);
                number = lesson.getString(API_LESSONS_NUMBER);
                room = lesson.getString(API_LESSONS_ROOM);
                type = lesson.getString(API_LESSONS_TYPE);
                teacher = lesson.getString(API_LESSONS_TEACHER);

                resultStrs[i] = new Lesson(number, room, name, type, teacher);
            }

            for (Lesson s : resultStrs) {
                Log.v(LOG_CAT, "Schedule entry: " + s.toString());
            }
            return resultStrs;

        }

        @Override
        protected Lesson[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String scheduleJsonStr = null;


            try {

                final String FORECAST_BASE_URL =  "http://api.rozklad.org.ua/v2/groups/";
                //http://api.rozklad.org.ua/v2/groups/ia-23/lessons?filter={'day_number':3,'lesson_week':1}
                final String LESSONS = "lessons";
                final String FILTERS = "?filter={'day_number':4,'lesson_week':2}";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendEncodedPath(params[0])
                        .appendEncodedPath(LESSONS)
                        .appendEncodedPath(FILTERS).build();
                       // .appendEncodedPath(FILTER_MUCH).build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_CAT, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                scheduleJsonStr = buffer.toString();
                Log.v(LOG_CAT, "Schedule JSON string - " +  scheduleJsonStr);
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try{
                return getScheduleDataFromJson(scheduleJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_CAT, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        protected void onPostExecute(Lesson[] result) {
            if (result != null){
                mLessonAdapter.clear();
                mLessonAdapter.addAll(result);
            }
        }
    }


    // Adapter for ListView
    public class LessonAdapter extends ArrayAdapter<Lesson>{

        ArrayList<Lesson> lesons;


        public LessonAdapter(Context context, int textViewResourceId, ArrayList<Lesson> objects) {
            super(context, textViewResourceId, objects);
            this.lesons = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent){

            // assign the view we are converting to a local variable
            View v = convertView;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_item, null);
            }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
            Lesson i = lesons.get(position);

            if (i != null) {

                // This is how you obtain a reference to the TextViews.
                // These TextViews are created in the XML files we defined.

                TextView number_of_lesson_value = (TextView) v.findViewById(R.id.number_of_lesson_value);
                TextView room_value = (TextView) v.findViewById(R.id.room_value);
                TextView name_of_subject_value = (TextView) v.findViewById(R.id.name_of_subject_value);
                TextView teacher_name_value = (TextView) v.findViewById(R.id.teacher_name_value);
                TextView lesson_type_value = (TextView) v.findViewById(R.id.lesson_type_value);



                // check to see if each individual textview is null.
                // if not, assign some text!
                if (number_of_lesson_value != null){
                    number_of_lesson_value.setText(i.getLesson_number() + ". ");
                }
                if (room_value != null){
                    room_value.setText(" " + i.getLesson_room());
                }
                if (name_of_subject_value != null){
                    name_of_subject_value.setText(i.getLesson_name());
                }
                if (teacher_name_value != null){
                    teacher_name_value.setText(" " + i.getTeacher_name());
                }
                if (lesson_type_value != null){
                    lesson_type_value.setText(i.getLesson_type());
                }
            }

            // the view must be returned to our activity
            return v;

        }

    }
}
