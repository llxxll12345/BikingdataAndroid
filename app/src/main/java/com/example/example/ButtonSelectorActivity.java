package com.example.example;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ButtonSelectorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        Button switchBack = (Button)findViewById(R.id.bSwitchBack);
        switchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = "jerk";
                Intent data = new Intent();
                data.putExtra("data", content);
                setResult(2, data);
                finish();
            }
        });
        assert switchBack != null;

        final Button okButton = (Button)findViewById(R.id.okButton);

        // Show ok button state info.
        final TextView okButtonState = (TextView)findViewById(R.id.okButtonState);

        okButton.setEnabled(false);

        // Enable OK Button
        Button enableOkButton = (Button)findViewById(R.id.enableOkButton);
        enableOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okButton.setEnabled(true);
                okButtonState.setText("Ok Button is enabled.");
            }
        });

        // Disable OK Button
        Button disableOkButton = (Button)findViewById(R.id.disableOkButton);
        disableOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                okButton.setEnabled(false);
                okButtonState.setText("Ok Button is disabled.");
            }
        });

        // Listen to touch event.
        okButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                // Get touch action.
                int action = motionEvent.getAction();

                if(action == MotionEvent.ACTION_DOWN) {
                    // If pressed
                    okButtonState.setText("Ok Button is pressed.");

                }else if(action == MotionEvent.ACTION_UP) {
                    // If released.
                    okButtonState.setText("Ok Button is released.");
                }
                return false;
            }
        });
    }
}