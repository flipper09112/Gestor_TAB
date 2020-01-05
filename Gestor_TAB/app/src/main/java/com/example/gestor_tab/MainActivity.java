package com.example.gestor_tab;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.database.DataBaseUtil;
import com.example.gestor_tab.geocode.ReverseGeoCode;
import com.example.gestor_tab.notifications.AlarmReceiver;
import com.example.gestor_tab.notifications.NotificationActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import jxl.read.biff.BiffException;

public class MainActivity extends AppCompatActivity {

    private ClientsManager clientsManager;

    private NotificationManager mNotificationManager;

    ReverseGeoCode reverseGeoCode;

    private final ArrayList<Float> listSpeed = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final File excel = new File(getExternalFilesDir(null), "MeuArquivoXLS.xls");
        final DataBaseUtil db;

        Intent intent = getIntent();
        final Cliente cliente = (Cliente) intent.getSerializableExtra("Cliente");
        int refresh = intent.getIntExtra("refresh", -1);
        int position = 0;

        try {
            db = new DataBaseUtil(excel);
            this.clientsManager = db.getData();

            if (this.clientsManager == null) {
                //TODO mensagem erro
            }

            if (cliente != null) {
                position = clientsManager.getPosition(cliente);
                this.clientsManager.replaceCliente(position, cliente);
                db.guarda_em_ficheiro(clientsManager);

                if (refresh == 1) {
                    Intent myIntent = new Intent(getApplicationContext(), ClienteActivity.class);
                    myIntent.putExtra("Cliente", cliente);
                    startActivity(myIntent);
                }
            }


            final ListView listaDeClientes = (ListView) findViewById(R.id.lista);
            ArrayAdapter<Cliente> adapter = new ArrayAdapter<Cliente>(this, android.R.layout.simple_list_item_1, clientsManager.getClientsList()) {
                @Override
                public View getView(int pos, View convertView, ViewGroup parent){

                    View view = super.getView(pos, convertView, parent);

                    TextView ListItemShow = (TextView) view.findViewById(android.R.id.text1);

                    if (clientsManager.getCliente(pos).getTipoPagamento().equals("D"))
                        ListItemShow.setBackgroundColor(Color.parseColor("#0060ff"));

                    else if (clientsManager.getCliente(pos).getTipoPagamento().equals("M"))
                        ListItemShow.setBackgroundColor(Color.parseColor("#00ff00"));

                    else if (clientsManager.getCliente(pos).getTipoPagamento().equals("S"))
                        ListItemShow.setBackgroundColor(Color.parseColor("#ffff00"));

                    else if (clientsManager.getCliente(pos).getTipoPagamento().equals("LS"))
                        ListItemShow.setBackgroundColor(Color.parseColor("#ffa500"));
                    else
                        ListItemShow.setBackgroundColor(Color.parseColor("#ffffff"));



                    return view;
                }
            };

            listaDeClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent myIntent = new Intent(getApplicationContext(), ClienteActivity.class);
                    myIntent.putExtra("Cliente", clientsManager.getCliente(i));
                    startActivity(myIntent);
                }
            });

            listaDeClientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               final int pos, long id) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Escolha uma opção");
                    final String[] options = {"Adicionar um cliente na posição anterior", "Adicionar um cliente na posição seguinte", "Remover cliente"};
                    final int[] indice = new int[1];
                    builder.setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            indice[0] = item;
                        }
                    });
                    builder.setPositiveButton("Seguir", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            Intent intent;
                            Date date = new Date();
                            float[] despesa = {0, 0, 0, 0, 0, 0, 0, 0};
                            Cliente newCliente = new Cliente("Default", clientsManager.getId(), date, "S", despesa);
                            switch (indice[0]) {
                                case 0:
                                    clientsManager.addCliente(pos, newCliente);
                                    db.guarda_em_ficheiro(clientsManager);

                                    intent = new Intent(getApplicationContext(), EditClientActivity.class);
                                    intent.putExtra("Cliente", newCliente);
                                    startActivity(intent);
                                    break;
                                case 1:
                                    clientsManager.addCliente(pos + 1, newCliente);
                                    db.guarda_em_ficheiro(clientsManager);

                                    intent = new Intent(getApplicationContext(), EditClientActivity.class);
                                    intent.putExtra("Cliente", newCliente);
                                    startActivity(intent);
                                    break;
                                case 2:
                                    clientsManager.removeCliente(pos);
                                    db.guarda_em_ficheiro(clientsManager);

                                    Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(refresh);
                                    finish();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    AlertDialog actions;
                    actions = builder.create();
                    actions.show();
                    return true;
                }
            });


            listaDeClientes.setAdapter(adapter);

            final int finalPosition = position;
            listaDeClientes.post(new Runnable() {
                public void run() {
                    listaDeClientes.smoothScrollToPosition(finalPosition);
                }
            });


            setNotificationScheduler(getApplicationContext());
            //issueNotification();
            setEncomendas();

            //ReverseGeoCode reverseGeoCode = new ReverseGeoCode(new FileInputStream(getExternalFilesDir(null)+"/PT.txt"), true);
            try {
                reverseGeoCode = new ReverseGeoCode(new FileInputStream(getExternalFilesDir(null)+"/PT.txt"), true);
                setGPSData();
            } catch (IOException e) {
                final TextView yourTextView = findViewById(R.id.velocidade);
                final TextView mediatxt = findViewById(R.id.media);
                final TextView localidade = findViewById(R.id.localidade);

                yourTextView.setText("Waiting");
                mediatxt.setText("Waiting");
                localidade.setText("Waiting");
            }

        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void setGPSData() {
        final TextView yourTextView = findViewById(R.id.velocidade);
        final TextView mediatxt = findViewById(R.id.media);
        final TextView localidade = findViewById(R.id.localidade);
        mediatxt.setText("0");

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                //velocidade
                float speed = (float) (location.getSpeed() * 3.6);
                yourTextView.setText(String.format("%.0f Km/h", speed));

                //media
                listSpeed.add(speed);
                float sum = sumArray(listSpeed);
                float med = sum / listSpeed.size();
                mediatxt.setText(String.format("%.1f Km/h", med));

                //localidade
                List<Address> addresses = null;
                if (isNetworkAvailable()) {
                    Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        addresses = geo.getFromLocation(lat, lng, 1);

                    } catch (IOException e) {
                        localidade.setText("ERROR");
                    }

                    if (addresses == null) {
                        if (!localidade.getText().equals("ERROR"))
                                localidade.setText("Waiting for Location");

                    } else {
                        if (addresses.size() > 0) {
                            localidade.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                        }
                    }
                } else {
                    reverseGeoCode.nearestPlace(lat, lng);
                    localidade.setText(reverseGeoCode.nearestPlace(lat, lng).toString());
                }


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
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                0, locationListener);


    }

    private float sumArray(ArrayList<Float> listSpeed) {
        float total = 0;
        for (float f : listSpeed) {
            total += f;
        }
        return total;
    }


    private void setEncomendas() {
        TextView textView = findViewById(R.id.info);
        textView.setText("Encomendas para entregar hoje!");

        final ListView listaDeEncomendas = (ListView) findViewById(R.id.listaEncomendas);
        RegistosManager registosManager = DataBaseUtil.getAllLogsEncomendas(getApplicationContext(), new Date());

        ArrayList<String> infos = new ArrayList<>();

        ArrayList<Registo> registoArrayList = registosManager.getRegistosEncomendasThisDay(new Date());

        if (registoArrayList.size() == 0) {
            infos.add("Nenhuma encomenda para o dia de hoje!");
        }

        for (int i = 0; i < registoArrayList.size(); i++) {
            infos.add(
                    "Cliente: " +
                    clientsManager.getClienteName(registoArrayList.get(i).getId()).toUpperCase() +
                            "\n" +
                            registoArrayList.get(i).toStringEncomenda());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(
                MainActivity.this,
                android.R.layout.simple_list_item_1,
                infos);

        listaDeEncomendas.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.encomenda:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                return true;
            case R.id.upload_item:
                // do your code
                return true;
            case R.id.copy_item:
                // do your code
                return true;
            case R.id.print_item:
                // do your code
                return true;
            case R.id.share_item:
                // do your code
                return true;
            case R.id.bookmark_item:
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setNotificationScheduler(Context context) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 10);
        calendar.set(Calendar.MINUTE, 45);

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        ArrayList<String> encomendas = new ArrayList<>();
        encomendas.add("Test");
        intentAlarm.putStringArrayListExtra("Encomendas", encomendas);
        System.out.println("calling Alarm receiver ");
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //set the notification to repeat every fifteen minutes
        // set unique id to the pending item, so we can call it when needed
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime(), 60*1000, pi);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

    }


    public void resetAverage(View view) {
        this.listSpeed.clear();
    }


}
