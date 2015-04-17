package expark.com.totenclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import mask.PlaqueMask;
import models.GlobalParameters;


public class BuyTicketActivity extends ActionBarActivity {
    EditText mPlacaEditText;
    EditText mSpaceEditText;
    RadioButton mCarRadio;
    RadioButton mMotorcicleRadio;
    RadioButton mVehicleChecked;
    RadioButton mVehicleTypes[];

    RadioButton mThirty;
    RadioButton mSixty;
    RadioButton mNinety;
    RadioButton mOneHundredAndTwenty;
    RadioButton mTimeSelectChecked;

    RadioButton mTimeSelectRadios[];
    ProgressDialog mLoadingDialog;

    ArrayList<View> mViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        android.support.v7.app.ActionBar action=getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setTitle("Comprar Ticket");
        mPlacaEditText = (EditText)findViewById(R.id.plate_input);

        mPlacaEditText.addTextChangedListener(PlaqueMask.insert(mPlacaEditText));


        mSpaceEditText = (EditText)findViewById(R.id.space_input);


        mCarRadio = (RadioButton)findViewById(R.id.radio_car);
        mMotorcicleRadio = (RadioButton)findViewById(R.id.motorcicle_radio);

        mVehicleTypes = new RadioButton[2];
        mVehicleTypes[0] = mCarRadio;
        mVehicleTypes[1] = mMotorcicleRadio;
        mVehicleChecked = mCarRadio;

        mThirty = (RadioButton)findViewById(R.id.thirty);
        mSixty = (RadioButton)findViewById(R.id.sixty);
        mNinety = (RadioButton)findViewById(R.id.ninety);
        mOneHundredAndTwenty = (RadioButton)findViewById(R.id.one_hundred_and_twenty);

        mTimeSelectRadios = new RadioButton[4];
        mTimeSelectRadios[0] = mThirty;
        mTimeSelectRadios[1] = mSixty;
        mTimeSelectRadios[2] = mNinety;
        mTimeSelectRadios[3] = mOneHundredAndTwenty;

        mTimeSelectChecked = mThirty;

        mViews = new ArrayList<View>();

        mViews.add(mSpaceEditText);
        mViews.add(mPlacaEditText);



    }

    public void onVehicleRadioClick(View view) {
        for (int i=0; i< mVehicleTypes.length; i++){
            RadioButton current = (mVehicleTypes[i]);
            current.setChecked(false);
        }
        mVehicleChecked = (RadioButton)view;
        mVehicleChecked.setChecked(true);

    }

    public void onTimeChooseRadioClick(View view) {
        for (int i=0; i< mTimeSelectRadios.length; i++){
            RadioButton current = (mTimeSelectRadios[i]);
            current.setChecked(false);
        }
        mTimeSelectChecked = (RadioButton)view;
        mTimeSelectChecked.setChecked(true);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_buy_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                // remove the following flag for version < API 19
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public Boolean buyRequisition(View view){
        boolean cancel = false;
        View focusView = null;

        for (int i=0; i < mViews.size(); i++){
            EditText lview = (EditText)mViews.get(i);
            if (TextUtils.isEmpty(lview.getText().toString())){
                lview.setError(getString(R.string.cant_be_empty));
                focusView = lview;
                cancel = true;
            }else{
                lview.setError(null);
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return true;
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            final Context ctx = this.getApplicationContext();



            final AlertDialog.Builder successDialog = new AlertDialog.Builder(this);
            successDialog.setTitle(R.string.buy_success_title)
                    .setMessage(R.string.buy_success_message)
                    .setPositiveButton(R.string.success_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(ctx, MainActivity.class);
                            startActivity(intent);
                        }
                    });

            successDialog.create();

            /** Criando Dialog para sucesso**/
            final AlertDialog.Builder failedDialog = new AlertDialog.Builder(this);
            failedDialog.setTitle("Erro")
                    .setMessage("Erro")
                    .setPositiveButton(R.string.success_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            failedDialog.create();


            RequestQueue queue = Volley.newRequestQueue(this);

            mLoadingDialog = ProgressDialog.show(this, "",
                    "Processando...", true);

            JSONObject params = new JSONObject();
            JSONObject sale = new JSONObject();

            String plate = mPlacaEditText.getText().toString();
            String spaceNumber = mSpaceEditText.getText().toString();
            Integer time = 0;
            Integer vehicleType = 1;

            switch(mVehicleChecked.getId()) {
                case R.id.radio_car:
                    vehicleType = 1;
                    break;
                case R.id.motorcicle_radio:
                    vehicleType = 2;
                    break;
            }

            switch(mTimeSelectChecked.getId()) {
                case R.id.thirty:
                    time = 30;
                    break;
                case R.id.sixty:
                    time = 60;
                    break;
                case R.id.ninety:
                    time = 90;
                    break;
                case R.id.one_hundred_and_twenty:
                    time = 120;
                    break;
            }


            try {
                sale.put("space_number", spaceNumber);
                sale.put("plate", plate);
                sale.put("vehicle_type", vehicleType);
                params.put("time", time);
                params.put("sale", sale);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            String pdv_uuid = sharedPref.getString(getString(R.string.pref_pdv_uuid),
                    getString(R.string.pref_pdv_uuid_default));


            String url = GlobalParameters.getInstance().defaultUrl+"/pdv_sales/"+pdv_uuid+".json";

            JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    mLoadingDialog.dismiss();
                    try {
                        Log.d("Resposta", response.toString());
                        if (Boolean.valueOf(response.getBoolean("success"))){
                            successDialog.show();
                        }else{
                            failedDialog.show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    mLoadingDialog.dismiss();

                }
            });

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(jsObjRequest);

            return true;
        }


    }

}
