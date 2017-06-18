package com.stream.jerye.queue;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Player;
import com.stream.jerye.queue.MessagePage.MessageFragment;
import com.stream.jerye.queue.MusicPage.MusicFragment;

public class MainActivity extends AppCompatActivity {
    public static final String CLIENT_ID = "06a251bae8ae4881bb0022223b960c1d";
    private static final String REDIRECT_URI = "https://en.wikipedia.org/wiki/Whitelist";
    private static final int REQUEST_CODE = 42;
    private ViewPager mPager;
    private Player mSpotifyPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);

        mPager = (ViewPager) findViewById(R.id.view_pager);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.d("MainActivity.java", "onActivityResult: " + requestCode);

        if (requestCode == REQUEST_CODE) {
            Log.d("MainActivity.java", "requestcode");

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Log.d("MainActivity.java", "type: " + response.getType());

                String mToken = response.getAccessToken();
                SharedPreferences prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
                prefs.edit().putString("token", mToken).apply();

                mPager.setAdapter(new SimpleFragmentPageAdapter(getSupportFragmentManager()));

//                //SpotifyMusicPlayer
//                Config playerConfig = new Config(this, mToken, MainActivity.CLIENT_ID);
//                Spotify.getPlayer(playerConfig,this,new SpotifyPlayer.InitializationObserver() {
//                    @Override
//                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
//                        mSpotifyPlayer = spotifyPlayer;
//                        mSpotifyPlayer.addConnectionStateCallback(MainActivity.this);
//
//                        Log.d("MainActivity.java",mSpotifyPlayer.getPlaybackState()+ "");
//
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
//                    }
//                });
            }
        }
    }



    private class SimpleFragmentPageAdapter extends FragmentStatePagerAdapter {

        public SimpleFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return MusicFragment.newInstance();
            }else{
               return  MessageFragment.newInstance();
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }
    }



}
