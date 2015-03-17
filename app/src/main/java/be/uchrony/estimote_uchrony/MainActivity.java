package be.uchrony.estimote_uchrony;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;


public class MainActivity extends Activity {

    private BeaconManager beaconManager;
    private static int CODE_ACTIVATION_BLUE = 5421;
    static String EXTRA_BEACON = "Extra_beacon";
    private ListeBeacons listeBeacons;
    private final static String TAG_DEBUG = "TAG_DEBUG_MainActivity";
    private static final Region REGION_BEACONS = new Region("rid", null, null, null);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        listeBeacons = new ListeBeacons(this);
        ListView listeVue = (ListView) findViewById(R.id.liste_de_beacons);
        listeVue.setAdapter(listeBeacons);
        // permet d'interagire à chaque clic sur un element de la liste
        listeVue.setOnItemClickListener(getListener());

        // Barre de Menu avec le cercle de chargement
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setSubtitle("En cours de scan...");
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.Orange)));
        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Entre ici a chaque fois qu'on detecte des beacons
                        listeBeacons.replacerLaListe(beacons);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Verifie que le GSM possède le Bluetooth LE
        if (!beaconManager.hasBluetooth()) {
            Toast.makeText(this, "Votre appareil n'a pas le Bluetooth LE", Toast.LENGTH_SHORT).show();
            Log.d(TAG_DEBUG, "Votre appareil n'a pas le Bluetooth LE");
            return;
        }

        // Si le bluetooth n'est pas activer on demande de l'activer
        if (!beaconManager.isBluetoothEnabled()) {
            // lancement de l'activité qui vas faire une demande d'activation du bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Log.d(TAG_DEBUG,"Le bluetooth n'est pas activer <Lancement de l'intent>");
            // on démare une activité plus haut et içi je vais vérifier si elle à réussi ou non
            startActivityForResult(enableBtIntent, CODE_ACTIVATION_BLUE);
        } else {
            Log.d(TAG_DEBUG,"Le bluetooth est  activer");
            // on lance le scan de beacons parceque le blue est déja activé
            LancerScan();
        }
    }

    /**
     * Permet de vérifié comment à finir une activité lancé
     * @param codeRequete code de l'activité lancer
     * @param codeResultat code de résultat du lancement de l'activité
     * @param data
     */
    @Override
    protected void onActivityResult(int codeRequete, int codeResultat, Intent data) {
        // si c'est le resultat de l'activiter activation du bluetooth
        if (codeRequete == CODE_ACTIVATION_BLUE) {
            // je vérifie si le résultat est ok
            if (codeResultat == Activity.RESULT_OK) {
                Log.d(TAG_DEBUG,"Retour de lancement Intent l'activation du blue a réussie");
                // l'activation à réussi alors on lance le scan
                LancerScan();
            } else {
                Toast.makeText(this, "L'activation du bluetooth a échoué", Toast.LENGTH_SHORT).show();
                Log.d(TAG_DEBUG,"Retour de lancement Intent l'activation du blue a échoué");
            }
        }
        super.onActivityResult(codeRequete, codeResultat, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Permet d'ajouter le cercle qui tourne
        MenuItem refreshItem = menu.findItem(R.id.zoneChargement);
        refreshItem.setActionView(R.layout.cercle_chargement);
        return true;
    }

    /**
     * Appeler à la destruction de l'application
     */
    @Override
    protected void onDestroy() {
        // ne pas oublié de deconnecter le beaconManager
        beaconManager.disconnect();
        // et d'éteindre le bluetooth
        BluetoothAdapter.getDefaultAdapter().disable();
        super.onDestroy();
    }

    /**
     * Permet de lancer le Scan de Ibeacons
     */
    private void LancerScan() {
        // on vide la liste de beacons trouvé
        listeBeacons.replacerLaListe(Collections.<Beacon>emptyList());
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(REGION_BEACONS);
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this, "Impossible de lancer le scan",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG_DEBUG, "Impossible de lancer le scan", e);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener getListener() {
        return new AdapterView.OnItemClickListener() {
            // içi on définie ce qu'on vas faire lorsque l'on clic sur un élement
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // on lance l'activité BeaconActivity pour voir les caractéristiques
                Intent intent = new Intent(MainActivity.this,BeaconActivity.class);
                intent.putExtra(EXTRA_BEACON,listeBeacons.getItem(position));
                startActivityForResult(intent,0,null);
            }
        };
    }
}
