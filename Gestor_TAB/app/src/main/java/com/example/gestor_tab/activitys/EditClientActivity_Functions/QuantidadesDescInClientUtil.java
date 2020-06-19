package com.example.gestor_tab.activitys.EditClientActivity_Functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.gestor_tab.R;
import com.example.gestor_tab.activitys.ClienteActivity;
import com.example.gestor_tab.activitys.EditClientActivity;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class QuantidadesDescInClientUtil {
    private String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};

    public static int SEGUNDA = 0;
    public static int TERCA = 1;
    public static int QUARTA = 2;
    public static int QUINTA = 3;
    public static int SEXTA = 4;
    public static int SABADO = 5;
    public static int DOMINGO = 6;

    private int dia;
    private final Context context;
    private final Cliente cliente;
    private final Activity activity;
    private final TabelaPrecos tabelaPrecos;
    private AlertDialog alertDialog;
    private ArrayList<CheckBox> checkDias;

    public QuantidadesDescInClientUtil(final Context context, final Cliente cliente, final int dia, final TabelaPrecos tabelaPrecos) {
        this.dia = dia;
        this.cliente = cliente;
        this.context = context;
        this.activity = (Activity) context;
        this.tabelaPrecos = tabelaPrecos;
    }

    public void run() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

        // Get the layout inflater
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.alterecao_quantidades_desc, null);

        setInfoInView(view);

        //alertDialogBuilder.setCancelable(false);

        //alertDialogBuilder.show();
        alertDialog = alertDialogBuilder.create();

        alertDialog.show();
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setAttributes(layoutParams);

        alertDialog.getWindow().setContentView(view);
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

    }

    private void setInfoInView(View view) {
        TextView dia = view.findViewById(R.id.dia);
        Spinner spinner = (Spinner) view.findViewById(R.id.listaPao);
        EditText editText = view.findViewById(R.id.editTextProduto);
        Button button = view.findViewById(R.id.button5);
        Button salvar = view.findViewById(R.id.salvar);
        Button sair = view.findViewById(R.id.sair);
        LinearLayout linearLayout = view.findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow = view.findViewById(R.id.row);




        dia.setText("Alteração do registo de " + dias[this.dia]);
        setCheckboxConfig(view);
        setSpinnerConfig(spinner, editText);
        setButtonFunction(this.context, button, linearLayout, linearLayoutRow);
        setButtonFunctionSalvar(this.context, salvar, linearLayout, linearLayoutRow);
        setUltimoRegistoEncomenda(linearLayout, linearLayoutRow);

        sair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void setUltimoRegistoEncomenda(LinearLayout linearLayout, LinearLayout linearLayoutRow) {
        boolean first = true;

        for (String dia : this.cliente.getQuantidades().keySet()) {
            if (Cliente.dias[this.dia].equals(dia)) {
                for (String produto : this.cliente.getQuantidades().get(dia).keySet()) {
                    if (first) {
                        first = false;
                        setDataInFirstRow(linearLayoutRow, tabelaPrecos.getNomeProdutoFromID(produto), this.cliente.getQuantidades().get(dia).get(produto));
                    } else {
                        setDataInRow(linearLayout, linearLayoutRow, tabelaPrecos.getNomeProdutoFromID(produto), this.cliente.getQuantidades().get(dia).get(produto));
                    }
                }
            }
        }

    }

    private void setDataInFirstRow(LinearLayout linearLayoutRow, String nomeProdutoFromID, Integer quandtidade) {
        Spinner spinner = (Spinner) linearLayoutRow.getChildAt(0);
        EditText editText = (EditText) linearLayoutRow.getChildAt(1);

        if (nomeProdutoFromID != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            int position = adapter.getPosition(nomeProdutoFromID);
            spinner.setSelection(position);
        }

        if (quandtidade != -1) {
            editText.setText(Integer.toString(quandtidade));
        }
    }

    private void setDataInRow(LinearLayout linearLayout, LinearLayout linearLayoutRow, String produto, Integer integer) {
        addProduto(linearLayout, linearLayoutRow, produto, integer);
    }

    private void setCheckboxConfig(View view) {
        CheckBox seg = view.findViewById(R.id.check_seg);
        CheckBox ter = view.findViewById(R.id.check_ter);
        CheckBox qua = view.findViewById(R.id.check_qua);
        CheckBox qui = view.findViewById(R.id.check_qui);
        CheckBox sex = view.findViewById(R.id.check_sex);
        CheckBox sab = view.findViewById(R.id.check_sab);
        CheckBox dom = view.findViewById(R.id.check_dom);

        checkDias = new ArrayList<>();
        checkDias.add(seg);
        checkDias.add(ter);
        checkDias.add(qua);
        checkDias.add(qui);
        checkDias.add(sex);
        checkDias.add(sab);
        checkDias.add(dom);

        checkDias.get(this.dia).setChecked(true);

    }

    private void setButtonFunctionSalvar(final Context context, final Button salvar, final LinearLayout linearLayout, final LinearLayout linearLayoutRow) {

        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float quantidade;
                HashMap<String, Integer> descricao = new HashMap<>();

                for (int pos = 0; pos < linearLayout.getChildCount() - 1; pos++) {
                    LinearLayout row = (LinearLayout) linearLayout.getChildAt(pos);

                    Spinner spinner = (Spinner) row.getChildAt(0);
                    EditText editText = (EditText) row.getChildAt(1);

                    quantidade = Float.parseFloat(editText.getText().toString());

                    if (quantidade > 0) {
                        if (quantidade % 1 == 0) {
                            descricao.put(Integer.toString(tabelaPrecos.getIDFromProduct(spinner.getSelectedItem().toString())), (int) quantidade);
                        } else {
                            Toast.makeText(context, "Some Problem", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                int countDia = 0;
                for (CheckBox checkBox : checkDias) {
                    if (checkBox.isChecked()) {
                        cliente.replaceDescricaoQuantidades(countDia, descricao, tabelaPrecos);
                    }
                    countDia++;
                }
                try {
                    EditClientActivity editClientActivity = (EditClientActivity) context;
                    editClientActivity.setDataOnScreen();

                } catch (ClassCastException e) {
                    ClienteActivity clienteActivity = (ClienteActivity) context;
                    clienteActivity.setDespesaInChangeQuantidadesLayout();
                }

                alertDialog.dismiss();
            }
        });


    }

    private void setButtonFunction(final Context context, final Button button, final LinearLayout linearLayout, final LinearLayout linearLayoutRow) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            addProduto(linearLayout, linearLayoutRow, null, -1);
            }
        });
    }

    private void addProduto(LinearLayout linearLayout, LinearLayout linearLayoutRow, String produto, int quandtidade) {
        LinearLayout newLayout = new LinearLayout(context);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramSpinner = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context,
                R.layout.spinner_item_encomendas,
                tabelaPrecos.getProdutos()
        );

        spinner.setAdapter(adapter);
        spinner.setLayoutParams(paramSpinner);

        if (produto != null) {
            int position = adapter.getPosition(produto);
            spinner.setSelection(position);
        }


        final EditText editText = new EditText(context);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setText("0");
        editText.setLayoutParams(paramText);
        editText.setEms(10);

        if (quandtidade != -1) {
            editText.setText(Integer.toString(quandtidade));
        }

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

    private void setSpinnerConfig(Spinner spinner, final EditText editText) {
        ArrayAdapter produtos;

        if (tabelaPrecos == null) {
            ArrayList<String> listaErro = new ArrayList();
            listaErro.add("Nenhum produto encontrado!");
            produtos = new ArrayAdapter<String>(context, R.layout.spinner_item, listaErro);
        } else {
            produtos = new ArrayAdapter<String>(context, R.layout.spinner_item, tabelaPrecos.getProdutos());
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
    }


}
