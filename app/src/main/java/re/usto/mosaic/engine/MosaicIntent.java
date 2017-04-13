package re.usto.mosaic.engine;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by gabriel on 27/03/17.
 */

public class MosaicIntent {

    public static final String ACTION_UPDATED_REGISTRATION_STATE =
            "re.usto.mosaic.UPDATED_REGISTRATION_STATE";

    public static final String ACTION_GET_REGISTRATION_STATE =
            "re.usto.mosaic.GET_REGISTRATION_STATE";

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

    public static final String ACTION_START_SERVICE =
            "re.usto.mosaic.START_SERVICE";

    public static final String ACTION_TOGGLE_MUTE_MICROPHONE =
            "re.usto.mosaic.TOGGLE_MUTE_MICROPHONE";

    static final String ACTION_MUTE_AUDIO =
            "re.usto.mosaic.MUTE_AUDIO";

    static final String ACTION_PLAY_MEDIA =
            "re.usto.mosaic.PLAY_MEDIA";

    static final String ACTION_STOP_MEDIA =
            "re.usto.mosaic.STOP_MEDIA";

    // Extras
    public static final String EXTRA_REGISTRATION_STATE = "extraConnectionStatus";
    public static final String EXTRA_USER_KEY = "userId";
    public static final String EXTRA_CALL_DESTINY = "callDestiny";
    public static final String EXTRA_CALL_INFO = "callInfo";
    public static final String EXTRA_CALL_ACTIVITY_CLASS_NAME = "callActivityClassName";
    static final String EXTRA_MEDIA_TYPE = "mediaType";

    /*
     * Filters for BroadcastReceivers
     */
    public static class FilterBuilder {

        private IntentFilter filter;

        public FilterBuilder() {
            filter = new IntentFilter();
        }

        public FilterBuilder addRegistrationStateAction() {
            filter.addAction(ACTION_UPDATED_REGISTRATION_STATE);
            return this;
        }

        public FilterBuilder addConnectivityChangeAction() {
            filter.addAction(ACTION_CONNECTIVITY_CHANGE);
            return this;
        }

        public FilterBuilder addDisconnectedCallAction() {
            filter.addAction(ACTION_DISCONNECTED_CALL);
            return this;
        }

        public FilterBuilder addToggleMuteMicAction() {
            filter.addAction(ACTION_TOGGLE_MUTE_MICROPHONE);
            return this;
        }

        public IntentFilter build() {
            return filter;
        }
    }

    // Broadcast Intents
    Intent updatedRegistrationState(boolean state) {
        return new Intent()
                .setAction(ACTION_UPDATED_REGISTRATION_STATE)
                .putExtra(EXTRA_REGISTRATION_STATE, state);
    }

    Intent disconnectedCall() {
        return new Intent().setAction(ACTION_DISCONNECTED_CALL);
    }

    Intent toggleMuteMic() {
        return new Intent().setAction(ACTION_TOGGLE_MUTE_MICROPHONE);
    }

    // MosaicService calls
    public Intent startService(Context context, String callActivityClassName) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_START_SERVICE)
                .putExtra(EXTRA_CALL_ACTIVITY_CLASS_NAME, callActivityClassName);
    }

    public Intent makeCall(Context context, String destiny) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_MAKE_CALL)
                .putExtra(EXTRA_CALL_DESTINY, destiny);
    }

    public Intent registerUser(Context context, String userKey) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_REGISTER_USER)
                .putExtra(EXTRA_USER_KEY, userKey);
    }

    public Intent getRegState(Context context) {
        return new Intent(context, MosaicService.class).setAction(ACTION_GET_REGISTRATION_STATE);
    }

    Intent connectivityChanged(Context context) {
        return new Intent(context, MosaicService.class).setAction(ACTION_CONNECTIVITY_CHANGE);
    }

    public Intent acceptCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_ACCEPT_CALL);
    }

    public Intent declineCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_DECLINE_CALL);
    }

    public Intent hangupCall(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_HANGUP_CALL);
    }

    public Intent toggleMuteMic(Context context) {
        return new Intent(context, MosaicService.class)
                .setAction(ACTION_TOGGLE_MUTE_MICROPHONE);
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
