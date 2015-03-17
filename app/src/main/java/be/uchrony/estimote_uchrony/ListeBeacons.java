package be.uchrony.estimote_uchrony;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Abdelhalim on 14/02/2015.
 */
public class ListeBeacons extends BaseAdapter {

    private final static String TAG_DEBUG = "TAG_DEBUG_ListeBeacons";
    private ArrayList<Beacon> beacons;
    private LayoutInflater inflater;

    public ListeBeacons(Context context) {
        Log.d(TAG_DEBUG, "constructeur");
        this.inflater = LayoutInflater.from(context);
        this.beacons = new ArrayList<Beacon>();
    }
    public void replacerLaListe(Collection<Beacon> nouveausBeacons) {
        Log.d(TAG_DEBUG,"remplacerLaliste");
        this.beacons.clear();
        this.beacons.addAll(nouveausBeacons);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        Log.d(TAG_DEBUG,"getCount");
        return beacons.size();
    }

    @Override
    public Beacon getItem(int position) {
        Log.d(TAG_DEBUG,"getItem");
        return beacons.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG_DEBUG,"getItemId");
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d(TAG_DEBUG,"getView");
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view);
        return view;
    }

    private void bind(Beacon beacon, View view) {
        Log.d(TAG_DEBUG,"bind");
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.macadresse.setText(String.format("%s", beacon.getMacAddress()));
        holder.major.setText(Integer.toString(beacon.getMajor()));
        holder.minor.setText(Integer.toString(beacon.getMinor()));
        holder.mpower.setText(Integer.toString(beacon.getMeasuredPower()));
        holder.rssi.setText(Integer.toString(beacon.getRssi()));
        holder.uuid.setText(beacon.getProximityUUID());
        holder.nomBeacon.setText(beacon.getName());
        holder.distance.setText(String.format("%.2f mètre", Utils.computeAccuracy(beacon)));
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        Log.d(TAG_DEBUG,"inflateIfRequired");
        if (view == null) {
            view = inflater.inflate(R.layout.beacon_info_layout, null);
            view.setTag(new ViewHolder(view));
        }
        return view;
    }

    static class ViewHolder {
        final TextView macadresse;
        final TextView major;
        final TextView minor;
        final TextView mpower;
        final TextView rssi;
        final TextView uuid;
        final TextView nomBeacon;
        final TextView distance;

        ViewHolder(View view) {
            Log.d(TAG_DEBUG,"zoneINI");
            macadresse = (TextView) view.findViewById(R.id.macadresse);
            major = (TextView) view.findViewById(R.id.major);
            minor = (TextView)view.findViewById(R.id.minor);
            mpower = (TextView) view.findViewById(R.id.mpower);
            rssi = (TextView) view.findViewById(R.id.rssi);
            uuid = (TextView) view.findViewById(R.id.uuid);
            nomBeacon = (TextView) view.findViewById(R.id.nombeacon);
            distance = (TextView) view.findViewById(R.id.distanceBeacon);
        }
    }
}

