package com.adpdigital.chabok.starter.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.common.Constants;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView lblDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText userId = (EditText) findViewById(R.id.user_id);
        Button registerBtn = (Button) findViewById(R.id.register_btn);

        lblDescription = (TextView) findViewById(R.id.textView);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId.getText() == null || userId.getText().toString().trim().equals("")) {
                    userId.requestFocus();
                    userId.setError(getString(R.string.invalid_user_id));
                } else {
                    AdpPushClient.get().login(userId.getText().toString());
                    RegisterActivity.subscribe(Constants.CHANNEL_NAME);
                    RegisterActivity.subscribe(Constants.PRIVATE_CHANNEL_NAME);

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Intent intent = getIntent();
        AdpPushClient.get().appWillOpenUrl(intent.getData());
        lblDescription.setText("Welcome to the Chabok starter project, App opened by deep-link = " + intent.getData().toString());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();
        AdpPushClient.get().appWillOpenUrl(data);
        lblDescription.setText("Welcome to the Chabok starter project. App opened by deep-link = " + data.toString());
    }

    private static void subscribe(final String channel) {
        AdpPushClient.get().subscribe(channel, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "successfully subscribed on channel [" + channel + "]");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "failed to subscribe on channel [" + channel + "]");
            }
        });
    }
}
