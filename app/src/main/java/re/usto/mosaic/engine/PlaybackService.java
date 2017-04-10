package re.usto.mosaic.engine;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import re.usto.mosaic.R;

/**
 * Created by gabriel on 05/04/17.
 */

public class PlaybackService extends Service {

    private static final String TAG = PlaybackService.class.getSimpleName();

    @IntDef({MediaType.RINGTONE, MediaType.DIAL_TONE, MediaType.DISCONNECTED_TONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface MediaType {
        int RINGTONE = 0;
        int DIAL_TONE = 1;
        int DISCONNECTED_TONE = 2;
    }

    private static final long[] VIBRATOR_PATTERN = {0, 1000, 1000};

    private MediaPlayer mMediaPlayer;
    private Uri mRingtoneUri;
    private Vibrator mVibrator;
    private boolean mVibratorState = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                this, RingtoneManager.TYPE_RINGTONE);
        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return super.onStartCommand(null, flags, startId);

        if (MosaicIntent.ACTION_PLAY_MEDIA.equals(intent.getAction())
                && intent.hasExtra(MosaicIntent.EXTRA_MEDIA_TYPE)) {
            @MediaType int mediaType = intent.getIntExtra(MosaicIntent.EXTRA_MEDIA_TYPE, 666);
            handlePlayMedia(mediaType);
            return START_STICKY;
        }
        else if (MosaicIntent.ACTION_STOP_MEDIA.equals(intent.getAction())) {
            handleStopMedia();
            return START_NOT_STICKY;
        }

        return super.onStartCommand(null, flags, startId);
    }

    synchronized void handlePlayMedia(@MediaType int mediaType) {
        mMediaPlayer = new MediaPlayer();
        switch (mediaType) {
            case MediaType.RINGTONE:
                mVibrator.vibrate(VIBRATOR_PATTERN, 0);
                mVibratorState = true;
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

    synchronized void handleStopMedia() {
        if (mVibratorState) {
            mVibratorState = false;
            mVibrator.cancel();
        }
        if (mMediaPlayer != null && (mMediaPlayer.isPlaying() || mMediaPlayer.isLooping())) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
        }
        mMediaPlayer = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
