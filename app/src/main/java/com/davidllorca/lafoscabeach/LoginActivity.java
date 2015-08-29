package com.davidllorca.lafoscabeach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /* Views */
    private EditText usernameEt;
    private EditText passwordEt;
    private Button loginBtn;
    private Button registerBtn;

    /* Variables */
    private static String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();
    }

    @Override
    public void onClick(View v) {
        if (validateFields()) {
            switch (v.getId()) {
                case R.id.login_btn:
                    login();
                    break;
                case R.id.register_btn:
                    register();
                    break;
                default:
                    break;
            }
        } else {
            // Show info message
            showToast(this.getString(R.string.empty_fields));
        }
    }

    private void login() {
        // do login
        new LoginTask().execute();
    }

    private void register() {
        // do register
        new RegisterTask().execute();
    }

    /**
     * Check if fields are empty or not.
     *
     * @return true if there are data in both fields, false otherwise.
     */
    private boolean validateFields() {
        String[] credentials = getUserCredentials();
        // If some EditText is empty...
        return !credentials[0].equals("") && !credentials[1].equals("");
    }

    /**
     * Get credentials of EditTexts.
     *
     * @return String[] where param[0] is username and param[1] is password.
     */
    private String[] getUserCredentials() {
        // Get text of fields
        String username = usernameEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();

        return new String[]{username, password};
    }

    /**
     * Launch MainActivity in a new stack.
     *
     * @param token
     */
    private void launchMainActivity(String token) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("token", token);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Get views references.
     */
    private void initComponents() {
        //Inflate layout
        setContentView(R.layout.activity_login);
        // Views references
        usernameEt = (EditText) findViewById(R.id.username_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        loginBtn = (Button) findViewById(R.id.login_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);
        // Set listeners
        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    /**
     * AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
     */
    public class RegisterTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // Set url
            HttpPost httpPost = new HttpPost(BASE_URL + CREATE_USER);

            // Create JSON object
            JSONObject jsonObject = new JSONObject();
            try {
                JSONObject userData = new JSONObject();
                userData.put("username", getUserCredentials()[0]);
                userData.put("password", getUserCredentials()[1]);

                JSONArray data = new JSONArray();
                data.put(userData);

                jsonObject.put("user", userData);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //Encoding POST data
            try {
                httpPost.setHeader("Accept", "application/json");
                StringEntity se = new StringEntity(jsonObject.toString());
                se.setContentType("application/json");
                httpPost.setEntity(se);
                Log.d("JSON", jsonObject.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            // Request
            try {
                HttpResponse response = httpClient.execute(httpPost);
                // write response to log
                Log.d("Http Post Response:", response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase() + "");
                handleResult(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void handleResult(HttpResponse response) {
            switch (response.getStatusLine().getStatusCode()) {
                case 201: // If register has been successful...
                    login();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     *
     */
    private class LoginTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // Set url
            HttpGet httpGet = new HttpGet(BASE_URL + LOGIN);

            //Encoding GET data
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(getUserCredentials()[0], getUserCredentials()[1]), "UTF-8", false));

            // Request
            try {
                HttpResponse response = httpClient.execute(httpGet);
                // write response to log
                Log.d("Http Get Response:", response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase() + "");
                handleResult(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Void handleResult(HttpResponse response) {
            switch (response.getStatusLine().getStatusCode()) {
                case OK:
                    try {
                        String result = EntityUtils.toString(response.getEntity());
                        JSONObject jsonObject = new JSONObject(result);
                        Log.d("token: ", jsonObject.getString("authentication_token"));
                        launchMainActivity(jsonObject.getString("authentication_token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    showToast(response.getStatusLine().getStatusCode() + ": " + response.getStatusLine().getReasonPhrase());
                    break;
            }
            return null;
        }

        /**
         * Muestra un mensaje Toast en la UI que ha llamado al Asynctask
         *
         * @param infoMessage
         */
        private void showToast(final String infoMessage) {
            Handler handler = new Handler(getApplicationContext().getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(), infoMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
