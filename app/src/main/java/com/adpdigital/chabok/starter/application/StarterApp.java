package com.adpdigital.chabok.starter.application;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adpdigital.chabok.starter.activity.MainActivity;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ChabokNotification;
import com.adpdigital.push.ChabokNotificationAction;
import com.adpdigital.push.LogLevel;
import com.adpdigital.push.NotificationHandler;
import com.adpdigital.push.OnDeeplinkResponseListener;
import com.adpdigital.push.PushMessage;
import com.adpdigital.push.config.Environment;

import org.json.JSONObject;

public class StarterApp extends Application {

    private final String TAG = this.getClass().getName();
    private AdpPushClient chabok = null;

    @Override
    public void onCreate() {
        super.onCreate();

        initPushClient();
    }

    private synchronized void initPushClient() {
        if (chabok == null) {
            
            AdpPushClient.setDefaultTracker("8iFRmA"); // Optional
            AdpPushClient.setLogLevel(LogLevel.VERBOSE); // Optional
            AdpPushClient.configureEnvironment(Environment.SANDBOX); // Mandatory

            chabok = AdpPushClient.get();
            chabok.setEnableAlertForNotSupportingGcm(false);
            chabok.addListener(this);
            chabok.addNotificationHandler(getNotificationHandler());
            chabok.setOnDeeplinkResponseListener(new OnDeeplinkResponseListener() {
                @Override
                public boolean launchReceivedDeeplink(Uri uri) {
                    return true;
                }
            });
        }
    }

    private NotificationHandler getNotificationHandler() {
        return new NotificationHandler() {

            @Override
            public Class getActivityClass(ChabokNotification chabokNotification) {
                // return preferred activity class to be opened on this message's notification
                return MainActivity.class;
            }

            @Override
            public boolean buildNotification(ChabokNotification chabokNotification,
                                             NotificationCompat.Builder builder) {
                // use builder to customize the notification object
                // return false to prevent this notification to be shown to the user, otherwise true
                getDataFromChabokNotification(chabokNotification);
                return true;
            }

            @Override
            public boolean notificationOpened(ChabokNotification message,
                                              ChabokNotificationAction notificationAction) {
                if (notificationAction.type == ChabokNotificationAction.ActionType.ActionTaken) {
                    //Click on an action.
                } else if (notificationAction.type == ChabokNotificationAction.ActionType.Opened) {
                    //Notification opened
                } else if (notificationAction.type == ChabokNotificationAction.ActionType.Dismissed) {
                    //Notification dismissed
                }

                //true to prevent launch activity that returned from getActivityClass or navigation to a url.
                return super.notificationOpened(message, notificationAction);
            }
        };
    }

    private void getDataFromChabokNotification(ChabokNotification chabokNotification) {
        if (chabokNotification != null) {
            if (chabokNotification.getExtras() != null) {
                Bundle payload = chabokNotification.getExtras();

                //FCM message data is here
                Object data = payload.get("data");
                if (data != null) {
                    Log.d(TAG, "getDataFromChabokNotification: The ChabokNotification data is : " + String.valueOf(data));
                }
            } else if (chabokNotification.getMessage() != null) {
                PushMessage payload = chabokNotification.getMessage();

                //Chabok message data is here
                JSONObject data = payload.getData();
                if (data != null) {
                    Log.d(TAG, "getDataFromChabokNotification: The ChabokNotification data is : " + data);
                }
            }
        }
    }

    public void onEvent(PushMessage message) {
        Log.d(TAG, "\n\n--------------------\n\nGOT MESSAGE " + message + "\n\n");
        JSONObject data = message.getData();
        if (data != null) {
            Log.d(TAG, "--------------------\n\n The message data is : " + data + "\n\n");
        }
    }
}
