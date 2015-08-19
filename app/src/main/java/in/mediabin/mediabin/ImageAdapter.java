package in.mediabin.mediabin;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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
        double aspectRatio = 1.6;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);
            imageView = new ImageView(mContext);

            int width = metrics.widthPixels/2;
            int height = (int) (width * aspectRatio);
            imageView.setLayoutParams(new GridView.LayoutParams(width,height ));
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setPadding(0, 0, 0, 0);


        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext)
                .load(mPosters[position])
                .fit()
                .into(imageView);
        return imageView;
    }
    public void setPosters(String[] posters)
    {
        mPosters = posters;
    }


}
