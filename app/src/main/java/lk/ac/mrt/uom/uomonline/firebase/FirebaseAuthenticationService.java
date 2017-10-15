package lk.ac.mrt.uom.uomonline.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import lk.ac.mrt.uom.uomonline.R;

/**
 * Created by Dimuth Tharaka on 8/28/2017.
 */

public class FirebaseAuthenticationService {

    private static GoogleApiClient mGoogleApiClient = null;
    private static GoogleSignInOptions gso = null;


    public static void InitializeAuthentication(FragmentActivity context) {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
                .enableAutoManage(context, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("TAG", "Connection failed for GoogleAPI Client");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    public static GoogleApiClient getApiClient() {
        return mGoogleApiClient;
    }

    public static GoogleSignInOptions getSignInOptions() {
        return gso;
    }
}
