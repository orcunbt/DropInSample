package com.example.orcunbt.dropinsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.braintreepayments.api.interfaces.HttpResponseCallback;
import com.braintreepayments.api.internal.HttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

public class MainActivity extends AppCompatActivity {

    private static final int DROP_IN_REQUEST = 1;
    private PaymentMethodNonce mNonce;
    private Button mDropInButton;
    String token;
    Button simpleButton1;
    final String get_token = "http://orcodevbox.co.uk/BTOrcun/tokenGen.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simpleButton1 = (Button) findViewById(R.id.simpleButton);

        simpleButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Invoke HTTP request to get a client-token and launch Drop-In UI
                new HttpRequest().execute();
            }
        });
    }


    // Initialize DropInRequest
    protected void onAuthorizationFetched() {
        DropInRequest dropInRequest = new DropInRequest()
                .clientToken(token);
        startActivityForResult(dropInRequest.getIntent(this), DROP_IN_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == DROP_IN_REQUEST) {
                DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
                Log.d("Nonce", result.getPaymentMethodNonce().getNonce().toString());
                Toast.makeText(MainActivity.this, "Successfully generated a nonce. Send it to your server to create a transaction: \n" + result.getPaymentMethodNonce().getNonce().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Log.d("else triggered", "else is triggered");
            }
        } else if (resultCode != RESULT_CANCELED) {
            Log.d("CANCELED", "User canceled");
        }
    }

    // HttpRequest class to get a client-token
    private class HttpRequest extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Object doInBackground(Object[] objects) {
            HttpClient client = new HttpClient();
            client.get(get_token, new HttpResponseCallback() {
                @Override
                public void success(String responseBody) {
                    Log.d("mylog", responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Successfully got a client-token", Toast.LENGTH_SHORT).show();
                        }
                    });
                    token = responseBody;
                    onAuthorizationFetched();
                }

                @Override
                public void failure(Exception exception) {
                    final Exception ex = exception;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Failed to get a client-token: " + ex.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }
    }

}

