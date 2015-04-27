package expark.com.totenclient;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import org.json.JSONException;
import org.json.JSONObject;

import mask.MoneyTextWatcher;
import models.GlobalParameters;


public class QrcodeCardActivity extends ActionBarActivity implements QRCodeReaderView.OnQRCodeReadListener {
    private QRCodeReaderView mydecoderview;
    private EditText mCardNumber;
    EditText mMoneyTextView;
    ProgressDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_card);

        mydecoderview = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        mydecoderview.setOnQRCodeReadListener(this);

        mCardNumber = (EditText)findViewById(R.id.cardNumber);

        mMoneyTextView = (EditText)findViewById(R.id.money_input);
        mMoneyTextView.addTextChangedListener(new MoneyTextWatcher(mMoneyTextView));

        android.support.v7.app.ActionBar action=getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setTitle(R.string.back_title);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_qrcode_card, menu);
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

    public void scanQrcode(View view){

    }

    @Override
    public void onQRCodeRead(String text, PointF[] pointFs) {
        mCardNumber.setText(text);
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 300); // 200 is duration in ms
        Toast.makeText(this, R.string.qrcode_inserted, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void cameraNotFound() {

    }

    @Override
    public void QRCodeNotFoundOnCamImage() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mydecoderview.getCameraManager().startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mydecoderview.getCameraManager().stopPreview();
    }

    public Boolean buyRequisition(View view){
        final Context ctx = this.getApplicationContext();
//        if (!checkInputs())
//            return false;

        /** Criando Dialog para sucesso**/
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
                .setMessage("Verifique se o número do cartão está correto")
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

        try {
            sale.put("sale_type", 3);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String pin = mCardNumber.getText().toString();
        //Double value = Double.parseDouble(mMoneyTextView.getText().toString());
        String amount = mMoneyTextView.getText().toString();

        try {
            sale.put("amount", amount);
            params.put("pin", pin);
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
                error.printStackTrace();

            }
        });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);

        return true;
    }
}
