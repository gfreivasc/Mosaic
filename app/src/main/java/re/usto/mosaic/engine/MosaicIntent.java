package re.usto.mosaic.engine;

import android.content.Context;
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

    public static final String ACTION_MAKE_CALL =
            "re.usto.mosaic.MAKE_CALL";

    // Extras
    public static final String EXTRA_REGISTRATION_STATE = "extraConnectionStatus";
    public static final String EXTRA_USER_KEY = "userId";
    public static final String EXTRA_CALL_DESTINY = "callDestiny";

    public static class FilterBuilder {

        private IntentFilter filter;

        public FilterBuilder() {
            filter = new IntentFilter();
        }

        public FilterBuilder addRegistrationStateAction() {
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

    public Intent registerUser(Context context, String userKey) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_REGISTER_USER)
                .putExtra(EXTRA_USER_KEY, userKey);
    }

    Intent updateRegistrationState(pjsip_status_code statusCode) {
        return new Intent()
                .setAction(ACTION_UPDATE_REGISTRATION_STATE)
                .putExtra(EXTRA_REGISTRATION_STATE, statusCode.toString());
    }

    Intent connectivityChanged(Context context) {
        return new Intent(context, MosaicService.class).setAction(ACTION_CONNECTIVITY_CHANGE);
    }

    Intent receivingIncomingCall() {
        return new Intent().setAction(ACTION_INCOMING_CALL);
    }

    public Intent makeCall(Context context, String destiny) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_MAKE_CALL)
                .putExtra(EXTRA_CALL_DESTINY, destiny);
    }
}
