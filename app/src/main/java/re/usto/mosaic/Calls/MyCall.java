package re.usto.mosaic.Calls;

import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;

/**
 * Created by Lucas on 22/03/2017.
 */

public class MyCall extends Call {

    public MyCall(long cPtr, boolean cMemoryOwn) {
        super(cPtr, cMemoryOwn);
    }

    private void MakeCall(){
        
    }


    // Notification when call’s state has changed.
    @Override
    public void onCallState(OnCallStateParam prm) {
        super.onCallState(prm);
    }

    // Notification when call’s media state has changed.
    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
    }

}
