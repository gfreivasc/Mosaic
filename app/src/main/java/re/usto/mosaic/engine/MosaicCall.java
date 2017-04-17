package re.usto.mosaic.engine;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_media_status;

/**
 * @author gfreivasc (Gabriel Vasconcelos)
 */

class MosaicCall extends Call {

    private static final String TAG = MosaicCall.class.getSimpleName();
    private MosaicAccount mAccount;
    private boolean mMicMute = false;
    private boolean mAudioMute = false;

    MosaicCall(MosaicAccount account) {
        super(account);
        mAccount = account;
    }

    MosaicCall(MosaicAccount account, int callId) {
        super(account, callId);
        mAccount = account;
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
        CallInfo ci;

        try {
            ci = getInfo();
        }
        catch (Exception e) {
            Log.e(TAG, "onCallMediaState: error while getting call info", e);
            return;
        }

        for (int i = 0; i < ci.getMedia().size(); ++i) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = ci.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);
                AudDevManager mgr = mAccount.getService().getEp().audDevManager();

                try {
                    audioMedia.adjustRxLevel(1.5f);
                    audioMedia.adjustTxLevel(1.5f);
                }
                catch (Exception e) {
                    Log.e(TAG, "Error while adjusting levels", e);
                }

                try {
                    audioMedia.startTransmit(mgr.getPlaybackDevMedia());
                    mgr.getCaptureDevMedia().startTransmit(audioMedia);
                }
                catch (Exception e) {
                    Log.e(TAG, "Error while connecting audio media to sound device", e);
                }
            }
        }
    }


    @Override
    public void onCallState(OnCallStateParam prm) {
        super.onCallState(prm);

        CallInfo ci = null;
        try {
            ci = getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        PJSIP_INV_STATE_NULL
        Before INVITE is sent or received

        PJSIP_INV_STATE_CALLING
        After INVITE is sent

        PJSIP_INV_STATE_INCOMING
        After INVITE is received.

        PJSIP_INV_STATE_EARLY
        After response with To tag.

        PJSIP_INV_STATE_CONNECTING
        After 2xx is sent/received.

        PJSIP_INV_STATE_CONFIRMED
        After ACK is sent/received.

        PJSIP_INV_STATE_DISCONNECTED
        Session is terminated.
         */

        if(ci!=null) {
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                pjsip_status_code reason = ci.getLastStatusCode();
                Log.d(TAG,"CALL DISCONNECTED REASON: " + reason);

                mAccount.getService().stopMediaPlayback();
                LocalBroadcastManager.getInstance(mAccount.getService()).sendBroadcast(
                        new MosaicIntent().disconnectedCall()
                );
                delete();
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
                mAccount.getService().stopMediaPlayback();
                Log.d(TAG,"CALL CONFIRMED");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
                mAccount.getService().startMediaPlayback(PlaybackService.MediaType.DIAL_TONE);
                Log.d(TAG,"CALLING");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_NULL){
                Log.d(TAG,"NULL STATE");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
                Log.d(TAG,"INCOMING CALL");
            }else if(ci.getState()== pjsip_inv_state.PJSIP_INV_STATE_EARLY){
                Log.d(TAG, "EARLY");
            }
        }
    }

    void toggleMuteMic() {
        CallInfo ci = null;
        try {
            ci = getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ci == null || ci.getState() != pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED)
            return;

        mMicMute = !mMicMute;

        for (int i = 0; i < ci.getMedia().size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = ci.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                try {
                    AudDevManager mgr = mAccount.getService().getEp().audDevManager();

                    if (mMicMute) {
                        mgr.getCaptureDevMedia().stopTransmit(audioMedia);
                    } else {
                        mgr.getCaptureDevMedia().startTransmit(audioMedia);
                    }

                } catch (Exception exc) {
                    Log.e(TAG, "Couldn't manage audio capture transmission", exc);
                }
            }
        }

        LocalBroadcastManager.getInstance(mAccount.getService()).sendBroadcast(
                new MosaicIntent().toggleMuteMic()
        );
    }

    void toggleMuteAudio() {
        CallInfo ci = null;
        try {
            ci = getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ci == null || ci.getState() != pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED)
            return;

        mAudioMute = !mAudioMute;

        for (int i = 0; i < ci.getMedia().size(); i++) {
            Media media = getMedia(i);
            CallMediaInfo mediaInfo = ci.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                if (audioMedia == null)
                    throw new NullPointerException("Could not get audio media");

                try {
                    AudDevManager mgr = mAccount.getService().getEp().audDevManager();

                    if (mAudioMute) {
                        audioMedia.stopTransmit(mgr.getPlaybackDevMedia());
                    } else {
                        audioMedia.startTransmit(mgr.getPlaybackDevMedia());
                    }

                } catch (Exception exc) {
                    Log.e(TAG, "Couldn't manage audio capture transmission", exc);
                }
            }
        }

        LocalBroadcastManager.getInstance(mAccount.getService()).sendBroadcast(
                new MosaicIntent().toggleMuteAudio()
        );
    }

    void accept() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);

        try {
            answer(prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error accepting call ", e);
        }
    }

    void decline() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);

        try {
            answer(prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error declining call ", e);
        }
    }

    void busyHere() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);

        try {
            answer(prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error sending busy state ", e);
        }
    }

    void hangup() {
        CallOpParam prm = new CallOpParam();

        try {
            super.hangup(prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Could not hangup call ", e);
        }
    }
}
