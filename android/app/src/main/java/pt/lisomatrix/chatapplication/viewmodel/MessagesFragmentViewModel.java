package pt.lisomatrix.chatapplication.viewmodel;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import pt.lisomatrix.chatapplication.database.Database;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;
import pt.lisomatrix.chatapplication.model.Message;
import pt.lisomatrix.chatapplication.retrofit.RetrofitSingleton;
import pt.lisomatrix.chatapplication.retrofit.api.MessagesService;

public class MessagesFragmentViewModel extends ViewModel {

    /**
     * Application Context
     */
    private Context mContext;

    /**
     * Message Service instance
     */
    private MessagesService mMessagesService;

    /**
     * Authentication token
     */
    private String token;

    /**
     * Shared preferences helper
     */
    private SharedPreferencesHelper mSharedPreferencesHelper;

    /**
     * Are the messages already fetched
     */
    private boolean hasFetchedMessages = false;

    /**
     * Messages Subject
     */
    private Subject<List<Message>> mMessagesSubject = BehaviorSubject.create();

    /**
     * Set application context and peer user id and this user id
     *
     * @param context Application Context
     * @param destiny Peer user id
     * @param sender  This user id
     */
    public void setContext(Context context, String destiny, String sender) {
        mContext = context.getApplicationContext();

        mMessagesService = RetrofitSingleton.getInstance().create(MessagesService.class);
        mSharedPreferencesHelper = new SharedPreferencesHelper(mContext);

        token = mSharedPreferencesHelper.getToken();

        if (!hasFetchedMessages) {
            getMessagesFromNetwork();
        }

        setChatUsers(destiny, sender);
    }

    /**
     * Get messages observable
     *
     * @return
     */
    public Observable<List<Message>> getMessages() {
        return mMessagesSubject.serialize()
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Set user ids in order to get messages
     *
     * @param destinyId
     * @param senderId
     */
    private void setChatUsers(String destinyId, String senderId) {
        Database.getInstance(mContext)
                .messageDao()
                .getConversationMessages(destinyId, senderId)
                .subscribeOn(Schedulers.io())
                .subscribe(messages -> mMessagesSubject.onNext(messages));
    }

    /**
     * Get messages from network
     */
    private void getMessagesFromNetwork() {
        mMessagesService.getMessages(token)
                .subscribeOn(Schedulers.io())
                .subscribe(messages -> {
                    Database.getInstance(mContext)
                            .messageDao()
                            .insertAll(messages)
                            .subscribeOn(Schedulers.io())
                            .subscribe();

                    hasFetchedMessages = true;
                });
    }
}
