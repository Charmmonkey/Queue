package com.stream.jerye.queue.firebase;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.jerye.queue.MessagePage.Message;
import com.stream.jerye.queue.MusicPage.SimpleTrack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerye on 6/20/2017.
 */

public class FirebaseEventBus {
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mMusicDatabaseReference, mMessageDatabaseReference;
    private static String TAG = "FirebaseEventBus.java";

    public interface FirebasePeekHandler {
        void peekedResult(List<SimpleTrack> list);
    }

    public interface FirebaseQueueAdapterHandler {
        void enqueue(SimpleTrack simpleTrack);
    }

    public interface FirebaseMessageHandler{
        void addMessage(Message message);
    }


    public static class MusicDatabaseAccess {
        private FirebasePeekHandler mFirebasePeekHandler;
        private FirebaseQueueAdapterHandler mFirebaseQueueAdapterHandler;

        public MusicDatabaseAccess(FirebasePeekHandler firebasePeekHandler) {
            mFirebasePeekHandler = firebasePeekHandler;
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMusicDatabaseReference = mFirebaseDatabase.getReference().child("tracks");
        }
        public MusicDatabaseAccess(FirebaseQueueAdapterHandler firebaseQueueAdapterHandler){
            mFirebaseQueueAdapterHandler = firebaseQueueAdapterHandler;
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMusicDatabaseReference = mFirebaseDatabase.getReference().child("tracks");
        }

        public void addChildListener() {
            mMusicDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    SimpleTrack simpleTrack = dataSnapshot.getValue(SimpleTrack.class);
                    simpleTrack.setKey(dataSnapshot.getKey());
                    mFirebaseQueueAdapterHandler.enqueue(simpleTrack);
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
        public void push(SimpleTrack simpleTrack){
            mMusicDatabaseReference.push().setValue(simpleTrack);
        }

        public void peek() {
            mMusicDatabaseReference.orderByKey().limitToFirst(2).addChildEventListener(new ChildEventListener() {
                List<SimpleTrack> list = new ArrayList<>(2);
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "peeked");
                    SimpleTrack track = dataSnapshot.getValue(SimpleTrack.class);
                    track.setKey(dataSnapshot.getKey());
                    list.add(track);
                    if(list.size()==2){
                        mFirebasePeekHandler.peekedResult(list);
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

        public void remove(SimpleTrack track){
            mMusicDatabaseReference.child(track.getKey()).removeValue();
        }
    }

    public static class MessageDatabaseAccess {
        private FirebaseMessageHandler mFirebaseMessageHandler;

        public MessageDatabaseAccess(FirebaseMessageHandler firebaseMessageHandler){
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mMessageDatabaseReference = mFirebaseDatabase.getReference().child("messages");
            mFirebaseMessageHandler = firebaseMessageHandler;
        }

        public void addChildListener() {
            mMessageDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mFirebaseMessageHandler.addMessage(message);
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

        public void push(String chatMessage, String userName){
            Message message = new Message(chatMessage, userName, null);
            mMessageDatabaseReference.push().setValue(message);
        }
    }

}
