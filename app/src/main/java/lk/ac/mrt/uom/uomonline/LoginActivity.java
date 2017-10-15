package lk.ac.mrt.uom.uomonline;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chootdev.csnackbar.Duration;
import com.chootdev.csnackbar.Snackbar;
import com.chootdev.csnackbar.Type;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import lk.ac.mrt.uom.uomonline.firebase.FirebaseAuthenticationService;


public class LoginActivity extends AppCompatActivity {

    private static int RC_SIGN_IN =1;
    private static String TAG = "LOGIN";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton googleSignInButton;
    private GoogleSignInOptions gso;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        FirebaseAuthenticationService.InitializeAuthentication(this);
        googleSignInButton = findViewById(R.id.googleButton);
        gso = FirebaseAuthenticationService.getSignInOptions();
        mGoogleApiClient = FirebaseAuthenticationService.getApiClient();
        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignIn();
            }
        });
    }


    private void GoogleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            gso = FirebaseAuthenticationService.getSignInOptions();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                mGoogleApiClient.clearDefaultAccountAndReconnect();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                System.out.println(result.getStatus());
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        final MaterialDialog materialDialog = new MaterialDialog.Builder(LoginActivity.this)
                .progress(true, 0)
                .title("Please Wait")
                .content("Logging In.")
                .show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("Name",user.getDisplayName());
                            intent.putExtra("Email",user.getEmail());
                            intent.putExtra("Phone",user.getPhoneNumber());
                            intent.putExtra("Image",user.getPhotoUrl());
                            startActivity(intent);
                            materialDialog.dismiss();
                            LoginActivity.this.finish();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(LoginActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            Snackbar.with(LoginActivity.this,null)
                                    .type(Type.ERROR)
                                    .message("Login Failed! Please try again.")
                                    .duration(Duration.LONG)
                                    .show();
                            materialDialog.dismiss();
                        }

                        // ...
                    }
                });

    }
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        Snackbar.with(LoginActivity.this,null)
                .type(Type.CUSTOM, 0xFF0288D1)
                .message("Please click BACK again to exit")
                .duration(Duration.LONG)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}

