package com.oet.quiz.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.oet.quiz.R;
import com.oet.quiz.WebServices.Constant;

import java.util.concurrent.TimeUnit;

public class ActivityOTPVerify extends AppCompatActivity {

    private static final String TAG = "ActivityOTPVerify";
    private final Constant mConstant = new Constant();
    private Context mContext;
    private EditText mEditText;
    private Intent mIntent;
    private MaterialButton btnOTPVerify;
    private TextView OTPResend, Title;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String MobileNumber, Number, Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verify);
        mContext = ActivityOTPVerify.this;
        mIntent = getIntent();
        Activity = mIntent.getStringExtra("Activity");
        Number = mIntent.getStringExtra("MobileNumber");
        FindViewByID();
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(mContext, "Invalid Request " + e.toString(), Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(mContext, "The SMS quota for the project has been exceeded " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        Body();
    }

    private void FindViewByID() {
        Title = findViewById(R.id.Title);
        mEditText = findViewById(R.id.EtOTP);
        btnOTPVerify = findViewById(R.id.btnOTPVerify);
        OTPResend = findViewById(R.id.OTPResnd);
    }

    private void Body() {

        String CountryCode = "+91";
        MobileNumber = CountryCode + Number;
        Title.setText("We have sent verification code on : " + MobileNumber);
        Timer();
        startPhoneNumberVerification(MobileNumber);
        btnOTPVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConstant.isNetworkAvailable(mContext)) {
                    String code = mEditText.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        mEditText.setError("Cannot be empty.");
                    } else {
                        verifyPhoneNumberWithCode(mVerificationId, mEditText.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(mContext, "No Internet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Timer() {
        new CountDownTimer(20000, 1000) {
            public void onTick(long millisUntilFinished) {
                OTPResend.setText("Resend OTP in : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                OTPResend.setText("Resend!");
                OTPResend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendVerificationCode(MobileNumber, mResendToken);
                    }
                });
            }

        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");
                    if (Activity.equals("ActivityForgotPassword")) {
                        mIntent = new Intent(mContext, ActivityPasswordSettingUpFirstTime.class);
                        mIntent.putExtra("Activity", Activity);
                        mIntent.putExtra("MobileNumber", Number);
                    } else {
                        mIntent = new Intent(mContext, ActivitySelectStreamCourse.class);
                    }
                    startActivity(mIntent);
                    FirebaseUser user = task.getResult().getUser(); //Getting Firebase Data
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        mEditText.setError("Invalid code.");
                    }
                }
            }
        });
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // Activity (for callback binding)
                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d("currentUser:", "" + currentUser);
    }
}