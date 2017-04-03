package re.usto.mosaic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import re.usto.mosaic.engine.MosaicIntent;
import re.usto.mosaic.engine.MosaicService;

public class IncomingCallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        Button acceptButton = (Button) findViewById(R.id.acceptButton);
        Button declineButton = (Button) findViewById(R.id.declineButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomingCallActivity.this, MosaicService.class);
                intent.setAction(MosaicIntent.ACTION_ACCEPT_CALL);
                startService(intent);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IncomingCallActivity.this, MosaicService.class);
                intent.setAction(MosaicIntent.ACTION_DECLINE_CALL);
                startService(intent);
                finish();
            }
        });
    }
}
