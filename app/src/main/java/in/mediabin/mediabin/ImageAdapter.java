package in.mediabin.mediabin;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Avinash on 8/18/2015.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private String[] mPosters = new String[20];
    public ImageAdapter(Context c) {
        mContext = c;
    }
    public int getCount() {
        return 20;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(240, 390));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);


        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mPosters[position])
                .into(imageView);
        return imageView;
    }
    public void getPosters(String[] posters)
    {
        mPosters = posters;
    }


}
