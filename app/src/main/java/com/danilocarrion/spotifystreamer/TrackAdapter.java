package com.danilocarrion.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by dcarrion on 7/13/2015.
 */
public class TrackAdapter extends BaseAdapter{
    Context mContext;
    LayoutInflater mInflater;
    ArrayList<Track> mTracksArray;

    public TrackAdapter(Context context,LayoutInflater inflater) {
        mContext=context;
        mInflater =inflater;
        mTracksArray = new ArrayList<>();
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount()  {
        return mTracksArray.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mTracksArray.get(position);

    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

       // check if the view already exists
        // if so, no need to inflate and findViewById again!
        if (convertView == null) {

            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.track_row_layout, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.track_img_thumbnail);
            holder.albumNameTextView = (TextView) convertView.findViewById(R.id.album_title);
            holder.trackNameTextView = (TextView) convertView.findViewById(R.id.track_name);


            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        } else {

            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // Get the current track's data from ArrayList
        Track trackObject = (Track)getItem(position);

        // See if there is an image for the Object
        if (!trackObject.album.images.isEmpty()) {

            // Construct the image URL (specific to API) get The second biggest piicture
            String track_ImageURL = trackObject.album.images.get(trackObject.album.images.size()-2).url;

            // Use Picasso to load the image
            // Temporarily have a placeholder in case it's slow to load
            Picasso.with(mContext).load(track_ImageURL).placeholder(R.mipmap.ic_launcher).into(holder.thumbnailImageView);
        } else {

            // If there is no cover ID in the object, use a placeholder
            holder.thumbnailImageView.setImageResource(R.mipmap.ic_launcher);
        }

        // Grab track name from ArrayList
        String trackName = "";

        //Grab album name from object
        String albumName = "";

        if (trackObject.name != null) {
            trackName = trackObject.name;
            albumName = trackObject.album.name;

            // Send these Strings to the TextViews for display
            holder.trackNameTextView.setText(trackName);
            holder.albumNameTextView.setText(albumName);

        }

        return convertView;
    }

    // this is used so you only ever have to do
    // inflation and finding by ID once ever per View
    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView albumNameTextView;
        public TextView trackNameTextView;
    }

    public void updateData(ArrayList<Track> trackArray) {
        // update the adapter's dataset
        mTracksArray = trackArray;
        notifyDataSetChanged();
    }

}
