package com.example.gestor_tab;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

class Map extends Activity {
    MapFragment mf;
    GoogleMap gm;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mf = MapFragment.newInstance();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(android.R.id.content, mf);
        ft.commit();
    }
    public void onResume() {
        super.onResume();

        // created map
        //gm = mf.getMap();
        gm.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        gm.setMyLocationEnabled(true);

        // set latitude and longitude
        LatLng ltn = new LatLng(40, 135);

        // create marker
        marker = new MarkerOptions();
        marker.position(ltn);

        // operation can be done, when click happens
        gm.setOnMapClickListener(new SampleMapClickListener());
    }

    class SampleMapClickListener implements GoogleMap.OnMapClickListener {

        @Override
        public void onMapClick(LatLng ltn) {
            marker = new MarkerOptions();
            marker.position(ltn);
            //gm = mf.getMap();
            // add marker
            gm.addMarker(marker);

        }

    }
}
