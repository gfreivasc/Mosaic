package re.usto.mosaic.engine;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
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
    private static final int SIP_SERVER_USER_BASE = 2600;
    private static final int SIP_SERVER_PORT = 5060;
    private static final String SIP_SERVER = SIP_PROTOCOL + SIP_SERVER_IP;
    private static final Endpoint ep = new Endpoint();
    private MosaicAccount mAccount;
    private Call mCall = null;

    public MosaicService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Creating PJSIP service");
        super.onCreate();

        try {
            ep.libCreate();
            EpConfig epCfg = new EpConfig();
            ep.libInit(epCfg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TransportConfig sipTpConfig = new TransportConfig();
        sipTpConfig.setPort(SIP_SERVER_PORT);
        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            ep.libStart();
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
        }
    }

    private void handleRegister(Intent intent) {
        registerToServer(String.valueOf(
                SIP_SERVER_USER_BASE + intent.getIntExtra(MosaicIntent.EXTRA_USER_KEY, 0)));
    }

    private void registerToServer(String userId) {
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

        mCall = new Call(mAccount);
        CallOpParam prm = new CallOpParam(true);
        try {
            mCall.makeCall(destUri, prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error. Unable to make call: ", e);
            mCall = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mAccount != null) mAccount.delete();

        try {
            ep.libDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ep.delete();
        super.onDestroy();
    }

    public Call getCall() {
        return mCall;
    }

    public void setCall(Call call, boolean incoming) {
        mCall = call;

        if (incoming) {
            startActivity(new Intent(this, IncomingCallActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "Removing call from queue.");
                    mCall.delete();
                    mCall = null;
                }
            }, 30000);
        }
    }
}
