package com.studymobile.moonlight.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studymobile.moonlight.Analytics.AnalyticsManager;
import com.studymobile.moonlight.Common.CommonUser;
import com.studymobile.moonlight.Models.User;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.Services.InputValidation;

import java.util.Objects;

public class ActivitySignUp extends AppCompatActivity implements View.OnClickListener
{
    private static final int INPUT_PARAM = 2;
    private static final int ERROR_PARAM = 0;
    private static final int TOAST_PARAM = 1;
    private static final int MAX_PARAMS = 3;
    private static final String EMAIL_AND_PASSWORD_USER = "EMAIL AUTH";
    public static final String EMAIL_PASSWORD = "Email/Password";

    private EditText m_EmailSignUpField, m_PasswordSignUpField;
    private ProgressDialog m_LoadingBar;

    private FirebaseAuth m_Auth;
    private FirebaseDatabase m_Database;

    private AnalyticsManager m_AnalyticsManager = AnalyticsManager.GetInstance();

    @Override
    protected void onCreate(@Nullable Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_signup);
        setContentViewFields();
        setFirebaseData();
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.btn_signUp)
        {
            m_AnalyticsManager.TrackSignUpEvent(EMAIL_PASSWORD);
            createAccount();
        }
        else if (id == R.id.txt_link_already_have_an_account) {
            startLoginActivity();
        }
    }

    private void setContentViewFields()
    {
        m_LoadingBar = new ProgressDialog(this);
        m_EmailSignUpField = findViewById(R.id.edit_email_signUp);
        m_PasswordSignUpField = findViewById(R.id.edit_password_signUp);
        findViewById(R.id.txt_link_already_have_an_account).setOnClickListener(this);
        findViewById(R.id.btn_signUp).setOnClickListener(this);
    }

    private void setFirebaseData()
    {
        m_Auth = FirebaseAuth.getInstance();
        m_Database = FirebaseDatabase.getInstance();
    }

    private void createAccount()
    {
        String[] emailParams = new String[MAX_PARAMS];
        String[] passwordParams = new String[MAX_PARAMS];
        emailParams[INPUT_PARAM] = m_EmailSignUpField.getText().toString();
        passwordParams[INPUT_PARAM] = m_PasswordSignUpField.getText().toString();

        if (!InputValidation.IsValidEmail(emailParams))
        {
            m_EmailSignUpField.setError(emailParams[ERROR_PARAM]);
            Toast.makeText(this, emailParams[TOAST_PARAM], Toast.LENGTH_SHORT).show();

            return;
        }
        if (!InputValidation.IsValidPassword(passwordParams)) {
            m_PasswordSignUpField.setError(passwordParams[ERROR_PARAM]);
            Toast.makeText(this, passwordParams[TOAST_PARAM], Toast.LENGTH_SHORT).show();

            return;
        }

        m_LoadingBar.setTitle("Creating new account...");
        m_LoadingBar.setMessage("Wait please...");
        m_LoadingBar.setCanceledOnTouchOutside(true);
        m_LoadingBar.show();

        createUserWithEmailAndPassword();
    }

    private void createUserWithEmailAndPassword()
    {
        final String email = m_EmailSignUpField.getText().toString();
        String password = m_PasswordSignUpField.getText().toString();

        m_Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            signUpWithEmailAndPassword();
                            m_AnalyticsManager.SetUserID(Objects.requireNonNull(m_Auth.getCurrentUser()).getUid(), true);
                            m_AnalyticsManager.SetUserProperty(
                                    "Email_Password_Users", m_Auth.getCurrentUser().getEmail());
                            CommonUser.setContext("Email/Password_Users");
                            CommonUser.setName(email);
                            CommonUser.setIsAnonymous(false);
                            startLoginActivity();
                            Toast.makeText(ActivitySignUp.this,
                                    "Account created successfully", Toast.LENGTH_SHORT).show();
                            m_LoadingBar.dismiss();
                        } else {
                            String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                            Toast.makeText(ActivitySignUp.this, "Authentication failed:\n" + msg,
                                    Toast.LENGTH_SHORT).show();
                            m_LoadingBar.dismiss();
                        }
                    }
                });
    }

    private void signUpWithEmailAndPassword()
    {
        User user = new User();
        FirebaseUser currentUser = m_Auth.getCurrentUser();
        DatabaseReference databaseRef = m_Database.getReference("Users");

        if(currentUser != null)
        {
            user.setContext(EMAIL_AND_PASSWORD_USER);
            user.setName(currentUser.getDisplayName());
            user.setEmail(currentUser.getEmail());
            databaseRef.child(currentUser.getUid()).setValue(user);
        }
    }

    private void startLoginActivity()
    {
        Intent LoginIntent = new Intent(ActivitySignUp.this, ActivityLogin.class);
        startActivity(LoginIntent);
    }
}

