package re.usto.mosaic.engine;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import re.usto.mosaic.R;
import re.usto.mosaic.components.IncomingCallFragment;
import re.usto.mosaic.components.OnCallFragment;

public class CallActivity extends AppCompatActivity {

    public static final String DIALING = "dialing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            Bundle bundle = new Bundle();
            bundle.putBoolean(DIALING, true);
            OnCallFragment fragment = new OnCallFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction().add(
                    R.id.call_layout, fragment
            ).commit();
        }
    }

    @Override
    protected void onDestroy() {
        startService(new MosaicIntent().stopMedia(this));
        startService(new MosaicIntent().hangupCall(this));
        super.onDestroy();
    }
}
