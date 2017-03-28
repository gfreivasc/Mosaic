package re.usto.mosaic.engine;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnRegStateParam;

import re.usto.mosaic.Mosaic;

/**
 * Created by gabriel on 21/03/17.
 */

public class MosaicAccount extends Account {

    private static final String TAG = "MosaicAccount";
    private Context mContext;

    public MosaicAccount(Context context) {
        mContext = context;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.v(TAG, "User reg code " + prm.getCode().toString());

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                new MosaicIntent().updateConnectionStatus(prm.getCode())
        );
    }
}
