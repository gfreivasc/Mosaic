package re.usto.mosaic.engine;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;

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
                new MosaicIntent().updateRegistrationState(prm.getCode())
        );
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);

        if (((MosaicService)mContext).getCall() != null) {
            //TODO: Decline this call and notify missed call
            return;
        }

        ((MosaicService)mContext).setCall(new MosaicCall(this, prm.getCallId()), true);

        LocalBroadcastManager.getInstance(mContext).sendBroadcast(
                new MosaicIntent().receivingIncomingCall()
        );
    }
}
