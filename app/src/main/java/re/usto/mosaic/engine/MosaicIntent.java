package re.usto.mosaic.engine;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import org.pjsip.pjsua2.pjsip_status_code;

import static org.pjsip.pjsua2.pjsip_status_code.PJSIP_SC_OK;

/**
 * Created by gabriel on 27/03/17.
 */

public class MosaicIntent {

    public static final String ACTION_UPDATE_CONNECTION_STATUS =
            "re.usto.mosaic.UPDATE_CONNECTION_STATUS";

    public static final String ACTION_CONNECTIVITY_CHANGE =
            "android.net.conn.CONNECTIVITY_CHANGE";

    public static final String ACTION_REGISTER_USER =
            "re.usto.mosaic.REGISTER_USER";

    // Extras
    public static final String EXTRA_CONNECTION_STATUS = "extraConnectionStatus";

    // PJSIP Status constants
    public static final String PJSIP_SC_OK = pjsip_status_code.PJSIP_SC_OK.toString();
    public static final String PJSIP_SC_TRYING = pjsip_status_code.PJSIP_SC_TRYING.toString();
    public static final String PJSIP_SC_FORBIDDEN = pjsip_status_code.PJSIP_SC_FORBIDDEN.toString();

    public static class FilterBuilder {

        private IntentFilter filter;

        public FilterBuilder() {
            filter = new IntentFilter();
        }

        public FilterBuilder addConnectionStatusAction() {
            filter.addAction(ACTION_UPDATE_CONNECTION_STATUS);
            return this;
        }

        public FilterBuilder addConnectivityChangeAction() {
            filter.addAction(ACTION_CONNECTIVITY_CHANGE);
            return this;
        }

        public IntentFilter build() {
            return filter;
        }
    }

    public Intent updateConnectionStatus(pjsip_status_code statusCode) {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CONNECTION_STATUS);
        intent.putExtra(EXTRA_CONNECTION_STATUS, statusCode.toString());
        return intent;
    }
}
