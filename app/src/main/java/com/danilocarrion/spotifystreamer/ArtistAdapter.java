package com.danilocarrion.spotifystreamer;

import android.content.Context;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by dcarrion on 7/13/2015.
 */
public class ArtistAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater mInflater;
    ArrayList<Artist> mArtistArray;

    public ArtistAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        mArtistArray = new ArrayList<>();
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mArtistArray.size();
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
        return mArtistArray.get(position);
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

            // Inflate the custom row layout from the row_layout XML.
            convertView = mInflater.inflate(R.layout.row_layout, null);

            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbnailImageView = (ImageView) convertView.findViewById(R.id.img_thumbnail);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.text_artist);

            // hang onto this holder for future recycling
            convertView.setTag(holder);
        } else {
            // skip all the expensive inflation/findViewById  and just get the holder below
            holder = (ViewHolder) convertView.getTag();
        }


        // Get the current Artist Object from the selected item on the List
        Artist artistObject = (Artist) getItem(position);

        // See if there is an image for the Object
        if (!artistObject.images.isEmpty()) {
            // Construct the image URL (specific to API). Get the second most smallest image available.
            String imageURL = artistObject.images.get(artistObject.images.size() - 2).url;

            //Verify that the URL is legitimate
            boolean isValidURL = Patterns.WEB_URL.matcher(imageURL).matches();

            // Use Picasso to load the image if it is a valid URL. If not, display default image.
            // Temporarily have a placeholder in case it's slow to load
            if (isValidURL)
                Picasso.with(mContext).load(imageURL).placeholder(R.mipmap.ic_launcher).into(holder.thumbnailImageView);
            else
                holder.thumbnailImageView.setImageResource(R.mipmap.ic_launcher);
        } else {

            // If there is no cover ID in the object, use a placeholder
            holder.thumbnailImageView.setImageResource(R.mipmap.ic_launcher);
        }

        //  artist/artistId variables to store  from ArrayList
        String artistName, artistId = "";

        //If artist is not empty, grab its name and id
        if (artistObject.name != null) {
            artistName = artistObject.name;
            artistId = artistObject.id;

            // Send these Strings to the TextViews for display
            holder.artistTextView.setText(artistName);
        }
        return convertView;
    }

    public void updateData(ArrayList<Artist> artistArray) {
        // update the adapter's dataset
        mArtistArray = artistArray;
        notifyDataSetChanged();
    }

    // this is used so you only ever have to do
    // inflation and finding by ID once ever per View
    private static class ViewHolder {
        public ImageView thumbnailImageView;
        public TextView artistTextView;
    }
}
