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

    public static final String ACTION_ACCEPT_CALL =
            "re.usto.mosaic.ACCEPT_CALL";

    public static final String ACTION_DECLINE_CALL =
            "re.usto.mosaic.DECLINE_CALL";

    public static final String ACTION_HANGUP_CALL =
            "re.usto.mosaic.HANGUP_CALL";

    public static final String ACTION_DISCONNECTED_CALL =
            "re.usto.mosaic.DISCONNECTED_CALL";

    static final String ACTION_PLAY_MEDIA =
            "re.usto.mosaic.PLAY_MEDIA";

    static final String ACTION_STOP_MEDIA =
            "re.usto.mosaic.STOP_MEDIA";

    // Extras
    public static final String EXTRA_REGISTRATION_STATE = "extraConnectionStatus";
    public static final String EXTRA_USER_KEY = "userId";
    public static final String EXTRA_CALL_DESTINY = "callDestiny";
    public static final String EXTRA_CALL_INFO = "callInfo";
    static final String EXTRA_MEDIA_TYPE = "mediaType";

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

        public FilterBuilder addDisconnectedCallAction() {
            filter.addAction(ACTION_DISCONNECTED_CALL);
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

    public Intent registerUser(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_REGISTER_USER);
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

    Intent disconnectedCall() {
        return new Intent().setAction(ACTION_DISCONNECTED_CALL);
    }

    public Intent makeCall(Context context, String destiny) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_MAKE_CALL)
                .putExtra(EXTRA_CALL_DESTINY, destiny);
    }

    public Intent acceptCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(MosaicIntent.ACTION_ACCEPT_CALL);
    }

    public Intent declineCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(MosaicIntent.ACTION_DECLINE_CALL);
    }

    public Intent hangupCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(MosaicIntent.ACTION_HANGUP_CALL);
    }

    public Intent playMedia(Context context, @PlaybackService.MediaType int mediaType) {
        return new Intent(context, PlaybackService.class)
                .setAction(ACTION_PLAY_MEDIA)
                .putExtra(EXTRA_MEDIA_TYPE, mediaType);
    }

    public Intent stopMedia(Context context) {
        return new Intent(context, PlaybackService.class)
                .setAction(ACTION_STOP_MEDIA);
    }
}
