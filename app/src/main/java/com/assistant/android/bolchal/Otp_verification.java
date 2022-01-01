package com.assistant.android.bolchal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.assistant.android.bolchal.ui.OptEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Otp_verification extends AppCompatActivity {

    private static final String TAG = "LogIn_page";
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String SmsCode;
    private TextView timer;
    private TextView resendTxtView;
    private TextView verify;
    private TextView xxPhone_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_otp_verification);

        //Hide Top ActionBar
        Objects.requireNonNull(getSupportActionBar()).hide();

        OptEditText code = findViewById(R.id.codeTextView);
        verify = findViewById(R.id.verify);
        timer = findViewById(R.id.timer);
        resendTxtView = findViewById(R.id.resend);
        xxPhone_num = findViewById(R.id.xxx_Ph);


        mAuth = FirebaseAuth.getInstance();

        /**
         * get Phone Num as extra from Intent of Phone Number Sent
         */
        String phone_num = getIntent().getStringExtra("phoneNum");

        //Set coded phoneNUm to the screen
        String coded = "+" + phone_num.charAt(1) + phone_num.charAt(2) + "XXXXXXX" + phone_num.charAt(phone_num.length()-3) +
                       phone_num.charAt(phone_num.length()-2) + phone_num.charAt(phone_num.length()-1);
        xxPhone_num.setText(phone_num);


        /**
                 * Verification Check up
                 */
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            /**
             * ON different device Manually input code
             *
             * @param s
             * @param forceResendingToken
             */
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                SmsCode = s;
            }

            @Override //Automatic code verify in case of same number in the device
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Otp_verification.this, e + " exception", Toast.LENGTH_SHORT).show();
            }
        };


        /**
         * This Method Sends Sms for verification
         */
        sendMsg(phone_num);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (code.getText().toString().length() == 6) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(SmsCode, code.getText().toString());
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }
        });

        //SetTimer
        startTimer();

        resendTxtView.setOnClickListener(view -> {
            resendTxtView.setVisibility(View.GONE);
            timer.setVisibility(View.VISIBLE);
            sendMsg(phone_num);
            startTimer();
        });

    }

    //Send messages
    private void sendMsg(String phone_num){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone_num)       // Phone number to verify
                        .setTimeout(30L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(Otp_verification.this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //Make CountDown Timer
    private void startTimer() {
        new CountDownTimer(30000,1000){

            @Override
            public void onTick(long l) {
                long sec = l/1000;
                timer.setText("Resend OTP in: 00:"+sec);
            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);
                resendTxtView.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(Otp_verification.this,details.class);

                            FirebaseUser user = task.getResult().getUser();

                            intent.putExtra("userName",user);
                            startActivity(intent);
                            finish();
                            // Update UI
                        } else {
                            // Sign in failed, display a message and update the UI
                            Toast.makeText(Otp_verification.this, "signInWithCredential:failure" + task.getException(),Toast.LENGTH_LONG).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}