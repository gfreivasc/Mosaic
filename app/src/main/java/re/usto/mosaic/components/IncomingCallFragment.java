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

import re.usto.mosaic.CallActivity;
import re.usto.mosaic.R;
import re.usto.mosaic.engine.MosaicIntent;
import re.usto.mosaic.engine.MosaicService;

/**
 * Created by gabriel on 03/04/17.
 */

public class IncomingCallFragment extends Fragment {

    private static final String TAG = IncomingCallFragment.class.getSimpleName();

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
        View rootView = inflater.inflate(R.layout.fragment_incoming_call, container, false);
        rootView.setTag(TAG);

        Button acceptButton = (Button) rootView.findViewById(R.id.acceptButton);
        Button declineButton = (Button) rootView.findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(
                        new MosaicIntent().acceptCall(getActivity())
                );
                getActivity().getFragmentManager().beginTransaction().replace(
                        R.id.call_layout, new OnCallFragment()
                ).commit();
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(
                        new MosaicIntent().declineCall(getActivity())
                );
            }
        });

        return rootView;
    }

    class CallDisconnectedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
