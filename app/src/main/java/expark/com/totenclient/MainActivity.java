package expark.com.totenclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }




    public void launchSignIn(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void launchTicketBuy(View view){
        Intent intent = new Intent(this, BuyTicket.class);
        startActivity(intent);
    }

    public void launchCreditBuy(View view){
        Intent intent = new Intent(this, BuyCredit.class);
        startActivity(intent);
    }
}
