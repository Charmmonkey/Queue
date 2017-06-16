package com.stream.jerye.queue.MessagePage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stream.jerye.queue.R;

import java.util.List;

/**
 * Created by jerye on 6/12/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public Context mContext;
    public List<Message> mMessageList;

    public MessageAdapter(Context context, List<Message> list) {
        mContext = context;
        mMessageList = list;
    }


    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.messageName.setText(mMessageList.get(position).getName());
        holder.messageContent.setText(mMessageList.get(position).getText());

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_layout, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (mMessageList == null) {
            return 0;
        } else {
            return mMessageList.size();
        }
    }

    public void add(Message message) {
        mMessageList.add(message);
        notifyDataSetChanged();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageName;
        TextView messageContent;

        public MessageViewHolder(View view) {
            super(view);

            messageName = (TextView) view.findViewById(R.id.message_name);
            messageContent = (TextView) view.findViewById(R.id.message_content);
        }

        ;
    }
}
