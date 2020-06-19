package com.example.gestor_tab.activitys.ClienteActivity_Functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gestor_tab.R;
import com.example.gestor_tab.activitys.EditClientActivity_Functions.QuantidadesDescInClientUtil;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.database.ListaPrecosBaseUtil;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;

import java.util.Calendar;

import static com.example.gestor_tab.database.LogsBaseUtil.logs;


public class OutrasOpcoes {
    private final Context context;
    private final Cliente cliente;
    private final Activity activity;

    public OutrasOpcoes(final Context context, final Cliente cliente) {
        this.cliente = cliente;
        this.context = context;
        this.activity = (Activity) context;
    }

    public void run() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Escolha uma opção");

        alertDialogBuilder.setItems(new CharSequence[]
                        {"Definir alteração de quantidades", "Outra opção (indisponivel)"/*, "button 3", "button 4"*/},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        switch (which) {
                            case 0:
                                if (cliente.getTipoPagamento().equals("M")) {
                                    alteracaoQuantidadesMes();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(context, "Não disponivel para este cliente", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 1:
                                Toast.makeText(context, "Não disponivel", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(context, "clicked 3", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(context, "clicked 4", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });

        // set dialog message
        //alertDialogBuilder.setCancelable(false);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alteracaoQuantidadesMes() {
        activity.setContentView(R.layout.alterecao_quantidades);
        setConfigInputSectors();
        setDataOnScreen();
    }

    private void setConfigInputSectors() {
        int mYear, mMonth, mDay;
        final TextView textViewDate = this.activity.findViewById(R.id.textView44);

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        textViewDate.setText(mDay + "/" + (mMonth + 1) + "/" + mYear);


        EditText editText = activity.findViewById(R.id.segunda);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.terca);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.quarta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.quinta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.sexta);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.sabado);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        editText = activity.findViewById(R.id.domingo);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(9, 2)});

        TextView seg = activity.findViewById(R.id.textseg);
        TextView ter = activity.findViewById(R.id.textter);
        TextView qua = activity.findViewById(R.id.textqua);
        TextView qui = activity.findViewById(R.id.textqui);
        TextView sex = activity.findViewById(R.id.textsex);
        TextView sab = activity.findViewById(R.id.textsab);
        TextView dom = activity.findViewById(R.id.textdom);

        View.OnClickListener funcao = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.textseg) {
                    TabelaPrecos tabelaPrecos = new ListaPrecosBaseUtil().getTabela(context);

                    QuantidadesDescInClientUtil quantidadesDescInClientUtil = new QuantidadesDescInClientUtil(context, cliente, 0, tabelaPrecos);
                    quantidadesDescInClientUtil.run();

                } else if (v.getId() == R.id.textter) {

                } else if (v.getId() == R.id.textqua) {

                } else if (v.getId() == R.id.textqui) {

                } else if (v.getId() == R.id.textsex) {

                } else if (v.getId() == R.id.textsab) {

                } else if (v.getId() == R.id.textdom) {

                }
            }
        };

        seg.setOnClickListener(funcao);
        ter.setOnClickListener(funcao);
        qua.setOnClickListener(funcao);
        qui.setOnClickListener(funcao);
        sex.setOnClickListener(funcao);
        sab.setOnClickListener(funcao);
        dom.setOnClickListener(funcao);

    }



    public void setDataOnScreen() {
        float[] despesa = cliente.getDespesa();

        EditText editText = activity.findViewById(R.id.segunda);
        editText.setText(Float.toString(despesa[0]));

        editText = activity.findViewById(R.id.terca);
        editText.setText(Float.toString(despesa[1]));

        editText = activity.findViewById(R.id.quarta);
        editText.setText(Float.toString(despesa[2]));

        editText = activity.findViewById(R.id.quinta);
        editText.setText(Float.toString(despesa[3]));

        editText = activity.findViewById(R.id.sexta);
        editText.setText(Float.toString(despesa[4]));

        editText = activity.findViewById(R.id.sabado);
        editText.setText(Float.toString(despesa[5]));

        editText = activity.findViewById(R.id.domingo);
        editText.setText(Float.toString(despesa[6]));
    }

    public float[] getDataFromScreen() {
        float[] despesa = new float[8];

        EditText editText = activity.findViewById(R.id.segunda);
        despesa[0] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.terca);
        despesa[1] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.quarta);
        despesa[2] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.quinta);
        despesa[3] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.sexta);
        despesa[4] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.sabado);
        despesa[5] = Float.parseFloat(editText.getText().toString());

        editText = activity.findViewById(R.id.domingo);
        despesa[6] = Float.parseFloat(editText.getText().toString());

        despesa[7] = this.cliente.getDespesa()[7];

        return despesa;
    }



}
