package re.usto.mosaic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import re.usto.mosaic.components.IncomingCallFragment;
import re.usto.mosaic.components.OnCallFragment;
import re.usto.mosaic.engine.MosaicIntent;

public class CallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        if (MosaicIntent.ACTION_INCOMING_CALL.equals(getIntent().getAction())) {
            getFragmentManager().beginTransaction().add(
                    R.id.call_layout, new IncomingCallFragment()
            ).commit();
        }
        else if (MosaicIntent.ACTION_MAKE_CALL.equals(getIntent().getAction())) {
            getFragmentManager().beginTransaction().add(
                    R.id.call_layout, new OnCallFragment()
            ).commit();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new CallDisconnectedReceiver(),
                new MosaicIntent.FilterBuilder().addDisconnectedCallAction().build()
        );
    }

    private class CallDisconnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
