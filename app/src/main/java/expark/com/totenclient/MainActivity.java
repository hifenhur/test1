package expark.com.totenclient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {

    private int title_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title_click = 0;

    }




    public void launchSignIn(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void launchTicketBuy(View view){
        Intent intent = new Intent(this, BuyTicketActivity.class);
        startActivity(intent);
    }

    public void launchCreditBuy(View view){
        Intent intent = new Intent(this, BuyCreditActivity.class);
        startActivity(intent);
    }

    public void launchQrcodeCard(View view){
        Intent intent = new Intent(this, QrcodeCardActivity.class);
        startActivity(intent);
    }

    public void launchSettings(View view){
        final Context context = this.getApplicationContext();

        if (title_click == 7){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();

            final View inflaterView = inflater.inflate(R.layout.dialog_settings_sign, null);
            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflaterView)
                    // Add action buttons
                    .setPositiveButton(R.string.signin, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            EditText password = (EditText)inflaterView.findViewById(R.id.password_settings);
                            Log.d("senha", password.getText().toString());
                            DateFormat dateFormat = new SimpleDateFormat("HHdd");
                            Date date = new Date();

                            if (password.getText().toString().equals("tornado"+ dateFormat.format(date))){
                                Log.d("log in", "entrou");
                                Intent intent = new Intent(context, SettingsActivity.class);
                                startActivity(intent);
                            }

                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            builder.create().show();

        }else{
            title_click += 1;
        }
        Log.d("clicks", ""+title_click);

    }
}
