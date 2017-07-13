package com.stream.jerye.queue.messagePage;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.stream.jerye.queue.R;
import com.stream.jerye.queue.firebase.FirebaseEventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements FirebaseEventBus.FirebaseMessageHandler {
    private EditText mEditText;
    private ImageView mMessageSendButton;
    private String mUsername;
    private RecyclerView mRecyclerView;
    private MessageAdapter mMessageAdapter;
    private List<Message> messageList;
    private View mRootView;
    private FirebaseEventBus.MessageDatabaseAccess mMessageDatabaseAccess;
    private String TAG = "MessageFragment.java";

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
        mRootView = inflater.inflate(R.layout.chat_fragment, container, false);
        mEditText = (EditText) mRootView.findViewById(R.id.edit_textbox);
        mMessageSendButton = (ImageView) mRootView.findViewById(R.id.send_button);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.text);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        messageList = new ArrayList<>();

        SharedPreferences prefs = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);
        mUsername = prefs.getString("profile name", "unknown");
        mMessageAdapter = new MessageAdapter(getContext(), messageList);
        mRecyclerView.setAdapter(mMessageAdapter);



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
                mMessageDatabaseAccess.push(mEditText.getText().toString(), mUsername);
                mEditText.setText("");
            }
        });

        return mRootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessageDatabaseAccess = new FirebaseEventBus.MessageDatabaseAccess(getContext(),this);
        mMessageDatabaseAccess.addChildListener();
    }

    @Override
    public void addMessage(Message message) {
        mMessageAdapter.add(message);
        Log.d(TAG, "added Message");
    }
}
