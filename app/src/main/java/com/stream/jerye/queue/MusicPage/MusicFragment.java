package com.stream.jerye.queue.MusicPage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.jerye.queue.R;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends Fragment implements Search.View{
    private LinearLayoutManager mResultsLayoutManager = new LinearLayoutManager(getContext()), mQueueLayoutManager = new LinearLayoutManager(getContext());
    private ScrollListener mScrollListener = new ScrollListener(mResultsLayoutManager);
    private SearchResultsAdapter mSearchResultsAdapter;
    private DatabaseReference mDatabaseTracksReference;
    private SearchView mSearchView;
    private RecyclerView mMusicResultsList, mMusicQueueList;
    private FirebaseDatabase mFirebaseDatabase;
    private View mRootView;
    private MusicQueueAdapter mQueueMusicAdapter;
    private com.spotify.sdk.android.player.Player mSpotifyPlayer;
    private String mCurrentTrack;
    private QueuePlayer mPlayer;
    private static final String KEY_CURRENT_QUERY = "CURRENT_QUERY";
    private Search.ActionListener mActionListener;
    private String TAG = "MainActivity.java";
    private String mSpotifyAccessToken;


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

        mSearchView = (SearchView) mRootView.findViewById(R.id.search_view);
        // Setup search field
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mActionListener.search(newText);
                return true;
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
                String latestAddition = spotifyTrack.getTrack();
                mQueueMusicAdapter.enqueue(latestAddition);

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

        mActionListener = new SearchPresenter(getContext(), this);
        mActionListener.init(mSpotifyAccessToken);

        // If Activity was recreated wit active search restore it
        if (savedInstanceState != null) {
            String currentQuery = savedInstanceState.getString(KEY_CURRENT_QUERY);
            mActionListener.search(currentQuery);
        }
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
}
