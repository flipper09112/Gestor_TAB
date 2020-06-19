package com.example.gestor_tab.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.database.ListaClientesBaseUtil;
import com.example.gestor_tab.geocode.ReverseGeoCode;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;
import com.github.pires.obd.commands.control.IgnitionMonitorCommand;
import com.github.pires.obd.commands.fuel.ConsumptionRateCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.commands.temperature.AmbientAirTemperatureCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnsupportedCommandException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import jxl.read.biff.BiffException;

import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogsEncomendas;
import static java.lang.Thread.sleep;

public class SnoozeActivity extends AppCompatActivity {

    private ReverseGeoCode reverseGeoCode;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ClientsManager clientsManager;


    private BluetoothSocket socket = null;

    private RPMCommand rpmCommand;
    private ConsumptionRateCommand consumptionRateCommand;
    private DistanceSinceCCCommand distanceSinceCCCommand;
    private AsyncTask asyncTask;
    private IgnitionMonitorCommand ignitionMonitorCommand;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snooze);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        try {
            reverseGeoCode = new ReverseGeoCode(getResources().openRawResource(R.raw.pt), true);

        } catch (IOException e) {
            final TextView localidade = findViewById(R.id.localidade);
            localidade.setText("Error");
        }

        ListaClientesBaseUtil db = new ListaClientesBaseUtil(this);

        try {
            clientsManager = db.getData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        setGPSData();
        setEncomendas();

        asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                while (true) {
                    try {
                        //Toast.makeText(SnoozeActivity.this, "Try connect to device", Toast.LENGTH_SHORT).show();
                        if (getDataOBDII()) {
                            //connected
                            //Toast.makeText(SnoozeActivity.this, "Device connected!", Toast.LENGTH_SHORT).show();
                            return null;
                        } else {
                            //try again
                        }

                        if (isCancelled()) return null;
                        sleep(5*60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        asyncTask.execute();

    }

    private boolean getDataOBDII() {

        String btAddress = null;

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                if (device.getName().equals("OBDII")) {
                    btAddress = (String) device.getAddress();
                } else {
                    //TODO erro
                }
                /*deviceStrs.add(device.getName() + "\n" + device.getAddress());
                devices.add(device.getAddress());*/
            }
        }

        if (btAddress!=null) {
            BluetoothDevice device = btAdapter.getRemoteDevice(btAddress);

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


            try {
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                socket.connect();

                EchoOffCommand echoOffCommand = new EchoOffCommand();
                echoOffCommand.run(socket.getInputStream(), socket.getOutputStream());
                echoOffCommand.getFormattedResult();

                LineFeedOffCommand lineFeedOffCommand = new LineFeedOffCommand();
                lineFeedOffCommand.run(socket.getInputStream(), socket.getOutputStream());

                TimeoutCommand timeoutCommand = new TimeoutCommand(125);
                timeoutCommand.run(socket.getInputStream(), socket.getOutputStream());

                SelectProtocolCommand selectProtocolCommand = new SelectProtocolCommand(ObdProtocols.AUTO);
                selectProtocolCommand.run(socket.getInputStream(), socket.getOutputStream());

                AmbientAirTemperatureCommand ambientAirTemperatureCommand = new AmbientAirTemperatureCommand();
                ambientAirTemperatureCommand.run(socket.getInputStream(), socket.getOutputStream());

                rpmCommand = new RPMCommand();
                consumptionRateCommand = new ConsumptionRateCommand();
                distanceSinceCCCommand = new DistanceSinceCCCommand();
                ignitionMonitorCommand = new IgnitionMonitorCommand();


            } catch (IOException e) {
                e.printStackTrace();
                return false;

            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }
        } else {
            //TODO error
            return false;
        }

        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
        asyncTask.cancel(true);
        Log.i("Debug on Pause", "onPause, done");
    }


    private void setEncomendas() {

        final ListView listaDeEncomendas = (ListView) findViewById(R.id.lista_encomendas);
        RegistosManager registosManager = getAllLogsEncomendas(getApplicationContext(), new Date());

        ArrayList<String> infos = new ArrayList<>();
        ArrayList<Registo> registoArrayList = registosManager.getRegistosEncomendasThisDaySort(new Date(), clientsManager.getClientsList());

        if (registoArrayList.size() == 0) {
            infos.add("Nenhuma encomenda para o dia de hoje!");
        }

        for (int i = 0; i < registoArrayList.size(); i++) {
            infos.add(
                    "Cliente: " +
                            clientsManager.getClienteId(registoArrayList.get(i).getId()).toString().replaceAll("\n", "\n               ") +
                            "\n" +
                            registoArrayList.get(i).toStringEncomenda());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(
                SnoozeActivity.this,
                android.R.layout.simple_list_item_1,
                infos) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);

                return view;
            }
        };

        listaDeEncomendas.setAdapter(adapter);
    }

    private void setGPSData() {
        final TextView local = findViewById(R.id.velocidade);
        final TextView localidade = findViewById(R.id.localidade);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                System.out.println("Tela de descanço GPS: " + location.getLatitude() + " " + location.getLongitude());
                float speed = (float) (location.getSpeed() * 3.6);
                local.setText(String.format("%.0f Km/h", speed));

                reverseGeoCode.nearestPlace(lat, lng);
                localidade.setText(reverseGeoCode.nearestPlace(lat, lng).toString());

                getIgnition();
                setRPM();
                setFuelConsuption();
                setDistance();

            }

            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0, locationListener);
    }

    private void getIgnition() {
        final TextView rpm = findViewById(R.id.ignition);
        if (ignitionMonitorCommand != null) {
            try {
                ignitionMonitorCommand.run(socket.getInputStream(), socket.getOutputStream());
                rpm.setText(String.valueOf(ignitionMonitorCommand.getFormattedResult()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoDataException e) {
                e.printStackTrace();
                closeSocket();
                ignitionMonitorCommand = null;
            }
        } else {
            rpm.setText("Not connected");
        }
    }

    private void setDistance() {

        final TextView rpm = findViewById(R.id.distance);
        if (distanceSinceCCCommand != null) {
            try {
                distanceSinceCCCommand.run(socket.getInputStream(), socket.getOutputStream());
                rpm.setText(String.valueOf(distanceSinceCCCommand.getFormattedResult()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnsupportedCommandException e) {

            } catch (NoDataException e) {
                e.printStackTrace();
                closeSocket();
                distanceSinceCCCommand = null;
            }
        } else {
            rpm.setText("Not connected");
        }
    }

    private void setFuelConsuption() {

        final TextView rpm = findViewById(R.id.fuel);
        if (consumptionRateCommand != null) {
            try {
                consumptionRateCommand.run(socket.getInputStream(), socket.getOutputStream());
                rpm.setText(String.valueOf(consumptionRateCommand.getFormattedResult()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (UnsupportedCommandException e) {

            } catch (NoDataException e) {
                e.printStackTrace();
                closeSocket();
                consumptionRateCommand = null;
            }
        } else {
            rpm.setText("Not connected");
        }
    }

    private void setRPM() {
        final TextView rpm = findViewById(R.id.rpm);
        if (rpmCommand != null) {
            try {
                rpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                rpm.setText(String.valueOf(rpmCommand.getFormattedResult()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (NoDataException e) {
                e.printStackTrace();
                closeSocket();
                rpmCommand = null;
            }
        } else {
            rpm.setText("Not connected");
        }
    }

    private void closeSocket() {
        try {
            socket.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();

        locationManager.removeUpdates(locationListener);
        asyncTask.cancel(true);

        /*Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);*/
        finish();
    }
}
