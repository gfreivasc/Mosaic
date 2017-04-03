package re.usto.mosaic.engine;

import android.content.Context;
import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.ConfPortInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua_call_media_status;

/**
 * @author gfreivasc (Gabriel Vasconcelos)
 */

public class MosaicCall extends Call {

    private static final String TAG = MosaicCall.class.getSimpleName();
    private static MosaicService service;
    private static Endpoint ep;
    private Context context;


    MosaicCall(Context context, Account account, int callId) {
        super(account, callId);
        this.service = (MosaicService) context;
        this.context = context;
    }


    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
        CallInfo ci;
        ep = service.getEp();
        ConfPortInfo confPortInfo = new ConfPortInfo();
        confPortInfo.setPortId(5060);

        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }
        if(ci!=null){
            CallMediaInfoVector cmiv = ci.getMedia();
            for(int i = 0;i < cmiv.size() ;i++){
                CallMediaInfo cmi = cmiv.get(i);
                if(cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO){
                    if(cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE
                            || (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD)){

                        Media media = getMedia(i);
                        AudioMedia am = AudioMedia.typecastFromMedia(media);

                        //Sending media to EP code below
                        try {
                            ep.audDevManager().getCaptureDevMedia().startTransmit(am);
                            if (am != null) {
                                am.startTransmit(ep.audDevManager().getPlaybackDevMedia());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
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
                delete();
                Log.d(TAG,"CALL DISCONNECTED");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
                Log.d(TAG,"CALL CONFIRMED");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
                Log.d(TAG,"CALLING");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_NULL){
                Log.d(TAG,"NULL STATE");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
                Log.d(TAG,"INCOMING CALL");
            }else if(ci.getState()== pjsip_inv_state.PJSIP_INV_STATE_EARLY){

            }
        }
    }
}
