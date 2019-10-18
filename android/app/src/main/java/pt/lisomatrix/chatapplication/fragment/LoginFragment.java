package pt.lisomatrix.chatapplication.fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.activity.MessagesActivity;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;
import pt.lisomatrix.chatapplication.model.Token;
import pt.lisomatrix.chatapplication.network.Authenticate;
import pt.lisomatrix.chatapplication.viewmodel.LoginFragmentViewModel;

public class LoginFragment extends Fragment {

    private View view;

    // UI Views
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button noAccountButton;
    private Button loginButton;

    /**
     * This class view model
     */
    private LoginFragmentViewModel mViewModel;

    /**
     * Shared preferences instance
     */
    private SharedPreferencesHelper mSharedPreferencesHelper;

    /**
     * Creates this fragment
     * @return
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.login_fragment, container, false);

        emailEditText = view.findViewById(R.id.email_text);
        passwordEditText = view.findViewById(R.id.password_text);
        noAccountButton = view.findViewById(R.id.create_button);
        loginButton = view.findViewById(R.id.login_button);

        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginFragmentViewModel.class);
        mViewModel.setContext(getContext());

        mSharedPreferencesHelper = new SharedPreferencesHelper(getContext());
    }

    private void init() {
        loginButton.setOnClickListener(this::authenticate);
        noAccountButton.setOnClickListener(this::showRegisterFragment);
    }

    private void showRegisterFragment(View view) {
        Fragment fragment = new RegisterFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.animator.slide_in_left ,R.animator.slide_out_right, 0, 0);
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private void authenticate(View view) {
        Authenticate authenticate = new Authenticate();
        authenticate.setPassword(passwordEditText.getText().toString());
        authenticate.setEmail(emailEditText.getText().toString());

        mViewModel.authenticate(authenticate).subscribe(this::saveToken);
    }

    private void saveToken(Token token) {
        mSharedPreferencesHelper.setToken(token.getToken());
        MessagesActivity.startActivity(getContext());
    }
}
