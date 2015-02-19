package expark.com.totenclient;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import mask.MaskTextWatcher;
import models.GlobalParameters;


public class BuyTicket extends ActionBarActivity {
    EditText mPlacaEditText;
    EditText mSpaceEditText;
    EditText mTimeEditText;
    ProgressDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        android.support.v7.app.ActionBar action=getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setTitle("Comprar Ticket");
        mPlacaEditText = (EditText)findViewById(R.id.plate_input);
        final MaskTextWatcher plateWatcher = new MaskTextWatcher("UUU-AAAA");
        mPlacaEditText.addTextChangedListener(plateWatcher);


        mSpaceEditText = (EditText)findViewById(R.id.space_input);

        mTimeEditText = (EditText)findViewById(R.id.time_input);



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
        final Context ctx = this.getApplicationContext();
        if(mTimeEditText.getText().toString().length() > 0){
            int minutes = Integer.parseInt(mTimeEditText.getText().toString());
            if ( minutes < 30 || minutes > 120){
                mTimeEditText.setError("O tempo precisa estar entre 30 e 120 minutos");
                return false;
            }
        }
        else{
            mTimeEditText.setError("O tempo precisa estar entre 30 e 120 minutos");
            return false;
        }


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
        String time = mTimeEditText.getText().toString();


        try {
            sale.put("space_number", spaceNumber);
            sale.put("plate", plate);
            params.put("time", time);
            params.put("sale", sale);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = GlobalParameters.getInstance().defaultUrl+"/pdv_sales/b39cf2a7-b1a0-4e60-99d3-06d9bf1562aa.json";

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
