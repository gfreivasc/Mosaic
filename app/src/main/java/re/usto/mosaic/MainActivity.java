package re.usto.mosaic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import re.usto.mosaic.engine.MosaicService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button calling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MosaicService.class);
        intent.putExtra(MosaicService.USER_KEY, 7);
        startService(intent);

        calling = (Button) findViewById(R.id.button);

        calling.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

    }
}
