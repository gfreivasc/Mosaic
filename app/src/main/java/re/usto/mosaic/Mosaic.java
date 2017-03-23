package re.usto.mosaic;

import android.app.Application;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;

import re.usto.mosaic.engine.MosaicAccount;

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

    }
}
