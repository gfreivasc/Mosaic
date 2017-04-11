package re.usto.mosaic.components;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import re.usto.mosaic.CallActivity;
import re.usto.mosaic.R;
import re.usto.mosaic.engine.MosaicIntent;

/**
 * Created by gabriel on 03/04/17.
 */

public class OnCallFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_on_call, container, false);

        Button hangupCall = (Button) rootView.findViewById(R.id.hangupCall);

        hangupCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CallActivity)getActivity()).getConnected()) {
                    ((CallActivity)getActivity()).setDismissed(true);
                    getActivity().startService(
                            new MosaicIntent().hangupCall(getActivity())
                    );
                }
                else {
                    getActivity().startService(new MosaicIntent().stopMedia(getActivity()));
                    getActivity().finish();
                }
            }
        });
        return rootView;
    }
}
