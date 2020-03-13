package com.example.gestor_tab.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.bt.Connection_bt;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;
import com.example.gestor_tab.geocode.ReverseGeoCode;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.gestor_tab.database.DataBaseUtil.replaceOccurance;
import static com.example.gestor_tab.database.ListaPrecosBaseUtil.getTabela;
import static com.example.gestor_tab.database.LogsBaseUtil.deleteAllLogs;
import static com.example.gestor_tab.database.LogsBaseUtil.getLogsClient;
import static com.example.gestor_tab.database.LogsBaseUtil.logs;

public class ClienteActivity extends AppCompatActivity {

    private Cliente cliente;

    private RegistosManager registosManager;

    private TabelaPrecos tabelaPrecos;

    private boolean withRegisto = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        Intent intent = getIntent();
        this.cliente = (Cliente) intent.getSerializableExtra("Cliente");

        if (this.cliente.getTipoPagamento().equals("LS")) {
            withRegisto = true;
        }

        setDataOnScreen(withRegisto);

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int pos, long id) {

                TextView pagar = findViewById(R.id.pagar);
                Spinner despesa = findViewById(R.id.spinner);

                if (withRegisto) {
                    pagar.setText(String.format("%.2f €", registosManager.getTotalDespesa(cliente, despesa)));
                } else {
                    pagar.setText(String.format("%.2f €", cliente.getDespesa(despesa.getSelectedItem().toString())));
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_detalhes, menu);
        menu.setHeaderTitle("Ações de administrador");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            deleteAllLogs(this);
            setDataOnScreen(false);
            Toast.makeText(getApplicationContext(), "file deletado", Toast.LENGTH_LONG).show();
        } else {
            return false;
        }
        return true;
    }

    private void setDataOnScreen(boolean clienteWithRegisto) {
        registosManager = getLogsClient(getApplicationContext(), cliente.getId());
        ArrayList<String> pagarAte;

        TextView nome = findViewById(R.id.nome);
        nome.setText(this.cliente.getName());

        TextView pagoAte = findViewById(R.id.ate);
        pagoAte.setText("Pago até dia: " + this.cliente.getPagamento());

        Spinner despesa = findViewById(R.id.spinner);
        pagarAte = cliente.getNextDate();
        ArrayAdapter lista = new ArrayAdapter<String>(this, R.layout.spinner_item, pagarAte);
        despesa.setAdapter(lista);

        TextView pagar = findViewById(R.id.pagar);
        if (clienteWithRegisto) {
            pagar.setText(String.format("%.2f €", registosManager.getTotalDespesa(cliente, despesa)));
            //TODO carefull with clientgetDespesa no caso LS

        } else {
            pagar.setText(String.format("%.2f €", cliente.getDespesa(despesa.getSelectedItem().toString())));
        }

        //set despesas
        TextView segunda = findViewById(R.id.segunda);
        segunda.setText(String.format("%.2f", cliente.getDespesa()[0]));

        TextView terca = findViewById(R.id.terca);
        terca.setText(String.format("%.2f", cliente.getDespesa()[1]));

        TextView quarta = findViewById(R.id.quarta);
        quarta.setText(String.format("%.2f", cliente.getDespesa()[2]));

        TextView quinta = findViewById(R.id.quinta);
        quinta.setText(String.format("%.2f", cliente.getDespesa()[3]));

        TextView sexta = findViewById(R.id.sexta);
        sexta.setText(String.format("%.2f", cliente.getDespesa()[4]));

        TextView sabado = findViewById(R.id.sabado);
        sabado.setText(String.format("%.2f", cliente.getDespesa()[5]));

        TextView domingo = findViewById(R.id.domingo);
        domingo.setText(String.format("%.2f", cliente.getDespesa()[6]));

        TextView extra = findViewById(R.id.extra);
        extra.setText(String.format("%.2f", cliente.getDespesa()[7]));

        final TabHost tabHost = findViewById(R.id.tab);
        tabHost.setup();

        TabHost.TabSpec spec;

        spec = tabHost.newTabSpec("Calendário");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Calendário");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Detalhes");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Detalhes");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Encomendas");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Encomendas");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Localização");
        spec.setContent(R.id.map);
        spec.setIndicator("Localização");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Registo");
        spec.setContent(R.id.registo);
        spec.setIndicator("Registo");
        tabHost.addTab(spec);

        //PARTE DOS CLIENTES LS
        if (!clienteWithRegisto)
            tabHost.getTabWidget().getChildAt(4).setVisibility(View.GONE);
        this.tabelaPrecos = getTabela(getApplicationContext());
        Spinner spinner = findViewById(R.id.listaPao);
        final EditText editText = findViewById(R.id.quantidadeProduto);
        ArrayAdapter produtos;
        if (tabelaPrecos == null) {
            ArrayList<String> listaErro = new ArrayList();
            listaErro.add("Nenhum produto encontrado!");
            produtos = new ArrayAdapter<String>(this, R.layout.spinner_item, listaErro);
        } else {
            produtos = new ArrayAdapter<String>(this, R.layout.spinner_item, tabelaPrecos.getProdutos());
        }
        spinner.setAdapter(produtos);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!tabelaPrecos.getProdutosIsUnit(position)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,3)});
                } else {
                    try {
                        Integer.parseInt(editText.getText().toString());
                    } catch (NumberFormatException e) {
                        editText.setText("0");
                    }
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView dataRegisto = findViewById(R.id.dataRegisto);
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        EditText latitude = findViewById(R.id.lat);
        if (cliente.getCoordenadas() != null)
            latitude.setText(cliente.getCoordenadas());
        else
            latitude.setText("NaN");

        ReverseGeoCode reverseGeoCode = null;
        try {
            reverseGeoCode = new ReverseGeoCode(getResources().openRawResource(R.raw.pt), true);
            //reverseGeoCode = new ReverseGeoCode(new FileInputStream(getExternalFilesDir(null) + "/pt.txt"), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView local = findViewById(R.id.localidade);

        if (cliente.getLat()>0)
             local.setText(reverseGeoCode.nearestPlace(cliente.getLat(), cliente.getLng()).toString());
        //-----------------------------------------------------

        dataRegisto.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);


        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int tab = tabHost.getCurrentTab();
                if (tab == 1) {
                    registosManager = getLogsClient(getApplicationContext(), cliente.getId());

                    ListView listaDeEventos = (ListView) findViewById(R.id.eventos);

                    ArrayAdapter adapter;
                    if (!registosManager.getRegistos().isEmpty()) {
                        adapter = new ArrayAdapter<Registo>(
                                ClienteActivity.this,
                                android.R.layout.simple_list_item_1,
                                registosManager.getRegistos());

                    } else {
                        ArrayList<String> infos = new ArrayList<>();
                        infos.add("Sem registos!");
                        adapter = new ArrayAdapter<String>(
                                ClienteActivity.this,
                                android.R.layout.simple_list_item_1,
                                infos);
                    }

                    listaDeEventos.setAdapter(adapter);
                    registerForContextMenu(listaDeEventos);
                } else if (tab == 2) {
                    registosManager = getLogsClient(getApplicationContext(), cliente.getId());

                    ListView listaDeEncomendas = (ListView) findViewById(R.id.encomendas);

                    ArrayList<String> infos = new ArrayList<>();
                    for (int i = 0; i < registosManager.getRegistosEncomendas().size(); i++) {
                        infos.add(registosManager.getRegistosEncomendas().get(i).toStringEncomenda());
                    }

                    if (infos.isEmpty()) {
                        infos.add("Sem registos!");
                    }

                    ArrayAdapter adapter = new ArrayAdapter<String>(
                            ClienteActivity.this,
                            android.R.layout.simple_list_item_1,
                            infos);

                    listaDeEncomendas.setAdapter(adapter);
                    registerForContextMenu(listaDeEncomendas);
                }
            }
        });

        tabHost.setCurrentTab(0);
    }

    public void addExtra(View view) {

        final Spinner spinner = new Spinner(ClienteActivity.this);
        ArrayAdapter<String> lista = new ArrayAdapter<String>(this, R.layout.spinner_item);
        lista.add("Adicionar");
        lista.add("Descontar");
        spinner.setAdapter(lista);

        final EditText editText = new EditText(ClienteActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        LinearLayout layout = new LinearLayout(ClienteActivity.this);
        layout.addView(spinner);
        layout.addView(editText);

        new AlertDialog.Builder(ClienteActivity.this)

                .setTitle("Adicinar extra")

                .setMessage("Qual o valor da despesa extra?")

                .setView(layout)

                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date date = new Date();
                        String info = "ERROR-" + dateFormat.format(date);

                        if (spinner.getSelectedItem().toString().equals("Adicionar")) {
                            cliente.addExtra(Float.parseFloat(editText.getText().toString()));

                            info = "Adicionado um valor de " + editText.getText().toString() + "€ no dia-" + dateFormat.format(date);

                        } else if (spinner.getSelectedItem().toString().equals("Descontar")) {
                            cliente.addExtra(-Float.parseFloat(editText.getText().toString()));
                            info = "Removido um valor de " + editText.getText().toString() + "€ no dia-" + dateFormat.format(date);
                        }

                        //MainActivity.saveChanges(getApplicationContext(), cliente);
                        logs(getApplicationContext(), 0, cliente.getId(), info);

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("Cliente", cliente);
                        intent.putExtra("refresh", 1);
                        startActivity(intent);

                        Toast.makeText(ClienteActivity.this, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })

                .setNeutralButton("NÃO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ClienteActivity.this, "Operação cancelada", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    public void openCalculator(View view) {

        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.calculator2");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }

    }

    public void pagamento(View view) {
        final TextView valor = findViewById(R.id.pagar);
        final Spinner spinner = findViewById(R.id.spinner);


        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText("Pagar extra de : " + cliente.getDespesa()[7] + "€");
        checkBox.setChecked(true);

        if (valor.getText().toString().equals("0,00 €")) {
            new AlertDialog.Builder(ClienteActivity.this)

                    .setTitle("Operação indisponivel!")

                    .setMessage("Valor de 0.00€ por liquidar.")

                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Toast.makeText(ClienteActivity.this, "Operação cancelada", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(ClienteActivity.this)

                    .setTitle("Realizar pagamento")

                    .setMessage("Deseja confirmar o pagamento de " +
                            valor.getText().toString().replaceAll(" ", "") +
                            " respetivo até ao dia " + spinner.getSelectedItem().toString() + "?")

                    .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            Date date = new Date();

                            if (checkBox.isChecked()) {
                                cliente.setNewPagamento(spinner.getSelectedItem().toString(), true);
                            } else {
                                cliente.setNewPagamento(spinner.getSelectedItem().toString(), false);
                            }

                            String info = "Realizado pagamento até dia " + spinner.getSelectedItem().toString() + " com valor de " + valor.getText().toString() + "€ no dia-" + dateFormat.format(date);
                            logs(getApplicationContext(), 1, cliente.getId(), info);

                            Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                            myIntent.putExtra("Cliente", cliente);
                            startActivity(myIntent);

                            Toast.makeText(ClienteActivity.this, "Operação realizada com sucesso!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })

                    .setNeutralButton("NÃO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ClienteActivity.this, "Operação cancelada", Toast.LENGTH_SHORT).show();
                        }
                    });

            if (cliente.getDespesa()[7] > 0) {
                dialog.setView(checkBox);
            }

            dialog.show();


        }

    }

    public void editCliente(View view) {
        Intent intent = new Intent(getApplicationContext(), EditClientActivity.class);
        intent.putExtra("Cliente", cliente);
        startActivity(intent);
        finish();
    }

    public void addEncomenda(View view) {
        setContentView(R.layout.encomendas);

        setDataEncomendaOnScreen();
    }

    private void setDataEncomendaOnScreen() {
        TextView txtDate = findViewById(R.id.date);
        int mYear, mMonth, mDay;

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        txtDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);

        txtDate = findViewById(R.id.nome);
        txtDate.setText(cliente.getName());

        Spinner spinner = (Spinner) findViewById(R.id.listaPao);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.spinner_item_encomendas,
                tabelaPrecos.getProdutos()
        );
        final EditText editText = findViewById(R.id.editTextProduto);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!tabelaPrecos.getProdutosIsUnit(position)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,3)});
                } else {
                    try {
                        Integer.parseInt(editText.getText().toString());
                    } catch (NumberFormatException e) {
                        editText.setText("0");
                    }
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner.setAdapter(adapter);
    }

    public void chooseDate(View view) {
        int mYear, mMonth, mDay;
        final TextView txtDate = findViewById(R.id.date);

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

    public void chooseDateLD(View view) {
        int mYear, mMonth, mDay;
        final TextView txtDate = findViewById(R.id.dataRegisto);

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

    public void addProdutoTabelaPrecos(View view) {

        LinearLayout linearLayout = findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow = findViewById(R.id.row);

        LinearLayout newLayout = new LinearLayout(this);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramSpinner = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(this);
        ArrayAdapter produtos = new ArrayAdapter<String>(this, R.layout.spinner_item, tabelaPrecos.getProdutos());
        spinner.setAdapter(produtos);
        spinner.setLayoutParams(paramSpinner);


        final EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("0");
        editText.setLayoutParams(paramText);
        editText.setEms(10);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!tabelaPrecos.getProdutosIsUnit(position)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,3)});
                } else {
                    try {
                        Integer.parseInt(editText.getText().toString());
                    } catch (NumberFormatException e) {
                        editText.setText("0");
                    }
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newLayout.addView(spinner);
        newLayout.addView(editText);

        linearLayout.addView(newLayout, linearLayout.getChildCount() - 2);
    }

    public void addProduto(View view) {

        LinearLayout linearLayout = findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow = findViewById(R.id.row);

        LinearLayout newLayout = new LinearLayout(this);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramSpinner = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getApplicationContext(),
                R.layout.spinner_item_encomendas,
                tabelaPrecos.getProdutos()
        );

        spinner.setAdapter(adapter);
        spinner.setLayoutParams(paramSpinner);


        final EditText editText = new EditText(this);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("0");
        editText.setLayoutParams(paramText);
        editText.setEms(10);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!tabelaPrecos.getProdutosIsUnit(position)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,3)});
                } else {
                    try {
                        Integer.parseInt(editText.getText().toString());
                    } catch (NumberFormatException e) {
                        editText.setText("0");
                    }
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newLayout.addView(spinner);
        newLayout.addView(editText);

        linearLayout.addView(newLayout, linearLayout.getChildCount() - 1);
    }

    public void saveEncomenda(View view) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        TextView data = findViewById(R.id.date);
        Date dateEncomenda = null;
        try {
            dateEncomenda = dateFormat.parse(data.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateFormat.format(date).equals(dateFormat.format(dateEncomenda))) {
            new AlertDialog.Builder(this)
                    .setTitle("Encomenda marcada para hoje!")
                    .setMessage("Insira nova data")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("OK", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        boolean encomenda = false;
        String text = "";
        int quantidade;
        LinearLayout verticalLayout = findViewById(R.id.listaItems);

        text += "Encomenda para dia " + data.getText().toString() + ",";


        for (int pos = 0; pos < verticalLayout.getChildCount() - 1; pos++) {
            LinearLayout row = (LinearLayout) verticalLayout.getChildAt(pos);

            Spinner spinner = (Spinner) row.getChildAt(0);
            EditText editText = (EditText) row.getChildAt(1);

            quantidade = Integer.parseInt(editText.getText().toString());

            if (quantidade > 0) {
                encomenda = true;
                text += "]" + spinner.getSelectedItem().toString() + " ; " + quantidade + ",";
            }
        }



        text += "-" + dateFormat.format(date);

        if (encomenda)
            logs(this, 3, cliente.getId(), text);

        setContentView(R.layout.activity_cliente);
        setDataOnScreen(withRegisto);
    }

    public void voltar(View view) {
        setContentView(R.layout.activity_cliente);
        setDataOnScreen(withRegisto);
    }

    public void voltarMain(View view) {
        Intent myIntent = new Intent(this, MainActivity.class);

        myIntent.putExtra("Cliente", cliente);

        startActivity(myIntent);
        finish();
    }

    public void options(View view) {
        deleteAllLogs(this);
    }

    public void imprime(View view) {
        final Activity activity = this;

        if (cliente.getTipoPagamento().equals("D")) {
            new AlertDialog.Builder(this)
                    .setTitle("Função indisponivel para clientes de pagamento diário!")

                    .setPositiveButton("Ok", null)

                    .show();

            return;
        }

        new AlertDialog.Builder(this)

                .setTitle("Pretende Imprimir a conta?")

                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Connection_bt bt = new Connection_bt(activity, cliente);
                        try {
                            if (cliente.getTipoPagamento().equals("LS")) {
                                bt.imprimeLS();
                            } else {
                                bt.imprime(cliente);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })

                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })

                .show();


    }

    public void salvarRegisto(View view) {
        RegistosManager registosManager= getLogsClient(this, cliente.getId());
        String info = "";
        int quantidade;
        float total = 0;
        ArrayList<Float> tabelaCliente = this.tabelaPrecos.getTabelaPrecosCliente(this.cliente.getId());

        LinearLayout verticalLayout = findViewById(R.id.listaItems);
        TextView data = findViewById(R.id.dataRegisto);
        info += "Registo do dia " + data.getText().toString() + " ";

        for (int pos = 0; pos < verticalLayout.getChildCount() - 2; pos++) {
            LinearLayout row = (LinearLayout) verticalLayout.getChildAt(pos);

            Spinner spinner = (Spinner) row.getChildAt(0);
            EditText editText = (EditText) row.getChildAt(1);

            quantidade = Integer.parseInt(editText.getText().toString());

            if (quantidade > 0) {
                total += quantidade * tabelaCliente.get(tabelaPrecos.getIdProduto(spinner.getSelectedItem().toString()));
                //Encomenda para dia 21/12/2019,]Pão Paris ; 20,]Broa Milho (Kg) ; 2,
                info += ",]" + spinner.getSelectedItem().toString() + " ; " + editText.getText().toString();
            }
        }

        if (total > 0) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            info += ",]Total ; " + String.format(Locale.ROOT, "%.2f", total) + "-" + dateFormat.format(new Date());

            if (!registosManager.verificaRegistoRepetido(cliente.getId(), data.getText().toString())) {
                logs(this, 6, cliente.getId(), info);
            } else {
                Toast.makeText(getApplicationContext(), "Operação errada: Dados para o dia " +data.getText().toString() + " já inseridos!" , Toast.LENGTH_SHORT).show();
                return;
            }

            this.cliente.updateDay(total, data.getText().toString());

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("Cliente", this.cliente);
            intent.putExtra("refresh", 1);
            startActivity(intent);
            finish();
            //setDataOnScreenLD(false);
        } else {
            Toast.makeText(getApplicationContext(), "Operação errada: sem dados novos", Toast.LENGTH_SHORT).show();

        }

    }

    private int countOccurencesChar(String someString, char someChar) {
        int count = 0;

        for (int i = 0; i < someString.length(); i++) {
            if (someString.charAt(i) == someChar) {
                count++;
            }
        }

        return count;
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

    public void saveNewLocation(View view) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        EditText editText = findViewById(R.id.lat);

        if (editText.getText().toString().equals("NaN")) {
            Toast.makeText(getApplicationContext(),"Operação impossivel (sem localização)",Toast.LENGTH_SHORT).show();
            return;
        }

        String oldLocalizacao;
        if (this.cliente.getCoordenadas() == null)
            oldLocalizacao = "NaN";
        else
            oldLocalizacao = this.cliente.getCoordenadas();

        String info = "Alteração da posição do cliente de [" + oldLocalizacao + "] para [" + editText.getText().toString() + "]-" + dateFormat.format(date);
        logs(getApplicationContext(), 4, this.cliente.getId(), info);
        this.cliente.setCoordenadas(editText.getText().toString());

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("Cliente", this.cliente);
        intent.putExtra("refresh", 1);
        startActivity(intent);
    }
}
