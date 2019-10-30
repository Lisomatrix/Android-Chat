package pt.lisomatrix.chatapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.fragment.LoginFragment;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferencesHelper mSharedPreferencesHelper;

    /**
     * Start this activity
     *
     * @param context
     */
    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Check if has a token
        mSharedPreferencesHelper = new SharedPreferencesHelper(this);

        try {
            String token = mSharedPreferencesHelper.getToken();

            // If a token is found then go to messages activity
            if (!token.equals("")) {
                MessagesActivity.startActivity(this);
            }
        } catch (Exception ex) {
            MessagesActivity.startActivity(this);
        }

        // Otherwise show login fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        showLoginFragment();
    }

    /**
     * Shows login fragment
     */
    private void showLoginFragment() {
        Fragment fragment = new LoginFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
