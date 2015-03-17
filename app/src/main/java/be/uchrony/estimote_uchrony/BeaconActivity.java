package be.uchrony.estimote_uchrony;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.connection.BeaconConnection;

import java.util.List;

/**
 * Activité pour l'affichage d'un Ibeacon et ces caractéristiques.
 *
 * @author  Chetouani Abdelhalim
 * @version 0.1
 */
public class BeaconActivity extends Activity{

    private BeaconConnection beaconConnection;
    private Beacon beacon;
    private BeaconConnection.BeaconCharacteristics beaconCaract;

    TextView batterie;
    EditText major;
    EditText minor;
    EditText uuid;
    TextView rssi;
    EditText txPower;
    EditText frequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon_layout);

        batterie = (TextView) findViewById(R.id.batterie);
        major = (EditText) findViewById(R.id.beacon_major);
        minor = (EditText) findViewById(R.id.beacon_minor);
        uuid = (EditText) findViewById(R.id.beacon_uuid);
        rssi = (TextView) findViewById(R.id.beacon_rssi);
        txPower = (EditText) findViewById(R.id.beacon_tx_power);
        frequence = (EditText) findViewById(R.id.frequence);

        uuid.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                beaconConnection.writeProximityUuid(uuid.getText().toString(), fonctionCallBack());
                return true;
            }
        });

        major.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                beaconConnection.writeMajor(Integer.parseInt(major.getText().toString()), fonctionCallBack());
                return true;
            }
        });

        minor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                beaconConnection.writeMinor(Integer.parseInt(minor.getText().toString()), fonctionCallBack());
                return true;
            }
        });

        txPower.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                beaconConnection.writeBroadcastingPower(Integer.parseInt(txPower.getText().toString()), fonctionCallBack());
                return true;
            }
        });

        frequence.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                beaconConnection.writeAdvertisingInterval(Integer.parseInt(frequence.getText().toString()), fonctionCallBack());
                return true;
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuration_beacon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.configurer) {
            beaconConnection.writeMajor(Integer.parseInt(major.getText().toString()), fonctionCallBack());
            beaconConnection.writeMinor(Integer.parseInt(minor.getText().toString()), fonctionCallBack());
        }
        return  true;
    }

    private BeaconConnection.WriteCallback fonctionCallBack() {
        return new BeaconConnection.WriteCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lancerToast("Changement fait");
                    }
                });
            }

            @Override
            public void onError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lancerToast("Changement erreur");
                    }
                });
            }
        };
    }


    private void initConnection() {
        final TextView etatConnection = (TextView) findViewById(R.id.etat_connection);
        beaconConnection = new BeaconConnection(BeaconActivity.this,beacon,new BeaconConnection.ConnectionCallback() {

            @Override
            public void onAuthenticated(final BeaconConnection.BeaconCharacteristics beaconCharacteristics) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etatConnection.setText("Connection réussi");
                        Log.d("TAGR", "Connection réussi");
                        beaconCaract = beaconCharacteristics;
                        miseAJourLayout();
                    }
                });
            }

            @Override
            public void onAuthenticationError() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etatConnection.setText("Connection échoué");
                        Log.d("TAGR", "Connection échoué");
                    }
                });
            }

            @Override
            public void onDisconnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        etatConnection.setText("Déconnection");
                        Log.d("TAGR","Déconnection");
                    }
                });
            }
        });
        beaconConnection.authenticate();

    }

    private void lancerToast(String texte) {
        Toast.makeText(this, texte, Toast.LENGTH_LONG).show();
    }

    /**
     * Met a jour l'affichage des caractérisques du Ibeacon
     *
     */
    private void miseAJourLayout() {
        major.setText(""+beacon.getMajor());
        minor.setText(""+beacon.getMinor());
        uuid.setText(beacon.getProximityUUID().toString());
        txPower.setText("" + beacon.getMeasuredPower());
        frequence.setText("" + beaconCaract.getAdvertisingIntervalMillis());
        rssi.setText(""+beacon.getRssi());
        batterie.setText(beaconCaract.getBatteryPercent().toString()+" %");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconConnection.close();
    }

    /**
     * Donne la proximité du Ibeacon (Loin, trés proche et proche)
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
