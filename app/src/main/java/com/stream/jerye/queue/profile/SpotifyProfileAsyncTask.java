package com.stream.jerye.queue.profile;

import android.content.Context;
import android.os.AsyncTask;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;

/**
 * Created by jerye on 7/10/2017.
 */

public class SpotifyProfileAsyncTask extends AsyncTask<Void, Void, UserPrivate> {

    private SpotifyService spotifyService;
    private Context mContext;
    private SpotifyProfileCallback mSpotifyProfileCallback;

    public interface SpotifyProfileCallback{
        void createProfile(UserPrivate userPrivate);
    }

    public SpotifyProfileAsyncTask(Context context, SpotifyProfileCallback spotifyProfileCallback, String accessToken) {
        mSpotifyProfileCallback = spotifyProfileCallback;
        mContext = context;
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(accessToken);
        spotifyService = api.getService();
    }


    @Override
    protected UserPrivate doInBackground(Void... params) {
        return spotifyService.getMe();
    }

    @Override
    protected void onPostExecute(UserPrivate userPrivate) {
        super.onPostExecute(userPrivate);
        mSpotifyProfileCallback.createProfile(userPrivate);

    }
}
