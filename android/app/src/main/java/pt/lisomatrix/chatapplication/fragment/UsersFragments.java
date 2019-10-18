package pt.lisomatrix.chatapplication.fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.adapter.UserListAdapter;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.viewmodel.UsersFragmentsViewModel;

public class UsersFragments extends Fragment {

    private View view;
    private RecyclerView mUsersRecyclerView;
    private UserListAdapter mUserListAdapter;

    private UsersFragmentsViewModel mViewModel;

    public static UsersFragments newInstance() {
        return new UsersFragments();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.users_fragments_fragment, container, false);
        mUsersRecyclerView = view.findViewById(R.id.users_recycler_view);
        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Generate view model
        mViewModel = ViewModelProviders.of(this).get(UsersFragmentsViewModel.class);
        mViewModel.setContext(getContext());

        // Get users from database
        mViewModel.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> mUserListAdapter.setUsers(users));

        // Update users from network
        mViewModel.updateFromNetwork();
    }

    private void init() {
        // Initialize recycler view
        mUserListAdapter = new UserListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        mUsersRecyclerView.setHasFixedSize(true);
        mUsersRecyclerView.setAdapter(mUserListAdapter);
        mUsersRecyclerView.setLayoutManager(layoutManager);

        mUserListAdapter.getClickedItem().subscribe(this::showMessagesFragment);
    }

    private void showMessagesFragment(User user) {
        Fragment messagesFragment = MessagesFragment.newInstance(user, mViewModel.getThisUser().getUserId());
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, messagesFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
