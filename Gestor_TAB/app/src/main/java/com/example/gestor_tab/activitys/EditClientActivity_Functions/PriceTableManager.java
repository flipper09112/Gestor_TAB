package com.example.gestor_tab.activitys.EditClientActivity_Functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.database.ListaClientesBaseUtil;
import com.example.gestor_tab.database.ListaPrecosBaseUtil;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;

import java.io.IOException;
import java.util.ArrayList;

import jxl.read.biff.BiffException;

public class PriceTableManager {


    private final Context context;
    private final Cliente cliente;
    private final Activity activity;
    private final TabelaPrecos tabelaPrecos;
    private ClientsManager clienteManager;
    private Intent intent = null;

    public PriceTableManager(final Context applicationContext, final Cliente cliente, final TabelaPrecos tabelaPrecos) {

        this.cliente = cliente;
        this.context = applicationContext;
        this.activity = (Activity) applicationContext;
        this.tabelaPrecos = tabelaPrecos;

        try {
            this.clienteManager = new ListaClientesBaseUtil(this.activity).getData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

    }


    public void run() {
        this.activity.setContentView(R.layout.price_list_new_client_ls);
        setSalvarButtonConfig();
        setDataInList();
    }

    private void setSalvarButtonConfig() {
        Button saveButton = this.activity.findViewById(R.id.button15);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNewPrices();

                if (intent != null) {
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        });

    }

    private void getNewPrices() {
        LinearLayout linearLayout = this.activity.findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow;

        ArrayList<Float> precos = new ArrayList<>();

        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            linearLayoutRow = (LinearLayout) linearLayout.getChildAt(i);
            Spinner spinner = (Spinner) linearLayoutRow.getChildAt(0);
            EditText editText= (EditText) linearLayoutRow.getChildAt(1);

            precos.add(Float.parseFloat(editText.getText().toString()));
        }

        tabelaPrecos.setNewClientInfo(this.cliente.getId(), precos);
        new ListaPrecosBaseUtil().saveTabela(this.context, tabelaPrecos);
    }

    private void setDataInList() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Escolha uma opção");

        alertDialogBuilder.setItems(getPsiibilidadesPrecosClientes(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Float> precos;

                switch (which) {
                    case 0:
                        precos = tabelaPrecos.getPrecos();
                        break;
                    default:
                        precos = tabelaPrecos.getTabelaPrecosCliente(tabelaPrecos.getClientes().get(which - 1));
                        break;
                }

                for (int i = 0; i< tabelaPrecos.getProdutos().size(); i++) {
                    addProduto(tabelaPrecos.getProdutos().get(i), precos.get(i));
                }
                LinearLayout linearLayout = activity.findViewById(R.id.listaItems);
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
            }
        });

        alertDialogBuilder.create().show();

    }

    private CharSequence[] getPsiibilidadesPrecosClientes() {
        CharSequence[] sequence = new String[1 + this.tabelaPrecos.getClientes().size()];
        int count = 0;
        sequence[count] = "Preço ao público";
        count++;

        for (int clienteId : this.tabelaPrecos.getClientes()) {
            sequence[count] = this.clienteManager.getClienteName(clienteId);
            count++;
        }

        return sequence;
    }

    public void addProduto(String produto, float valor) {
        LinearLayout linearLayout = this.activity.findViewById(R.id.listaItems);
        LinearLayout linearLayoutRow = this.activity.findViewById(R.id.row);

        LinearLayout newLayout = new LinearLayout(this.context);

        LinearLayout.LayoutParams paramParent = (LinearLayout.LayoutParams) linearLayout.getChildAt(0).getLayoutParams();
        newLayout.setLayoutParams(paramParent);

        LinearLayout.LayoutParams paramSpinner = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(0).getLayoutParams();
        LinearLayout.LayoutParams paramText = (LinearLayout.LayoutParams) linearLayoutRow.getChildAt(1).getLayoutParams();

        newLayout.setOrientation(LinearLayout.HORIZONTAL);

        Spinner spinner = new Spinner(this.context);
        ArrayList<String> lProduto = new ArrayList<>();
        lProduto.add(produto);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.context,
                R.layout.spinner_item_encomendas,
                lProduto);
        spinner.setAdapter(adapter);
        spinner.setLayoutParams(paramSpinner);


        EditText editText = new EditText(this.context);
        editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});
        editText.setText(String.format("%.2f", valor).replaceAll(",", "."));

        editText.setLayoutParams(paramText);
        editText.setEms(10);

        newLayout.addView(spinner);
        newLayout.addView(editText);

        linearLayout.addView(newLayout, linearLayout.getChildCount() - 1);
    }

    public void deleteCliente(Cliente cliente) {
        tabelaPrecos.removeCliente(cliente.getId());
        new ListaPrecosBaseUtil().saveTabela(this.context, tabelaPrecos);
    }

    public void setIntent(final Intent intent) {
        this.intent = intent;
    }
}
