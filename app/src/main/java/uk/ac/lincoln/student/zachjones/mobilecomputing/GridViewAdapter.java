/**
 * Zach Jones (JON11356270), University of Lincoln
 *
 * Mobile Computing (CMP3109M-1516), Assessment 1
 *
 * All code used follows Android's "Code Style for Contributors" guidelines:
 *
 *          https://source.android.com/source/code-style.html
 *
 * References:
 *          http://square.github.io/picasso/
 *          http://javatechig.com/android/download-and-display-image-in-android-gridview
 * */

package uk.ac.lincoln.student.zachjones.mobilecomputing;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class GridViewAdapter extends ArrayAdapter<GridItem>
{
    private Context mContext;
    private int mLayoutResourceId;
    private ArrayList<GridItem> mGridData = new ArrayList<GridItem>();

    /** Sets private variables */
    public GridViewAdapter(Context mContext, int layoutResourceId, ArrayList<GridItem> mGridData)
    {
        super(mContext, layoutResourceId, mGridData);
        this.mLayoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /** Updates grid data and refresh grid items. */
    public void setGridData(ArrayList<GridItem> mGridData)
    {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    /** Builds the gallery grid view */
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) row.getTag();
        }

        GridItem item = mGridData.get(position);

        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    /** Gives each image a unique image view */
    static class ViewHolder
    {
        ImageView imageView;
    }
}