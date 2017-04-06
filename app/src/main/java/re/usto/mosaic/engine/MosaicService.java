package re.usto.mosaic.engine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua_stun_use;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

import re.usto.mosaic.CallActivity;
import re.usto.mosaic.Mosaic;
import re.usto.mosaic.R;

/**
 * @author gabriel & lucas on 23/03/17.
 */

public class MosaicService extends BackgroundService {

    private static final String TAG = MosaicService.class.getSimpleName();
    private static final String SIP_PROTOCOL = "sip:";
    private static final String SIP_SERVER_IP = "192.168.174.106";
    private static final int SIP_SERVER_PORT = 5060;
    private static final String SIP_SERVER = SIP_PROTOCOL + SIP_SERVER_IP;
    private static final long[] VIBRATOR_PATTERN = {0, 1000, 1000};

    @IntDef({MediaType.RINGTONE, MediaType.DIAL_TONE, MediaType.DISCONNECTED_TONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface MediaType {
        int RINGTONE = 0;
        int DIAL_TONE = 1;
        int DISCONNECTED_TONE = 2;
    }

    private static final Endpoint mEndpoint = new Endpoint();
    private MosaicAccount mAccount;
    private MosaicCall mCall = null;
    private MediaPlayer mMediaPlayer;
    private Uri mRingtoneUri;
    private Vibrator mVibrator;

    private CallDisconnectedReceiver mCallDisconnectedReceiver;

    public MosaicService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Creating PJSIP service");
        super.onCreate();

        try {
            mEndpoint.libCreate();
            EpConfig epCfg = new EpConfig();
            mEndpoint.libInit(epCfg);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        TransportConfig sipTpConfig = new TransportConfig();
        sipTpConfig.setPort(SIP_SERVER_PORT);
        try {
            mEndpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
            mEndpoint.libStart();
        }
        catch (Exception e) {
            Log.e(TAG, "Error establishing transport");
            e.printStackTrace();
        }

        mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                this, RingtoneManager.TYPE_RINGTONE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        mCallDisconnectedReceiver = new CallDisconnectedReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mCallDisconnectedReceiver,
                new MosaicIntent.FilterBuilder().addDisconnectedCallAction().build()
        );
    }

    @Override
    protected void onReceivedIntent(@Nullable Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;

        switch (intent.getAction()) {
            case MosaicIntent.ACTION_REGISTER_USER:
                handleRegister(intent);
                break;

            case MosaicIntent.ACTION_CONNECTIVITY_CHANGE:
                handleConnectivityChange();
                break;

            case MosaicIntent.ACTION_MAKE_CALL:
                handleMakeCall(intent);
                break;

            case MosaicIntent.ACTION_ACCEPT_CALL:
                handleAcceptCall();
                break;

            case MosaicIntent.ACTION_DECLINE_CALL:
                handleDeclineCall();
                break;

            case MosaicIntent.ACTION_HANGUP_CALL:
                handleHangupCall();
                break;
        }
    }

    private void handleRegister(Intent intent) {
        if (!intent.hasExtra(MosaicIntent.EXTRA_USER_KEY)) {
            try {
                mAccount.setRegistration(true);
            }
            catch (Exception e) {
                Log.e(TAG, "Could not renew registration ", e);
            }
            return;
        }

        String userId = intent.getStringExtra(MosaicIntent.EXTRA_USER_KEY);

        Log.i(TAG, "Registering user " + userId);
        AccountConfig accountConfig = new AccountConfig();
        accountConfig.setIdUri(String.format(Locale.US,
                "%1$s%2$s@%3$s",
                SIP_PROTOCOL,
                userId,
                SIP_SERVER_IP
        ));
        accountConfig.getRegConfig().setRegistrarUri(SIP_SERVER);
        AuthCredInfo authCredInfo = new AuthCredInfo("Digest", "*", userId, 0, "4567");
        accountConfig.getSipConfig().getAuthCreds().add(authCredInfo);
        accountConfig.getNatConfig().setSipStunUse(pjsua_stun_use.PJSUA_STUN_USE_DEFAULT);
        accountConfig.getNatConfig().setMediaStunUse(pjsua_stun_use.PJSUA_STUN_USE_DEFAULT);

        mAccount = new MosaicAccount(this);
        try {
            mAccount.create(accountConfig);
        } catch (Exception e) {
            Log.e(TAG, "Error on registration.");
            e.printStackTrace();
        }
    }

    private void handleConnectivityChange() {
        try {
            mAccount.setRegistration(true);
        }
        catch (Exception e) {
            Log.e(TAG, "Error connecting to server: ", e);
        }
    }

    private void handleMakeCall(Intent intent) {
        if (!intent.hasExtra(MosaicIntent.EXTRA_CALL_DESTINY)) return;
        if (mAccount == null) {
            return;
        }

        String destUri = String.format(Locale.US,
                "%1$s%2$s@%3$s",
                SIP_PROTOCOL,
                intent.getStringExtra(MosaicIntent.EXTRA_CALL_DESTINY),
                SIP_SERVER_IP);

        mCall = new MosaicCall(mAccount);
        CallOpParam prm = new CallOpParam(true);
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            mCall.makeCall(destUri, prm);
        }
        catch (Exception e) {
            Log.e(TAG, "Error. Unable to make call: ", e);
            mCall = null;
            return;
        }

        startActivity(new Intent(this, CallActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .setAction(MosaicIntent.ACTION_MAKE_CALL));
    }

    private void handleAcceptCall() {
        mCall.accept();
    }

    private void handleDeclineCall() {
        mCall.decline();
    }

    private void  handleHangupCall() {
        mCall.hangup();
    }

    @Override
    public void onDestroy() {
        if (mAccount != null) mAccount.delete();

        try {
            mEndpoint.libDestroy();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        mEndpoint.delete();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mCallDisconnectedReceiver);
        super.onDestroy();
    }

    public Call getCall() {
        return mCall;
    }

    public void setCall(MosaicCall call) {
        mCall = call;
    }

    Endpoint getEp(){
        return mEndpoint;
    }

    synchronized void startMediaPlayback(@MediaType int mediaType) {
        // startService(new MosaicIntent().playMedia(this, mediaType));

        mMediaPlayer = new MediaPlayer();
        switch (mediaType) {
            case MediaType.RINGTONE:
                mVibrator.vibrate(VIBRATOR_PATTERN, 0);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                try {
                    mMediaPlayer.setDataSource(this, mRingtoneUri);
                }
                catch (IOException e) {
                    Log.e(TAG, "Could not setup media player", e);
                }

                break;

            case MediaType.DIAL_TONE:
                AssetFileDescriptor afd = this.getResources().openRawResourceFd(R.raw.dial_tone);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                try {
                    mMediaPlayer.setDataSource(
                            afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                }
                catch (IOException e) {
                    Log.e(TAG, "Could not setup media player", e);
                }
                break;

            case MediaType.DISCONNECTED_TONE:
                afd = this.getResources().openRawResourceFd(R.raw.dial_tone);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                try {
                    mMediaPlayer.setDataSource(
                            afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                }
                catch (IOException e) {
                    Log.e(TAG, "Could not setup media player", e);
                }
                break;
        }

        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        mMediaPlayer.prepareAsync();
    }

    synchronized void stopMediaPlayback() {
        mVibrator.cancel();
        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() || mMediaPlayer.isLooping())) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }

    private class CallDisconnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mCall == null) return;
            mCall = null;
        }
    }
}
