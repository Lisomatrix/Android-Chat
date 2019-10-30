package pt.lisomatrix.chatapplication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import pt.lisomatrix.chatapplication.database.Database;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.retrofit.RetrofitSingleton;
import pt.lisomatrix.chatapplication.retrofit.api.UserService;

public class UsersFragmentsViewModel extends ViewModel {

    private User mThisUser;

    private String token;

    private SharedPreferencesHelper mSharedPreferencesHelper;

    private BehaviorSubject<List<User>> usersSubject = BehaviorSubject.create();

    private UserService mUserService;
    private Context mContext;

    public void setContext(Context context) {
        this.mContext = context.getApplicationContext();
        mUserService = RetrofitSingleton.getInstance().create(UserService.class);

        mSharedPreferencesHelper = new SharedPreferencesHelper(context.getApplicationContext());

        try {
            token = mSharedPreferencesHelper.getToken();
        } catch (Exception ex) {

        }

        if (usersSubject.getValue() == null) {
            getUsersFromDatabase();
        }

        if (mThisUser == null) {
            mUserService.getMe(token)
                    .subscribeOn(Schedulers.io())
                    .subscribe(user -> mThisUser = user);
        }
    }

    public User getThisUser() {
        return mThisUser;
    }

    public Flowable<List<User>> getUsers() {
        return usersSubject.toFlowable(BackpressureStrategy.BUFFER);
    }

    public void updateFromNetwork() {
        mUserService.getUsers(token)
                .subscribeOn(Schedulers.io())
                .subscribe(users -> {
                    List<User> currentUsers = usersSubject.getValue();

                    if (currentUsers == null || currentUsers.size() == 0) {
                        addUsersToDatabase(users);
                    } else {
                        addUsersToDatabase(filterList(currentUsers, users));
                    }
                });
    }

    private void addUsersToDatabase(List<User> users) {
        Database.getInstance(mContext)
                .userDao()
                .insert(users)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void getUsersFromDatabase() {
        Database.getInstance(mContext)
                .userDao().getAll()
                .subscribeOn(Schedulers.io())
                .subscribe(users -> usersSubject.onNext(users));
    }

    private List<User> filterList(List<User> currentUsers, List<User> users) {
        List<User> newUsers = new ArrayList<>();

        for (int i = 0; i < currentUsers.size(); i++) {
            boolean isDuplicate = false;
            User temp = currentUsers.get(i);
            for (int y = 0; y < users.size(); y++) {

                if (temp.getUserId().equals(users.get(y).getUserId())) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                newUsers.add(temp);
            }
        }

        return newUsers;
    }
}