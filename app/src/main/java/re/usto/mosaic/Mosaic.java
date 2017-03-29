package re.usto.mosaic;

import android.app.Application;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;

import re.usto.mosaic.engine.ConnectionStatusReceiver;
import re.usto.mosaic.engine.MosaicAccount;
import re.usto.mosaic.engine.MosaicIntent;

/**
 * Created by gabriel on 21/03/17.
 */

public class Mosaic extends Application {

    static {
        System.loadLibrary("pjsua2");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Connectivity_Change broadcasts are only listened by receivers registered at main thread.
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new ConnectionStatusReceiver(),
                new MosaicIntent.FilterBuilder().addConnectivityChangeAction().build()
        );
        
        startService(
                new MosaicIntent().registerUser(this,
                        PreferenceManager.getDefaultSharedPreferences(this).getString(
                                getString(R.string.pref_user_id_key),
                                getString(R.string.pref_user_id_default)
                        ))
        );
    }
}
