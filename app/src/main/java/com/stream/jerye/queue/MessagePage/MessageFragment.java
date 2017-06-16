package com.stream.jerye.queue.MessagePage;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stream.jerye.queue.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private EditText mEditText;
    private Button mMessageSendButton;
    private String mUsername;
    private DatabaseReference mDatabaseMessagesReference;
    private ChildEventListener mMessagesChildEventListener;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    private List<Message> messageList;
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private View mRootView;


    public MessageFragment() {
        // Required empty public constructor
    }


    public static MessageFragment newInstance() {
        Bundle args = new Bundle();

        MessageFragment fragment = new MessageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.chat_fragment,container,false);
        mEditText = (EditText) mRootView.findViewById(R.id.edit_textbox);
        mMessageSendButton = (Button) mRootView.findViewById(R.id.send_button);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.text);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        messageList = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(getContext(), messageList);
        mRecyclerView.setAdapter(mMessageAdapter);
        mUsername = "Kevin";
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message(mEditText.getText().toString(), mUsername, null);

                mDatabaseMessagesReference.push().setValue(message);
                mEditText.setText("");
            }
        });

        mMessagesChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                mMessageAdapter.add(message);

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
        };
        mDatabaseMessagesReference.addChildEventListener(mMessagesChildEventListener);
        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseMessagesReference = mFirebaseDatabase.getReference().child("messages");


    }
}
