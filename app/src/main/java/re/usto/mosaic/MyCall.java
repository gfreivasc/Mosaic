package re.usto.mosaic;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;

/**
 * Created by Lucas on 22/03/2017.
 */

public class MyCall extends Call {

    public MyCall(Account acc) {
        super(acc);
    }

    @Override
    public void onCallState(OnCallStateParam prm) {
        super.onCallState(prm);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
    }

}
