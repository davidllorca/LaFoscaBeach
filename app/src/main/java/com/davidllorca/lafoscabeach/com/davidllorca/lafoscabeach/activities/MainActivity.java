package com.davidllorca.lafoscabeach.com.davidllorca.lafoscabeach.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;

import com.davidllorca.lafoscabeach.R;
import com.davidllorca.lafoscabeach.model.Kid;
import com.davidllorca.lafoscabeach.model.ListKidsAdapater;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Main UI.
 * <p/>
 * Connect with server and get info from Fosca beach and show beach features and list of lost kids.
 * <p/>
 * Created by David Llorca <davidllorcabaron@gmail.com> on 28/8/15.
 */
public class MainActivity extends BaseActivity {

    /* Views */
    private Spinner flagSpinner;
    private Switch beachSwitch;
    private EditText searchEt;
    private ListView listview;
    private LinearLayout beachDataLayout;

    /* Variables */
    private String token;
    //Flag statements
    private String[] flagsStates;
    private List<Kid> kidsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComponents();

        // Check beach's state
        checkBeachState();

    }

    /**
     * Check beach's state and get data from http request.
     *
     * @return
     */
    private void checkBeachState() {

        if (isOnline()) {
            String result = null;
            try {
                result = new GetStateTask().execute(
                        // Get authorization token from Bundle
                        getIntent().getExtras().getString("token")).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // Process result
            if (result != null) {
                processBeachData(result);
            } else {
                showToast(getString(R.string.error_result));
            }
        } else {
            showToast(getString(R.string.no_connection));
        }

        }

        /**
         * Get parameters of beach amb process data.
         * <p/>
         * In case of beach is open put content into layout, hide layout otherwise.
         *
         * @param result JSON with beach parameters
         */

    private void processBeachData(String result) {
        try {
            // Convert response in JSON
            JSONObject beachData = new JSONObject(result);
            // Extract "state" value
            String beachState = beachData.getString("state");

            // If beach is open it process a JSON data
            if (beachState.equals("open")) {
                // Put switch in open position
                beachSwitch.setChecked(true);

                // Set flag in spinner
                String flag = beachData.getString("flag");
                Log.d("flag:", flag);
                setFlagInSpinner(flag);

                // Get list of kids
                JSONArray lostKidsJsonArray = beachData.getJSONArray("kids");
                // Prepare List where Kid's objects will added
                kidsList = new ArrayList<Kid>();
                // Iterate array and add kids to List
                for (int i = 0; i < lostKidsJsonArray.length(); i++) {
                    JSONObject kidJsonObject = lostKidsJsonArray.getJSONObject(i);
                    // Create new Kid instance
                    Kid kid = new Kid();
                    kid.setName(kidJsonObject.getString("name"));
                    kid.setAge(Integer.parseInt(kidJsonObject.getString("age")));
                    // Add to List of lost kids
                    kidsList.add(kid);
                }

                // Set data on ListView
                listview.setAdapter(new ListKidsAdapater(this, kidsList));
            } else {
                // Set invisible all view components
                beachDataLayout.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connect with server and set beach's state="open". If operation is successful
     * it get new data of beach with a new call to method checkBeachState().
     */
    private void openBeach() {
        if(isOnline()) {
            int resultChangeBeachState = -1;
            try {
                resultChangeBeachState = new ChangeStateTask().execute(OPEN).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if (resultChangeBeachState != -1) {
                checkBeachState();
                // Set visible all view components
                beachDataLayout.setVisibility(View.VISIBLE);
            } else {
                // Show error message
                showToast(getString(R.string.error_result));
            }
        }else{
            showToast(getString(R.string.no_connection));
        }
    }

    /**
     * Connect with server and set beach's state="close". If operation is successful
     * it get new data of beach with a new call to method checkBeachState().
     */
    private void closeBeach() {
        int resultChangeBeachState = -1;
        try {
            resultChangeBeachState = new ChangeStateTask().execute(CLOSE).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (resultChangeBeachState != -1) {
            // Set invisible all view components
            beachDataLayout.setVisibility(View.INVISIBLE);
        } else {
            // Show error message
            showToast(getString(R.string.error_result));
        }
    }

    /**
     * Select beach's flag item in spinner.
     * Position 0 -> green
     * Position 1 -> yellow
     * Position 2 -> red
     *
     * @param flag
     */
    private void setFlagInSpinner(String flag) {
        flagSpinner.setSelection(Integer.parseInt(flag));
    }

    /**
     * Get views references and add listeners
     */
    private void initComponents() {
        //Inflate layout
        setContentView(R.layout.activity_main);

        // Views references
        flagSpinner = (Spinner) findViewById(R.id.flag_spin);
        beachSwitch = (Switch) findViewById(R.id.beach_switch);
        beachDataLayout = (LinearLayout) findViewById(R.id.beach_data_layout);
        searchEt = (EditText) findViewById(R.id.search_ed);
        listview = (ListView) findViewById(R.id.kids_lv);

        // Add items to flagSpinner
        flagsStates = getResources().getStringArray(R.array.flags);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, flagsStates);
        flagSpinner.setAdapter(adapter);

        // Add Listeners
        beachSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    openBeach();
                } else {
                    closeBeach();
                }
            }
        });
        flagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // First it change flag in server
                int resultChangeFlag = -1;
                try {
                    resultChangeFlag = new ChangeStateTask().execute(FLAG).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                // If operation has been successful it change flag in spinner
                if (resultChangeFlag != -1) {
                    flagSpinner.setSelection(position);
                } else {
                    // Show error message
                    showToast(getString(R.string.error_result));
                    onNothingSelected(parent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Listview filter by name
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ListKidsAdapater adapter = (ListKidsAdapater) listview.getAdapter();
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /*
        ASYNCTASKS
     */

    /**
     * Call server to get information of the beach.
     * <p/>
     * param[0] must be authorization token
     */
    private class GetStateTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // Set url
            HttpGet httpGet = new HttpGet(BASE_URL + STATE);

            //Encoding GET header
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-Type", "application/json");
            httpGet.addHeader("Authorization", "Token token=\"" + params[0] + "\"");

            // Request
            try {
                HttpResponse response = httpClient.execute(httpGet);
                // write response to log
                Log.d("Http GetState Response:", response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase() + "");
                return handleResult(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private String handleResult(HttpResponse response) {
            // Check HTTP code of response
            switch (response.getStatusLine().getStatusCode()) {
                case 200: // In case successful access
                    // Get JSON data of body
                    String result = null;
                    try {
                        result = EntityUtils.toString(response.getEntity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("Result", result);
                    return result;
                default:
                    break;
            }
            return null;
        }
    }

    /**
     * Call server with PUT method.
     * param[0] = CLOSE -> Close beach.
     * param[0] = OPEN -> Open beach.
     * param[0] = FLAG-> Change flag.
     * <p/>
     * If connection and action are successful return 0, -1 otherwise.
     */
    private class ChangeStateTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            // Set url
            HttpPut httpPut = new HttpPut(BASE_URL + params[0]);

            //Encoding PUT header
            httpPut.setHeader("Accept", "application/json");
            httpPut.setHeader("Content-Type", "application/json");
            httpPut.addHeader("Authorization", "Token token=\"" +
                    getIntent().getExtras().getString("token") + "\"");

            // In case of change flag action it add flag in request body
            if (params[0].equals(FLAG)) {
                // Create JSON object
                JSONObject flagJsonObject = new JSONObject();
                try {
                    flagJsonObject.put("flag", flagSpinner.getSelectedItemPosition());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Encoding data
                try {
                    StringEntity se = new StringEntity(flagJsonObject.toString());
                    se.setContentType("application/json");
                    httpPut.setEntity(se);
                    Log.d("JSON", flagJsonObject.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            // Request
            try {
                HttpResponse response = httpClient.execute(httpPut);
                // write response to log
                Log.d("Http PutState Response:", response.getStatusLine().getStatusCode() + response.getStatusLine().getReasonPhrase() + "");
                return handleResult(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private int handleResult(HttpResponse response) {
            // Check HTTP code of response
            int httpCode = response.getStatusLine().getStatusCode();
            //
            if (httpCode == OK || httpCode == NO_CONTENT) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
