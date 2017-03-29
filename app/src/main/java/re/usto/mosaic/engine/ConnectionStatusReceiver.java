package re.usto.mosaic.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import re.usto.mosaic.network.NetworkUtils;

/**
 * Created by gabriel on 27/03/17.
 */

public class ConnectionStatusReceiver extends BroadcastReceiver {

    private static final String TAG = ConnectionStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int status = NetworkUtils.getConnectivityStatus(context);
        if(status != NetworkUtils.TYPE_NOT_CONNECTED) {
            Log.d("NETWORK","Trying to start service, network status changed");
            context.startService(new MosaicIntent().connectivityChanged(context));
        }
    }
}
