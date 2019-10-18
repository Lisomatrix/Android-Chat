package pt.lisomatrix.chatapplication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;
import pt.lisomatrix.chatapplication.model.Token;
import pt.lisomatrix.chatapplication.network.Authenticate;
import pt.lisomatrix.chatapplication.retrofit.RetrofitSingleton;
import pt.lisomatrix.chatapplication.retrofit.api.UserService;

public class LoginFragmentViewModel extends ViewModel {

    private UserService mUserService;
    private Context mContext;

    public void setContext(Context context) {
        mContext = context.getApplicationContext();
        mUserService = RetrofitSingleton.getInstance().create(UserService.class);
    }

    public Maybe<Token> authenticate(Authenticate authenticate) {
        return mUserService.authenticate(authenticate)
                .subscribeOn(Schedulers.io());
    }


}
