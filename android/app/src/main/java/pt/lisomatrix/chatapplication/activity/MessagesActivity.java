package pt.lisomatrix.chatapplication.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.IBinder;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.fragment.UsersFragments;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;
import pt.lisomatrix.chatapplication.model.Message;
import pt.lisomatrix.chatapplication.service.SocketService;
import pt.lisomatrix.chatapplication.service.SocketServiceCallbacks;
import pt.lisomatrix.chatapplication.viewmodel.MessagesViewModel;

public class MessagesActivity extends AppCompatActivity {

    /**
     * User list fragment
     */
    private Fragment usersFragment;

    /**
     * This activity view model
     */
    private MessagesViewModel mViewModel;

    /**
     * WebSocket Service Binder
     */
    private SocketService.LocalBinder mLocalBinder;

    /**
     * WebSocket Service instance
     */
    private SocketService mSocketService;

    /**
     * Is service connected
     */
    private boolean isServiceConnected = false;

    /**
     * Shared preferences helper instance
     */
    private SharedPreferencesHelper mSharedPreferencesHelper;

    /**
     * Starts this activity
     *
     * @param context
     */
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MessagesActivity.class));
    }

    /**
     * Service connection instance
     */
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServiceConnected = true;
            mSocketService = ((SocketService.LocalBinder) service).getService();
            mLocalBinder = (SocketService.LocalBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceConnected = false;
        }
    };

    /**
     * Sets service call backs in order to update the UI
     *
     * @param socketServiceCallbacks
     */
    public void setCallbacks(SocketServiceCallbacks socketServiceCallbacks) {
        mLocalBinder.setCallbacks(socketServiceCallbacks);
    }

    /**
     * Send message to server
     *
     * @param message
     */
    public void sendMessage(Message message) {
        mSocketService.sendMessageEvent(message);
    }

    public void isTyping(boolean typing) {
        mSocketService.sendTypingEvent(typing);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if use has token, if not send user back to login screen
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);

        try {
            String token = mSharedPreferencesHelper.getToken();

            if (token.equals("")) {
                LoginActivity.startActivity(this);
            }
        } catch (Exception ex) {
            LoginActivity.startActivity(this);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mViewModel = ViewModelProviders.of(this).get(MessagesViewModel.class);

        showUsersFragment();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            // Bind Service
            Intent startService = new Intent(this, SocketService.class);
            startService.putExtra("token", mSharedPreferencesHelper.getToken());

            bindService(startService, mServiceConnection, Context.BIND_AUTO_CREATE);
            startService(new Intent(this, SocketService.class));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind Service
        if (isServiceConnected) {
            unbindService(mServiceConnection);
            stopService(new Intent(this, SocketService.class));
            isServiceConnected = false;
        }
    }

    /**
     * Shows users fragment
     */
    private void showUsersFragment() {
        usersFragment = new UsersFragments();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, usersFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
