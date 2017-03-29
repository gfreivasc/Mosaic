package re.usto.mosaic.engine;

import android.util.Log;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
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
    private static  Endpoint ep;

    MosaicCall(Account account, int callId) {
        super(account, callId);
    }


    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
        CallInfo ci;
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
        if(ci!=null) {
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                Log.d(TAG, "STATE_DISCONNECTED");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED){
                Log.d(TAG,"CALL CONFIRMED");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CALLING){
                Log.d(TAG,"CALLING");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_NULL){
                Log.d(TAG,"NULL STATE");
            }else if(ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_INCOMING){
                Log.d(TAG,"INCOMING CALL");
            }
        }
    }
}
