package re.usto.mosaic.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by gabriel on 27/03/17.
 */

public class ConnectionStatusReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Connectivity changed!");
        context.startService(new MosaicIntent().connectivityChanged());
    }
}
