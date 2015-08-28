package in.mediabin.mediabin;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
public class DetailActivityFragment extends Fragment {

    public static  final String LOG_TAG = "app.Mediabin.Detail";
    ArrayList<String> cast_posters = new ArrayList<>();
    ArrayList<String> character = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();

    CastImageAdapter castimageadapter;
    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        String summary = intent.getStringExtra(MainActivityFragment.EXTRA_SUMMARY);
        String backgrnd_url = intent.getStringExtra(MainActivityFragment.EXTRA_BACKGRND);
        String title = intent.getStringExtra(MainActivityFragment.EXTRA_TITLE);
        String series_id = intent.getStringExtra(MainActivityFragment.EXTRA_ID);
        getActivity().setTitle(title);
        TextView summaryTextView = (TextView) rootView.findViewById(R.id.summary);
        summaryTextView.setText(summary);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        DisplayMetrics metrics = new DisplayMetrics();
        double aspectRatio = 0.5;
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = (int) (width * aspectRatio);
        Picasso.with(getActivity())
                .load(backgrnd_url)
                .resize(width,height)
                .centerCrop()
                .into(imageView);
        new FetchCast().execute(series_id);
        castimageadapter = new CastImageAdapter(getActivity());
        ExpandableHeightGridView cast_gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.cast_gridView);
        cast_gridView.setFocusable(false);
        cast_gridView.setExpanded(true);
        cast_gridView.setAdapter(castimageadapter);

        cast_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return rootView;
    }

    public class FetchCast extends AsyncTask<String,Void,ArrayList> {


        private ArrayList getDataFromJson(String mediaJsonStr)
                throws JSONException
        {
            final String TMDB_CAST = "cast";
            final String TMDB_CAST_POSTER = "profile_path";
            final String TMDB_IMG_BASE = "http://image.tmdb.org/t/p/";
            final String TMDB_POSTER_SIZE = "w185";
//            final String TMDB_BACKGRND_SIZE = "w500";
            final String TMDB_CHARACTER = "character";
            final String TMDB_NAME = "name";
            final String TMDB_ID = "id";
            JSONObject mediaJson = new JSONObject(mediaJsonStr);
            JSONArray mediaResults = mediaJson.getJSONArray(TMDB_CAST);

            for(int i=0;i<mediaResults.length();i++)
            {
                cast_posters.add(TMDB_IMG_BASE
                        + TMDB_POSTER_SIZE
                        + mediaResults.getJSONObject(i).getString(TMDB_CAST_POSTER));

                character.add(mediaResults.getJSONObject(i).getString(TMDB_CHARACTER));

                name.add(mediaResults.getJSONObject(i).getString(TMDB_NAME));

                Log.d(LOG_TAG, cast_posters.get(i));
            }

            return cast_posters;

        }


        @Override
        protected ArrayList doInBackground(String... params) {
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
                String id= params[0];
                final String TMBD_BASE_URL =
                        "http://api.themoviedb.org/3/";
                final String URL_CATEGORY = "tv/"+id+"/credits?";
                final String API_KEY_PARAM = "api_key";
                final String API_KEY = "9b4ce4f4209c8d8e9ce97f073100672b";

                Uri builtUri = Uri.parse(TMBD_BASE_URL + URL_CATEGORY).buildUpon()
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
                Log.d(LOG_TAG,mediaJsonStr);

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


            castimageadapter.setCastPosters(result);
            castimageadapter.setCharacters(character);
            castimageadapter.setNames(name);
            castimageadapter.notifyDataSetChanged();

        }

    }

}
