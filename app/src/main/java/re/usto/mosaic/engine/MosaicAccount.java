package re.usto.mosaic.engine;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;

import re.usto.mosaic.IncomingCallActivity;

import static org.pjsip.pjsua2.pjsip_status_code.PJSIP_SC_OK;

/**
 * Created by gabriel on 21/03/17.
 */

public class MosaicAccount extends Account {

    private static final String TAG = "MosaicAccount";
    private MosaicService mService;

    public MosaicAccount(MosaicService service) {
        mService = service;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.v(TAG, "User reg code " + prm.getCode().toString());

        LocalBroadcastManager.getInstance(mService).sendBroadcast(
                new MosaicIntent().updateRegistrationState(prm.getCode())
        );
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);

        if (mService.getCall() != null) {
            //TODO: Decline this call and notify missed call
            return;
        }

        mService.setCall(new MosaicCall(this, prm.getCallId()));
        mService.startActivity(new Intent(mService, IncomingCallActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    MosaicService getService() {
        return mService;
    }
}
