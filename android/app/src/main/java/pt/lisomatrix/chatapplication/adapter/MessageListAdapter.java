package pt.lisomatrix.chatapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.model.Message;
import pt.lisomatrix.chatapplication.model.User;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private User mPeerUser;

    /**
     * This adapter recycler view
     */
    private RecyclerView mRecyclerView;

    /**
     * List of current user messages
     */
    private List<Message> mMessages = new ArrayList<>();

    public MessageListAdapter(User user) {
        mPeerUser = user;
    }

    /**
     * Adds message to the list and update UI
     * @param message
     */
    public void addMessage(Message message) {
        mMessages.add(message);
        notifyItemInserted(mMessages.size() > 0 ? mMessages.size() - 1 : 0);
        mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
    }

    /**
     * Replace messages list and updates UI
     *
     * @param messages
     */
    public void setMessages(List<Message> messages) {
        mMessages = messages;
        notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);

        if (message.getSender().equals(mPeerUser)) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        RecyclerView.ViewHolder viewHolder;

        if (VIEW_TYPE_MESSAGE_RECEIVED == viewType) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_message_list, parent, false);
            viewHolder = new ReceivedMessageItemListViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_list_item, parent, false);
            viewHolder = new SentMessageItemListViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message temp = mMessages.get(position);


        if (holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT) {
            ((SentMessageItemListViewHolder) holder).bind(temp);
        } else {
            ((ReceivedMessageItemListViewHolder) holder).bind(temp);
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class SentMessageItemListViewHolder extends RecyclerView.ViewHolder {

        private TextView senderMessageText;

        public SentMessageItemListViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.sender_message_view);
        }


        public void bind(Message message) {
            senderMessageText.setText(message.getMessage());
        }
    }

    public class ReceivedMessageItemListViewHolder extends RecyclerView.ViewHolder {

        private TextView messageText;
        private TextView timeText;
        private TextView nameText;
        private ImageView userImage;

        public ReceivedMessageItemListViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            userImage = itemView.findViewById(R.id.image_message_profile);
            nameText = itemView.findViewById(R.id.text_message_name);
        }

        public void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText("10:12");
            nameText.setText(mPeerUser.getName());
        }
    }
}
