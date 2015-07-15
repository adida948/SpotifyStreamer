package com.danilocarrion.spotifystreamer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TopTracksActivity extends ActionBarActivity {
    String artistID = "";
    String artistName = "";
    ListView trackListView;
    ProgressDialog mDialog;
    TrackAdapter mTracksAdapter;
    ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);


        //Enable up button for more navegation options
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        trackListView = (ListView) findViewById(R.id.tracks_listView);

        //Create a TrackAdapter for the ListView
        mTracksAdapter = new TrackAdapter(this, getLayoutInflater());
        // Set the ListView to use the ArrayAdapter
        trackListView.setAdapter(mTracksAdapter);

        //Get ID from Intent
        artistID = this.getIntent().getExtras().getString("artistId");

        //Get artist name from Intent
        artistName = this.getIntent().getExtras().getString("artistName");

        //Set Subtitle in Action Bar to resemble the mock.
        mActionBar = getSupportActionBar();
        mActionBar.setSubtitle(artistName);


        //Send all the top tracks to the adapter with the Artist ID from the previous activity.
        queryTopTracks(artistID);



        //Set Dialog
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching for Tracks");
        mDialog.setCancelable(false);
    }



    //Method used to get result back from Spofity API
    private void queryTopTracks(String searchTracksId) {

        RetrieveData runTopTracks = new RetrieveData();
        runTopTracks.execute(searchTracksId);

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

            //Making calls to Spotify API by using the Wrapper.
            String artistIdFromIntent = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //making the map for country and id.
            HashMap countryCode = new HashMap();
            countryCode.put("country", "US");


            spotify.getArtistTopTrack(artistIdFromIntent, countryCode, new Callback<Tracks>() {
                /**
                 * Successful HTTP response.
                 *
                 * @param tracks
                 * @param response
                 */
                @Override
                public void success(Tracks tracks, Response response) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mDialog.dismiss();
                        }
                    });

                    Tracks results = tracks;

                    ArrayList<Track> listOfTracks = (ArrayList) results.tracks;

                    if (listOfTracks.isEmpty()) {
                        runOnUiThread(new Runnable() {
                            public void run() {

                                Toast.makeText(getApplicationContext(), "No Top Tracks found.", Toast.LENGTH_LONG).show();

                            }
                        });
                    }

                    //testing purposes
                    for (Track element : listOfTracks) {
                        String trackName = element.name;
                        String albumName = element.album.name;
                        String imageUrl = element.album.images.get(0).url;

                        Log.v("Track Success", trackName + " " + albumName + "\n " + imageUrl);
                    }


                    mTracksAdapter.updateData(listOfTracks);

                }

                /**
                 * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
                 * exception.
                 *
                 * @param error
                 */
                @Override
                public void failure(RetrofitError error) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mDialog.dismiss();
                        }
                    });
                    Log.d("Track failure", error.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getApplicationContext(), "An error has occurred.", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });

            return null;
        }


    }

}
