package expark.com.totenclient;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mask.MaskTextWatcher;
import mask.PlaqueMask;
import models.CPF;
import models.GlobalParameters;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends ActionBarActivity implements LoaderCallbacks<Cursor> {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ProgressDialog mLoadingDialog;
    private EditText mPasswordConfirmView;
    private EditText mPhoneEditText;
    private EditText mCpfEditText;
    private EditText mNameView;
    private EditText mPlacaEditText;
    private CheckBox mAcceptanceTerms;
    private ArrayList<String> mPlates;
    ArrayList<View> mViews;
    AlertDialog.Builder mSuccessDialog;
    AlertDialog.Builder mFailedDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        android.support.v7.app.ActionBar action=getSupportActionBar();
        action.setDisplayHomeAsUpEnabled(true);
        action.setTitle("Cadastrar ");
        mPlates = new ArrayList<>();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mNameView = (EditText) findViewById(R.id.name);
        mCpfEditText = (EditText) findViewById(R.id.cpf_input);
        mPhoneEditText = (EditText) findViewById(R.id.phone);
        mPlacaEditText = (EditText)findViewById(R.id.plate_to_add);
        mAcceptanceTerms = (CheckBox) findViewById(R.id.acceptance_terms);


        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirm);

        mViews = new ArrayList<View>();

        mViews.add(mEmailView);
        mViews.add(mNameView);
        mViews.add(mCpfEditText);
        mViews.add(mPhoneEditText);
        mViews.add(mPasswordView);
        mViews.add(mPasswordConfirmView);





        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mPlacaEditText.addTextChangedListener(PlaqueMask.insert(mPlacaEditText));

        setAlertDialogs();
    }

    public void setAlertDialogs(){
        final Context ctx = this.getApplicationContext();
        /** Criando Dialog para sucesso**/
        mSuccessDialog = new AlertDialog.Builder(this);
        mSuccessDialog.setTitle(R.string.register_success_title)
                .setMessage(R.string.register_success_message)
                .setPositiveButton(R.string.success_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(ctx, MainActivity.class);
                        startActivity(intent);
                    }
                });

        mSuccessDialog.create();

        /** Criando Dialog para sucesso**/
        mFailedDialog = new AlertDialog.Builder(this);
        mFailedDialog.setTitle("Erro")
                .setPositiveButton(R.string.success_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        mFailedDialog.create();
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

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {


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

        if (!mPasswordConfirmView.getText().toString().equals(mPasswordView.getText().toString())){
            mPasswordConfirmView.setError(getString(R.string.cant_be_different));
            focusView = mPasswordConfirmView;
            cancel = true;
        }else{
            mPasswordConfirmView.setError(null);
        }

        if (!CPF.validaCPF(mCpfEditText.getText().toString().replaceAll("\\D+",""))){
            mCpfEditText.setError(getString(R.string.invalid_cpf));
            focusView = mCpfEditText;
            cancel = true;
        }else{
            mCpfEditText.setError(null);
        }

        if(!mAcceptanceTerms.isChecked()){
            mAcceptanceTerms.setError(getString(R.string.not_accepted));
            focusView = mAcceptanceTerms;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            registerRequest();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public void checkTerms(View view){
        final CheckBox checkbox = (CheckBox)view;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.terms_title)
                .setMessage(R.string.use_terms)
                .setPositiveButton(R.string.accept_terms, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkbox.setChecked(true);
                    }
                })
                .setNegativeButton(R.string.not_acept, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkbox.setChecked(false);
                    }
                });
        builder.create();

        builder.show();


    }

    public void explainAutoDebit(View view){
        final CheckBox checkbox = (CheckBox)view;


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.auto_debit_title)
                .setMessage(R.string.auto_debit_explain)
                .setPositiveButton(R.string.accept_auto_debit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkbox.setChecked(true);
                    }
                })
                .setNegativeButton(R.string.not_acept_auto_debit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        checkbox.setChecked(false);
                    }
                });
        builder.create();

        builder.show();
    }


    public void registerRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        final Context ctx = this.getApplicationContext();

        JSONObject client = new JSONObject();
        JSONObject userAttributes = new JSONObject();
        JSONObject params = new JSONObject();
        JSONArray platesAttributes = new JSONArray();

        for (String plate:mPlates){
            JSONObject plateObject = new JSONObject();
            try {
                plateObject.put("plate", plate);
                platesAttributes.put(plateObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        try {
            userAttributes.put("login", mPhoneEditText.getText().toString().replaceAll("\\D+", ""));
            userAttributes.put("cpf", mCpfEditText.getText().toString().replaceAll("\\D+",""));
            userAttributes.put("email", mEmailView.getText().toString());
            userAttributes.put("password", mPasswordView.getText().toString());
            userAttributes.put("password_confirmation", mPasswordConfirmView.getText().toString());
            userAttributes.put("acceptance_terms", mAcceptanceTerms.isChecked());

            client.put("name", mNameView.getText().toString());

            client.put("user_attributes", userAttributes);
            if (platesAttributes.length() > 0) {
                client.put("plates_attributes", platesAttributes);
            }
            params.put("client", client);
        } catch (JSONException e) {
            e.printStackTrace();
        }




        mLoadingDialog = ProgressDialog.show(this, "",
                "Processando...", true);

        String url = GlobalParameters.getInstance().defaultUrl+"/usuarios.json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                mLoadingDialog.dismiss();
                try {
                    Log.d("Resposta", response.toString());
                    if (Boolean.valueOf(response.getBoolean("success"))){
                        mSuccessDialog.show();
                    }else{

                        String stringErrors = response.get("errors").toString();

                        mFailedDialog.setMessage(stringErrors).show();
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
    }


    public void addPlate(View view){
        String plate = mPlacaEditText.getText().toString();
        boolean add = false;
        if (mPlates.size() < 1){
            add = true;
        }else{
            for (String iplate:mPlates){
                if (plate.equals(iplate)){
                    add = false;
                }else{
                    add = true;
                }
            }
        }

        if (add){
            mPlates.add(plate);
            mPlacaEditText.setText("");
            reloadPlatesList();
        }else{
            mFailedDialog.setMessage("Essa placa já foi adcionada").show();
        }



    }

    private void reloadPlatesList(){
        ListView platesView = (ListView)findViewById(android.R.id.list);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mPlates);
        platesView.setAdapter(adapter);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) platesView.getLayoutParams();
        params.height = mPlates.size() * 50;
        platesView.setLayoutParams(params);


    }


}



