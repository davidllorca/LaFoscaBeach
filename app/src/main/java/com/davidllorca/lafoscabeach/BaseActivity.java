package com.davidllorca.lafoscabeach;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by David Llorca <davidllorcabaron@gmail.com> on 8/27/15.
 */
public class BaseActivity extends Activity {

    /* Constants HTTP requests*/
    public static final String BASE_URL = "http://lafosca-beach.herokuapp.com/api/v1";
    public static final String CREATE_USER = "/users";
    public static final String LOGIN = "/user";
    public static final String STATE = "/state";
    public static final String OPEN = "/open";
    public static final String CLOSE = "/close";
    public static final String FLAG = "/flag";

    /* Response Codes */
    public static final int OK = 200;
    public static final int UNAUTHORIZED = 401;
    public static final int NO_CONTENT = 204;

    /**
     * Show toast message.
     *
     * @param text The text to show.
     */
    public void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
