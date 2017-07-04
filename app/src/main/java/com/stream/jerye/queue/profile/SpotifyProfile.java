package com.stream.jerye.queue.profile;

import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jerye on 6/26/2017.
 */

public class SpotifyProfile {
    private SpotifyService spotifyService;
    private SpotifyProfileCallback mSpotifyProfileCallback;

    public interface SpotifyProfileCallback{
        void createProfile(UserPrivate userPrivate);
    }

    public SpotifyProfile(SpotifyProfileCallback spotifyProfileCallback, String accessToken){
        mSpotifyProfileCallback = spotifyProfileCallback;
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);
        spotifyService = api.getService();
    }

    public void getUserProfile(){
        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                Log.d("Spotify Profile", "success");

                mSpotifyProfileCallback.createProfile(userPrivate);
            }

            @Override
            public void failure(RetrofitError error) {
                mSpotifyProfileCallback.createProfile(null);
                Log.d("Spotify Profile", error.toString());
            }
        });
    }

}
