package com.danilocarrion.spotifystreamer;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class TopTracksActivity extends ActionBarActivity  {
    String artistID = "";
    ListView trackListView;
    ProgressDialog mDialog;
    TrackAdapter mTracksAdapter;


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


        //Send all the top tracks to the adapter with the Artist ID from the previous activity.
        queryTopTracks(artistID);

        //Set Dialog
        mDialog = new ProgressDialog(this);
        mDialog.setMessage("Searching for Artist");
        mDialog.setCancelable(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_tracks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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

            String artistId = params[0];
            SpotifyApi api = new SpotifyApi();
            SpotifyService spotify = api.getService();

            //making the map for country and id.
            HashMap countryCode = new HashMap();
            countryCode.put("country", "US");


            spotify.getArtistTopTrack(artistId, countryCode, new Callback<Tracks>() {
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

                        Log.v("album success", trackName + " " + albumName + "\n " + imageUrl);
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
                    Log.d("Album failure", error.toString());
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getApplicationContext(), "An error has occurred.", Toast.LENGTH_LONG).show();

                        }
                    });
                }
            });

/*
            spotify.searchTracks(artistId, new Callback<TracksPager>(){


                */
/**
 * Successful HTTP response.
 *
 * @param tracksPager
 * @param response
 *//*

                @Override
                public void success(TracksPager tracksPager, Response response) {

                    runOnUiThread(new Runnable() {public void run(){ mDialog.dismiss();}});
                    TracksPager results = tracksPager;

                    ArrayList <Track> listOfTracks = (ArrayList)results.tracks.items;

                    if(listOfTracks.isEmpty()){
                        runOnUiThread(new Runnable() {public void run(){

                            Toast.makeText(getApplicationContext(), "No Artists found.\n Please try again.", Toast.LENGTH_LONG).show();

                        }});}

                    for (Track element : listOfTracks) {
                        String name = element.name;
                        String albumName = element.album.name;
                        Log.v("album success", name + " " + albumName);
                    }

                   // mArtistAdapter.updateData(listOfArtists);
                }

                @Override
                public void failure(RetrofitError error) {
                    runOnUiThread(new Runnable() {public void run(){ mDialog.dismiss();}});

                    Log.d("Album failure", error.toString());
                }



            });
*/


            return null;
        }


    }

}
