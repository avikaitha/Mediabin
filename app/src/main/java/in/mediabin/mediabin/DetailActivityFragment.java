package in.mediabin.mediabin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

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
        getActivity().setTitle(title);
        TextView summaryTextView = (TextView) rootView.findViewById(R.id.summary);
        summaryTextView.setText(summary);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.backgrnd);
        Picasso.with(getActivity())
                .load(backgrnd_url)
                .resize(480,240)
                .centerCrop()
                .into(imageView);
        return rootView;
    }
}
