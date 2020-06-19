package com.example.gestor_tab.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.activitys.EditClientActivity_Functions.PriceTableManager;
import com.example.gestor_tab.activitys.EditClientActivity_Functions.QuantidadesDescInClientUtil;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.gestor_tab.database.DataBaseUtil.countOccurencesChar;
import static com.example.gestor_tab.database.DataBaseUtil.replaceOccurance;
import static com.example.gestor_tab.database.ListaPrecosBaseUtil.getTabela;
import static com.example.gestor_tab.database.LogsBaseUtil.logs;


public class EditClientActivity extends AppCompatActivity {

    private static final String SYSTEM_CONFIG = "systemConfig";
    private static final String SNOOZE_TIME = "snoozeTime";

    private Handler handler;
    private Runnable r;

    private Cliente cliente;

    boolean newCliente = false;

    private TabelaPrecos tabelaPrecos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client_v2);
        getSupportActionBar().hide();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        cliente = (Cliente) intent.getParcelableExtra("Cliente");

        this.tabelaPrecos = getTabela(this);

        if (cliente.getName().equals("Default"))
            newCliente = true;

        setConfigInputSectors();
        setDataOnScreen();
        //inatividade();
    }


    private void setConfigInputSectors() {
        EditText editText = findViewById(R.id.segunda);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.terca);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.quarta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.quinta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.sexta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.sabado);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        editText = findViewById(R.id.domingo);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

        final TabHost tabHost = findViewById(R.id.tab);
        tabHost.setup();

        TabHost.TabSpec spec;
        spec = tabHost.newTabSpec("Nomenclatura");
        spec.setContent(R.id.infoCliente);
        spec.setIndicator("Info");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Tipo Pagamento");
        spec.setContent(R.id.tab5);
        spec.setIndicator("Tipo Pagamento");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Admin");
        spec.setContent(R.id.tab6);
        spec.setIndicator("Admin");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Localização");
        spec.setContent(R.id.map);
        spec.setIndicator("Localização");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Calendário");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Calendário");
        tabHost.addTab(spec);

        editText = findViewById(R.id.extra);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

    }

    public void setDataOnScreen() {
        float[] despesa = cliente.getDespesa();

        EditText editText = findViewById(R.id.segunda);
        editText.setText(String.format("%.2f", despesa[0]));

        editText = findViewById(R.id.terca);
        editText.setText(String.format("%.2f", despesa[1]));

        editText = findViewById(R.id.quarta);
        editText.setText(String.format("%.2f", despesa[2]));

        editText = findViewById(R.id.quinta);
        editText.setText(String.format("%.2f", despesa[3]));

        editText = findViewById(R.id.sexta);
        editText.setText(String.format("%.2f", despesa[4]));

        editText = findViewById(R.id.sabado);
        editText.setText(String.format("%.2f", despesa[5]));

        editText = findViewById(R.id.domingo);
        editText.setText(String.format("%.2f", despesa[6]));

        editText = findViewById(R.id.nome);
        editText.setText(cliente.getName());

        TextView textView = findViewById(R.id.descricao);
        textView.setText("ID: " + cliente.getId());

        RadioGroup radioGroup = findViewById(R.id.radioG);
        if (cliente.getTipoPagamento().equals("D"))
            radioGroup.check(R.id.diario);
        else if (cliente.getTipoPagamento().equals("S"))
            radioGroup.check(R.id.semanal);
        else if (cliente.getTipoPagamento().equals("M"))
            radioGroup.check(R.id.mensal);
        else if (cliente.getTipoPagamento().equals("LS"))
            radioGroup.check(R.id.lojaSemanal);
        else if (cliente.getTipoPagamento().equals("JD"))
            radioGroup.check(R.id.juntaDias);
        else
            radioGroup.check(R.id.especial);

        editText = findViewById(R.id.data);
        editText.setText(cliente.getPagamento());

        editText = findViewById(R.id.extra);
        editText.setText(Float.toString(despesa[7]));

        editText = findViewById(R.id.morada);
        if (cliente.getMorada().equals(""))
            editText.setText("Sem morada definida");
        else
            editText.setText(cliente.getMorada());

        editText = findViewById(R.id.lat);
        String localizacao = this.cliente.getCoordenadas();
        if (localizacao != null) {
            editText.setText(this.cliente.getCoordenadas());
        } else {
            editText.setText("Sem localização definida");
        }

        Button button = findViewById(R.id.searchLocal);
        if (cliente.getLat() != 0) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText1 = findViewById(R.id.morada);

                    if (isNetworkAvailable()) {
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses;
                        try {
                            addresses = geo.getFromLocation(cliente.getLat(), cliente.getLng(), 1);

                            if (addresses.size() > 0) {
                                editText1.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                            }

                        } catch (IOException e) {

                        }

                    } else {
                        Toast.makeText(EditClientActivity.this, "Internet indisponivel", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else
            button.setVisibility(View.GONE);

        editText = findViewById(R.id.nPorta);
        editText.setText(Integer.toString(cliente.getNPorta()));

        editText = findViewById(R.id.alcunha);
        if (cliente.getAlcunha().equals(""))
            editText.setText("Sem alcunha definida");
        else
            editText.setText(cliente.getAlcunha());

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void salvar(View view) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        EditText editText = findViewById(R.id.data);
        String newDate = editText.getText().toString();

        editText = findViewById(R.id.nome);
        String nome = editText.getText().toString();

        float[] despesa = Arrays.copyOf(cliente.getDespesa(), cliente.getDespesa().length);

        editText = findViewById(R.id.segunda);
        despesa[0] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.terca);
        despesa[1] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.quarta);
        despesa[2] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.quinta);
        despesa[3] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.sexta);
        despesa[4] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.sabado);
        despesa[5] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.domingo);
        despesa[6] = Float.parseFloat(editText.getText().toString().replace(",","."));

        editText = findViewById(R.id.extra);
        despesa[7] = Float.parseFloat(editText.getText().toString().replace(",","."));

        RadioButton radioButtonDiario = findViewById(R.id.diario);
        RadioButton radioButtonSemanal = findViewById(R.id.semanal);
        RadioButton radioButtonMensal = findViewById(R.id.mensal);
        RadioButton radioButtonLS = findViewById(R.id.lojaSemanal);
        RadioButton radioButtonJD = findViewById(R.id.juntaDias);
        RadioButton radioButtonEspecial = findViewById(R.id.especial);
        String tipoPagamento = "";

        PriceTableManager priceTableManager = new PriceTableManager(this, this.cliente, tabelaPrecos);
        boolean changeToLS = false;

        if (!radioButtonLS.isChecked() && cliente.getTipoPagamento().equals("LS")) {
            priceTableManager.deleteCliente(cliente);
        }

        if (radioButtonDiario.isChecked()) {
            tipoPagamento = "D";
        } else if (radioButtonSemanal.isChecked()) {
            tipoPagamento = "S";
        } else if (radioButtonMensal.isChecked()) {
            tipoPagamento = "M";
        } else if (radioButtonLS.isChecked()) {
            if (!cliente.getTipoPagamento().equals("LS")) {
                TabelaPrecos tabelaPrecos = getTabela(getApplicationContext());
                if (!tabelaPrecos.verificaCliente(cliente.getId())) {
                    //priceTableManager.run();
                    changeToLS = true;
                }
            }
            tipoPagamento = "LS";
        } else if (radioButtonEspecial.isChecked()) {
            tipoPagamento = "E";
        } else if (radioButtonJD.isChecked()) {
            tipoPagamento = "JD";
        }


        editText = findViewById(R.id.morada);
        String morada = editText.getText().toString();
        if (morada.contains("-")) {
            Toast.makeText(this, "A morada nao pode conter '-'", Toast.LENGTH_SHORT).show();
            return;
        }

        editText = findViewById(R.id.lat);
        if (!editText.getText().toString().equals("Sem localização definida")) {
            cliente.setCoordenadas(editText.getText().toString());
        }

        editText = findViewById(R.id.nPorta);
        int noPorta = Integer.parseInt(editText.getText().toString());

        editText = findViewById(R.id.alcunha);
        String alcunha = editText.getText().toString();

        try {
            String mudancas = cliente.updateCliente(nome, dateFormat.parse(newDate), tipoPagamento, despesa, morada, noPorta, alcunha, null, null);
            if (!mudancas.split("-")[0].equals("") && !newCliente)
                logs(this, 2, cliente.getId(), mudancas);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Cliente", this.cliente);
        if (!newCliente) {
            switch (view.getId()) {
                case R.id.pagamento:
                    intent.putExtra("refresh", 1);
                    break;
                case R.id.pagamento2:
                    intent.putExtra("refresh", -1);
                    break;
            }
        } else {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            String date = mDay + "/" + (mMonth + 1) + "/" + mYear;

            String info = "Criado um novo cliente ("+cliente.getName()+") " + "com id ("+cliente.getId()+")-" + date;
            logs(getApplicationContext(), 8, cliente.getId(), info);
        }

        if (changeToLS) {
            priceTableManager.setIntent(intent);
            priceTableManager.run();
        } else {
            startActivity(intent);
            finish();
        }

    }

    public void voltarClientePage(View view) {
        Intent intent = new Intent(this, ClienteActivity.class);

        intent.putExtra("Cliente", this.cliente);

        startActivity(intent);
        finish();
    }

    public void openMap(View view) {
        EditText lat = findViewById(R.id.lat);
        String coordenadas = lat.getText().toString();

        if (countOccurencesChar(coordenadas, ',') > 1) {
            coordenadas = replaceOccurance(coordenadas, ",", ".", 1);
            coordenadas = replaceOccurance(coordenadas, ",", ".", 2);
        }

        coordenadas.replaceAll(" ", "");

        if (coordenadas.equals("NaN")) {
            //TODO alert dialog
            return;
        }

        Intent mapIntent = new Intent();

        // we want to view the map
        mapIntent.setAction(Intent.ACTION_VIEW);

        // this will be shown as primary marker in the map
        // the coordinate 53.2,8.8 is in north germany where the map is centered around
        // z=1 means zoomlevel=1 showing the continent
        // the marker's caption will be "primary marker"
        Uri uri = Uri.parse("geo:" + coordenadas + "?q=" + coordenadas + "(Localização+do+Cliente:+" + this.cliente.getName() + ")&z=14");
        mapIntent.setDataAndType(uri, null);

        try {
            startActivityForResult(mapIntent, 4711);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void saveLiveLocation(View view) {
        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();

                EditText editText = findViewById(R.id.lat);
                editText.setText(String.format("%f, %f", lat, lng));

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
            return;
        }
        locationManager.requestSingleUpdate(criteria, locationListener, looper);
    }

    public void setQuantidadesDesc(View view) {
        QuantidadesDescInClientUtil quantidades;
        switch (view.getId()) {
            case R.id.textView33:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.SEGUNDA, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView34:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.TERCA, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView35:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.QUARTA, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView36:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.QUINTA, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView37:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.SEXTA, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView38:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.SABADO, this.tabelaPrecos);
                quantidades.run();
                break;

            case R.id.textView39:
                quantidades = new QuantidadesDescInClientUtil(this, this.cliente, QuantidadesDescInClientUtil.DOMINGO, this.tabelaPrecos);
                quantidades.run();
                break;
        }
    }

}
