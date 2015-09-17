package in.mediabin.mediabin;

import android.content.Intent;
import android.net.Uri;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {


    public static  final String LOG_TAG = "app.Mediabin.Detail";
    public static final String ACTOR_ID = "app.Mediabin.Actor";
    public static final String ACTOR_NAME = "app.Mediabin.NAME";
    public static final int RESULT_OK = 1;
    ArrayList<String> cast_posters = new ArrayList<>();
    ArrayList<String> character = new ArrayList<>();
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> cast_id = new ArrayList<>();
    CastImageAdapter castimageadapter;
    TextView created_by_textView,genre_textView,network_textView,status_textView;
    public DetailActivityFragment() {
    }

    private void getCast(String id) {


        final String URL_CATEGORY = "tv/"+id+"/credits?";
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
                    ArrayList<String> mPosters = new ArrayList<>();

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            getDataFromJson(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        castimageadapter.setCastPosters(cast_posters);
                        castimageadapter.setCharacters(character);
                        castimageadapter.setNames(name);
                        castimageadapter.notifyDataSetChanged();


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub

                    }
                });
        VolleySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsObjRequest);



    }
    private void getDataFromJson(JSONObject mediaJson)
            throws JSONException
    {
        final String TMDB_CAST = "cast";
        final String TMDB_CAST_POSTER = "profile_path";

        final String TMDB_POSTER_SIZE = "w185";
//            final String TMDB_BACKGRND_SIZE = "w500";
        final String TMDB_CHARACTER = "character";
        final String TMDB_NAME = "name";
        final String TMDB_ACTOR_ID = "id";

        JSONArray mediaResults = mediaJson.getJSONArray(TMDB_CAST);

        for(int i=0;i<mediaResults.length();i++)
        {
            cast_posters.add(GetString.TMDB_IMG_BASE
                    + TMDB_POSTER_SIZE
                    + mediaResults.getJSONObject(i).getString(TMDB_CAST_POSTER));

            character.add(mediaResults.getJSONObject(i).getString(TMDB_CHARACTER));

            name.add(mediaResults.getJSONObject(i).getString(TMDB_NAME));
            cast_id.add(mediaResults.getJSONObject(i).getString(TMDB_ACTOR_ID));
            Log.d(LOG_TAG, cast_posters.get(i));
        }



    }

    public void getSeries(String id) {
        final String URL_CATEGORY = "tv/"+id;
        final String API_KEY_PARAM = "api_key";
        final String TMDB_CREATED = "created_by";
        final String TMDB_GENRE = "genres";
        final String TMDB_NETWORKS = "networks";
        final String TMDB_STATUS = "status";

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


                    public String makeStringfromArray(JSONArray array) {
                        String result = "";
                        try {
                            for(int i=0;i<(array.length()-1);i++)
                            {
                                result = result + array.getJSONObject(i).getString("name")+", ";
                            }
                            result = result + array.getJSONObject(array.length()-1).getString("name");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return result;
                    }
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray createdbyArray = response.getJSONArray(TMDB_CREATED);
                            JSONArray genreArray = response.getJSONArray(TMDB_GENRE);
                            JSONArray networksArray = response.getJSONArray(TMDB_NETWORKS);

                            String created_by = makeStringfromArray(createdbyArray);
                            String genre = makeStringfromArray(genreArray);
                            String networks = makeStringfromArray(networksArray);
                            String status = response.getString(TMDB_STATUS);
                            created_by_textView.setText(created_by);
                            genre_textView.setText(genre);
                            network_textView.setText(networks);
                            status_textView.setText(status);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        created_by_textView = (TextView) rootView.findViewById(R.id.createdby_textView);
        genre_textView = (TextView) rootView.findViewById(R.id.genre_textView);
        network_textView = (TextView) rootView.findViewById(R.id.network_textView);
        status_textView = (TextView) rootView.findViewById(R.id.status);
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
        double aspectRatio = 0.6;
        getActivity().getWindowManager()
                .getDefaultDisplay()
                .getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = (int) (width * aspectRatio);

        getSeries(series_id);
        Picasso.with(getActivity())
                .load(backgrnd_url)
                .resize(width,height)
                .centerCrop()
                .into(imageView);
        getCast(series_id);
        castimageadapter = new CastImageAdapter(getActivity());
        ExpandableHeightGridView cast_gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.cast_gridView);
        cast_gridView.setFocusable(false);
        cast_gridView.setExpanded(true);
        cast_gridView.setAdapter(castimageadapter);

        cast_gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ActorActivity.class)
                        .putExtra(ACTOR_ID,cast_id.get(position))
                        .putExtra(ACTOR_NAME,name.get(position));
                startActivity(intent);
            }
        });

        return rootView;
    }



}
