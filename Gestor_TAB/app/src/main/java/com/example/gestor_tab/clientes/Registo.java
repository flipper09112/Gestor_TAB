package com.example.gestor_tab.clientes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.gestor_tab.database.DataBaseUtil.countOccurencesChar;
import static com.example.gestor_tab.database.DataBaseUtil.replaceOccurance;

public class Registo {

    private int idCliente;

    private String tipo;

    private String info;

    private Date data;

    private Date dateEncomenda;

    //for registo das lojas
    private float total;

    public String getInfo() {
        return this.info;
    }

    public Registo(int id, String tipo, String info, String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        //System.out.println(id + ":" + tipo + ":" + info + ":" + data);

        try {
            if (data != null)
                this.data = formatter.parse(data);
            this.idCliente = id;
            this.tipo = tipo;
            this.info = info;
            if (this.info.startsWith(" ")) {
                this.info = this.info.substring(1);
            }
            if (tipo.equals("ENCOMENDA") || tipo.equals("TOTALENCOMENDA")) {
                String date = this.info.split(" ")[3].split("\n")[0];
                this.dateEncomenda = formatter.parse(date);
            }
            if (tipo.equals("REGISTO")) {
                String date = this.info.split(" ")[3].split("\n")[0];
                this.dateEncomenda = formatter.parse(date);

                this.total = Float.parseFloat(this.info.split("\tTotal - ")[1]);
                this.info = this.info.split("\tTotal ;  ")[0];
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String data;
        if (this.data == null) {
            data = "";
        } else {
            Date date;
            data = formatter.format(this.data);
        }

        return tipo + " [" +  data + "]: " + info;
    }

    public String getTipo() {
        return this.tipo;
    }

    public String toStringEncomenda() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String data;
        if (this.data == null) {
            data = "";
        } else {
            Date date;
            data = formatter.format(this.data);
        }

        String text;
        text = info.replaceAll(";", "-");
        text = text.replaceAll("]", "\t");


        return tipo + " [" +  data + "]: " + text;
    }

    public int getId() {
        return this.idCliente;
    }

    public Date getDateEncomenda() {
        return this.dateEncomenda;
    }

    public float getTotal() {
        return this.total;
    }

    public Date getData() {
        return this.data;
    }

    public String toFileTxt() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String line = "";

        //antes :
        line = this.tipo + " [" + this.idCliente + "]: ";

        //info
        line += inverteInfo();

        //data
        line += "-" + formatter.format(this.data);

        return line;
    }

    private String inverteInfo() {
        String info = "";

        if (this.tipo.equals("PAGAMENTO") || this.tipo.equals("EXTRA")) {
            if (this.tipo.equals("PAGAMENTO"))
                info = this.info + "€ no dia";
            else
                info = this.info + " no dia";
        }
        else if (tipo.equals("EDICAO") || tipo.equals("SOBRA")) {
            info = this.info.replaceAll("\n", ",");
        }

        else if (tipo.equals("ENCOMENDA") || tipo.equals("REGISTO") || tipo.equals("TOTALENCOMENDA")) {
            info = this.info.replaceAll("\n", ",");
            info = info.replaceAll("\\.", "&");

            if (tipo.equals("REGISTO")) {

                info = info.replaceAll("\t", "]");
                info = info.replaceAll("-", ";");
                //info += "Total - " + String.format(Locale.ROOT, "%.2f", this.total);
            }
        }

        else if (tipo.equals("LOCALIZAÇÃO")) {
            int ocur = countOccurencesChar(this.info, '&');
            if (ocur > 1) {
                info = replaceOccurance(this.info, "&", "-", ocur);
                info = info.substring(0, info.indexOf("&"));
            }
            else
                info = this.info;

        } else if (tipo.equals("INATIVIDADE") || tipo.equals("NOVOCLIENTE") || tipo.equals("REMOVECLIENTE")) {
            info = this.info;
        }

        return info;
    }
}
