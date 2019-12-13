package com.example.myapplication.ui.Account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import com.example.myapplication.R;

public class AccountFragment extends Fragment {
    Fragment fragmentRegistration;
    private AccountViewModel accountViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        accountViewModel  = ViewModelProviders.of(this).get(AccountViewModel.class);
        View root = inflater.inflate(R.layout.user_registration_fragment, container, false);

        EditText editTextPin = (EditText) root.findViewById(R.id.EditTextPin);
        editTextPin.setText("Пишу");
        Button buttonRegistration = (Button) root.findViewById(R.id.buttonRegistration);
        Button buttonEntry = (Button) root.findViewById(R.id.buttonEntrance);
        buttonEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRegistration = new UserRegistration();
                assert getFragmentManager() != null;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.add(R.id.nav_host_fragment, fragmentRegistration);
                transaction.commit();
            }
        });return root;
    }

    public AccountViewModel getAccountViewModel() {
        return accountViewModel;
    }

    public void setAccountViewModel(AccountViewModel accountViewModel) {
        this.accountViewModel = accountViewModel;
    }
}
