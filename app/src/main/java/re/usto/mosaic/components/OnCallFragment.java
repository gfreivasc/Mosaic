package re.usto.mosaic.components;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import re.usto.mosaic.ExampleCallActivity;
import re.usto.mosaic.R;
import re.usto.mosaic.engine.CallActivity;
import re.usto.mosaic.engine.MosaicIntent;
import re.usto.mosaic.engine.PlaybackService;

/**
 * @author gabriel
 */

public class OnCallFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = OnCallFragment.class.getSimpleName();
    private boolean mDialing = false;
    private boolean mDisconnected = false;
    private boolean mDismissed = false;
    private PowerManager.WakeLock mProximityWakeLock;
    private OnCallReceiver mReceiver;
    private boolean mMuteMic = false;
    private boolean mSpeaker = false;
    private ImageView mToggleMuteMic;
    private ImageView mToggleSpeaker;
    private TextView mCallStateView;
    private int mTimeCounter = 0;
    private Timer mTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(CallActivity.DIALING))
            mDialing = getArguments().getBoolean(CallActivity.DIALING);

        PowerManager pm = (PowerManager) getActivity().getSystemService(
                AppCompatActivity.POWER_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mProximityWakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, TAG);
        }
        else {
            int WAKE_LOCK = -1;
            try {
                WAKE_LOCK = PowerManager.class.getClass().getField(
                        "PROXIMITY_SCREEN_OFF_WAKE_LOCK"
                ).getInt(-1);
            }
            catch (Throwable ignored) { }
            mProximityWakeLock = pm.newWakeLock(WAKE_LOCK, TAG);
        }

        mProximityWakeLock.acquire();
        mReceiver = new OnCallReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mReceiver,
                new MosaicIntent.FilterBuilder()
                        .addDisconnectedCallAction()
                        .addConfirmedCallAction()
                        .addToggleMuteMicAction()
                        .addToggleSpeakerAction()
                        .build()
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_call, container, false);

        Button hangupCall = (Button) rootView.findViewById(R.id.hangupCall);
        TextView remoteUriView = (TextView) rootView.findViewById(R.id.callRemoteUri);
        mToggleMuteMic = (ImageView) rootView.findViewById(R.id.toggleMuteMic);
        mToggleSpeaker = (ImageView) rootView.findViewById(R.id.toggleSpeaker);
        mCallStateView = (TextView) rootView.findViewById(R.id.callState);

        if (mDialing) {
            mToggleMuteMic.setVisibility(View.INVISIBLE);
            mCallStateView.setText(getString(R.string.state_calling));
        }

        String remoteUri = ((ExampleCallActivity)getActivity()).getRemoteUri();
        remoteUriView.setText(remoteUri != null ? remoteUri
                : getActivity().getString(R.string.remote_uri_unknown));

        hangupCall.setOnClickListener(this);

        mToggleMuteMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new MosaicIntent().toggleMuteMic(getActivity()));
            }
        });

        mToggleSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new MosaicIntent().toggleSpeaker(getActivity()));
            }
        });
        return rootView;
    }

    class OnCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case MosaicIntent.ACTION_DISCONNECTED_CALL:
                    mTimer.cancel();
                    if (mDismissed) {
                        getActivity().finish();
                    } else {
                        getActivity().startService(new MosaicIntent().playMedia(
                                getActivity(), PlaybackService.MediaType.DISCONNECTED_TONE
                        ));
                        mDisconnected = true;
                        mCallStateView.setText(String.format(
                                Locale.getDefault(),
                                getString(R.string.state_disconnected),
                                mTimeCounter / 60, mTimeCounter % 60));
                        mToggleMuteMic.setVisibility(View.GONE);
                    }
                    break;

                case MosaicIntent.ACTION_CONFIRMED_CALL:
                    mDialing = false;
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);
                    mToggleMuteMic.setVisibility(View.VISIBLE);
                    break;

                case MosaicIntent.ACTION_TOGGLE_MUTE_MICROPHONE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mMuteMic = !mMuteMic;
                        mToggleMuteMic.setImageDrawable(getActivity().getDrawable(
                                mMuteMic ? R.drawable.ic_mic_off_black_24dp
                                        : R.drawable.ic_mic_black_24dp));
                    }
                    break;

                case MosaicIntent.ACTION_TOGGLE_SPEAKER:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mSpeaker = !mSpeaker;
                        mToggleSpeaker.setImageDrawable(getActivity().getDrawable(
                                mSpeaker ? R.drawable.ic_volume_up_black_24dp
                                        : R.drawable.ic_volume_down_black_24dp));
                    }
                    break;
            }
        }
    }

    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallStateView.setText(
                            String.format(Locale.getDefault(),
                                    "%02d:%02d", mTimeCounter / 60, mTimeCounter % 60)
                    );
                    mTimeCounter++;
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        if (!mDisconnected) {
            getActivity().startService(
                    new MosaicIntent().hangupCall(getActivity())
            );
            mDismissed = true;
        }
        else {
            getActivity().startService(new MosaicIntent().stopMedia(getActivity()));
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);

        AudioManager audioManager = (AudioManager)getActivity()
                .getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isSpeakerphoneOn()) audioManager.setSpeakerphoneOn(false);

        mProximityWakeLock.release();
        super.onDestroy();
    }
}
