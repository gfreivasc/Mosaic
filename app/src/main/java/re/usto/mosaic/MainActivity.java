package re.usto.mosaic;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import re.usto.mosaic.engine.MosaicIntent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button calling;
    private TextView mConnectionStatusText;
    private RegistrationStateReceiver mRegStateReceiver;
    private boolean mOnline = false;
    private static final int mStatusRefreshInterval = 1000;
    private static final int mOnlineRefreshInterval = 10 * mStatusRefreshInterval;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calling = (Button) findViewById(R.id.button);
        mConnectionStatusText = (TextView) findViewById(R.id.connectionStatus);

        calling.setOnClickListener(this);

        mRegStateReceiver = new RegistrationStateReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mRegStateReceiver,
                new MosaicIntent.FilterBuilder().addRegistrationStateAction().build()
        );

        mHandler = new Handler();
        mStatusRefresh.run();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.RECORD_AUDIO}, 200);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Lucas", "Foooi permitido");
                } else {
                    Log.d("Lucas", "FUDEU");
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!mOnline) {
            Toast.makeText(
                    this,
                    "Não foi possível contactar o servidor. Tente novamente mais tarde",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        startService(new MosaicIntent().makeCall(this, sharedPreferences.getString(
                getString(R.string.pref_target_id_key),
                getString(R.string.pref_target_id_default)
        )));
    }

    private Runnable mStatusRefresh = new Runnable() {
        @Override
        public void run() {
            try {
                startService(new MosaicIntent().getRegState(MainActivity.this));
            }
            finally {
                mHandler.postDelayed(mStatusRefresh,
                        mOnline ? mOnlineRefreshInterval : mStatusRefreshInterval);
            }
        }
    };

    public class RegistrationStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MosaicIntent.EXTRA_REGISTRATION_STATE)) {
                mOnline = intent.getBooleanExtra(
                                MosaicIntent.EXTRA_REGISTRATION_STATE, false);

                mConnectionStatusText.setText(getString(
                        mOnline ? R.string.status_on : R.string.status_off));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return  true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mRegStateReceiver);
        super.onDestroy();
        mHandler.removeCallbacks(mStatusRefresh);
    }
}
