package com.bjbarber.barber;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ULoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private int RC_SIGN_IN = 1;
    public static String TAG = "TAG_Key";
    CallbackManager callbackManager;
    TextView FacebookDataTextView;
    LoginButton loginButton;
    AccessTokenTracker accessTokenTracker;
    //ProfilePictureView profilePictureView;
    String FacebookUserID;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //one signal ki line ko in 2 lines k darmyan mei likhna hai. ye 2 lines pehly hi likhi hu gi.
        OneSignal.startInit(this).setNotificationOpenedHandler(new ExampleNotificationOpenedHandler()).init();
        setContentView(R.layout.activity_ulogin);

        mAuth = FirebaseAuth.getInstance();

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("keyHashh:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        getSupportActionBar().setTitle("Login");


        //checkInternet();

    }

    private void Login() {

        SignInButton googleSignIn = findViewById(R.id.sign_in_button);
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                mGoogleSignInClient = GoogleSignIn.getClient(ULoginActivity.this, gso);

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        // Passing MainActivity in Facebook SDK.
        FacebookSdk.sdkInitialize(ULoginActivity.this);

        // Adding Callback Manager.
        callbackManager = CallbackManager.Factory.create();


        // Assign TextView ID.
        FacebookDataTextView = (TextView) findViewById(R.id.TextView1);

        // Assign Facebook Login button ID.
        loginButton = (LoginButton) findViewById(R.id.login_button);


        // Giving permission to Login Button.
        loginButton.setReadPermissions("email");

        // Checking the Access Token.
        if (AccessToken.getCurrentAccessToken() != null) {

            ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null) {
                Toast.makeText(ULoginActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
            } else {
                GraphLoginRequest(AccessToken.getCurrentAccessToken());

                // If already login in then show the Toast.
                Toast.makeText(ULoginActivity.this, "Already logged in", Toast.LENGTH_SHORT).show();

            }


        } else {

            // If not login in then show the Toast.
            Toast.makeText(ULoginActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }

        // Adding Click listener to Facebook login button.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                // Calling method to access User Data After successfully login.
                GraphLoginRequest(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

                Toast.makeText(ULoginActivity.this, "Login Canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {

                Toast.makeText(ULoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }

        });

        // Detect user is login or not. If logout then clear the TextView and delete all the user info from TextView.
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {

                    // Clear the TextView after logout.
                    FacebookDataTextView.setText("");

                    // Clear the profilePictureView after logout.
                    //profilePictureView.setProfileId(null);

                }
            }
        };
    }

    private void checkInternet() {

        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(ULoginActivity.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
        } else {
            Login();
        }
    }

    // Method to access Facebook User Data.
    protected void GraphLoginRequest(AccessToken accessToken) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                        try {

                            // Getting Facebook User ID into variable.
                            FacebookUserID = jsonObject.getString("id");

                            // Adding Facebook User Full name into TextView.
                            // FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nName : " + jsonObject.getString("name"));

                            // Adding Facebook User Email into TextView.
                            //FacebookDataTextView.setText(FacebookDataTextView.getText() + "\nEmail : " + jsonObject.getString("email"));

                            // BY using FacebookUserID call profile pic from facebook server.
                            //profilePictureView.setProfileId(FacebookUserID);

                            Intent intent = new Intent(ULoginActivity.this, MainActivity.class);
                            startActivity(intent);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putString(
                "fields",
                "id,name,email"
        );
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(ULoginActivity.this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(ULoginActivity.this);

    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(ULoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
    }

    class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {

        }
    }

    public void keyHashes() {


    }
}
