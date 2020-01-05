package com.example.gestor_tab.clientes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Registo {

    private int idCliente;

    private String tipo;

    private String info;

    private Date data;

    private Date dateEncomenda;

    public Registo(int id, String tipo, String info, String data) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (data != null)
                this.data = formatter.parse(data);
            this.idCliente = id;
            this.tipo = tipo;
            this.info = info;
            if (tipo.equals("ENCOMENDA")) {
                String date = this.info.split(" ")[4].split("\n")[0];
                this.dateEncomenda = formatter.parse(date);
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
}
