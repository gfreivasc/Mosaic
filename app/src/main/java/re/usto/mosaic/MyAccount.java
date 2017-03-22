package re.usto.mosaic;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountInfo;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;

/**
 * Created by Lucas on 22/03/2017.
 */

public class MyAccount extends Account{

    public AccountConfig cfg;

    public MyAccount(AccountConfig config){
        super();
        cfg = config;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        try {
            AccountInfo accountInfo = getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        super.onIncomingCall(prm);

    }
}
