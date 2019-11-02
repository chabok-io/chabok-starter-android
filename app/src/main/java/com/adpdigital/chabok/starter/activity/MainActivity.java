package com.adpdigital.chabok.starter.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adpdigital.chabok.starter.R;
import com.adpdigital.chabok.starter.common.ChabokHelper;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.AppState;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ChabokEvent;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AdpPushClient chabok;

    private EditText userIdTxt;
    private EditText channelTxt;
    private EditText messageBodyTxt;
    private EditText messgeUserIdTxt;
    private EditText messageChannelTxt;
    private EditText tagNameTxt;
    private TextView messageLogsTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chabok = AdpPushClient.get();
        chabok.addListener(this);

        if (chabok.hasProtectedAppSupport()) {
            chabok.showProtectedAppSettings(MainActivity.this,
                    getString(R.string.app_name),
                    null,
                    null);
        }

        this.userIdTxt = findViewById(R.id.useridTextView);
        this.channelTxt = findViewById(R.id.channelTextView);

        this.messageBodyTxt = findViewById(R.id.messageBodyEditText);
        this.messgeUserIdTxt = findViewById(R.id.messageUseridTextView);
        this.messageChannelTxt = findViewById(R.id.messageChannelTextView);

        this.tagNameTxt = findViewById(R.id.tagsNameTextView);
        this.messageLogsTxt = findViewById(R.id.messageLogsTextView);

        final String chabokUserId = AdpPushClient.get().getUserId();
        if (chabokUserId != null) {
            this.userIdTxt.setText(chabokUserId);
        }

        Intent intent = getIntent();
        AdpPushClient.get().appWillOpenUrl(intent.getData());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Uri data = intent.getData();
        AdpPushClient.get().appWillOpenUrl(data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachPushClient();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachPushClient();
    }

    private void attachPushClient() {
        if (chabok != null) {
            chabok.addListener(this);
        }
        fetchAndUpdateConnectionStatus();
    }

    private void detachPushClient() {
        if (chabok != null) {
            chabok.removePushListener(this);
        }
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(final PushMessage message) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                MainActivity.this.messageLogsTxt.setText(
                        MainActivity.this.messageLogsTxt.getText() +
                                "\n " + message + "\n\n---------------------");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
                Log.i(TAG, status.name());
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void onEvent(AppState state) {
        switch (state) {
            case REGISTERED:
                Log.d(TAG, "Registered ..........");
                break;
            case INSTALL:
                Log.d(TAG, "Install ..........");
                break;
            case LAUNCH:
                Log.d(TAG, "Launch ..........");
                break;
            default:
                Log.d(TAG, "Protected grant needed ..........");
        }
    }

    private void fetchAndUpdateConnectionStatus() {
        if (chabok == null) {
            return;
        }
        chabok.getStatus(new Callback<ConnectionStatus>() {
            @Override
            public void onSuccess(ConnectionStatus connectionStatus) {
                Log.i(TAG + "_fetch", connectionStatus.name());
                updateConnectionStatus(connectionStatus);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "errrror ");
            }
        });
    }

    private void updateConnectionStatus(ConnectionStatus status) {
        View connectionStatusView = findViewById(R.id.connectionStatusView);
        TextView connectionStatus = (TextView) findViewById(R.id.connectionStatusTextView);

        if (connectionStatus != null && status != null) {
            switch (status) {
                case CONNECTED:
                    connectionStatus.setText(getString(R.string.connected));
                    connectionStatusView.setBackgroundResource(R.drawable.green_circle);
                    break;

                case CONNECTING:
                    connectionStatus.setText(getString(R.string.connecting));
                    connectionStatusView.setBackgroundResource(R.drawable.orange_circle);
                    break;

                case DISCONNECTED:
                    connectionStatus.setText(getString(R.string.disconnected));
                    connectionStatusView.setBackgroundResource(R.drawable.red_circle);
                    break;
            }
        }
    }

    //------------ Register to chabok
    public void registerBtnOnClick(View v) {
        String userId = MainActivity.this.userIdTxt.getText().toString();
        if (!userId.trim().contentEquals("")) {
            AdpPushClient.get().login(userId);
        } else {
            Toast.makeText(getApplicationContext(), "UserId is empty. Please, enter a userId", Toast.LENGTH_SHORT).show();
        }
    }

    public void unregisterBtnOnClick(View v) {
        AdpPushClient.get().logout();
    }

    public void subscribeBtnOnClick(View v) {
        final String channel = MainActivity.this.channelTxt.getText().toString();
        if (!channel.isEmpty()) {
            ChabokHelper.subscribe(channel);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Channel is empty. Please, enter a channel name to subscribe on it",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void unsubscribeBtnOnClick(View v) {
        final String channel = MainActivity.this.channelTxt.getText().toString();
        if (!channel.isEmpty()) {
            ChabokHelper.unsubscribe(channel);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Channel is empty. Please, enter a channel name to unsubscribe to it",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------- Publish
    public void publishMessageBtnOnClick(View v) {
        String userId = MainActivity.this.messgeUserIdTxt.getText().toString();
        if (!userId.isEmpty()) {
            String channel = MainActivity.this.messageChannelTxt.getText().toString();
            String messageBody = MainActivity.this.messageBodyTxt.getText().toString();

            if (channel.isEmpty()) {
                channel = "default";
            }

            if (messageBody.isEmpty()) {
                messageBody = "Hello world :)";
            }

            ChabokHelper.publish(userId, channel, messageBody);
        }
    }

    public void publishEventBtnOnClick(View v) {
        String eventName = MainActivity.this.messageChannelTxt.getText().toString();
        String msg = MainActivity.this.messageBodyTxt.getText().toString();

        if (!eventName.isEmpty()) {
            if (msg.isEmpty()) {
                msg = "Goal for Iran :)";
            }

            JSONObject data = new JSONObject();
            try {
                data.put("msg", msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AdpPushClient.get().publishEvent(eventName, data);
        } else {
            Toast.makeText(getApplicationContext(), "Event name is empty.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- Tags
    public void addTagBtnOnClick(View v) {
        String tagName = MainActivity.this.tagNameTxt.getText().toString();
        if (!tagName.isEmpty()) {
            ChabokHelper.addTag(tagName);
        } else {
            Toast.makeText(getApplicationContext(), "Tag name is empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void removeTagBtnOnClick(View v) {
        String tagName = MainActivity.this.tagNameTxt.getText().toString();
        if (!tagName.isEmpty()) {
            ChabokHelper.removeTag(tagName);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Tag name is empty",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ------------ Track
    public void addToCartBtnOnClick(View v) {
        JSONObject data = new JSONObject();
        try {
            data.put("value", "PRODUCT_123");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AdpPushClient.get().track("AddToCard", data);
    }

    public void purchaseBtnOnClick(View v) {
        JSONObject data = new JSONObject();
        try {
            data.put("capId", 123456);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AdpPushClient.get().trackPurchase("Purchase",
                new ChabokEvent(10000, "RIAL"));
    }

    public void likeBtnOnClick(View v) {
        JSONObject data = new JSONObject();
        try {
            data.put("postId", 654321);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AdpPushClient.get().track("Like", data);
    }

    public void commentBtnOnClick(View v) {
        JSONObject data = new JSONObject();
        try {
            data.put("postId", 8654321);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AdpPushClient.get().track("Comment", data);
    }

    public void setUserAttributeButtonOnClick(View v) {
        HashMap<String, Object> attribute = new HashMap<>();
        attribute.put("firstName", "Chabok");
        attribute.put("lastName", "Platform");
        attribute.put("age", 5);
        attribute.put("gender", "Male");
        attribute.put("shoesSize", 43);

        AdpPushClient.get().setUserAttributes(attribute);
    }

    public void incrementUserAttributeButtonOnClick(View v) {
        AdpPushClient.get().incrementUserAttribute("comedy_movie", 1);
    }
}
