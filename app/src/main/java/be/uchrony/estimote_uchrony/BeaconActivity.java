package be.uchrony.estimote_uchrony;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;
import com.estimote.sdk.connection.BeaconConnection;

/**
 * Activité pour l'affichage d'un Ibeacon et ces caractéristiques.
 *
 * @author  Chetouani Abdelhalim
 * @version 0.1
 */
public class BeaconActivity extends Activity{

    BeaconConnection beaconConnection;
    Beacon beacon;
    BeaconConnection.BeaconCharacteristics beaconCaract;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_layout);

        // active le retour à l'activity parent MainActivity
        getActionBar().setDisplayHomeAsUpEnabled(true);
        // met en couleur l'action bar
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.Orange)));
        // on récupère le beaconDevice que MainActivity nous à passer
        Bundle extra = getIntent().getExtras();
        Object o = extra.get(MainActivity.EXTRA_BEACON);
        if ( o != null) {
            beacon = (Beacon) o;
            // affiche les informations liée à ce Ibeacon
            initConnection();
        }
    }

    private void initConnection() {
        final TextView etatConnection = (TextView) findViewById(R.id.etat_connection);
        beaconConnection = new BeaconConnection(BeaconActivity.this,beacon,new BeaconConnection.ConnectionCallback() {
            @Override
            public void onAuthenticated(BeaconConnection.BeaconCharacteristics beaconCharacteristics) {
                etatConnection.setText("Connection réussi");
                Log.d("TAGR", "Connection réussi");
                beaconCaract = beaconCharacteristics;
            }

            @Override
            public void onAuthenticationError() {
                etatConnection.setText("Connection échoué");
                Log.d("TAGR", "Connection échoué");
            }

            @Override
            public void onDisconnected() {
                etatConnection.setText("Déconnection");
                Log.d("TAGR","Déconnection");
            }
        });
        /*
        beaconConnection.authenticate();
        initActivity();
        beaconConnection.close();*/
    }

    /**
     * Met a jour l'affichage des caractérisques du Ibeacon
     *
     */
    private void initActivity() {
        TextView distance = (TextView) findViewById(R.id.distance);
        TextView macAdresse = (TextView) findViewById(R.id.mac_adresse);
        TextView batterie = (TextView) findViewById(R.id.batterie);
        TextView major = (TextView) findViewById(R.id.beacon_major);
        TextView minor = (TextView) findViewById(R.id.beacon_minor);
        TextView nom = (TextView) findViewById(R.id.beacon_nom);
        TextView uuid = (TextView) findViewById(R.id.beacon_uuid);
        TextView rssi = (TextView) findViewById(R.id.beacon_rssi);
        TextView txPower = (TextView) findViewById(R.id.beacon_tx_power);

        macAdresse.setText(beacon.getMacAddress());
        major.setText(""+beacon.getMajor());
        minor.setText(""+beacon.getMinor());
        nom.setText(beacon.getName());
        uuid.setText(beacon.getProximityUUID().toString());
        txPower.setText("" + beacon.getMeasuredPower());
        rssi.setText(""+beacon.getRssi());
        distance.setText(String.format("%.2f mètre", Utils.computeAccuracy(beacon)));
       // batterie.setText(beaconCaract.getBatteryPercent().toString()+" %");
    }

    /**
     * Donne la proximité du Ibeacon (Loin, trés proche et proche)
     * @param bd le Ibeacon concerné
     * @return la proximité sous forme de chaine de caractére
     */
    /*private String getDistance(Beacon bd) {
        if (bd.getProximity() == Proximity.FAR) {
            return "LOIN";
        } else if (bd.getProximity() == Proximity.IMMEDIATE) {
            return "Très proche";
        } else {
            return "Proche";
        }
    }*/

}
