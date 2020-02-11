package io.github.blackfishlabs.artem.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.github.blackfishlabs.artem.R;
import io.github.blackfishlabs.artem.ui.common.BaseActivity;

import static java.util.Objects.requireNonNull;

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.btnLogin)
    Button btnLogin;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private static final int RC_SIGN_IN = 123;

    @Override
    protected int provideContentViewResource() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void onCreate(@Nullable Bundle inState) {
        super.onCreate(inState);

        auth = FirebaseAuth.getInstance();
        authListener = firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                navigate().toMainActivity();
                finish();
            }
        };

        btnLogin.setOnClickListener(view -> startSignIn());
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.logo)
                        .setTheme(R.style.LoginTheme)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {
                navigate().toMainActivity();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(this, getString(R.string.all_retry), Toast.LENGTH_LONG).show();
                    return;
                }

                if (requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, getString(R.string.all_network_error_message), Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(this, getString(R.string.all_unknown_error_message), Toast.LENGTH_LONG).show();
            }
        }
    }

}
