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
import android.widget.AbsListView;
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
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static  final String LOG_TAG = "app.Mediabin";
    public static  final String EXTRA_BACKGRND = "app.Mediabin.EXTRA_BACKGRND";
    public static  final String EXTRA_SUMMARY = "app.Mediabin.EXTRA_SUMMARY";
    public static  final String EXTRA_TITLE = "app.Mediabin.EXTRA_TITLE";
    public static  final String EXTRA_ID = "app.Mediabin.EXTRA_ID";
    ArrayList<String> backgrnd = new ArrayList<>();
    ArrayList<String> summary = new ArrayList<>();
    ImageAdapter imageAdapter;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> posters = new ArrayList<>();
    ArrayList<String> series_id = new ArrayList<>();
    GridView gridview;
    int pageNo = 1;
    public MainActivityFragment() {
    }
    private void updateMedia() {
        Fetchmedia mediaTask = new Fetchmedia();
        mediaTask.execute(pageNo);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.gridview);

        imageAdapter = new ImageAdapter(getActivity());
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(EXTRA_BACKGRND, backgrnd.get(position));
                intent.putExtra(EXTRA_SUMMARY, summary.get(position));
                intent.putExtra(EXTRA_TITLE, titles.get(position));
                intent.putExtra(EXTRA_ID,series_id.get(position));
                startActivity(intent);
            }
        });


        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            int currentFirstVisibleItem = 0;
            int currentVisibleItemCount = 0;
            int totalItemCount = 0;
            int currentScrollState = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
            }

            private void isScrollCompleted() {
                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
                    /*** In this way I detect if there's been a scroll which has completed ***/
                    /*** do the work for load more date! ***/
                    if (pageNo < 3) {

                        pageNo++;
                        new Fetchmedia().execute(pageNo);
                        Log.d(LOG_TAG, pageNo + "");

                    }
                }
            }
        });

        updateMedia();
        return rootView;
    }

    public class Fetchmedia extends AsyncTask<Integer,Void,ArrayList> {

        int page;
        private ArrayList getDataFromJson(String mediaJsonStr)
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
            final String TMDB_ID = "id";
            JSONObject mediaJson = new JSONObject(mediaJsonStr);
            JSONArray mediaResults = mediaJson.getJSONArray(TMDB_RESULTS);

            for(int i=0;i<mediaResults.length();i++)
            {
                series_id.add(mediaResults.getJSONObject(i).getString(TMDB_ID));
                posters.add(TMDB_IMG_BASE
                        +TMDB_POSTER_SIZE
                        +mediaResults.getJSONObject(i).getString(TMDB_POSTER));

                backgrnd.add(TMDB_IMG_BASE
                        + TMDB_BACKGRND_SIZE
                        + mediaResults.getJSONObject(i).getString(TMDB_BACKGRND));

                summary.add(mediaResults.getJSONObject(i).getString(TMDB_OVERVIEW));

                titles.add(mediaResults.getJSONObject(i).getString(TMDB_TITLE));

                Log.d(LOG_TAG,posters.get(i));
            }

            return posters;

        }


        @Override
        protected ArrayList doInBackground(Integer... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String mediaJsonStr = "";





            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                final String TMBD_BASE_URL =
                        "http://api.themoviedb.org/3/";
                final String URL_CATEGORY = "tv/popular?";
                final String PAGE_PARAM = "page";
                final String SORT_BY_PARAM = "sort_by";
                final String API_KEY_PARAM = "api_key";
                final String API_KEY = "9b4ce4f4209c8d8e9ce97f073100672b";
                 page = params[0];
                    Uri builtUri = Uri.parse(TMBD_BASE_URL + URL_CATEGORY).buildUpon()
                            .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                            .appendQueryParameter(PAGE_PARAM, page + "")
                            .appendQueryParameter(API_KEY_PARAM, API_KEY)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.d(LOG_TAG, url.toString());
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
                    mediaJsonStr = mediaJsonStr+buffer.toString();

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
        protected void onPostExecute(ArrayList result) {
            super.onPostExecute(result);

            imageAdapter.notifyDataSetChanged();
            imageAdapter.setPosters(result);

        }

    }
}
