package pt.lisomatrix.chatapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.model.User;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListItemViewHolder> {

    /**
     * Subject to warn the UI for elements clicked
     */
    private Subject<User> mClickedItem = PublishSubject.create();
    private List<User> mUsers = new ArrayList<>();

    /**
     * Replaces users list and updates UI
     *
     * @param users
     */
    public void setUsers(List<User> users) {
        if (users.size() == 0) {
            return;
        }

        mUsers = users;
        notifyDataSetChanged();
    }

    /**
     * Get stream of items clicked
     * @return
     */
    public Observable<User> getClickedItem() {
        return mClickedItem.serialize();
    }

    @NonNull
    @Override
    public UserListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);

        UserListItemViewHolder viewHolder = new UserListItemViewHolder(parentView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListItemViewHolder holder, int position) {
        User temp = mUsers.get(position);
        holder.setName(temp.getName());

        holder.itemView.setOnClickListener(view -> mClickedItem.onNext(temp));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class UserListItemViewHolder extends RecyclerView.ViewHolder {

        private TextView userTextView;


        public UserListItemViewHolder(@NonNull View itemView) {
            super(itemView);
            userTextView = itemView.findViewById(R.id.user_name_view);
        }

        public void setName(String name) {
            userTextView.setText(name);
        }
    }
}
