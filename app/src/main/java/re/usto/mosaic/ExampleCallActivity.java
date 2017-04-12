package re.usto.mosaic;

import android.os.Bundle;

import re.usto.mosaic.engine.CallActivity;
import re.usto.mosaic.engine.MosaicIntent;

public class ExampleCallActivity extends CallActivity {

    private String mRemoteUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_call);

        if (getIntent().hasExtra(MosaicIntent.EXTRA_CALL_INFO))
            mRemoteUri = getIntent().getStringExtra(MosaicIntent.EXTRA_CALL_INFO);
    }

    public String getRemoteUri() {
        return mRemoteUri;
    }
}
