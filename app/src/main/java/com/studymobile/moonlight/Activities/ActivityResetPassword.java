package com.studymobile.moonlight.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.studymobile.moonlight.R;
import com.studymobile.moonlight.Services.InputValidation;

import java.util.Objects;

public class ActivityResetPassword extends AppCompatActivity implements View.OnClickListener
{
    private static final int INPUT_PARAM = 2;
    private static final int ERROR_PARAM = 0;
    private static final int TOAST_PARAM = 1;
    private static final int MAX_PARAMS = 3;

    private EditText m_SendEmailField;
    private FirebaseAuth m_Auth;

    @Override
    protected void onCreate(Bundle i_SavedInstanceState)
    {
        super.onCreate(i_SavedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setFirebaseData();
        findViewById(R.id.btn_send_email).setOnClickListener(this);
    }

    @Override
    public void onClick(View i_View)
    {
        int id = i_View.getId();

        if (id == R.id.btn_send_email)
        {
            sendEmailToResetUserPassword();
        }
    }

    private void setFirebaseData()
    {
        m_Auth = FirebaseAuth.getInstance();
        m_SendEmailField = findViewById(R.id.edit_send_email);
    }

    private void sendEmailToResetUserPassword()
    {
        String[] emailParams = new String[MAX_PARAMS];
        emailParams[INPUT_PARAM] = m_SendEmailField.getText().toString();

        if (!InputValidation.IsValidEmail(emailParams))
        {
            m_SendEmailField.setError(emailParams[ERROR_PARAM]);
            Toast.makeText(this, emailParams[TOAST_PARAM], Toast.LENGTH_SHORT).show();

            return;
        }
        else {
            m_Auth.sendPasswordResetEmail(emailParams[INPUT_PARAM])
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ActivityResetPassword.this,
                                        "Please check your email address to reset your password",
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ActivityResetPassword.this,
                                        ActivityLogin.class));
                            }
                            else{
                                String msg = Objects.requireNonNull(task.getException()).getLocalizedMessage();
                                Toast.makeText(ActivityResetPassword.this, "ERROR:\n" + msg,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
