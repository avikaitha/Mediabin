package in.mediabin.mediabin;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Avinash on 8/18/2015.
 */
public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private LayoutInflater mInflater;

    private View mHeaderView;
    private Context mContext;
    private ArrayList<String> mPosters = new ArrayList<>();
    private ArrayList<Integer> mChecklist = new ArrayList<>();



    public boolean isHeader(int position) {
        return position == 0;
    }

    public ImageAdapter(Context context, ArrayList<String> posters, View headerView) {
        mContext = context;

        mInflater = LayoutInflater.from(context);
        mPosters = posters;
        mHeaderView = headerView;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(mHeaderView);
        } else {
            return new ItemViewHolder(mInflater.inflate(R.layout.grid_item_layout, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        double aspectRatio = 1.6;

        if (viewHolder instanceof ItemViewHolder) {
            ImageView imageView =
                    ((ItemViewHolder) viewHolder).posterimageView;
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
                        .load(mPosters.get(position-1))
                        .fit()
                        .error(R.drawable.error)
                        .into(imageView);
            }

             Button addButton = ((ItemViewHolder) viewHolder).addButton;
            if(!mChecklist.isEmpty()) {
                if(mChecklist.indexOf(position) != -1)
                {
                    addButton.setSelected(true);
                }
            }


//            addButton.setOnClickListener(new View.OnClickListener() {
//                boolean selectFlag = mChecklist.indexOf(position) ==-1 ? false:true;
//
//
//                @Override
//                public void onClick(View v) {
//                    v.setSelected(!selectFlag);
//                    selectFlag = !selectFlag;
//                    if(selectFlag)
//                    {
//                        mChecklist.add(position);
//                    }
//                    else
//                    {
//                        int checkpos = mChecklist.indexOf(position);
//                        if(checkpos != -1)
//                        {
//                            mChecklist.remove(checkpos);
//                        }
//                    }
//
//
//                    Toast.makeText(mContext, "Test: " + position + selectFlag, Toast.LENGTH_SHORT).show();
//                }
//            });

        }

        // if it's not recycled, initialize some attributes


    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null) {
            return mPosters.size();
        } else {
            return mPosters.size() + 1;
        }
    }

    // create a new ImageView for each item referenced by the Adapter
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        View resultView = inflater.inflate(R.layout.grid_item_layout,null);
//        ImageView imageView = (ImageView) resultView.findViewById(R.id.imageView);
//
//
//
//
//        return resultView;
//    }


    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View view) {
            super(view);
        }
    }

     class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView posterimageView;
        Button addButton;

        public ItemViewHolder(View view) {
            super(view);
            posterimageView = (ImageView) view.findViewById(R.id.imageView);
            addButton = (Button) view.findViewById(R.id.add_poster_button);
            posterimageView.setOnClickListener(new View.OnClickListener() {
                AppCompatActivity activity = (AppCompatActivity)mContext;


                @Override
                public void onClick(View v) {
                    MainActivityFragment mainActivityFragment =
                            (MainActivityFragment) activity
                                    .getSupportFragmentManager()
                                    .findFragmentById(R.id.main_fragment);
                    mainActivityFragment.handlePosterClick(getPosition()-1);
                }
            });

        }
    }

    public void setPosters(ArrayList posters) { mPosters = posters; }



}
