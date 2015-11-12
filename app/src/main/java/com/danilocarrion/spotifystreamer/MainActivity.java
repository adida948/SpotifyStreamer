package com.danilocarrion.spotifystreamer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    SearchView mSearchView;
    ListView mainListView;
    ArtistAdapter mArtistAdapter;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Access the SearchView
        mSearchView = (SearchView) findViewById(R.id.searchView);

        //Access the listView
        mainListView = (ListView) findViewById(R.id.main_listview);

        // Set this activity to react to the items being pressed
        mainListView.setOnItemClickListener(this);

        // Create an ArtistAdapter for the ListView
        mArtistAdapter = new ArtistAdapter(this, getLayoutInflater());

        // Set the ListView to use the ArrayAdapter
        mainListView.setAdapter(mArtistAdapter);

        // Create a dialog to show waiting for Artist
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching for Artist");
        mDialog.setCancelable(false);

        //respond to the "Search" after user tabs on the phone
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setQueryHint("Artist Name");


        //*** setOnQueryTextListener ***
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                queryArtist(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                return false;
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //Get  artistId and Name to send it to the Top Tracks Activity
        Artist artistObject = (Artist) mArtistAdapter.getItem(position);
        String artistID = artistObject.id;
        String artistName = artistObject.name;

        Log.d("Position on List", position + ":" + artistObject.name);

        //Create an intent to take you to the next activity
        Intent detailIntent = new Intent(this, TopTracksActivity.class);

        //Put the data about the artist ID in intent before you make the call
        detailIntent.putExtra("artistId", artistID);

        //Put the artist name in intent's extras before making the call
        detailIntent.putExtra("artistName", artistName);

        //Start the activity using the previous intent
        startActivity(detailIntent);
    }


    //Method used to get result back from Spofity API
    private void queryArtist(String searchArtist) {

        RetrieveData getArtists = new RetrieveData();
        getArtists.execute(searchArtist);
    }

    private class RetrieveData extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {

            //Worker threads are meant for doing background tasks and you can't show anything on UI within a worker
            // thread unless you call method like runOnUiThread. If you try to show anything on UI thread without calling
            // runOnUiThread, there will be a java.lang.RuntimeException.
            runOnUiThread(new Runnable() {
                public void run() {
                    mDialog.show();
                }
            });

            String artistNames = params[0];

            //Making calls to Spotify API by using the Wrapper.
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();
            spotify.searchArtists(artistNames, new Callback<ArtistsPager>() {

                /**
                 * Successful    HTTP response.
                 *
                 * @param artistsPager
                 * @param response
                 */
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mDialog.dismiss();
                        }
                    });

                    ArtistsPager results = artistsPager;
                    ArrayList<Artist> listOfArtists = (ArrayList) results.artists.items;

                    if (listOfArtists.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No Artists found.\n Please try again.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    //testing purposes
                    for (Artist element : listOfArtists) {
                        String name = element.name;
                        String nameId = element.id;
                        Log.v("album success", name + " " + nameId);
                    }

                    //Updated the adapter with the list of Artists Objects
                    mArtistAdapter.updateData(listOfArtists);
                }

                @Override
                public void failure(RetrofitError error) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mDialog.dismiss();
                        }
                    });

                    Log.d("Album failure", error.toString());

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "There has been an error.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }
    }

}
