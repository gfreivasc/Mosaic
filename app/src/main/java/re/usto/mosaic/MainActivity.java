package re.usto.mosaic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import re.usto.mosaic.engine.MosaicIntent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button calling;
    private TextView mConnectionStatusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new MosaicIntent().registerUser(this, 1));

        calling = (Button) findViewById(R.id.button);
        mConnectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        calling.setOnClickListener(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new RegistrationStateReceiver(),
                new MosaicIntent.FilterBuilder().addRegistrationStateAction().build()
        );
    }

    @Override
    public void onClick(View v) {
        startService(new MosaicIntent().makeCall(this, "2605"));
    }

    public class RegistrationStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MosaicIntent.EXTRA_REGISTRATION_STATE)) {
                String statusCode = intent.getStringExtra(
                                MosaicIntent.EXTRA_REGISTRATION_STATE);

                switch (statusCode) {
                    case "PJSIP_SC_OK":
                        mConnectionStatusText.setText(getString(R.string.status_on));
                        break;
                    default:
                        mConnectionStatusText.setText(getString(R.string.status_off));
                }
            }
        }
    }
}
