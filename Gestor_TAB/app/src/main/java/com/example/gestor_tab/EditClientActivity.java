package com.example.gestor_tab;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.enumClasses.DecimalDigitsInputFilter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static com.example.gestor_tab.database.DataBaseUtil.logs;

public class EditClientActivity extends AppCompatActivity {

    Cliente cliente;

    boolean newCliente = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);
        getSupportActionBar().hide();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        cliente = (Cliente) intent.getSerializableExtra("Cliente");

        if (cliente.getName().equals("Default"))
            newCliente = true;

        setConfigInputSectors();
        setDataOnScreen();
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


        spec = tabHost.newTabSpec("Info");
        spec.setContent(R.id.tab5);
        spec.setIndicator("Info");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Admin");
        spec.setContent(R.id.tab6);
        spec.setIndicator("Admin");
        tabHost.addTab(spec);

        spec = tabHost.newTabSpec("Calendário");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Calendário");
        tabHost.addTab(spec);

        editText = findViewById(R.id.extra);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        editText.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(9,2)});

    }

    private void setDataOnScreen() {
        float[] despesa = cliente.getDespesa();

        EditText editText = findViewById(R.id.segunda);
        editText.setText(Float.toString(despesa[0]));

        editText = findViewById(R.id.terca);
        editText.setText(Float.toString(despesa[1]));

        editText = findViewById(R.id.quarta);
        editText.setText(Float.toString(despesa[2]));

        editText = findViewById(R.id.quinta);
        editText.setText(Float.toString(despesa[3]));

        editText = findViewById(R.id.sexta);
        editText.setText(Float.toString(despesa[4]));

        editText = findViewById(R.id.sabado);
        editText.setText(Float.toString(despesa[5]));

        editText = findViewById(R.id.domingo);
        editText.setText(Float.toString(despesa[6]));

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
        else
            radioGroup.check(R.id.especial);

        editText = findViewById(R.id.data);
        editText.setText(cliente.getPagamento());

        editText = findViewById(R.id.extra);
        editText.setText(Float.toString(despesa[7]));
    }

    public void salvar(View view) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        EditText editText = findViewById(R.id.data);
        String newDate = editText.getText().toString();

        editText = findViewById(R.id.nome);
        String nome = editText.getText().toString();

        float[] despesa = Arrays.copyOf(cliente.getDespesa(), cliente.getDespesa().length);

        editText = findViewById(R.id.segunda);
        despesa[0] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.terca);
        despesa[1] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.quarta);
        despesa[2] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.quinta);
        despesa[3] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.sexta);
        despesa[4] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.sabado);
        despesa[5] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.domingo);
        despesa[6] = Float.parseFloat(editText.getText().toString());

        editText = findViewById(R.id.extra);
        despesa[7] = Float.parseFloat(editText.getText().toString());

        RadioButton radioButtonDiario = findViewById(R.id.diario);
        RadioButton radioButtonSemanal = findViewById(R.id.semanal);
        RadioButton radioButtonMensal = findViewById(R.id.mensal);
        RadioButton radioButtonEspecial = findViewById(R.id.especial);
        String tipoPagamento = "";

        if (radioButtonDiario.isChecked()) {
            tipoPagamento = "D";
        } else if (radioButtonSemanal.isChecked()) {
            tipoPagamento = "S";
        } else if (radioButtonMensal.isChecked()) {
            tipoPagamento = "M";
        } else if (radioButtonEspecial.isChecked()) {
            tipoPagamento = "E";
        }

        try {
            String mudancas = cliente.updateCliente(nome, dateFormat.parse(newDate), tipoPagamento, despesa);
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
            //TODO log new cliente
        }
        startActivity(intent);
    }
}
