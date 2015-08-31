package in.mediabin.mediabin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
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
    int genreID = -1;
    public MainActivityFragment() {
    }
    private void updateMedia(int page, int genre) {


        final String URL_CATEGORY = "discover/tv?";
        final String PAGE_PARAM = "page";
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String GENRE_PARAM = "with_genres";
        Uri builtUri;
        if(genre != -1) {
             builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                    .appendQueryParameter(PAGE_PARAM, page + "")
                    .appendQueryParameter(GENRE_PARAM,genre+"")
                    .appendQueryParameter(API_KEY_PARAM, GetString.API_KEY)
                    .build();
        }
        else {
             builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                    .appendQueryParameter(PAGE_PARAM, page + "")
                    .appendQueryParameter(API_KEY_PARAM, GetString.API_KEY)
                    .build();
        }


        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(LOG_TAG,url.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, String.valueOf(url), (String)null, new Response.Listener<JSONObject>() {
                    ArrayList<String> mPosters = new ArrayList<>();

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mPosters = getDataFromJson(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imageAdapter.notifyDataSetChanged();
                        imageAdapter.setPosters(mPosters);

                        getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);



    }

    private ArrayList getDataFromJson(JSONObject mediaJson)
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
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
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
                intent.putExtra(EXTRA_ID, series_id.get(position));
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
                        updateMedia(pageNo,genreID);
                        Log.d(LOG_TAG, pageNo + "");

                    }
                }
            }
        });


        return rootView;
    }

    ArrayList<String> genres = new ArrayList<>();
    ArrayList<Integer> genreIDList = new ArrayList<>();
    public void getGenres() {
        final String URL_CATEGORY = "genre/tv/list";

        final String API_KEY_PARAM = "api_key";


        Uri builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, GetString.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, String.valueOf(url), (String)null, new Response.Listener<JSONObject>() {


                    final String TMDB_GENRES = "genres";
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray mediaResults = response.getJSONArray(TMDB_GENRES);

                            for(int i = 0; i<mediaResults.length();i++) {
                                genres.add(mediaResults.getJSONObject(i).getString("name"));
                                genreIDList.add(mediaResults.getJSONObject(i).getInt("id"));

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        genres.add("All");
        getGenres();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item,
                        genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                backgrnd.clear();
                titles.clear();
                posters.clear();
                series_id .clear();
                summary.clear();
                gridview.setSelection(0);
                pageNo = 1;
                if (position != 0) {

                    updateMedia(pageNo, genreIDList.get(position-1));
                    genreID = genreIDList.get(position - 1);
                } else {
                    genreID = -1;
                    updateMedia(pageNo, genreID);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
