package com.davidllorca.lafoscabeach.com.davidllorca.lafoscabeach.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Parent Activity of app's Activities.
 * <p/>
 * Created by David Llorca <davidllorcabaron@gmail.com> on 28/8/15.
 */
public class BaseActivity extends Activity {

    /* Constants HTTP requests*/
    protected static final String BASE_URL = "http://lafosca-beach.herokuapp.com/api/v1";
    protected static final String CREATE_USER = "/users";
    protected static final String LOGIN = "/user";
    protected static final String STATE = "/state";
    protected static final String OPEN = "/open";
    protected static final String CLOSE = "/close";
    protected static final String FLAG = "/flag";

    /* Response Codes */
    protected static final int OK = 200;
    protected static final int NO_CONTENT = 204;

    /**
     * Check if there are network connection.
     *
     * @return true if there are connection, false otherwise.
     */
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Show toast message.
     *
     * @param text The text to show.
     */
    protected void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
