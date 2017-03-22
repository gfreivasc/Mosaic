package re.usto.mosaic;

import android.app.Application;

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

    public static Endpoint ep;
    public MosaicAccount mAccount;

    @Override
    public void onCreate() {
        super.onCreate();
        try {

            ep = new Endpoint();
            ep.libCreate();
            EpConfig epCfg = new EpConfig();
            ep.libInit(epCfg);

            AccountConfig accountConfig = new AccountConfig();
            accountConfig.setIdUri("sip:2903@192.168.174.106");
            accountConfig.getRegConfig().setRegistrarUri("sip:192.168.174.106");
            AuthCredInfo authCredInfo = new AuthCredInfo("digest", "*", "2904", 0, "secret");
            accountConfig.getSipConfig().getAuthCreds().add(authCredInfo);

            mAccount = new MosaicAccount();
            mAccount.create(accountConfig);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        mAccount.delete();
        try {
            ep.libDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        ep.delete();
    }
}
