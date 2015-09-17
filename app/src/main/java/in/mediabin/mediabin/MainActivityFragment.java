package in.mediabin.mediabin;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

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
    int search_flag = 0;
    ArrayList<String> backgrnd = new ArrayList<>();
    ArrayList<String> summary = new ArrayList<>();
    ImageAdapter imageAdapter;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> posters = new ArrayList<>();
    ArrayList<String> series_id = new ArrayList<>();
    ArrayList<String> genres = new ArrayList<>();
    ArrayList<Integer> genreIDList = new ArrayList<>();
    ObservableRecyclerView recycler_gridView;
    final GridLayoutManager manager = new GridLayoutManager(getActivity(),2);
    private View mHeaderView;
    int pageNo = 1;
    int genreID = -1;
    private Toolbar mToolbar;
    public MainActivityFragment() {
    }
    private void updateMedia(int page, int genre) {


        final String URL_CATEGORY = "discover/tv?";
        final String PAGE_PARAM = "page";
        final String SORT_BY_PARAM = "sort_by";
        final String API_KEY_PARAM = "api_key";
        final String GENRE_PARAM = "with_genres";
        final String LANG_PARAM = "language";
        String language = "en";

        Uri builtUri;
        if(genre != -1) {
             builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                    .appendQueryParameter(PAGE_PARAM, page + "")
                    .appendQueryParameter(GENRE_PARAM,genre+"")
                    .appendQueryParameter(LANG_PARAM,language)
                    .appendQueryParameter(API_KEY_PARAM, GetString.API_KEY)
                    .build();
        }
        else {
             builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                    .appendQueryParameter(SORT_BY_PARAM, "popularity.desc")
                    .appendQueryParameter(PAGE_PARAM, page + "")
                    .appendQueryParameter(LANG_PARAM,language)
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
        final String TMDB_POSTER_SIZE = "w500";
        final String TMDB_BACKGRND_SIZE = "w780";
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

            Log.d(LOG_TAG,backgrnd.get(i));
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
        mHeaderView = rootView.findViewById(R.id.header);
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.recycler_header, null);
        recycler_gridView = (ObservableRecyclerView) rootView.findViewById(R.id.recycler_gridview);

        imageAdapter = new ImageAdapter(getActivity(),posters,headerView);



        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return imageAdapter.isHeader(position) ? manager.getSpanCount() : 1;
            }
        });
        recycler_gridView.setLayoutManager(manager);
//        recycler_gridView.setHasFixedSize(false);
        recycler_gridView.setAdapter(imageAdapter);



        mToolbar = (Toolbar) rootView.findViewById(R.id.spinner_toolbar);

//        recycler_gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

//        recycler_gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            int currentFirstVisibleItem = 0;
//            int currentVisibleItemCount = 0;
//            int totalItemCount = 0;
//            int currentScrollState = 0;
//            int mLastFirstVisibleItem = 0;
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
//                this.currentScrollState = scrollState;
//                if (this.currentScrollState == SCROLL_STATE_TOUCH_SCROLL) {
//
//                }
//                this.isScrollCompleted();
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                this.currentFirstVisibleItem = firstVisibleItem;
//                this.currentVisibleItemCount = visibleItemCount;
//                this.totalItemCount = totalItemCount;
//
//
//                mLastFirstVisibleItem = currentFirstVisibleItem;
//
//
//            }
//
//            private void isScrollCompleted() {
//                if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE && this.totalItemCount == (currentFirstVisibleItem + currentVisibleItemCount)) {
//                    /*** In this way I detect if there's been a scroll which has completed ***/
//                    /*** do the work for load more date! ***/
//
//                    if (pageNo < 3 && search_flag == 0) {
//
//                        pageNo++;
//                        updateMedia(pageNo, genreID);
//                        Log.d(LOG_TAG, pageNo + "");
//
//                    }
//                }
//            }
//        });

        Spinner genre_spinner = (Spinner) rootView.findViewById(R.id.genre_spinner);

        genres.add("All");
        getGenres();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(getActivity(),
                        R.layout.custom_spinner_item,
                        genres);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genre_spinner.setAdapter(adapter);
        genre_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dataDump();
                search_flag = 0;
                if (position != 0) {

                    updateMedia(pageNo, genreIDList.get(position - 1));
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

        return rootView;
    }

    public void handlePosterClick(int position) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(EXTRA_BACKGRND, backgrnd.get(position));
        intent.putExtra(EXTRA_SUMMARY, summary.get(position));
        intent.putExtra(EXTRA_TITLE, titles.get(position));
        intent.putExtra(EXTRA_ID, series_id.get(position));
        startActivity(intent);

    }

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

    public void dataDump()
    {
        backgrnd.clear();
        titles.clear();
        posters.clear();
        series_id.clear();
        summary.clear();
        manager.scrollToPosition(0);
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);

        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
//        recycler_gridView.setSelection(0);
        pageNo = 1;

    }


    public void getSearchResults(String query) {
        final String URL_CATEGORY = "search/tv";
        final String QUERY_PARAM = "query";
        final String API_KEY_PARAM = "api_key";

        search_flag = 1;
        Uri builtUri = Uri.parse(GetString.TMDB_BASE_URL + URL_CATEGORY).buildUpon()
                .appendQueryParameter(QUERY_PARAM,query)
                .appendQueryParameter(API_KEY_PARAM, GetString.API_KEY)
                .build();

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
                            dataDump();
                            mPosters = getDataFromJson(response);
                            imageAdapter.notifyDataSetChanged();
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
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
//        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                dataDump();
                search_flag = 0;
                updateMedia(pageNo,genreID);
                return true;
            }
        });

    }
}
