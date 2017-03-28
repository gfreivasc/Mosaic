package re.usto.mosaic.engine;

import android.content.Intent;
import android.content.IntentFilter;

import org.pjsip.pjsua2.pjsip_status_code;

/**
 * Created by gabriel on 27/03/17.
 */

public class MosaicIntent {

    public static final String ACTION_UPDATE_REGISTRATION_STATE =
            "re.usto.mosaic.UPDATE_REGISTRATION_STATE";

    public static final String ACTION_CONNECTIVITY_CHANGE =
            "android.net.conn.CONNECTIVITY_CHANGE";

    public static final String ACTION_REGISTER_USER =
            "re.usto.mosaic.REGISTER_USER";

    public static final String ACTION_INCOMING_CALL =
            "re.usto.mosaic.INCOMING_CALL";

    // Extras
    public static final String EXTRA_CONNECTION_STATUS = "extraConnectionStatus";

    public static class FilterBuilder {

        private IntentFilter filter;

        public FilterBuilder() {
            filter = new IntentFilter();
        }

        public FilterBuilder addConnectionStatusAction() {
            filter.addAction(ACTION_UPDATE_REGISTRATION_STATE);
            return this;
        }

        public FilterBuilder addConnectivityChangeAction() {
            filter.addAction(ACTION_CONNECTIVITY_CHANGE);
            return this;
        }

        public FilterBuilder addIncomingCallAction() {
            filter.addAction(ACTION_INCOMING_CALL);
            return this;
        }

        public IntentFilter build() {
            return filter;
        }
    }

    Intent updateConnectionStatus(pjsip_status_code statusCode) {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_REGISTRATION_STATE);
        intent.putExtra(EXTRA_CONNECTION_STATUS, statusCode.toString());
        return intent;
    }

    Intent receivingIncomingCall() {
        return new Intent().setAction(ACTION_INCOMING_CALL);
    }
}
