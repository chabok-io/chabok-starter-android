package com.adpdigital.chabok.starter.common;

import android.util.Log;

import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;

public class ChabokHelper {
    private static final String TAG = ChabokHelper.class.getSimpleName();

    private ChabokHelper() {
        // no implementation
    }

    public static void subscribe(final String channel) {
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

    public static void unsubscribe(final String channel) {
        AdpPushClient.get().unsubscribe(channel, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "successfully unsubscribed from channel [" + channel + "]");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "failed to unsubscribe from channel [" + channel + "]");
            }
        });
    }

    public static void publish(String userId, String channel, String messageBody) {
        AdpPushClient.get().publish(userId, channel, messageBody, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Message was successfully sent");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Fail to send message");
            }
        });
    }

    public static void addTag(String tagName) {
        AdpPushClient.get().addTag(tagName, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Tag added to userId devices");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Fail adding tag to userId devices");
            }
        });
    }

    public static void removeTag(String tagName) {
        AdpPushClient.get().removeTag(tagName, new Callback() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "Tag removed to userId devices");
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d(TAG, "Fail adding tag to userId devices");
            }
        });
    }
}
