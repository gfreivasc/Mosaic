package re.usto.mosaic.engine;

import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.OnRegStateParam;

/**
 * Created by gabriel on 21/03/17.
 */

public class MosaicAccount extends Account {

    private static final String TAG = "MosaicAccount";

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);
        Log.i(TAG, "User reg state " + prm.getReason());
    }
}
