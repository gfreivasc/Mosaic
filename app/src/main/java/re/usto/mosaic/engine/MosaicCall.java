package re.usto.mosaic.engine;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.Call;

/**
 * @author gfreivasc (Gabriel Vasconcelos)
 */

public class MosaicCall extends Call {

    MosaicCall(Account account, int callId) {
        super(account, callId);
    }


}
