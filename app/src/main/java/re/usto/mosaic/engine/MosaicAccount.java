package re.usto.mosaic.engine;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.pjsip_status_code;

import re.usto.mosaic.ExampleCallActivity;

/**
 * @author gabriel on 21/03/17.
 */

public class MosaicAccount extends Account {

    private static final String TAG = "MosaicAccount";
    private MosaicService mService;

    MosaicAccount(MosaicService service) {
        mService = service;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.v(TAG, "User reg code " + prm.getCode().toString());

        mService.setOnlineStatus(
                prm.getCode().toString().equals(pjsip_status_code.PJSIP_SC_OK.toString()));
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);
        mService.startMediaPlayback(PlaybackService.MediaType.RINGTONE);

        MosaicCall call = new MosaicCall(this, prm.getCallId());

        if (mService.getCall() != null) {
            call.decline();
            call.delete();
            return;
        }

        CallInfo ci = null;
        try {
            ci = call.getInfo();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        mService.setCall(call);
        mService.startActivity(new Intent(mService, mService.getCallActivity())
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setAction(MosaicIntent.ACTION_INCOMING_CALL)
                .putExtra(MosaicIntent.EXTRA_CALL_INFO,
                        ci != null ? ci.getRemoteUri() : null));
    }

    MosaicService getService() {
        return mService;
    }
}
