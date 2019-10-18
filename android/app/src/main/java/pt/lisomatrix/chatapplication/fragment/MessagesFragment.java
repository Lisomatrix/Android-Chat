package pt.lisomatrix.chatapplication.fragment;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.activity.MessagesActivity;
import pt.lisomatrix.chatapplication.adapter.MessageListAdapter;
import pt.lisomatrix.chatapplication.model.Message;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.service.SocketServiceCallbacks;
import pt.lisomatrix.chatapplication.viewmodel.MessagesFragmentViewModel;

public class MessagesFragment extends Fragment implements SocketServiceCallbacks {

    private User mSelectedUser;
    private String mThisUserId;

    private View view;
    private MessagesFragmentViewModel mViewModel;

    private RecyclerView mMessageList;
    private MessageListAdapter mMessageListAdapter;

    private EditText mMessageEditText;
    private ImageButton mSendImageButton;

    public static MessagesFragment newInstance(User user, String mThisUserId) {
        return new MessagesFragment(user, mThisUserId);
    }

    public MessagesFragment(User user, String thisUserId) {
        mSelectedUser = user;
        mThisUserId = thisUserId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.messages_fragment, container, false);
        mMessageList = view.findViewById(R.id.message_recycler_view);
        mMessageEditText = view.findViewById(R.id.message_text);
        mSendImageButton = view.findViewById(R.id.send_button);

        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MessagesFragmentViewModel.class);
        mViewModel.setContext(getContext(), mSelectedUser.getUserId(), mThisUserId);

        mViewModel.getMessages()
                .subscribe(mMessageListAdapter::setMessages);
    }

    private void init() {
        mMessageListAdapter = new MessageListAdapter(mSelectedUser);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(layoutManager);
        mMessageList.setAdapter(mMessageListAdapter);

        mSendImageButton.setOnClickListener(view -> {
            Message message = new Message();
            message.setDestiny(mSelectedUser.getUserId());
            message.setMessage(mMessageEditText.getText().toString());

            mMessageEditText.setText("");

            // Send message to socket service or to activity
            ((MessagesActivity) getActivity()).sendMessage(message);
        });

        ((MessagesActivity) getActivity()).setCallbacks(this::addMessage);
    }

    @Override
    public void addMessage(Message message) {
        mMessageListAdapter.addMessage(message);
    }
}
