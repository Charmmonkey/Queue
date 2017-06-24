package com.stream.jerye.queue;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.jerye.queue.MusicPage.SpotifyTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 6/20/2017.
 */

public class FirebaseEventBus {
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mMusicDatabaseReference, mMessageDatabaseReference;
    private static String TAG = "FirebaseEventBus.java";

    public interface FirebaseListener {
        void peekedResult(List<SpotifyTrack> list);
    }

    public static class MusicDatabaseAccess {
        private FirebaseListener mFirebaseListener;

        public MusicDatabaseAccess(FirebaseListener firebaseListener) {
            mFirebaseListener = firebaseListener;
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMusicDatabaseReference = mFirebaseDatabase.getReference().child("tracks");
        }

        public void addChildListener() {
            mMusicDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
        }

        public void peek() {
            mMusicDatabaseReference.orderByKey().limitToFirst(2).addChildEventListener(new ChildEventListener() {
                List<SpotifyTrack> list = new ArrayList<>(2);
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "peeked");
                    list.add(dataSnapshot.getValue(SpotifyTrack.class));
                    if(list.size()==2){
                        mFirebaseListener.peekedResult(list);
                    }
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
        }
    }

    public class MessageDatabaseAccess {
    }
}
