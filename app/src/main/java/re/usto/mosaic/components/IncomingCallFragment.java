package re.usto.mosaic.components;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
                getActivity().finish();
            }
        });

        return rootView;
    }
}
