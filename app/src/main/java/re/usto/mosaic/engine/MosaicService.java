package re.usto.mosaic.engine;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.util.Locale;

import re.usto.mosaic.IncomingCallActivity;

/**
 * Created by gabriel on 23/03/17.
 */

public class MosaicService extends BackgroundService {

    private static final String TAG = MosaicService.class.getSimpleName();
    private static final String SIP_PROTOCOL = "sip:";
    private static final String SIP_SERVER_IP = "192.168.174.106";
    private static final int SIP_SERVER_PORT = 5060;
    private static final String SIP_SERVER = SIP_PROTOCOL + SIP_SERVER_IP;
    private static final Endpoint mEndpoint = new Endpoint();
    private MosaicAccount mAccount;
    private MosaicCall mCall = null;

    public MosaicService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Creating PJSIP service");
        super.onCreate();

        try {
            mEndpoint.libCreate();
            EpConfig epCfg = new EpConfig();
            mEndpoint.libInit(epCfg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TransportConfig sipTpConfig = new TransportConfig();
        sipTpConfig.setPort(SIP_SERVER_PORT);
        try {
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            mEndpoint.libStart();
        }
        catch (Exception e) {
            Log.e(TAG, "Error establishing transport");
            e.printStackTrace();
        }
    }

    @Override
    protected void onReceivedIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        switch (intent.getAction()) {
            case MosaicIntent.ACTION_REGISTER_USER:
                handleRegister(intent);
                break;

            case MosaicIntent.ACTION_CONNECTIVITY_CHANGE:
                handleConnectivityChange();
                break;

            case MosaicIntent.ACTION_MAKE_CALL:
                handleMakeCall(intent);
                break;

            case MosaicIntent.ACTION_ACCEPT_CALL:
                handleAcceptCall();
                break;

            case MosaicIntent.ACTION_DECLINE_CALL:
                handleDeclineCall();
                break;
        }
    }

    private void handleRegister(Intent intent) {
        if (intent.hasExtra(MosaicIntent.EXTRA_USER_KEY)) {
            try {
                mAccount.setRegistration(true);
            }
            catch (Exception e) {
                Log.e(TAG, "Could not renew registration ", e);
            }
            return;
        }

        String userId = intent.getStringExtra(MosaicIntent.EXTRA_USER_KEY);

        Log.i(TAG, "Registering user " + userId);
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setIdUri(String.format(Locale.US,
                "%1$s%2$s@%3$s",
                SIP_PROTOCOL,
                userId,
                SIP_SERVER_IP
        ));
        accountConfig.getRegConfig().setRegistrarUri(SIP_SERVER);
        AuthCredInfo authCredInfo = new AuthCredInfo("Digest", "*", userId, 0, "4567");
        accountConfig.getSipConfig().getAuthCreds().add(authCredInfo);

        mAccount = new MosaicAccount(this);
        try {
            mAccount.create(accountConfig);
        } catch (Exception e) {
            Log.e(TAG, "Error on registration.");
            e.printStackTrace();
        }
    }

    private void handleConnectivityChange() {
        try {
            mAccount.setRegistration(true);
        }
        catch (Exception e) {
            Log.e(TAG, "Error connecting to server: ", e);
        }
    }

    private void handleMakeCall(Intent intent) {
        if (!intent.hasExtra(MosaicIntent.EXTRA_CALL_DESTINY)) return;
        String destUri = String.format(Locale.US,
                "%1$s%2$s@%3$s",
                SIP_PROTOCOL,
                intent.getStringExtra(MosaicIntent.EXTRA_CALL_DESTINY),
                SIP_SERVER_IP);

        mCall = new MosaicCall(mAccount);
        CallOpParam prm = new CallOpParam(true);
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            mCall.makeCall(destUri, prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error. Unable to make call: ", e);
            mCall = null;
        }
    }

    private void handleAcceptCall() {
        mCall.accept();
    }

    private void handleDeclineCall() {
        mCall.decline();
    }

    @Override
    public void onDestroy() {
        if (mAccount != null) mAccount.delete();

        try {
            mEndpoint.libDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mEndpoint.delete();
        super.onDestroy();
    }

    public Call getCall() {
        return mCall;
    }

    public void setCall(MosaicCall call) {
        mCall = call;
    }

    Endpoint getEp(){
        return mEndpoint;
    }
}
