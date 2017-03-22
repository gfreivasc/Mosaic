package re.usto.mosaic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

import static re.usto.mosaic.Mosaic.ep;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        creatingSipTransport();
        
    }



    public void creatingSipTransport(){
        try {
            TransportConfig sipTpConfig = new TransportConfig();
            sipTpConfig.setPort(5060);
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, sipTpConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
