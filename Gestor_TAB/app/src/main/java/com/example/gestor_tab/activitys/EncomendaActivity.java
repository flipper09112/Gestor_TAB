package com.example.gestor_tab.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.encomendas.Encomenda;
import com.example.gestor_tab.encomendas.EncomendaBolos;
import com.example.gestor_tab.encomendas.EncomendaManager;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.gestor_tab.database.EncomendaDistribuidorBaseUtil.getEncomenda;
import static com.example.gestor_tab.database.EncomendaDistribuidorBaseUtil.saveEncomenda;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogs;
import static com.example.gestor_tab.database.LogsBaseUtil.getAllLogsEncomendas;

public class EncomendaActivity extends AppCompatActivity {

    private String[] dayOfWeek = {"DOMINGO", "SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO"};

    NotificationManager notificationManager;

    File file;

    EncomendaManager encomendaManager;

    Encomenda encomenda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        file = new File(getExternalFilesDir(null), "EncomendasPanilima.txt");

        encomendaManager = new EncomendaManager();
        getEncomenda(file, encomendaManager);

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 24);
        int day = c.get(Calendar.DAY_OF_WEEK) - 1;

        encomenda = encomendaManager.getEncomenda(dayOfWeek[day]);

        setDataOnScreen();
        setDataOnEncomendasBolos();
        setInfoSobras();
        setEncomendasDiaSeguinte();
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
            list.add("Nenhum cliente registado! Long press para adicionar.");

            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
            listaEncomendas.setAdapter(adapter);

        } else {
            ArrayList<EncomendaBolos> list = new ArrayList<>();

            final ArrayAdapter<EncomendaBolos> adapter = new ArrayAdapter<EncomendaBolos>(this, android.R.layout.simple_list_item_1, encomenda.getEncomendaBolosArrayList());

            listaEncomendas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                    final EncomendaBolos encomendaBolos = (EncomendaBolos) adapterView.getItemAtPosition(i);
                    final ArrayList<CheckBox> checks = new ArrayList<>();
                    final ArrayList<LinearLayout> layoutsHorizontais = new ArrayList<>();

                    final ScrollView scrollView = new ScrollView(getApplicationContext());

                    final LinearLayout linearLayout = new LinearLayout(EncomendaActivity.this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    for (int tipoBolo : encomendaBolos.getTipoBolos()) {
                        final LinearLayout linearLayoutHorizontal = new LinearLayout(EncomendaActivity.this);
                        linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                        Spinner spinner1 = new Spinner(getApplicationContext());
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                getApplicationContext(),
                                R.array.bolos_array ,
                                R.layout.spinner_item_encomendas);


                        spinner1.setAdapter(adapter);
                        spinner1.setSelection(tipoBolo - 1);

                        Spinner spinner2 = new Spinner(getApplicationContext());
                        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                getApplicationContext(),
                                R.array.valores ,
                                R.layout.spinner_item_encomendas);

                        spinner2.setAdapter(adapter2);
                        spinner2.setSelection(encomendaBolos.getQuantidadeBolos(tipoBolo));

                        CheckBox checkBox = new CheckBox(getApplicationContext());
                        checks.add(checkBox);

                        linearLayoutHorizontal.addView(spinner1);
                        linearLayoutHorizontal.addView(spinner2);
                        linearLayoutHorizontal.addView(checkBox);

                        layoutsHorizontais.add(linearLayoutHorizontal);

                        linearLayout.addView(linearLayoutHorizontal);
                    }

                    scrollView.addView(linearLayout);

                    final RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    AlertDialog alertDialog = new AlertDialog.Builder(EncomendaActivity.this)

                            .setTitle("Encomenda de " + encomendaBolos.getName())

                            .setView(scrollView)

                            .setNeutralButton("Add", null)

                            .setNegativeButton("Remover selecionados", null)

                            .setPositiveButton("Guardar", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    for (LinearLayout linha : layoutsHorizontais) {
                                        int boloID = ((Spinner)linha.getChildAt(0)).getSelectedItemPosition() + 1;
                                        int quantidade = ((Spinner)linha.getChildAt(1)).getSelectedItemPosition();
                                        encomendaBolos.addBolos(boloID, quantidade);
                                    }
                                    setDataOnEncomendasBolos();
                                }
                            })

                            .show();

                    Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int countChecks = 0;
                            ArrayList<CheckBox> checkBoxesToDelete = new ArrayList<>();
                            for (CheckBox checkBox : checks) {
                                if (checkBox.isChecked()) {
                                    encomendaBolos.addBolos(((Spinner)layoutsHorizontais.get(countChecks).getChildAt(0)).getSelectedItemPosition() + 1, 0);
                                    linearLayout.removeView(layoutsHorizontais.get(countChecks));
                                    layoutsHorizontais.remove(countChecks);
                                    checkBoxesToDelete.add(checkBox);
                                }
                                countChecks++;

                            }
                            for (CheckBox checkBox : checkBoxesToDelete) {
                                checks.remove(checkBox);
                            }
                            setDataOnEncomendasBolos();
                        }
                    });

                    Button neutralButton = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
                    neutralButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final LinearLayout linearLayoutHorizontal = new LinearLayout(EncomendaActivity.this);
                            linearLayoutHorizontal.setOrientation(LinearLayout.HORIZONTAL);

                            Spinner spinner1 = new Spinner(getApplicationContext());
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                                    getApplicationContext(),
                                    R.array.bolos_array ,
                                    R.layout.spinner_item_encomendas);

                            spinner1.setAdapter(adapter);

                            Spinner spinner2 = new Spinner(getApplicationContext());
                            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                                    getApplicationContext(),
                                    R.array.valores ,
                                    R.layout.spinner_item_encomendas);

                            spinner2.setAdapter(adapter2);

                            CheckBox checkBox = new CheckBox(getApplicationContext());
                            checks.add(checkBox);

                            linearLayoutHorizontal.addView(spinner1);
                            linearLayoutHorizontal.addView(spinner2);
                            linearLayoutHorizontal.addView(checkBox);

                            layoutsHorizontais.add(linearLayoutHorizontal);

                            linearLayout.addView(linearLayoutHorizontal);

                            setDataOnEncomendasBolos();
                        }
                    });
                }
            });

            listaEncomendas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final EncomendaBolos encomendaBolos = (EncomendaBolos) adapterView.getItemAtPosition(i);

                    AlertDialog.Builder builder = new AlertDialog.Builder(EncomendaActivity.this);
                    builder.setTitle("Escolha uma opção");
                    final String[] options = { "Encomendar", "Não encomendar hoje!"};

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
                                    encomendaBolos.Encomenda();
                                    setDataOnEncomendasBolos();
                                    break;
                                case 1:
                                    encomendaBolos.naoEncomenda();
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

    @SuppressLint("SetTextI18n")
    private void setDataOnScreen() {
        EditText editText = findViewById(R.id.pParis);
        editText.setText(Integer.toString(encomenda.getPaoParis()));

        editText = findViewById(R.id.biju);
        editText.setText(Integer.toString(encomenda.getBijuTrigo()));

        editText = findViewById(R.id.pLata);
        editText.setText(Integer.toString(encomenda.getPaoLata()));

        editText = findViewById(R.id.pIntegral);
        editText.setText(Integer.toString(encomenda.getPaoIntegral()));

        editText = findViewById(R.id.escuro);
        editText.setText(Integer.toString(encomenda.getBijuEscuro()));

        editText = findViewById(R.id.bicas);
        editText.setText(Integer.toString(encomenda.getBicas()));

        editText = findViewById(R.id.cacetes);
        editText.setText(Integer.toString(encomenda.getCacetes()));

        editText = findViewById(R.id.cIntegral);
        editText.setText(Integer.toString(encomenda.getCacetinhosIntegral()));

        editText = findViewById(R.id.bolasKg);
        editText.setText(Integer.toString(encomenda.getBolasTrigoKg()));

        editText = findViewById(R.id.bolasHalfKg);
        editText.setText(Integer.toString(encomenda.getBolasTrigoHalfKg()));

        editText = findViewById(R.id.bmHalfKg);
        editText.setText(Integer.toString(encomenda.getBroaMilhoHalfKg()));

        editText = findViewById(R.id.bmKg);
        editText.setText(Integer.toString(encomenda.getBroaMilhoKg()));

        editText = findViewById(R.id.bcKg);
        editText.setText(Integer.toString(encomenda.getBroaCenteioKg()));

        editText = findViewById(R.id.bcHalfKg);
        editText.setText(Integer.toString(encomenda.getBroaCenteioHalfKg()));

        editText = findViewById(R.id.pqKg);
        editText.setText(Integer.toString(encomenda.getPaoQuadoKg()));

        editText = findViewById(R.id.pqHalfKg);
        editText.setText(Integer.toString(encomenda.getPaoQuadoHalfKg()));

        editText = findViewById(R.id.regueifa);
        editText.setText(Integer.toString(encomenda.getRegueifa()));

        editText = findViewById(R.id.regueifaG);
        editText.setText(Integer.toString(encomenda.getRegueifaG()));
    }

    private void getDataOnScreen() {
        EditText editText = findViewById(R.id.pParis);
        encomenda.setPaoParis(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.biju);
        encomenda.setBijuTrigo(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.pLata);
        encomenda.setPaoLata(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.pIntegral);
        encomenda.setPaoIntegral(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.escuro);
        encomenda.setBijuEscuro(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bicas);
        encomenda.setBicas(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.cacetes);
        encomenda.setCacetes(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.cIntegral);
        encomenda.setCacetinhosIntegral(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bolasKg);
        encomenda.setBolasTrigoKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bolasHalfKg);
        encomenda.setBolasTrigoHalfKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bmHalfKg);
        encomenda.setBroaMilhoHalfKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bmKg);
        encomenda.setBroaMilhoKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bcKg);
        encomenda.setBroaCenteioKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.bcHalfKg);
        encomenda.setBroaCenteioHalfKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.pqKg);
        encomenda.setPaoQuadoKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.pqHalfKg);
        encomenda.setPaoQuadoHalfKg(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.regueifa);
        encomenda.setRegueifa(Integer.parseInt(editText.getText().toString()));

        editText = findViewById(R.id.regueifaG);
        encomenda.setRegueifaG(Integer.parseInt(editText.getText().toString()));
    }

    public void sendEmail(View view) {
        getDataOnScreen();

        String data = encomenda.toStringWithoutZeros()
                + "--------------------------------------------------------\n\n"
                + encomenda.toStringEncomendaBolos();
        saveEncomenda(file, encomendaManager);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"encomendas@panilima.pt"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Encomenda");
        i.putExtra(Intent.EXTRA_TEXT   , data);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(EncomendaActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


}
