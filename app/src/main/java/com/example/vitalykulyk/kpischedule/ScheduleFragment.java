package com.example.vitalykulyk.kpischedule;

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
import android.widget.ListView;

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

/**
 * Created by Vitaly Kulyk on 23.02.2016.
 */
public class ScheduleFragment extends Fragment {

    //Array adapter for
    ArrayAdapter<String> mScheduleAdapter;

    ListView mListView;


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

        String[] scheduleArray = {
                "1. PPKS",
                "2. History of ukrainian culture",
                "3. KM",
                "4. TOE",
                "5. TEK",
                "6. FICT",
                "7. OK"
        };

        List<String> weekforecast = new ArrayList<>(Arrays.asList(scheduleArray));


        mScheduleAdapter = new ArrayAdapter<String>(
                //current context
                getActivity(),
                //ID of list item
                R.layout.list_item_schedule,
                //forecast data
                R.id.list_item_forecast_textview,
                //forecast data
                weekforecast);

        mListView = (ListView)rootView.findViewById(R.id.listview_forecast);

        mListView.setAdapter(mScheduleAdapter);

        return rootView;
    }

    public class ScheduleTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_CAT = ScheduleTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         *
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getScheduleDataFromJson(String scheduleJsonStr)
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

            String[] resultStrs = new String[dataArray.length()];
            for(int i = 0; i < dataArray.length(); i++) {
                // For now, using the format "number, description, teacher,room, type"
                String number;
                String description;
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
                description = lesson.getString(API_LESSONS_NAME);
                number = lesson.getString(API_LESSONS_NUMBER);
                room = lesson.getString(API_LESSONS_ROOM);
                type = lesson.getString(API_LESSONS_TYPE);
                teacher = lesson.getString(API_LESSONS_TEACHER);

                resultStrs[i] = number + ". #" + room + " /" + description + "/ " + type + " ^" + teacher;
            }

            for (String s : resultStrs) {
                Log.v(LOG_CAT, "Schedule entry: " + s);
            }
            return resultStrs;

        }



        @Override
        protected String[] doInBackground(String... params) {
            // If there's no zip code, there's nothing to look up.  Verify size of params.
            if (params.length == 0) {
                return null;
            }
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String format = "json";
            String units = "metric";
            int numDays = 7;


            try {

                final String FORECAST_BASE_URL =  "http://api.rozklad.org.ua/v2/groups/io-32/lessons?filter={'day_number':3,'lesson_week':1}";
                //http://api.rozklad.org.ua/v2/groups/ia-23/lessons?filter={'day_number':3,'lesson_week':1}
                final String FORMAT_PARAM = "lessons";
                final String FILTER = "filter=";
                final String FILTER_MUCH = "{'day_number':3,'lesson_week':1}";


                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendPath(params[0]).build();
//                        .appendQueryParameter(QUERY_PARAM)
//                        .appendQueryParameter(FORMAT_PARAM, format)
//
//                        .build();

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
                forecastJsonStr = buffer.toString();
                Log.v(LOG_CAT, "Forecast JSON string - " +  forecastJsonStr);
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
                return getScheduleDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_CAT, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }


        @Override
        protected void onPostExecute(String[] result) {
            if (result != null){
                mScheduleAdapter.clear();
                mScheduleAdapter.addAll(result);
            }
        }
    }
}
