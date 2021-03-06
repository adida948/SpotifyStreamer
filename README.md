# SpotifyStreamer
Spotify app to get top 10 tracks and display them.

Required:

- [x] User Interface - Layout
[Phone] UI contains a screen for searching for an artist and displaying a list of artist results Individual artist result layout contains - Artist Thumbnail , Artist name
[Phone] UI contains a screen for displaying the top tracks for a selected artist Individual track layout contains - Album art thumbnail, track name, album name
[Phone] UI contains a screen that represents the player. It contains playback controls for the currently selected track
Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for searching artists and the right fragment is for displaying top tracks of a selected artist. The Now Playing controls are displayed in a DialogFragment.

- [x] User Interface - Function
App contains a search field that allows the user to enter in the name of an artist to search for
When an artist name is entered, app displays list of artist results
App displays a Toast if the artist name is not found (asks to refine search)
When an artist is selected, app uses an Intent to launch the “Top Tracks” View
App displays a list of top tracks
When a track is selected, app uses an Intent to launch the Now playing screen and starts playback of the track.

- [x] Network API Implementation
App implements Artist Search + GetTopTracks API Requests (Using the Spotify wrapper or by making a HTTP request and deserializing the JSON data)
App stores the most recent top tracks query results and their respective metadata (track name , artist name, album name) locally in list. The queried results are retained on rotation.
