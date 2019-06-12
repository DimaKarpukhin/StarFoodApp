package com.studymobile.moonlight.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.Common.CommonUser;
import com.studymobile.moonlight.Models.User;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.Services.InputValidation;

import java.util.Arrays;
import java.util.Objects;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener
{
    private static final int INPUT_PARAM = 2;
    private static final int ERROR_PARAM = 0;
    private static final int TOAST_PARAM = 1;
    private static final int MAX_PARAMS = 3;
    private static  final int RC_SIGN_IN = 9001;
    private static final String ALLOW_ANONYMOUS_USER = "allow_anonymous_user";
    private static final String ANONYMOUS_REMOTE_ALLOW = "true";
    public static final String EMAIL_PASSWORD = "email/password";
    public static final String GOOGLE = "google";
    public static final String FACEBOOK = "facebook";
    public static final String ANONYMOUS = "anonymous";

    private FirebaseAuth m_Auth;
    private FirebaseRemoteConfig m_FirebaseRemoteConfig;
    private FirebaseDatabase m_Database;

    GoogleSignInClient m_GoogleSignInClient;
    CallbackManager m_FacebookCallbackManager;
    LoginButton m_FacebookLoginButton;

    private EditText m_EmailLoginField;
    private EditText m_PasswordLoginField;
    private ProgressDialog m_LoadingBar;
    private RelativeLayout m_LayoutSkip;

    private AnalyticsManager m_AnalyticsManager = AnalyticsManager.GetInstance();

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);

        setContentView(R.layout.activity_login);
        setContentViewFields();
        setFirebaseData();
        setGoogleData();
        setFacebookData();
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.btn_login) {
            signInWithEmailAndPassword();
            m_AnalyticsManager.TrackLoginEvent(EMAIL_PASSWORD);
        }
        else if (id == R.id.btn_google_sign_in) {
            signInWithGoogle();
            m_AnalyticsManager.TrackLoginEvent(GOOGLE);
        }
        else if(id == R.id.btn_fb_login) {
            signInWithFacebook();
            m_AnalyticsManager.TrackLoginEvent(FACEBOOK);
        }
        else if (id == R.id.txt_link_sign_up_now) {
            startActivity(new Intent(ActivityLogin.this, ActivitySignUp.class));
        }
        else if (id == R.id.txt_link_reset_password) {
            startActivity(new Intent (ActivityLogin.this, ActivityResetPassword.class));
        }
        else if (id == R.id.txt_link_skip) {
            m_AnalyticsManager.TrackLoginEvent(ANONYMOUS);
            signInAnonymously();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = m_Auth.getCurrentUser();

        if(currentUser!= null)
        {
            if(!Objects.requireNonNull(currentUser.getEmail()).isEmpty() )
            {
                CommonUser.setName(currentUser.getEmail());
                CommonUser.setIsAnonymous(false);
            }
            else if(!Objects.requireNonNull(currentUser.getDisplayName()).isEmpty() )
            {
                CommonUser.setName(currentUser.getDisplayName());
                CommonUser.setIsAnonymous(false);

            }else{
                CommonUser.setName("Guest");
                CommonUser.setIsAnonymous(true);
            }

            startHomeActivity();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }

    private void showLoadingBar()
    {
        m_LoadingBar.setTitle("Login...");
        m_LoadingBar.setMessage("Wait please...");
        m_LoadingBar.setCanceledOnTouchOutside(true);
        m_LoadingBar.show();
    }

    //EMAIL AND PASSWORD SIGN IN:
    private void signInWithEmailAndPassword()
    {
        String[] emailParams = new String[MAX_PARAMS];
        String[] passwordParams = new String[MAX_PARAMS];
        emailParams[INPUT_PARAM] = m_EmailLoginField.getText().toString();
        passwordParams[INPUT_PARAM] = m_PasswordLoginField.getText().toString();

        if (!InputValidation.IsValidEmail(emailParams))
        {
            m_EmailLoginField.setError(emailParams[ERROR_PARAM]);
            Toast.makeText(this, emailParams[TOAST_PARAM], Toast.LENGTH_SHORT).show();

            return;
        }
        if (!InputValidation.IsValidPassword(passwordParams))
        {
            m_PasswordLoginField.setError(passwordParams[ERROR_PARAM]);
            Toast.makeText(this, passwordParams[TOAST_PARAM], Toast.LENGTH_SHORT).show();

            return;
        }

        signIn();
    }

    private void signIn()
    {
        final String email = m_EmailLoginField.getText().toString();
        String password = m_PasswordLoginField.getText().toString();

        showLoadingBar();
        m_Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            createDatabaseUser("EMAIL AUTH");
                            m_AnalyticsManager.SetUserID(Objects.requireNonNull(m_Auth.getCurrentUser()).getUid(), false);
                            m_AnalyticsManager.SetUserProperty(
                                    "Email_Password_Users", m_Auth.getCurrentUser().getEmail());
                            Toast.makeText(ActivityLogin.this,
                                    "Logging in with Email and Password...", Toast.LENGTH_SHORT).show();
                            CommonUser.setContext("Email/Password_Users");
                            CommonUser.setName(email);
                            CommonUser.setIsAnonymous(false);
                            startHomeActivity();
                        } else {
                            String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                            Toast.makeText(ActivityLogin.this,
                                    "Authentication failed:\n" + msg, Toast.LENGTH_SHORT).show();
                        }

                        m_LoadingBar.dismiss();
                    }
                });
    }

    //GOOGLE SIGN IN:
    private void setGoogleData()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        m_GoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findViewById(R.id.btn_google_sign_in).setOnClickListener(this);
    }

    private void signInWithGoogle()
    {
        showLoadingBar();
        Intent signInIntent = m_GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int i_RequestCode, int i_ResultCode, Intent i_Intent)
    {
        super.onActivityResult(i_RequestCode, i_ResultCode, i_Intent);

        if (i_RequestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = com.google.android.gms.auth.
                    api.signin.GoogleSignIn.getSignedInAccountFromIntent(i_Intent);
            try
            {
                if(task.isSuccessful())
                {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    makeFirebaseAuthWithGoogle(Objects.requireNonNull(account));
                }

                m_LoadingBar.dismiss();
            }
            catch (ApiException e)
            {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(ActivityLogin.this, "ERROR: Google sign in\n"
                        + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                m_LoadingBar.dismiss();
            }
        }
        else {
            m_FacebookCallbackManager.onActivityResult(i_RequestCode, i_ResultCode, i_Intent);
        }
    }

    private void makeFirebaseAuthWithGoogle(GoogleSignInAccount i_Account)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(i_Account.getIdToken(), null);
        m_Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            createDatabaseUser("GOOGLE AUTH");
                            m_AnalyticsManager.SetUserID(Objects.requireNonNull(m_Auth.getCurrentUser()).getUid(), false);
                            m_AnalyticsManager.SetUserProperty("Google_Users", m_Auth.getCurrentUser().getUid());
                            Toast.makeText(ActivityLogin.this,
                                    "Logging in with Google...", Toast.LENGTH_SHORT).show();
                            CommonUser.setContext("Google_Users");
                            CommonUser.setName(m_Auth.getCurrentUser().getDisplayName());
                            CommonUser.setIsAnonymous(false);
                            startHomeActivity();
                        } else {
                            String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                            Toast.makeText(ActivityLogin.this,
                                    "Authentication with Google failed:\n" + msg, Toast.LENGTH_SHORT).show();
                        }

                        m_LoadingBar.dismiss();
                    }
                });
    }

    //FACEBOOK LOGIN:
    private void setFacebookData()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        m_FacebookLoginButton = findViewById(R.id.btn_fb_login);
        m_FacebookLoginButton.setOnClickListener(this);
        m_FacebookLoginButton.setReadPermissions(Arrays.asList("email"));//, "public_profile");
    }

    private void signInWithFacebook()
    {
        showLoadingBar();
        m_FacebookCallbackManager = CallbackManager.Factory.create();
        m_FacebookLoginButton.registerCallback(m_FacebookCallbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() { m_LoadingBar.dismiss(); }

            @Override
            public void onError(FacebookException error)
            {
                Toast.makeText(ActivityLogin.this,
                        "ERROR:\n" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                m_LoadingBar.dismiss();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken i_Token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(i_Token.getToken());
        m_Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            createDatabaseUser("FACEBOOK AUTH");
                            m_AnalyticsManager.SetUserID(Objects.requireNonNull(m_Auth.getCurrentUser()).getUid(), false);
                            m_AnalyticsManager.SetUserProperty(
                                    "Facebook_Users", m_Auth.getCurrentUser().getDisplayName());

                            CommonUser.setContext("Facebook_Users");
                            CommonUser.setName(m_Auth.getCurrentUser().getDisplayName());
                            CommonUser.setIsAnonymous(false);
                            startHomeActivity();
                            Toast.makeText(ActivityLogin.this,
                                    "Logging in with Facebook...", Toast.LENGTH_SHORT).show();

                        } else {
                            String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                            Toast.makeText(ActivityLogin.this,
                                    "Authentication with Facebook failed:" + msg, Toast.LENGTH_SHORT).show();
                        }

                        m_LoadingBar.dismiss();
                    }
                });
    }

    //ANONYMOUS AUTHENTICATION:
    private void signInAnonymously()
    {
        showLoadingBar();
        m_Auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            createDatabaseUser("ANONYMOUS");
                            m_AnalyticsManager.SetUserID(Objects.requireNonNull(m_Auth.getCurrentUser()).getUid(), false);
                            m_AnalyticsManager.SetUserProperty("Anonymous_Users", "Guest");

                            Toast.makeText(ActivityLogin.this, "Please wait...", Toast.LENGTH_SHORT).show();
                            CommonUser.setContext("Anonymous_Users");
                            CommonUser.setName("Guest");
                            CommonUser.setIsAnonymous(true);
                            startHomeActivity();
                        } else {
                            String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                            Toast.makeText(ActivityLogin.this, "ERROR:" + msg, Toast.LENGTH_SHORT).show();
                        }

                        m_LoadingBar.dismiss();
                    }
                });
    }

    //REMOTE CONFIG:
    private void setFirebaseData()
    {
        m_LoadingBar = new ProgressDialog(this);
        m_Auth = FirebaseAuth.getInstance();
        m_Database = FirebaseDatabase.getInstance();
        m_FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        m_FirebaseRemoteConfig.setConfigSettings(configSettings);
        m_FirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);

        fetchConfig();
    }

    private void fetchConfig()
    {
        long cacheExpiration = 3660; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0,
        // so each fetch will retrieve values from the service.
        if (m_FirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled())
        {
            cacheExpiration = 0;
        }
        m_FirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            m_FirebaseRemoteConfig.activateFetched();
                        } else {
                            Toast.makeText(ActivityLogin.this, "ERROR: Fetch from server failed",
                                    Toast.LENGTH_SHORT).show();
                        }

                        allowAnonymousUser();
                    }
                });
    }

    private void allowAnonymousUser()
    {
        if (m_FirebaseRemoteConfig.getBoolean(ALLOW_ANONYMOUS_USER))
        {
            m_LayoutSkip.setVisibility(View.VISIBLE);
        }

    }

    private void setContentViewFields()
    {
        m_EmailLoginField = findViewById(R.id.edit_email_login);
        m_PasswordLoginField = findViewById(R.id.edit_password_login);
        findViewById(R.id.btn_login).setOnClickListener(this);

        m_LayoutSkip = findViewById(R.id.layout_skip);

        findViewById(R.id.txt_link_skip).setOnClickListener(this);
        findViewById(R.id.txt_link_sign_up_now).setOnClickListener(this);
        findViewById(R.id.txt_link_reset_password).setOnClickListener(this);
    }

    private void createDatabaseUser(String i_Context)
    {
        User user = new User();
        FirebaseUser currentUser = m_Auth.getCurrentUser();

        if(currentUser != null)
        {
            user.setContext(i_Context);
            user.setName(currentUser.getDisplayName());
            user.setEmail(currentUser.getEmail());
            m_Database.getReference("Users").child(currentUser.getUid()).setValue(user);
        }
    }

    private void startHomeActivity()
    {
        Intent HomeIntent = (new Intent(ActivityLogin.this, ActivityHome.class));
        startActivity(HomeIntent);
    }
}