package com.example.gestor_tab.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.database.ListaClientesBaseUtil;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;
import com.example.gestor_tab.geocode.GeoCliente;
import com.example.gestor_tab.geocode.ReverseGeoCode;
import com.example.gestor_tab.geocode.ReverseGeoCodeCliente;
import com.example.gestor_tab.activitys.notifications.AlarmReceiver;
import com.example.gestor_tab.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import jxl.read.biff.BiffException;

import static com.example.gestor_tab.database.ListaPrecosBaseUtil.getTabela;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogs;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogsEncomendas;
import static com.example.gestor_tab.database.LogsBaseUtil.getTiposLogs;
import static com.example.gestor_tab.database.LogsBaseUtil.logs;
import static com.example.gestor_tab.database.LogsBaseUtil.saveAllLogs;


public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "101";

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;

    private ClientsManager clientsManager;

    private NotificationManager mNotificationManager;

    private ReverseGeoCode reverseGeoCode;

    private ReverseGeoCodeCliente reverseGeoCodeCliente;

    private ArrayList<Float> listSpeed;

    private ListaClientesBaseUtil db;

    private RegistosManager registosManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                Toast.makeText(getApplicationContext(), "Bem vindo de volta " + user.getEmail() + "!", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            mStorageRef = FirebaseStorage.getInstance().getReference();

            Intent intent = getIntent();
            int position = 0;
            final Cliente cliente = (Cliente) intent.getSerializableExtra("Cliente");
            onRestoreListAverageArray(savedInstanceState);

            int refresh = intent.getIntExtra("refresh", -1);

            db = new ListaClientesBaseUtil(this);
            registosManager = getAllLogs(getApplicationContext());
            try {
                this.clientsManager = db.getData();
            } catch (Resources.NotFoundException e) {
                return;
            }

            if (this.clientsManager == null) {
                setContentView(R.layout.error_template);

                TextView msgErro = findViewById(R.id.textView27);
                msgErro.setText("Nenhum cliente encontrado!");
            }

            //refresh and replace changes
            if (cliente != null) {
                position = clientsManager.getPosition(cliente);
                this.clientsManager.replaceCliente(position, cliente);
                db.guarda_em_ficheiro(clientsManager);

                if (refresh == 1) {
                    Intent myIntent = new Intent(getApplicationContext(), ClienteActivity.class);
                    myIntent.putExtra("Cliente", cliente);
                    startActivity(myIntent);
                    finish();
                }
            }

            try {
                reverseGeoCode = new ReverseGeoCode(getResources().openRawResource(R.raw.pt), true);
//                reverseGeoCode = new ReverseGeoCode(new FileInputStream(getExternalFilesDir(null)+"/pt.txt"), true);
                reverseGeoCodeCliente = new ReverseGeoCodeCliente(clientsManager.getClientsList());
            } catch (IOException e) {
                final TextView localidade = findViewById(R.id.localidade);
                localidade.setText("Error");
            }

            setListaClientes(position);
            //setNotificationScheduler(getApplicationContext());
            //issueNotification();
            setEncomendas();
            setGPSData();

        } catch (IOException | BiffException e) {
            setContentView(R.layout.error_template);

            TextView msgErro = findViewById(R.id.textView27);
            msgErro.setText("Erro na inicialização da main page!");
        } catch (NullPointerException e) {
            setContentView(R.layout.error_template);
            TextView t1=(TextView)findViewById(R.id.textView27);
            t1.setText("Descrição\nAlgum problema encontrado na trasição de conteúdo. \nReinicie a aplicação!");
            System.out.println("----------------------------------------------");
            e.printStackTrace();
        }


    }


    private void setListaClientes(int position) {
        final ListView listaDeClientes = (ListView) findViewById(R.id.lista);
        ArrayAdapter<Cliente> adapter = new ArrayAdapter<Cliente>(this, android.R.layout.simple_list_item_1, clientsManager.getClientsList()) {
            @Override
            public View getView(int pos, View convertView, ViewGroup parent){

                View view = super.getView(pos, convertView, parent);

                TextView ListItemShow = (TextView) view.findViewById(android.R.id.text1);

                if (!clientsManager.getCliente(pos).getAtivo())
                    ListItemShow.setBackgroundColor(Color.parseColor("#ff0000"));
                else if (clientsManager.getCliente(pos).getTipoPagamento().equals("D"))
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
                finish();
            }
        });

        listaDeClientes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int pos, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Escolha uma opção");
                final String[] options = {"Adicionar um cliente na posição anterior", "Adicionar um cliente na posição seguinte", "Remover cliente", "Paragem indeterminada / Recomeço", "Paragem por um periodo de tempo"};
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
                        Intent refresh;
                        switch (indice[0]) {
                            case 0:
                                clientsManager.addCliente(pos, newCliente);
                                db.guarda_em_ficheiro(clientsManager);

                                intent = new Intent(getApplicationContext(), EditClientActivity.class);
                                intent.putExtra("Cliente", newCliente);
                                startActivity(intent);
                                finish();

                                break;
                            case 1:
                                clientsManager.addCliente(pos + 1, newCliente);
                                db.guarda_em_ficheiro(clientsManager);

                                intent = new Intent(getApplicationContext(), EditClientActivity.class);
                                intent.putExtra("Cliente", newCliente);
                                startActivity(intent);
                                finish();

                                break;
                            case 2:
                                clientsManager.removeCliente(getApplicationContext(), pos);
                                db.guarda_em_ficheiro(clientsManager);

                                refresh = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(refresh);
                                finish();
                                break;

                            case 3:
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                Cliente cliente1 = clientsManager.getCliente(pos);
                                String info = "";
                                if (cliente1.getAtivo()) {
                                    info = "Definido periodo de inatividade por um período de tempo indeterminado"
                                            + "-"
                                            + formatter.format(new Date());
                                } else {
                                    info = "Interrupção da inatividade"
                                            + "-"
                                            + formatter.format(new Date());
                                }
                                cliente1.setAtivo();
                                logs(getApplicationContext(), 5, cliente1.getId(), info);
                                db.guarda_em_ficheiro(clientsManager);


                                refresh = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(refresh);
                                finish();
                                break;

                            case 4:
                                Cliente cliente2 = clientsManager.getCliente(pos);
                                pickIntervalDate(cliente2, db);
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
    }

    private void pickIntervalDate(final Cliente cliente2, final ListaClientesBaseUtil db) {
        final Dialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.date_interval_dialog, null);


        final int mYear, mMonth, mDay;

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final EditText startDateEditText = (EditText) view.findViewById(R.id.dpStartDate);
        final EditText endDateEditText = (EditText) view.findViewById(R.id.dpEndDate);

        startDateEditText.setFocusable(false);
        startDateEditText.setFocusableInTouchMode(false);
        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(builder.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDateEditText.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });
        endDateEditText.setFocusable(false);
        endDateEditText.setFocusableInTouchMode(false);
        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(builder.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDateEditText.setText(i2+"/"+(i1+1)+"/"+i);
                    }
                }, mYear, mMonth, mDay).show();
            }
        });

        builder.setView(view)
                // Add action buttons

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            if (startDateEditText.getText().toString().equals("") || endDateEditText.getText().toString().equals("")) {
                                new AlertDialog.Builder(builder.getContext())
                                        .setTitle("Operação impossivel")
                                        .setMessage("Falta selecionar uma data")
                                        .show();
                                return;
                            }

                            Date inicio = formatter.parse(startDateEditText.getText().toString());
                            Date fim = formatter.parse(endDateEditText.getText().toString());

                            if (!inicio.before(fim)){
                                if (!inicio.equals(fim)) {
                                    new AlertDialog.Builder(builder.getContext())
                                            .setTitle("Operação impossivel")
                                            .setMessage("Data de inicio maior que a data final")
                                            .show();
                                    return;
                                }
                            }

                            cliente2.setAtivo(startDateEditText.getText().toString(), endDateEditText.getText().toString());
                            String info = "Definido periodo de inatividade desde "
                                    + startDateEditText.getText().toString()
                                    + " até "
                                    + endDateEditText.getText().toString()
                                    + "-"
                                    + formatter.format(new Date());

                            logs(getApplicationContext(), 5, cliente2.getId(), info);

                            db.guarda_em_ficheiro(clientsManager);
                            Intent refresh = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(refresh);
                            finish();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setTitle("Escolher intervalo de tempo!")
                .setMessage("Escolher intervalo de tempo em que o cliente não pretende gastar nenhum produto.")
                .show();
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
        final RegistosManager registosManager = getAllLogsEncomendas(getApplicationContext(), new Date());
        final ArrayList<Registo> registoArrayList = registosManager.getRegistosEncomendasThisDay(new Date());

        final Uri notification = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.school_alarm);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);

        final boolean[] notificationON = {false};

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
                if (listSpeed == null)
                    listSpeed = new ArrayList<>();

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

                GeoCliente geoCliente =null;
                int position = -1;
                try {
                    geoCliente = reverseGeoCodeCliente.nearestPlace(lat, lng);
                    position = verificaClienteNotificacao(registoArrayList, geoCliente.clientID);
                } catch (NullPointerException e) {
                    //Log.e("No Clients location","No Clients location");
                }

                if (position != -1) {

                    if (distFrom(lat, lng, geoCliente.latitude, geoCliente.longitude) < 100) {

                        if (!notificationON[0]) {

                            notificationON[0] = true;

                            createAlertDialogTurnOffSoundNotification(r, registoArrayList.get(position).toStringEncomenda());

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Cliente")
                                    .setContentText("TEXTO aqui")
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setAutoCancel(true)
                                    .setLights(0xff0000ff, 300, 1000)
                                    .setNumber(3)// this shows a number in the notification dots
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());

                            createNotificationChannel();

                            try {
                                if (!r.isPlaying()){
                                    r.play();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // notificationId is a unique int for each notification that you must define
                            notificationManager.notify(101, builder.build());
                        }

                    } else {
                        notificationON[0] = false;
                        if (r.isPlaying())
                            r.stop();
                    }
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

    private void createAlertDialogTurnOffSoundNotification(final Ringtone r, String s) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle("Desligar som da notificação");

        // set dialog message
        alertDialogBuilder
                .setMessage(s)

                .setPositiveButton("STOP",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close
                        // current activity
                        r.stop();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private int verificaClienteNotificacao(ArrayList<Registo> registoArrayList, int clientID) {

        if (registoArrayList.isEmpty()){
            return -1;
        }
        else {
            for (int i = 0; i < registoArrayList.size(); i++) {
                if (registoArrayList.get(i).getId() == clientID) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notification_channel);
            String description = getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
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
        RegistosManager registosManager = getAllLogsEncomendas(getApplicationContext(), new Date());

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
        //Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.action_search:
                Context context;
                final AlertDialog dialog;
                final AlertDialog.Builder alert = new AlertDialog.Builder(this)
                        .setTitle("Pesquisando")
                        .setMessage("Aguarde um momento...");

                dialog = alert.create();
                dialog.show();

                final LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();

                        int id = -1;
                        try {
                            id = reverseGeoCodeCliente.nearestPlace(lat, lng).clientID;
                        } catch (NullPointerException e) {
                            //("Location", "No one clients with location");
                        }
                        Cliente cliente = clientsManager.getClienteId(id);
                        if (cliente != null) {
                            Intent myIntent = new Intent(getApplicationContext(), ClienteActivity.class);
                            myIntent.putExtra("Cliente", cliente);
                            startActivity(myIntent);
                            finish();
                        } else {
                            dialog.dismiss();
                            alert.setTitle("Resultado");
                            alert.setMessage("Nenhum cliente encontrado. Tente novamente!");
                            alert.setPositiveButton("OK", null);
                            alert.show();
                        }
                        //Toast.makeText(getApplicationContext(),reverseGeoCodeCliente.nearestPlace(lat, lng).toString(),Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d("Status Changed", String.valueOf(status));
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("Provider Enabled", provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("Provider Disabled", provider);
                    }
                };

                // Now first make a criteria with your requirements
                // this is done to save the battery life of the device
                // there are various other other criteria you can search for..
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setAltitudeRequired(false);
                criteria.setBearingRequired(false);
                criteria.setSpeedRequired(false);
                criteria.setCostAllowed(true);
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

                // Now create a location manager
                final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                // This is the Best And IMPORTANT part
                final Looper looper = null;

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    return true;
                }
                locationManager.requestSingleUpdate(criteria, locationListener, looper);
                return true;
            case R.id.encomenda:
                Intent intent = new Intent(this, EncomendaActivity.class);
                startActivity(intent);
                return true;
            case R.id.rota:
                showRoute();
                return true;
            case R.id.logs:
                setInfoAllRegistos();
                return true;
            case R.id.backup:
                saveDocsInCloud();
                return true;
            case R.id.Price_list:
                setPriceList();
                // do your code
                return true;
            case R.id.Sobras:
                registarSobras();
                return true;
            case R.id.logOut:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logOut() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void saveDocsInCloud() {
        File clientes = new File(getExternalFilesDir(null), "MeuArquivoXLS.xls");
        File encomendas = new File(getExternalFilesDir(null), "EncomendasPanilima.txt");
        File logs = new File(getExternalFilesDir(null), "logs/logs.txt");
        File tabelaPrecos = new File(getExternalFilesDir(null), "TabelaPrecos.xls");

        Uri file = Uri.fromFile(clientes);
        Uri file1 = Uri.fromFile(encomendas);
        Uri file2 = Uri.fromFile(logs);
        Uri file3 = Uri.fromFile(tabelaPrecos);

        StorageReference riversRef = mStorageRef.child("docs/MeuArquivoXLS.xls");
        StorageReference riversRef1 = mStorageRef.child("docs/EncomendasPanilima.txt");
        StorageReference riversRef2 = mStorageRef.child("docs/logs.txt");
        StorageReference riversRef3 = mStorageRef.child("docs/TabelaPrecos.xls");

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                //Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                //Toast.makeText(MainActivity.this, "NOT OK", Toast.LENGTH_SHORT).show();
            }
        });
        riversRef1.putFile(file1);
        riversRef2.putFile(file2);
        riversRef3.putFile(file3);

    }

    public void addProduto(String produto, float valor, boolean unit) {
        LinearLayout linearLayout = findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow = findViewById(R.id.row);

        LinearLayout newLayout = new LinearLayout(this);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramSpinner = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(this);
        ArrayList<String> lProduto = new ArrayList<>();
        lProduto.add(produto);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.spinner_item_encomendas,
                lProduto);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(paramSpinner);


        EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        if (unit) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            editText.setText(Integer.toString((int) valor));
        } else {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,3)});
            editText.setText(Float.toString(valor));
        }
        editText.setLayoutParams(paramText);
        editText.setEms(10);

        newLayout.addView(spinner);
        newLayout.addView(editText);

        linearLayout.addView(newLayout, linearLayout.getChildCount() - 1);
    }

    public void atualizaPrecoProduto(ArrayList<Float> precos) {
        LinearLayout linearLayout = findViewById(R.id.listaItems);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) linearLayout.getChildAt(i);

            EditText editText = (EditText) row.getChildAt(1);

            editText.setText(Float.toString(precos.get(i)));

        }
    }

    private void registarSobras() {
        TabelaPrecos tabelaPrecos = getTabela(getApplicationContext());
        setContentView(R.layout.sobras_layout);

        TextView txtDate = findViewById(R.id.date2);
        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        txtDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

        for (int i = 0; i < tabelaPrecos.getProdutos().size(); i++) {
            addProduto(tabelaPrecos.getProdutos().get(i), 0, tabelaPrecos.getProdutosIsUnit(i));
        }
        LinearLayout linearLayout = findViewById(R.id.listaItems);
        linearLayout.removeViewAt(linearLayout.getChildCount()-1);
    }

    private void setPriceList() {
        setContentView(R.layout.price_list);

        final TabelaPrecos tabelaPrecos = getTabela(getApplicationContext());

        Spinner spinner = findViewById(R.id.clientes);
        ArrayList<String> clientes = new ArrayList<>();
        clientes.add("Tabela de preço ao público");
        for (int i = 0; i<tabelaPrecos.getClientes().size();i++) {
            clientes.add(clientsManager.getClienteName(tabelaPrecos.getClientes().get(i)));
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, clientes);
        spinner.setAdapter(spinnerArrayAdapter);

        for (int i = 0; i< tabelaPrecos.getProdutos().size(); i++) {
            addProduto(tabelaPrecos.getProdutos().get(i), tabelaPrecos.getPrecos().get(i), tabelaPrecos.getProdutosIsUnit(i));
        }
        LinearLayout linearLayout = findViewById(R.id.listaItems);
        linearLayout.removeViewAt(linearLayout.getChildCount()-1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  if (position == 0) {
                      ArrayList<Float> ListadePrecos = tabelaPrecos.getPrecos();
                      atualizaPrecoProduto(ListadePrecos);

                  } else {
                      int clienteId = tabelaPrecos.getClientes().get(position - 1);

                      ArrayList<Float> ListadePrecos = tabelaPrecos.getTabelaPrecosCliente(clienteId);
                      atualizaPrecoProduto(ListadePrecos);
                  }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });

    }

    private void setInfoAllRegistos() {
        setContentView(R.layout.registos_layout);

        Spinner spinner = findViewById(R.id.tiposRegistos);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, getTiposLogs());
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                RegistosManager registosManager = getAllLogs(getApplicationContext());
                ListView listaDePrecos = findViewById(R.id.listaLogs);
                ArrayAdapter<Registo> produtos;

                ArrayList<String> listaInfo = new ArrayList<>();
                listaInfo.add("Sem logs encontrados");
                ArrayAdapter<String> semProdutos = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, listaInfo);
                switch (position) {
                    case 0: //TODOS
                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getAllRegistos());
                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 1: //Extras
                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosExtras());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 2:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosPagamentos());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 3:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosEdicao());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 4:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosEncomendas());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 5:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosLocalizacao());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 6:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosInatividade());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 7:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosRegisto());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 8:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosSobras());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 9:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosNewClients());

                        listaDePrecos.setAdapter(produtos);
                        break;

                    case 10:

                        produtos = new ArrayAdapter<Registo>
                                (getApplicationContext(), android.R.layout.simple_spinner_item, registosManager.getRegistosRemoveClients());

                        listaDePrecos.setAdapter(produtos);
                        break;

                }
                if (listaDePrecos.getAdapter().isEmpty()) {
                    listaDePrecos.setAdapter(semProdutos);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void showRoute() {
        /*String route = "https://www.google.com/maps/dir/?api=1&origin=";

        String origin = getFirstCoordenada();
        route += origin + "&";
        String destination = getLastCoordenada();
        route += "destination=" + destination + "&";
        ArrayList<String> points = getPoints(origin, destination);
        route += "waypoints=";
        int count = 1;
        for (String point : points) {
            route += point;

            if (count < points.size()) {
                route += "|";
            }
            count++;
        }
        route += "&travelmode=driving";

         */

        String house = "41.747741,-8.669348";
        String route = "https://maps.google.com/maps/dir/" + house;
        String origin = getFirstCoordenada();
        String destination = getLastCoordenada();
        ArrayList<String> points = getPoints("", "");

        /*final StringBuffer sBuf = new StringBuffer(route);
        sBuf.append("saddr=");
        sBuf.append(origin);
        sBuf.append("&daddr=");
        sBuf.append(house);



        int count = 1;
        for (String point : points) {
            sBuf.append("/");
            sBuf.append(point);

            if (count == 23){
                break;
            }
            count++;
        }


        sBuf.append("&travelmode=driving");

        Intent sendLocationToMap = new Intent(Intent.ACTION_VIEW,
                Uri.parse(sBuf.toString()));
        startActivity(sendLocationToMap);

         */

        /*Uri gmmIntentUri = Uri.parse(route);
        Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        intent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(this, "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }*/
    }

    private ArrayList<String> getPoints(String origin, String destination) {
        ArrayList<String> points = new ArrayList<>();
        String coordenada;
        for (Cliente cliente : clientsManager.getClientsList()) {
            if (cliente.getCoordenadas() != null) {
                coordenada = String.format(Locale.US, "%f,%f", cliente.getLat(), cliente.getLng());
                if (!coordenada.equals(origin) && !coordenada.equals(destination)) {
                    points.add(coordenada);
                }
            }
        }
        return points;
    }

    private String getLastCoordenada() {
        String lastCoordenada = "";
        for (Cliente cliente : clientsManager.getClientsList()) {
            if (cliente.getCoordenadas() != null) {
                lastCoordenada = String.format(Locale.US, "%f,%f", cliente.getLat(), cliente.getLng());
            }
        }
        return lastCoordenada;
    }

    private String getFirstCoordenada() {
        for (Cliente cliente : clientsManager.getClientsList()) {
            if (cliente.getCoordenadas() != null) {
                return String.format(Locale.US, "%f,%f", cliente.getLat(), cliente.getLng());
            }
        }
        return "";
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putSerializable("media", listSpeed);
        // etc.

        SharedPreferences prefs = getSharedPreferences("media_array", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("size" , listSpeed.size());
        for(int i=0;i<listSpeed.size();i++)
            editor.putString("media_pos_" + i, Float.toString(listSpeed.get(i)));
        editor.commit();
    }

    private void onRestoreListAverageArray(Bundle savedInstanceState) {
        ArrayList<Float> media = new ArrayList();
        SharedPreferences prefs = getSharedPreferences("media_array", MODE_PRIVATE);
        int size = prefs.getInt("size", 0);
        for(int i=0;i<size;i++)
            media.add(Float.parseFloat(prefs.getString("media_pos_" + i, "0")));
        this.listSpeed = media;
    }

    static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public void voltarMainPage(View view) {
        setContentView(R.layout.activity_main);
        setListaClientes(0);
        //setNotificationScheduler(getApplicationContext());
        //issueNotification();
        setEncomendas();
        setGPSData();
    }

    public void chooseDate(View view) {
        int mYear, mMonth, mDay;
        final TextView txtDate = findViewById(R.id.date2);

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void salvarSobras(View view) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        final TextView txtDate = findViewById(R.id.date2);
        try {
            if (registosManager.getRegistosSobrasSpecificDay(formatter.parse(txtDate.getText().toString())) != null) {
                Toast.makeText(this, "Registo já efetuado para o dia " + txtDate.getText().toString(), Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String info = "No dia " + txtDate.getText().toString() + " sobrou o seguinte;,";

        boolean something = false;

        LinearLayout linearLayout = findViewById(R.id.listaItems);
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) linearLayout.getChildAt(i);

            Spinner spinner = (Spinner) row.getChildAt(0);
            EditText editText = (EditText) row.getChildAt(1);

            try {
                if (Integer.parseInt(editText.getText().toString()) > 0) {
                    something = true;
                    info += spinner.getSelectedItem().toString() + " * " + editText.getText().toString() + ",";
                }
            } catch (NumberFormatException e) {
                if (Float.parseFloat(editText.getText().toString()) > 0) {
                    something = true;
                    info += spinner.getSelectedItem().toString() + " * " + editText.getText().toString() + ",";
                    //TODO penso que nao se pode por assim o float nos logs
                }
            }
        }

        info += "-" + txtDate.getText().toString();

        if (something) {
            logs(getApplicationContext(), 7, 0, info);
            voltarMainPage(view);
        } else {
            Toast.makeText(this, "Registo de Sobras vazio", Toast.LENGTH_SHORT).show();
        }
    }

    public void example(View view) {
        RegistosManager registosManager = getAllLogs(getApplicationContext());
        saveAllLogs(getApplicationContext(), registosManager);
    }
}
