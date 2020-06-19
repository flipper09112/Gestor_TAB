package com.example.gestor_tab.bt;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.encomendas.EncomendaManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.example.gestor_tab.database.LogsBaseUtil.getLogsClient;

public class Connection_bt {

    public BluetoothDevice mmDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    private Activity activity;
    private Cliente cliente;

    public Connection_bt(Activity act, Cliente cliente){
        activity=act;
        this.cliente = cliente;
    }

    public void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if(mBluetoothAdapter == null) {
                activity.setContentView(R.layout.error_template);
                TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
                t1.setText("Descrição[002]\nNo bluetooth adapter available");
                return;
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBluetooth, 0);
            }

            final Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.isEmpty()) {
                activity.setContentView(R.layout.error_template);
                TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
                t1.setText("Descrição[003]\nNenhum dispositivo encontrado!\nSugestão: Emparelhe o dispositivo e volte a tentar.");
                return;
            }

            int position = 0, count = 0;
            final Spinner spinner = new Spinner(activity);
            final ArrayAdapter<String> devices = new ArrayAdapter<String>(activity, R.layout.spinner_item);
            for (BluetoothDevice btDevice : pairedDevices) {
                devices.add(btDevice.getName());
                if (btDevice.getName().equals("MTP-II")) {
                    position = count;
                }
                count++;
            }
            spinner.setAdapter(devices);
            spinner.setSelection(position);

            for (BluetoothDevice btDevice : pairedDevices) {
                if (btDevice.getName().equals(spinner.getSelectedItem().toString())) {
                    mmDevice = btDevice;
                    break;
                }
            }

            /*new AlertDialog.Builder(activity)

                    .setTitle("Imprimir")

                    .setMessage("Dispositivo de impressão")

                    .setView(spinner)

                    .setPositiveButton("SIM", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            for (BluetoothDevice btDevice : pairedDevices) {
                                if (btDevice.getName().equals(spinner.getSelectedItem().toString())) {
                                    mmDevice = btDevice;
                                    break;
                                }
                            }
                        }
                    })

                    .setNeutralButton("NÃO", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    })
                    .show();

             */

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void openBT() throws IOException {
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            this.mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            this.mmSocket.connect();
            this.mmOutputStream = mmSocket.getOutputStream();
            this.mmInputStream = mmSocket.getInputStream();

        } catch (Exception e) {

            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[001]\nDispositivo não ligado!");
        }
    }
    private void sendData(boolean loja, ArrayList<String> encomendas) {
        try {
            byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
            byte[] left= {0x1B, 'a',0x00};
            byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
            byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text



            String separador="";
            String finish="\n\n\n\n";
            for (int i=0;i<32;i++){
                separador+="-";
            }
            separador+="\n";

            cabecalho();

            mmOutputStream.write(left);
            mmOutputStream.write(separador.getBytes());

            detalhesCliente();

            mmOutputStream.write(left);
            mmOutputStream.write(separador.getBytes());

            if(loja) {
                descricao(separador);
            } else {
                tabela(separador, encomendas);
            }

            mmOutputStream.write(left);
            mmOutputStream.write(separador.getBytes());

            rodape();

            mmOutputStream.write(finish.getBytes());
            mmOutputStream.flush();



        }  catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[009]\nDispositivo desligado ou não encontrado!");
        } catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }
    }

    private void descricao(String separador) {
        RegistosManager registosManager = getLogsClient(activity.getApplicationContext(), cliente.getId());
        Spinner dia2Spiner = activity.findViewById(R.id.spinner);
        String texto = "";
        String temp = "";

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calInicio = Calendar.getInstance();
        Calendar calFim = Calendar.getInstance();
        try {
            calInicio.setTime(sdf.parse(cliente.getPagamentoPlusOneDay()));
            calFim.setTime(sdf.parse(dia2Spiner.getSelectedItem().toString()));
        } catch (ParseException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição\nErro na ultima data de pagamento do cliente!");
        }

        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left= {0x1B, 'a',0x00};
        byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

        String tabela="Dia                       Total\n";

        Registo registo = null;
        while (calInicio.before(calFim) || calInicio.equals(calFim)) {
            registo = registosManager.getRegistoDia(cliente.getId(), calInicio.getTime());

            if (registo != null) {
                temp = sdf.format(calInicio.getTime()) + "                 " + String.format(Locale.ROOT, "%.2f", registo.getTotal()) +"\n";
                for (int i = 1 ; i < registo.getInfo().split("\t").length - 1; i++) {
                    temp += registo.getInfo().split("\t")[i];
                }
                texto += temp + "\n";
            } else {
                temp = sdf.format(calInicio.getTime()) + "                 0.00\n";
                texto += temp;
            }

            calInicio.add(Calendar.DAY_OF_MONTH, 1);
        }

        try {
            mmOutputStream.write(left);
            mmOutputStream.write(tabela.getBytes());
            mmOutputStream.write(separador.getBytes());
            mmOutputStream.write(texto.getBytes());

        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[007]\nDispositivo desligado ou não encontrado!");
        }
    }

    private void rodape() {
        TextView total_despesa = (TextView) activity.findViewById(R.id.pagar);

        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left= {0x1B, 'a',0x00};
        byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

        String total="Total";
        String valor = total_despesa.getText().toString().split(" ")[0];

        String finish = "Obrigado pela Preferencia";

        int remain = 32 - 5 - valor.length();
        for (int i = 0; i < remain; i++) {
            total += " ";
        }

        total += valor;


        try {
            mmOutputStream.write(left);
            mmOutputStream.write(total.getBytes());

            mmOutputStream.write(center);
            mmOutputStream.write(finish.getBytes());

        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[008]\nDispositivo desligado ou não encontrado!");
        } catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }
    }

    private void tabela(String separador, ArrayList<String> encomendas) {
        Spinner dia2Spiner = activity.findViewById(R.id.spinner);

        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left= {0x1B, 'a',0x00};
        byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

        String tabela="QTD  Dias          Preco  Total\n";
        int[] countDays = this.cliente.getDaysToPrint(dia2Spiner.getSelectedItem().toString());

        String segunda = countDays[0] + "," + "SEGUNDA" + "," + cliente.getDespesa()[0] + "," + String.format(Locale.ROOT, "%.2f", countDays[0]*cliente.getDespesa()[0]) ;
        String terca = countDays[1] + "," + "TERCA" + "," + cliente.getDespesa()[1] + "," + String.format(Locale.ROOT,"%.2f", countDays[1]*cliente.getDespesa()[1]) ;
        String quarta = countDays[2] + "," + "QUARTA" + "," + cliente.getDespesa()[2] + "," + String.format(Locale.ROOT,"%.2f", countDays[2]*cliente.getDespesa()[2]) ;
        String quinta = countDays[3] + "," + "QUINTA" + "," + cliente.getDespesa()[3] + "," + String.format(Locale.ROOT,"%.2f", countDays[3]*cliente.getDespesa()[3]) ;
        String sexta = countDays[4] + "," + "SEXTA" + "," + cliente.getDespesa()[4] + "," + String.format(Locale.ROOT,"%.2f", countDays[4]*cliente.getDespesa()[4]) ;
        String sabado = countDays[5] + "," + "SABADO" + "," + cliente.getDespesa()[5] + "," + String.format(Locale.ROOT,"%.2f", countDays[5]*cliente.getDespesa()[5]) ;
        String domingo = countDays[6] + "," + "DOMINGO" + "," + cliente.getDespesa()[6] + "," + String.format(Locale.ROOT,"%.2f", countDays[6]*cliente.getDespesa()[6]) ;
        String extras = 1 + "," + "EXTRAS" + "," + cliente.getDespesa()[7] + "," + cliente.getDespesa()[7];

        segunda = fixSpaces(segunda);
        terca = fixSpaces(terca);
        quarta = fixSpaces(quarta);
        quinta = fixSpaces(quinta);
        sexta = fixSpaces(sexta);
        sabado = fixSpaces(sabado);
        domingo = fixSpaces(domingo);
        extras = fixSpaces(extras);

        try {
            mmOutputStream.write(left);
            mmOutputStream.write(tabela.getBytes());

            mmOutputStream.write(separador.getBytes());

            mmOutputStream.write(segunda.getBytes());
            mmOutputStream.write(terca.getBytes());
            mmOutputStream.write(quarta.getBytes());
            mmOutputStream.write(quinta.getBytes());
            mmOutputStream.write(sexta.getBytes());
            mmOutputStream.write(sabado.getBytes());
            mmOutputStream.write(domingo.getBytes());
            mmOutputStream.write(extras.getBytes());

            for (String encomenda : encomendas) {
                String text = encomenda.substring(encomenda.indexOf(":")+1).trim();
                text = text.replace("para", "do");
                text += "\n";
                mmOutputStream.write(text.getBytes());
            }


        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[007]\nDispositivo desligado ou não encontrado!");
        } catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }
    }


    private String fixSpaces(String text) {
        String result = "";
        int[] spaces = {5, 19, 26, 32};
        int remain = 0;

        for (int i = 0; i < text.split(",").length ; i++) {
            if (i == 3) {
                remain = spaces[i] - text.split(",")[i].length() - result.length();
                while (remain > 0) {
                    result += " ";
                    remain--;
                }
            }
            result += text.split(",")[i];
            remain = spaces[i] - result.length();
            while (remain > 0) {
                result += " ";
                remain--;
            }
        }

        return result;
    }

    private void detalhesCliente() {
        Spinner dia2Spiner = activity.findViewById(R.id.spinner);

        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left= {0x1B, 'a',0x00};
        byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

        String cliente = "Cliente : " + this.cliente.getName() + "\nNo. Cliente: " + this.cliente.getId() + "\n\n";

        String intervaloTempoCab = "Despesa correspondente:\n";
        String intervalo = "De: " + this.cliente.getPagamentoPlusOneDay() + "  ate  " + dia2Spiner.getSelectedItem().toString() + "\n";

        try {
            mmOutputStream.write(left);
            mmOutputStream.write(cliente.getBytes());

            mmOutputStream.write(center);
            mmOutputStream.write(intervaloTempoCab.getBytes());

            mmOutputStream.write(left);
            mmOutputStream.write(intervalo.getBytes());

        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[006]\nDispositivo desligado ou não encontrado!");
        } catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }
    }

    private void cabecalho() {

        byte[] center =  { 0x1b, 'a', 0x01 }; // center alignment
        byte[] left= {0x1B, 'a',0x00};
        byte[] format = {29, 33, 35 }; // manipulate your font size in the second parameter
        byte[] format2 = {29, 20, 35 }; // manipulate your font size in the second parameter
        byte[] cc = new byte[]{0x1B,0x21,0x00};  // 0- normal size text
        byte[] bb = new byte[]{0x1B,0x21,0x08};  // 1- only bold text
        byte[] bb2 = new byte[]{0x1B,0x21,0x20}; // 2- bold with medium text
        byte[] bb3 = new byte[]{0x1B,0x21,0x10}; // 3- bold with large text

        String titulo= "Artigos de Padaria e Pastelaria ao Domicilio \nDistribuidor(a): Maria Manuela\nContacto: 964690528\n";

        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy           HH:mm");
        String dateString = "Data: " + sdf.format(date);

        try {
            mmOutputStream.write(format);
            mmOutputStream.write(center);
            mmOutputStream.write(titulo.getBytes());

            mmOutputStream.write(left);
            mmOutputStream.write(dateString.getBytes());

        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");

        }  catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }
    }

    public void closeBT(){
        try {
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
        } catch (Exception e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[004]\nErro ao fechar ligação bt!");
        }
    }


    public void imprime(Cliente cliente, ArrayList<String> encomendas) throws IOException {
        findBT();
        openBT();
        sendData(false, encomendas);
        closeBT();
    }

    public void imprimeLS() throws IOException {
        findBT();
        openBT();
        sendData(true, null);
        closeBT();
    }

    private void sendData(ArrayList<Registo> registosEncomendasThisDay) {
        try {
            byte[] center = {0x1b, 'a', 0x01}; // center alignment
            byte[] left = {0x1B, 'a', 0x00};
            byte[] format = {29, 33, 35}; // manipulate your font size in the second parameter
            byte[] cc = new byte[]{0x1B, 0x21, 0x00};  // 0- normal size text
            byte[] bb = new byte[]{0x1B, 0x21, 0x08};  // 1- only bold text
            byte[] bb2 = new byte[]{0x1B, 0x21, 0x20}; // 2- bold with medium text
            byte[] bb3 = new byte[]{0x1B, 0x21, 0x10}; // 3- bold with large text

            String separador="";
            String finish="\n\n\n\n";
            for (int i=0;i<32;i++){
                separador+="-";
            }
            separador+="\n";

            for (Registo registo : registosEncomendasThisDay) {
                String id = "Cliente id: " + Integer.toString(registo.getId()) + "\n\n";

                mmOutputStream.write(left);
                mmOutputStream.write(id.getBytes());
                mmOutputStream.write(registo.getInfo().getBytes());
                mmOutputStream.write(separador.getBytes());
                mmOutputStream.flush();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            mmOutputStream.write("FIM \n\n\n".getBytes());
            mmOutputStream.flush();


        } catch (IOException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[009]\nDispositivo desligado ou não encontrado!");
        } catch (NullPointerException e) {
            activity.setContentView(R.layout.error_template);
            TextView t1=(TextView)this.activity.findViewById(R.id.textView27);
            t1.setText("Descrição[005]\nDispositivo desligado ou não encontrado!");
        }

    }

    public void imprimeencomendas(final ArrayList<Registo> registosEncomendasThisDay) throws IOException {
        findBT();
        openBT();
        sendData(registosEncomendasThisDay);

        closeBT();
    }
}
