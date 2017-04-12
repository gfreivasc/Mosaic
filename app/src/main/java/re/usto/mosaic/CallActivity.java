package re.usto.mosaic;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import re.usto.mosaic.components.IncomingCallFragment;
import re.usto.mosaic.components.OnCallFragment;
import re.usto.mosaic.engine.MosaicIntent;
import re.usto.mosaic.engine.PlaybackService;

public class CallActivity extends AppCompatActivity {

    private String mRemoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        if (getIntent().hasExtra(MosaicIntent.EXTRA_CALL_INFO))
            mRemoteUri = getIntent().getStringExtra(MosaicIntent.EXTRA_CALL_INFO);

        if (MosaicIntent.ACTION_INCOMING_CALL.equals(getIntent().getAction())) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            getFragmentManager().beginTransaction().add(
                    R.id.call_layout, new IncomingCallFragment()
            ).commit();
        }
        else if (MosaicIntent.ACTION_MAKE_CALL.equals(getIntent().getAction())) {
            getFragmentManager().beginTransaction().add(
                    R.id.call_layout, new OnCallFragment()
            ).commit();
        }
    }

    @Override
    protected void onDestroy() {
        startService(new MosaicIntent().stopMedia(this));
        startService(new MosaicIntent().hangupCall(this));
        super.onDestroy();
    }

    public String getRemoteUri() {
        return mRemoteUri;
    }
}
