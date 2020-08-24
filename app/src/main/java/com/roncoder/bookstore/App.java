package com.roncoder.bookstore;

import android.app.Application;
import android.hardware.usb.UsbRequest;

import com.facebook.appevents.AppEventsLogger;
import com.roncoder.bookstore.models.User;
import com.roncoder.bookstore.utils.Utils;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppEventsLogger.activateApp(this);

        // TODO Managed this to get the current user of this application if possible.
        // TODO and set it to Utils static class using **Utils.setCurrentUser(User)**;
        Utils.setCurrentUser(new User());
    }
}
