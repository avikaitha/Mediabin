package in.mediabin.mediabin;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class ActorActivityFragment extends Fragment {

    TextView birthday_TextView,bio_textView;
    ImageView actor_image;
    public ActorActivityFragment() {
    }

    public void getActor(String id) {
        final String URL_CATEGORY = "person/"+id;
        final String API_KEY_PARAM = "api_key";
        final String TMDB_BIO = "biography";
        final String TMDB_ACTOR_IMG = "profile_path";
        final String TMDB_BIRTHDAY = "birthday";
        final String TMDB_POSTER_SIZE = "w500";
        final String TMDB_POB = "place_of_birth";
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

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            String bio = response.getString(TMDB_BIO);
                            String pob = response.getString(TMDB_POB);
                            String birthday = response.getString(TMDB_BIRTHDAY);

                            String actor_img_url = GetString.TMDB_IMG_BASE
                                    +TMDB_POSTER_SIZE+
                                    response.getString(TMDB_ACTOR_IMG);
                            Log.d("Actor_image",actor_img_url);
                            Picasso.with(getActivity())
                                    .load(actor_img_url)
                                    .fit()
                                    .error(R.drawable.error)
                                    .into(actor_image);
                            bio_textView.setText(bio);
                            birthday_TextView.setText(birthday + "\n" + pob);
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
        View rootView = inflater.inflate(R.layout.fragment_actor, container, false);
        Intent intent = getActivity().getIntent();
        String actor_id = intent.getStringExtra(DetailActivityFragment.ACTOR_ID);
        String name = intent.getStringExtra(DetailActivityFragment.ACTOR_NAME);

        bio_textView = (TextView) rootView.findViewById(R.id.bio);
        birthday_TextView = (TextView) rootView.findViewById(R.id.birthday);
        actor_image = (ImageView) rootView.findViewById(R.id.actor_image);
        getActivity().setTitle(name);

        getActor(actor_id);
        return rootView;
    }


}
