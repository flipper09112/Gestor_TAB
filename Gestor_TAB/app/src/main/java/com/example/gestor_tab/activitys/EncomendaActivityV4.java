package com.example.gestor_tab.activitys;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.database.FirebaseUtils;
import com.example.gestor_tab.database.ListaClientesBaseUtil;
import com.example.gestor_tab.encomendas.EncomendaBolos;
import com.example.gestor_tab.encomendas.EncomendaBolosV2;
import com.example.gestor_tab.encomendas.EncomendaManagerV2;
import com.example.gestor_tab.encomendas.EncomendaV2;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import jxl.read.biff.BiffException;

import static com.example.gestor_tab.database.EncomendaDistribuidorBaseUtil.getEncomendav2;
import static com.example.gestor_tab.database.EncomendaDistribuidorBaseUtil.saveEncomendaV2;
import static com.example.gestor_tab.database.ListaPrecosBaseUtil.getTabela;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogs;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogsEncomendas;
import static com.example.gestor_tab.database.LogsBaseUtil.getLogsClient;
import static com.example.gestor_tab.database.LogsBaseUtil.logs;

public class EncomendaActivityV4 extends AppCompatActivity {

    private String[] dayOfWeek = {"DOMINGO", "SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO"};

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private File file;
    private EncomendaManagerV2 encomendaManager;
    private EncomendaV2 encomenda;
    private TabelaPrecos tabelaPrecos;
    private RegistosManager registosManager;
    private ClientsManager clientsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_encomenda_v2);
        setContentView(R.layout.activity_encomenda_v4);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            //Toast.makeText(getApplicationContext(), "Bem vindo de volta " + user.getEmail() + "!", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        mStorageRef = FirebaseStorage.getInstance().getReference();

        ListaClientesBaseUtil db = new ListaClientesBaseUtil(this);
        try {
            clientsManager = db.getData();
        } catch (Resources.NotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        file = new File(getExternalFilesDir(null), "EncomendasV2.txt");

        tabelaPrecos = getTabela(getApplicationContext());

        registosManager = getLogsClient(this, 0);

        encomendaManager = new EncomendaManagerV2();
        getEncomendav2(file, encomendaManager, tabelaPrecos.getProdutos().size());
        encomenda = encomendaManager.getEncomenda(getStringDay());

        setDataOnScreen();
        setDataOnEncomendasBolos();
        setInfoSobras();
        setEncomendasDiaSeguinte();

    }

    public void sendEmail(View view) {
        getDataOnScreen();

        String data = encomenda.toStringWithoutZeros()
                + "--------------------------------------------------------\n\n"
                + encomenda.toStringEncomendaBolos();
        saveEncomendaV2(file, encomendaManager);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"encomendas@panilima.pt"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Encomenda");
        i.putExtra(Intent.EXTRA_TEXT   , data);

        if (!registosManager.verificaEncomendaTotalRepetida(0, getFormaterStringNextDay())) {
            logs(this, 10, 0, encomenda.toLog());
        } else {
            Toast.makeText(this, "Log nao atualizado!", Toast.LENGTH_SHORT).show();
        }

        //FirebaseUtils.saveDocsInCloudDevelop(this, mStorageRef);
        FirebaseUtils.saveDocsInCloud(this, mStorageRef);

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EncomendaActivityV4.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFormaterStringNextDay() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 24);

        return formatter.format(c.getTime());
    }

    private void getDataOnScreen() {

        LinearLayout linearLayout = findViewById(R.id.listaProdutos);

        for (int i = 0; i < tabelaPrecos.getProdutos().size(); i++) {
            LinearLayout linearLayoutRow = (LinearLayout) linearLayout.getChildAt(i);

            TextView textView = (TextView) linearLayoutRow.getChildAt(0);
            EditText editText = (EditText) linearLayoutRow.getChildAt(1);

            encomenda.setQuantidade(textView.getText().toString(), Float.parseFloat(editText.getText().toString()));

        }
    }

    private void setDataOnScreen() {
        int count = 0;
        ArrayList<String> lista = tabelaPrecos.getProdutos();

        for (String produto : lista) {
            addProduto(produto, encomenda.getQuantidade(produto), tabelaPrecos.getProdutosIsUnit(count));

            count++;
        }

        removeLastProduto();
        //addButtonToAddProduct();
    }

    private void addButtonToAddProduct() {
        LinearLayout linearLayout = findViewById(R.id.listaProdutos);
        Button button = new Button(this);
        button.setText("Adicionar Produto");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void removeLastProduto() {
        LinearLayout linearLayout = findViewById(R.id.listaProdutos);

        linearLayout.removeViewAt(linearLayout.getChildCount()-1);
    }

    public void addProduto(String produto, float valor, boolean unit) {
        LinearLayout linearLayout = findViewById(R.id.listaProdutos);
        LinearLayout linearLayoutRow = findViewById(R.id.linha);

        LinearLayout newLayout = new LinearLayout(this);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramTextview = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textView = new TextView(this);
        textView.setLayoutParams(paramTextview);
        textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        textView.setText(produto);

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

        newLayout.addView(textView);
        newLayout.addView(editText);

        linearLayout.addView(newLayout, linearLayout.getChildCount() - 1);
    }

    private void setInfoSobras() {
        final TextView infoSobras = findViewById(R.id.sobrasInfo);
        RegistosManager registosManager = getAllLogs(getApplicationContext());

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -6);
        Registo registo = registosManager.getRegistosSobrasSpecificDay(c.getTime());

        if (registo == null) {
            infoSobras.setText("Sem registo de sobras");
        } else {
            infoSobras.setText(registo.toString());
        }

    }

    private void setDataOnEncomendasBolos() {
        final ListView listaEncomendas = (ListView) findViewById(R.id.listaBolos);

        if (encomenda.getEncomendaBolosArrayList().isEmpty()) {
            ArrayList<String> list = new ArrayList<>();
            list.add("Nenhum cliente registado!");

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            listaEncomendas.setAdapter(adapter);

        } else {
            ArrayList<EncomendaBolos> list = new ArrayList<>();

            final ArrayAdapter<EncomendaBolosV2> adapter = new ArrayAdapter<EncomendaBolosV2>(this, android.R.layout.simple_list_item_1, encomenda.getEncomendaBolosArrayList());

            listaEncomendas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                    chooseNewOrder(adapterView, i);
                }
            });

            listaEncomendas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int i, long l) {
                    final EncomendaBolosV2 encomendaBolos = (EncomendaBolosV2) adapterView.getItemAtPosition(i);
                    final int pos = i;
                    AlertDialog.Builder builder = new AlertDialog.Builder(EncomendaActivityV4.this);
                    builder.setTitle("Escolha uma opção");
                    final String[] options = { "Encomendar (Não disponivel)", "Não encomendar hoje! (Não disponivel)", "Remover Cliente"};

                    final int[] indice = new int[1];
                    builder.setSingleChoiceItems(options,-1, new DialogInterface.OnClickListener() {
                        public void  onClick(DialogInterface dialog, int item) {
                            indice[0] = item;
                        }
                    });
                    builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            switch (indice[0]){
                                case 0:
                                    /*encomendaBolos.Encomenda();
                                    setDataOnEncomendasBolos();*/
                                    break;
                                case 1:
                                    /*encomendaBolos.naoEncomenda();
                                    setDataOnEncomendasBolos();*/
                                    break;
                                case 2:
                                    encomenda.removeClienteEncomendaBolos(encomendaBolos);
                                    setDataOnEncomendasBolos();
                                    break;
                                default:
                                    break;
                            }
                        }});

                    AlertDialog actions;
                    actions = builder.create();
                    actions.show();
                    return false;
                }
            });

            listaEncomendas.setAdapter(adapter);
        }


    }

    private void chooseNewOrder(AdapterView<?> adapterView, int pos) {
        final LinearLayout.LayoutParams LayoutParams= new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        final EncomendaBolosV2 encomendaBolos = (EncomendaBolosV2) adapterView.getItemAtPosition(pos);
        final ArrayList<CheckBox> checks = new ArrayList<>();
        final ArrayList<LinearLayout> layoutsHorizontais = new ArrayList<>();
        final ScrollView scrollView = new ScrollView(getApplicationContext());

        final LinearLayout linearLayout = new LinearLayout(EncomendaActivityV4.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(LayoutParams);

        final LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 1.0f);
        final LinearLayout.LayoutParams paramsNumber= new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 2f);
        final LinearLayout.LayoutParams paramsCheckBox= new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, 2f);

        for (int i = 0; i < encomendaBolos.getTotalBolosDaEncomenda(); i++) {
            final LinearLayout linearLayoutHorizontal = new LinearLayout(EncomendaActivityV4.this);
            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
            linearLayoutHorizontal.setLayoutParams(LayoutParams);

            Spinner spinner1 = new Spinner(getApplicationContext());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_encomendas, tabelaPrecos.getListaBolos());

            int position = adapter.getPosition(encomendaBolos.getListaBolos().get(i));
            spinner1.setAdapter(adapter);
            spinner1.setSelection(position);
            spinner1.setLayoutParams(params);

            Spinner spinner2 = new Spinner(getApplicationContext());
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                    getApplicationContext(),
                    R.array.valores ,
                    R.layout.spinner_item_encomendas);
            spinner2.setAdapter(adapter2);
            spinner2.setSelection(encomendaBolos.getQuantidades().get(i));
            spinner2.setLayoutParams(paramsNumber);

            CheckBox checkBox = new CheckBox(getApplicationContext());
            checks.add(checkBox);
            checkBox.setLayoutParams(paramsCheckBox);

            linearLayoutHorizontal.addView(spinner1);
            linearLayoutHorizontal.addView(spinner2);
            linearLayoutHorizontal.addView(checkBox);

            layoutsHorizontais.add(linearLayoutHorizontal);

            linearLayout.addView(linearLayoutHorizontal);
        }

        scrollView.addView(linearLayout);
        scrollView.setLayoutParams(LayoutParams);


        AlertDialog alertDialog = new AlertDialog.Builder(EncomendaActivityV4.this)

                .setTitle("Encomenda de " + encomendaBolos.getClienteName())

                .setView(scrollView)

                .setNeutralButton("Add Produto", null)

                .setNegativeButton("Remover selecionados", null)

                .setPositiveButton("Guardar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        encomendaBolos.reset();
                        for (LinearLayout linha : layoutsHorizontais) {

                            String bolo = (String) ((Spinner)linha.getChildAt(0)).getSelectedItem();
                            int quantidade = ((Spinner)linha.getChildAt(1)).getSelectedItemPosition();

                            encomendaBolos.addBolos(bolo, quantidade);
                        }
                        setDataOnEncomendasBolos();
                    }
                }).create();

        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int countChecks = 0;
                ArrayList<CheckBox> checkBoxesToDelete = new ArrayList<>();
                for (CheckBox checkBox : checks) {
                    if (checkBox.isChecked()) {
                        //encomendaBolos.addBolos(((Spinner)layoutsHorizontais.get(countChecks).getChildAt(0)).getSelectedItemPosition() + 1, 0);
                        linearLayout.removeView(layoutsHorizontais.get(countChecks));
                        layoutsHorizontais.remove(countChecks);
                        checkBoxesToDelete.add(checkBox);
                    }
                    countChecks++;

                }
                for (CheckBox checkBox : checkBoxesToDelete) {
                    checks.remove(checkBox);
                }
            }
        });

        Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        neutralButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final LinearLayout linearLayoutHorizontal = new LinearLayout(EncomendaActivityV4.this);
                linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);
                linearLayoutHorizontal.setLayoutParams(LayoutParams);

                Spinner spinner1 = new Spinner(getApplicationContext());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_item_encomendas, tabelaPrecos.getListaBolos());

                spinner1.setAdapter(adapter);
                spinner1.setLayoutParams(params);

                Spinner spinner2 = new Spinner(getApplicationContext());
                ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                        getApplicationContext(),
                        R.array.valores ,
                        R.layout.spinner_item_encomendas);
                spinner2.setAdapter(adapter2);
                spinner2.setLayoutParams(paramsNumber);

                CheckBox checkBox = new CheckBox(getApplicationContext());
                checks.add(checkBox);
                checkBox.setLayoutParams(paramsCheckBox);

                linearLayoutHorizontal.addView(spinner1);
                linearLayoutHorizontal.addView(spinner2);
                linearLayoutHorizontal.addView(checkBox);

                layoutsHorizontais.add(linearLayoutHorizontal);

                linearLayout.addView(linearLayoutHorizontal);
            }
        });
    }

    private void setEncomendasDiaSeguinte() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar amanha = Calendar.getInstance();
        Date hj = new Date();
        Date dt = null;
        amanha.setTime(hj);
        amanha.add(Calendar.DATE, 1);  // number of days to add
        try {
            dt = formatter.parse(amanha.get(Calendar.DAY_OF_MONTH) + "/" + (amanha.get(Calendar.MONTH)+1) + "/" + amanha.get(Calendar.YEAR));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RegistosManager registosManager = getAllLogsEncomendas(getApplicationContext(), dt);
        ArrayList<Registo> registos = registosManager.getRegistosEncomendasThisDay(amanha.getTime());

        final ListView listaEncomendas = (ListView) findViewById(R.id.lista);

        ListAdapter adapter;

        if (registos.isEmpty()) {
            ArrayList<String> list = new ArrayList<>();
            list.add("Nenhuma encomenda para o dia de amanhã!");
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        } else {
            ArrayList<String> infos = new ArrayList<>();
            for (int i = 0; i < registos.size(); i++) {
                infos.add(registos.get(i).toStringEncomenda());
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infos);
        }


        listaEncomendas.setAdapter(adapter);

    }

    private String getStringDay() {
        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 24);
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;

        return dayOfWeek[day];
    }

    public void verEstimativa(View view) {
        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Estimativa de pão vendido para amanhã");


        alertDialogBuilder.setMessage(getSomaProdutos());

        alertDialogBuilder.setNeutralButton("Escolher dias", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pickIntervalDate(alertDialogBuilder);
            }
        });


        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //alertDialogBuilder.setCancelable(false);

        //alertDialogBuilder.show();
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    public void verEstimativa(Date inicio, Date fim) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);

        alertDialogBuilder.setTitle("Estimativa de pão vendido entre os dias " + formatter.format(inicio) + " : " + formatter.format(fim));

        alertDialogBuilder.setMessage(getSomaProdutosIntervalDate(inicio, fim));

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //alertDialogBuilder.setCancelable(false);

        //alertDialogBuilder.show();
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }



    private void pickIntervalDate(final android.app.AlertDialog.Builder oi) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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
                                new android.app.AlertDialog.Builder(builder.getContext())
                                        .setTitle("Operação impossivel")
                                        .setMessage("Falta selecionar uma data")
                                        .show();
                                return;
                            }

                            Date inicio = formatter.parse(startDateEditText.getText().toString());
                            Date fim = formatter.parse(endDateEditText.getText().toString());

                            if (!inicio.before(fim)){
                                if (!inicio.equals(fim)) {
                                    new android.app.AlertDialog.Builder(builder.getContext())
                                            .setTitle("Operação impossivel")
                                            .setMessage("Data de inicio maior que a data final")
                                            .show();
                                    return;
                                }
                            }

                            dialog.dismiss();
                            verEstimativa(inicio, fim);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setTitle("Escolher intervalo de tempo!")
                .setMessage("Escolher intervalo de tempo para somar as quantidades de pão")
                .show();
    }

    private String getSomaProdutos() {
        HashMap<String, Integer> desc;
        String produtoID;

        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dia = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dia == -1)
            dia = 6;

        String message = "";
        int count = 0;

        for (int i = 0; i < this.tabelaPrecos.getProdutos().size(); i++) {
            for (Cliente cliente : clientsManager.getClientsList()) {
                desc = cliente.getDescricaoHashMap().get(Cliente.dias[dia]);
                produtoID = Integer.toString(this.tabelaPrecos.getIDs().get(i));

                if (desc.containsKey(produtoID) && cliente.getAtivo()) {
                    count += desc.get(produtoID);
                }
            }

            if (count > 0)
                message += this.tabelaPrecos.getProdutos().get(i) + " : " + count + "\n";

            count = 0;
        }

        return message;
    }

    private int getIntDia(Calendar DateDia) {

        int dia = DateDia.get(Calendar.DAY_OF_WEEK) - 2;
        if (dia == -1)
            dia = 6;

        return dia;
    }

    private String getSomaProdutosIntervalDate(Date inicio, Date fim) {
        HashMap<String, Integer> desc;
        String produtoID;
        int count = 0;

        String message = "";

        Calendar inicioCalendar = Calendar.getInstance();
        inicioCalendar.setTime(inicio);
        Calendar fimCalendar = Calendar.getInstance();
        fimCalendar.setTime(fim);

        for (int i = 0; i < this.tabelaPrecos.getProdutos().size(); i++) {
            while (inicioCalendar.before(fimCalendar) || inicioCalendar.equals(fimCalendar)) {
                for (Cliente cliente : clientsManager.getClientsList()) {
                    desc = cliente.getDescricaoHashMap().get(Cliente.dias[getIntDia(inicioCalendar)]);
                    produtoID = Integer.toString(this.tabelaPrecos.getIDs().get(i));

                    if (desc.containsKey(produtoID) && cliente.getAtivo()) {
                        count += desc.get(produtoID);
                    }
                }
                inicioCalendar.add(Calendar.DATE, 1);
            }

            if (count > 0) {
                message += this.tabelaPrecos.getProdutos().get(i) + " : " + count + "\n";
            }

            inicioCalendar.setTime(inicio);
            count = 0;
        }

        return message;
    }

    public void addClienteEncomendaBolos(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insira o nome do Cliente");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                encomenda.addClienteEncomendaBolos(input.getText().toString());
                setDataOnEncomendasBolos();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}
