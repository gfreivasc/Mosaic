package re.usto.mosaic.components;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import re.usto.mosaic.CallActivity;
import re.usto.mosaic.R;
import re.usto.mosaic.engine.MosaicIntent;
import re.usto.mosaic.engine.PlaybackService;

/**
 * Created by gabriel on 03/04/17.
 */

public class OnCallFragment extends Fragment implements View.OnClickListener {

    private boolean mDisconnected = false;
    private boolean mDismissed = false;
    private CallDisconnectedReceiver mReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mReceiver = new CallDisconnectedReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mReceiver,
                new MosaicIntent.FilterBuilder().addDisconnectedCallAction().build()
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_call, container, false);

        Button hangupCall = (Button) rootView.findViewById(R.id.hangupCall);
        TextView remoteUriView = (TextView) rootView.findViewById(R.id.callRemoteUri);

        String remoteUri = ((CallActivity)getActivity()).getRemoteUri();
        remoteUriView.setText(remoteUri != null ? remoteUri
                : getActivity().getString(R.string.remote_uri_unknown));

        hangupCall.setOnClickListener(this);
        return rootView;
    }

    class CallDisconnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDismissed) {
                getActivity().finish();
            }
            else {
                getActivity().startService(new MosaicIntent().playMedia(
                        getActivity(), PlaybackService.MediaType.DISCONNECTED_TONE
                ));
                mDisconnected = true;
            }
        }
    }

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
        super.onDestroy();
    }
}
