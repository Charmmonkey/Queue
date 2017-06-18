package com.stream.jerye.queue.MusicPage;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.player.Spotify;
import com.stream.jerye.queue.R;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment implements Search.View, MusicQueueListener {
    private LinearLayoutManager mResultsLayoutManager = new LinearLayoutManager(getContext()), mQueueLayoutManager = new LinearLayoutManager(getContext());
    private ScrollListener mScrollListener = new ScrollListener(mResultsLayoutManager);
    private SearchResultsAdapter mSearchResultsAdapter;
    private MusicQueueAdapter mQueueMusicAdapter;
    private DatabaseReference mDatabaseTracksReference;
    private SearchView mSearchView;
    private RecyclerView mMusicResultsList, mMusicQueueList;
    private Button mPlayButton, mPreviousbutton, mNextButton;
    private FirebaseDatabase mFirebaseDatabase;
    private View mRootView;
    private com.spotify.sdk.android.player.Player mSpotifyPlayer;
    private String mCurrentTrack;
    private QueuePlayer mPlayer;
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private Search.ActionListener mActionListener;
    private String TAG = "MainActivity.java";
    private String mSpotifyAccessToken;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "Service Connected");
            mPlayer = ((PlayerService.PlayerBinder) service).getService(getContext(), MusicFragment.this, mSpotifyAccessToken);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "Service DisConnected");

            mPlayer = null;
        }
    };


    public MusicFragment() {
        // Required empty public constructor
    }

    public static MusicFragment newInstance() {
        Bundle args = new Bundle();
        MusicFragment fragment = new MusicFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private class ScrollListener extends ResultListScrollListener {

        public ScrollListener(LinearLayoutManager layoutManager) {
            super(layoutManager);
        }

        @Override
        public void onLoadMore() {
            mActionListener.loadMoreResults();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.music_fragment, container, false);
        mPlayButton = (Button) mRootView.findViewById(com.stream.jerye.queue.R.id.play_button);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "play button clicked");


                if (mPlayer == null) {
                    Log.d("MainActivity.java", "mPlayer is null");
                    return;
                }

                String currentTrackUrl = mQueueMusicAdapter.peek();


                if (mPlayer.isPaused()) {
                    mPlayer.resume();
                    mPlayButton.setText("Pause");
                } else if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                    mPlayButton.setText("Play");

                } else {
                    mPlayer.play(currentTrackUrl);
                    mPlayer.nextTrack(mQueueMusicAdapter.peekMore());
                    mPlayButton.setText("Pause");
                }
            }
        });

        mNextButton = (Button) mRootView.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity.java", "Next button clicked");

                if (mPlayer == null) {
                    Log.d("MainActivity.java", "mPlayer is null");
                    return;
                }


            }
        });

        mSearchView = (SearchView) mRootView.findViewById(R.id.search_view);
        // Setup search field
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mActionListener.search(query);
                mSearchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mMusicResultsList = (RecyclerView) mRootView.findViewById(R.id.search_results);

        // Setup search results list
        mSearchResultsAdapter = new SearchResultsAdapter(getContext(), new SearchResultsAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(View itemView, Track item) {
                mActionListener.selectTrack(item);
            }
        });

        mMusicResultsList.setHasFixedSize(true);
        mMusicResultsList.setLayoutManager(mResultsLayoutManager);
        mMusicResultsList.setAdapter(mSearchResultsAdapter);
        mMusicResultsList.addOnScrollListener(mScrollListener);

        mMusicQueueList = (RecyclerView) mRootView.findViewById(R.id.music_queue);
        mMusicQueueList.setLayoutManager(mQueueLayoutManager);
        mQueueMusicAdapter = new MusicQueueAdapter(getContext());
        mMusicQueueList.setAdapter(mQueueMusicAdapter);

        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseTracksReference = mFirebaseDatabase.getReference().child("tracks");
        mDatabaseTracksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                SpotifyTrack spotifyTrack = dataSnapshot.getValue(SpotifyTrack.class);
                mCurrentTrack = spotifyTrack.getTrack();
                mQueueMusicAdapter.enqueue(mCurrentTrack);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        SharedPreferences prefs = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        mSpotifyAccessToken = prefs.getString("token", null);

        mActionListener = new SearchPresenter(getContext(), this);
        mActionListener.init(mSpotifyAccessToken);

        // If Activity was recreated wit active search restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }

        getContext().bindService(PlayerService.getIntent(getContext()), mServiceConnection, Activity.BIND_AUTO_CREATE);

    }

    @Override
    public void reset() {
        mScrollListener.reset();
        mSearchResultsAdapter.clearData();
    }

    @Override
    public void addData(List<Track> items) {
        mSearchResultsAdapter.addData(items);
    }

    @Override
    public void onTrackSelected(String trackUrl) {
        Log.d("MainActivity.java", "onTrackSelected");
        SpotifyTrack selectedTrack = new SpotifyTrack(trackUrl);
        mDatabaseTracksReference.push().setValue(selectedTrack);
    }


    @Override
    public void onPause() {
        super.onPause();
        mActionListener.pause();

    }

    @Override
    public void onResume() {
        super.onResume();
        mActionListener.resume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActionListener.getCurrentQuery() != null) {
            outState.putString(KEY_CURRENT_QUERY, mActionListener.getCurrentQuery());
        }
    }

    @Override
    public void onDestroyView() {
        Spotify.destroyPlayer(this);
        super.onDestroyView();
    }

    @Override
    public void dequeue() {
        Log.d("MainActivity.java", "QueueListener dequeue");
        mQueueMusicAdapter.dequeue();
    }

    @Override
    public void onStop() {
        super.onStop();

        getContext().unbindService(mServiceConnection);

    }


}
