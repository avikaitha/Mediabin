package in.mediabin.mediabin;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static  final String LOG_TAG = "app.Mediabin";
    public static  final String EXTRA_BACKGRND = "app.Mediabin.EXTRA_BACKGRND";
    public static  final String EXTRA_SUMMARY = "app.Mediabin.EXTRA_SUMMARY";
    public static  final String EXTRA_TITLE = "app.Mediabin.EXTRA_TITLE";
    String[] backgrnd;
    String[] summary;
    ImageAdapter imageAdapter;
    String[] titles;
    public MainActivityFragment() {
    }
    private void updateMedia() {
        Fetchmedia mediaTask = new Fetchmedia();
        mediaTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMedia();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        imageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                intent.putExtra(EXTRA_BACKGRND,backgrnd[position]);
                intent.putExtra(EXTRA_SUMMARY,summary[position]);
                intent.putExtra(EXTRA_TITLE,titles[position]);
                startActivity(intent);
            }
        });
        return rootView;
    }

    public class Fetchmedia extends AsyncTask<Void,Void,String[]> {

        private String[] getDataFromJson(String mediaJsonStr)
                throws JSONException
        {
            final String TMDB_RESULTS = "results";
            final String TMDB_POSTER = "poster_path";
            final String TMDB_IMG_BASE = "http://image.tmdb.org/t/p/";
            final String TMDB_POSTER_SIZE = "w185";
            final String TMDB_BACKGRND_SIZE = "w500";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_BACKGRND = "backdrop_path";
            final String TMDB_TITLE = "name";
            JSONObject mediaJson = new JSONObject(mediaJsonStr);
            JSONArray mediaResults = mediaJson.getJSONArray(TMDB_RESULTS);
            String[] posters = new String[mediaResults.length()];
            summary = new String[mediaResults.length()];
            backgrnd = new String[mediaResults.length()];
            titles = new String[mediaResults.length()];
            for(int i=0;i<mediaResults.length();i++)
            {
                posters[i] = TMDB_IMG_BASE
                        +TMDB_POSTER_SIZE
                        +mediaResults.getJSONObject(i).getString(TMDB_POSTER);

                backgrnd[i] = TMDB_IMG_BASE
                        +TMDB_BACKGRND_SIZE
                        +mediaResults.getJSONObject(i).getString(TMDB_BACKGRND);

                summary[i] = mediaResults.getJSONObject(i).getString(TMDB_OVERVIEW);

                titles[i] = mediaResults.getJSONObject(i).getString(TMDB_TITLE);

                Log.d(LOG_TAG,posters[i]);
            }

            return posters;

        }


        @Override
        protected String[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String mediaJsonStr = null;





            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/tv?";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                final String API_KEY = "9b4ce4f4209c8d8e9ce97f073100672b";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.d(LOG_TAG,url.toString());
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
                mediaJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(mediaJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            imageAdapter.setPosters(result);
        }
    }
}
