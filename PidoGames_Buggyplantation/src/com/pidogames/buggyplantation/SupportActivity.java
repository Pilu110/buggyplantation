package com.pidogames.buggyplantation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SupportActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.support);
        
        //Log.e("PHONY","DEVICE ID:" + ((TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()); 
        
        final Button btnStart = (Button)findViewById(R.id.btn_start);
        btnStart.setEnabled(false);
        btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SupportActivity.this,StartActivity.class);
				startActivity(intent);
			}
        });
        
        Button btnDonate = (Button)findViewById(R.id.btn_donate);
        btnDonate.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SupportActivity.this,DonatePayPalActivity.class);
				startActivity(intent);
			}
        });
        
        
        new CountDownTimer(10000, 1000) {
			@Override
			public void onFinish() {
				btnStart.setText(R.string.start_application);
				btnStart.setEnabled(true);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				btnStart.setText(millisUntilFinished/1000 + " " + getString(R.string.seconds_left));
			}
        }.start();
    }
}
