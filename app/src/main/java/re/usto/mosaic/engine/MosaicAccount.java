package re.usto.mosaic.engine;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;

import static org.pjsip.pjsua2.pjsip_status_code.PJSIP_SC_OK;

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
        CallOpParam callOpParam = new CallOpParam(true);
        callOpParam.setStatusCode(PJSIP_SC_OK);

        CallSetting opt = callOpParam.getOpt();
        opt.setAudioCount(1);
        opt.setVideoCount(0);
        callOpParam.setOpt(opt);


        ((MosaicService)mContext).setCall(new MosaicCall(mContext,this, prm.getCallId()), true,callOpParam);
    }
}
