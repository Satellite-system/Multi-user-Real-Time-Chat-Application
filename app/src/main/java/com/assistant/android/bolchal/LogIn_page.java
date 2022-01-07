package com.assistant.android.bolchal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;

public class LogIn_page extends Activity { //Before it was AppCompActivity

    private CountryCodePicker codePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_log_in_page);

        if(getActionBar()!=null) getActionBar().hide();

        EditText phoneNum = findViewById(R.id.phoneTxtView);
        codePicker = findViewById(R.id.countryCodeHolder);
        Button sendSms = findViewById(R.id.send_code);

        codePicker.registerCarrierNumberEditText(phoneNum);

        sendSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn_page.this,Otp_verification.class);
                intent.putExtra("phoneNum",codePicker.getFullNumberWithPlus().replace(" ",""));
                startActivity(intent);
                finish();
            }
        });

    }

}