package pt.lisomatrix.chatapplication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import io.reactivex.schedulers.Schedulers;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.retrofit.RetrofitSingleton;
import pt.lisomatrix.chatapplication.retrofit.api.UserService;

public class RegisterFragmentViewModel extends ViewModel {

    private UserService mUserService;

    public void setContext(Context context) {
        mUserService = RetrofitSingleton.getInstance().create(UserService.class);
    }


    public void register(User user) {
        mUserService.register(user)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
