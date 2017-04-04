package re.usto.mosaic.engine;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.pjsip_status_code;

import re.usto.mosaic.CallActivity;

/**
 * Created by gabriel on 21/03/17.
 */

public class MosaicAccount extends Account {

    private static final String TAG = "MosaicAccount";
    private MosaicService mService;
    private pjsip_status_code mRegState;

    MosaicAccount(MosaicService service) {
        mService = service;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.v(TAG, "User reg code " + prm.getCode().toString());

        mRegState = prm.getCode();

        LocalBroadcastManager.getInstance(mService).sendBroadcast(
                new MosaicIntent().updateRegistrationState(prm.getCode())
        );
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);
        mService.startRingtone();

        MosaicCall call = new MosaicCall(this, prm.getCallId());

        if (mService.getCall() != null) {
            call.decline();
            call.delete();
            return;
        }

        mService.setCall(call);
        mService.startActivity(new Intent(mService, CallActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setAction(MosaicIntent.ACTION_INCOMING_CALL));
    }

    MosaicService getService() {
        return mService;
    }

    pjsip_status_code getRegState() {
        return mRegState;
    }
}
