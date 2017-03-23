package re.usto.mosaic.engine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import java.util.Locale;

/**
 * Created by gabriel on 23/03/17.
 */

public class MosaicService extends Service {

    private static final String TAG = "MosaicService";
    private static final String SIP_PROTOCOL = "sip:";
    private static final String SIP_SERVER_IP = "192.168.174.106";
    private static final int SIP_SERVER_USER_BASE = 2600;
    private static final int SIP_SERVER_PORT = 5060;
    private static final String SIP_SERVER = SIP_PROTOCOL + SIP_SERVER_IP;
    private static final Endpoint ep = new Endpoint();
    private MosaicAccount mAccount;

    public static final String USER_KEY = "userId";

    @Override
    public void onCreate() {
        Log.i(TAG, "Creating service");
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        String userId = String.valueOf(SIP_SERVER_USER_BASE + intent.getIntExtra(USER_KEY, 0));
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

        mAccount = new MosaicAccount();
        try {
            mAccount.create(accountConfig);
        }
        catch (Exception e) {
            Log.e(TAG, "Error on registration.");
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mAccount.delete();

        try {
            ep.libDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ep.delete();
        super.onDestroy();
    }

    public String getUserKey() { return USER_KEY; }
}
