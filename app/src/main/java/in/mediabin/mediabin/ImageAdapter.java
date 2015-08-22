package in.mediabin.mediabin;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Avinash on 8/18/2015.
 */
public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> mPosters = new ArrayList<>();
    private static LayoutInflater inflater = null;
    public ImageAdapter(Context c) {
        mContext = c;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return mPosters.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        View resultView = inflater.inflate(R.layout.grid_item_layout,null);
        ImageView imageView = (ImageView) resultView.findViewById(R.id.imageView);


        double aspectRatio = 1.6;

            // if it's not recycled, initialize some attributes
            DisplayMetrics metrics = new DisplayMetrics();
            ((Activity)mContext).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);


            int width = metrics.widthPixels/2;
            int height = (int) (width * aspectRatio);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(width,height ));
            imageView.setScaleType(ImageView.ScaleType.FIT_END);
            imageView.setPadding(0, 0, 0, 0);
        if(!mPosters.isEmpty())
        {
            Picasso.with(mContext)
                    .load(mPosters.get(position))
                    .fit()
                    .error(R.drawable.fading_0)
                    .into(imageView);
        }

        Button addButton = (Button) resultView.findViewById(R.id.button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Test"+position, Toast.LENGTH_SHORT).show();
            }
        });

        return resultView;
    }
    public void setPosters(ArrayList<String> posters)
    {
        mPosters = posters;
    }


}
