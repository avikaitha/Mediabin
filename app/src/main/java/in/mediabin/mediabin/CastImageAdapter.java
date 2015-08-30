package in.mediabin.mediabin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Avinash on 8/28/2015.
 */
public class CastImageAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<String> mCastPosters = new ArrayList<>();
    private ArrayList<String> mCharacters = new ArrayList<>();;
    private ArrayList<String> mNames = new ArrayList<>();;
    private static LayoutInflater inflater = null;

    public CastImageAdapter(Context c) {
        mContext = c;
        inflater = ( LayoutInflater )mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mCastPosters.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View resultView = inflater.inflate(R.layout.cast_grid_item,null);
        ImageView imageView = (ImageView) resultView.findViewById(R.id.cast_image);
        TextView name_textView = (TextView) resultView.findViewById(R.id.cast_name_textView);
        TextView char_textView = (TextView) resultView.findViewById(R.id.cast_character_textView);
        name_textView.setText(mNames.get(position));
        char_textView.setText(mCharacters.get(position));
        if(!mCastPosters.isEmpty())
        {
            Picasso.with(mContext)
                    .load(mCastPosters.get(position))
                    .fit()
                    .error(R.drawable.error)
                    .into(imageView);
        }
        return resultView;
    }
    public void setCastPosters(ArrayList<String> castPosters)
    {
        mCastPosters = castPosters;
    }
    public void setCharacters(ArrayList<String> characters)
    {
        mCharacters = characters;
    }
    public void setNames(ArrayList<String> names)
    {
        mNames = names;
    }
}
