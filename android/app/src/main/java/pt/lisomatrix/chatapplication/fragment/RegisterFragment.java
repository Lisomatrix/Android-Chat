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
import android.widget.EditText;

import pt.lisomatrix.chatapplication.R;
import pt.lisomatrix.chatapplication.model.User;
import pt.lisomatrix.chatapplication.viewmodel.RegisterFragmentViewModel;

public class RegisterFragment extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;

    private Button registerButton;
    private Button haveAccountButton;

    private RegisterFragmentViewModel mViewModel;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.register_fragment, container, false);

        emailEditText = view.findViewById(R.id.email_text);
        passwordEditText = view.findViewById(R.id.password_text);
        nameEditText = view.findViewById(R.id.name_text);
        registerButton = view.findViewById(R.id.register_button);
        haveAccountButton = view.findViewById(R.id.have_account_button);

        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RegisterFragmentViewModel.class);
        mViewModel.setContext(getContext());
    }

    private void init() {
        registerButton.setOnClickListener((view) -> {
            User user = new User();
            user.setName(nameEditText.getText().toString());
            user.setEmail(emailEditText.getText().toString());
            user.setPassword(passwordEditText.getText().toString());

            mViewModel.register(user);
        });

        haveAccountButton.setOnClickListener((view) -> showLoginActivity());
    }

    private void showLoginActivity() {
        Fragment fragment = new LoginFragment();

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.setCustomAnimations(R.animator.slide_out_left ,R.animator.slide_in_right, 0, 0);
        transaction.replace(R.id.login_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }
}
