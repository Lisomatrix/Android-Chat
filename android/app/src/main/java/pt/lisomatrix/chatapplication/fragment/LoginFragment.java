package pt.lisomatrix.chatapplication.fragment;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.activity.MessagesActivity;
import pt.lisomatrix.chatapplication.helper.SharedPreferencesHelper;
import pt.lisomatrix.chatapplication.helper.ValidationHelper;
import pt.lisomatrix.chatapplication.model.Token;
import pt.lisomatrix.chatapplication.network.Authenticate;
import pt.lisomatrix.chatapplication.viewmodel.LoginFragmentViewModel;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private View view;

    // UI Views
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
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
        emailInputLayout = view.findViewById(R.id.email_input_layout);
        passwordInputLayout = view.findViewById(R.id.password_input_layout);

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
       if (!validateInputs()) {
           return;
       }

        Authenticate authenticate = new Authenticate();
        authenticate.setPassword(passwordEditText.getText().toString());
        authenticate.setEmail(emailEditText.getText().toString());

        mViewModel.authenticate(authenticate)
                .subscribe(this::saveToken);
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show());
    }

    private void saveToken(Response<Token> tokenResponse) {
        if (!tokenResponse.isSuccessful()) {
            showResponseErrors(tokenResponse);
            return;
        }

        try {
            mSharedPreferencesHelper.setToken(tokenResponse.body().getToken());
            MessagesActivity.startActivity(getContext());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showResponseErrors(Response response) {
        getActivity().runOnUiThread(() -> {
            try {
                JSONObject errorJson = new JSONObject(response.errorBody().string());

                if (errorJson.getBoolean("isEmailError")) {
                    emailInputLayout.setError(errorJson.getString("message"));
                    passwordInputLayout.setError(null);
                } else {
                    passwordInputLayout.setError(errorJson.getString("message"));
                    emailInputLayout.setError(null);
                }

                showToast(errorJson.getString("message"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private boolean validateInputs() {
        boolean valid = true;

        String emailText = emailEditText.getText().toString();

        if (passwordEditText.getText().toString().trim().equals("")) {
            passwordInputLayout.setError("Password field can't be empty!");
            valid = false;
        }


        if (emailText.trim().equals("")) {
            emailInputLayout.setError("Email field can't be empty!");
            valid = false;
        }

        if (!ValidationHelper.emailIsValid(emailText)) {
            emailInputLayout.setError("Email is invalid!");
            valid = false;
        }

        return valid;
    }
}
