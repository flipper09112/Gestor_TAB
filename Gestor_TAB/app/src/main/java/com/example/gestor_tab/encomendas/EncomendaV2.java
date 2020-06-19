package com.example.gestor_tab.encomendas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EncomendaV2 {

    private String dia;

    private ArrayList<String> produtos;

    private ArrayList<Float> quantidades;

    private ArrayList<EncomendaBolosV2> encomendaBolosArrayList;

    public EncomendaV2(String dia, ArrayList<String> produtos, ArrayList<Float> quantidades, ArrayList<EncomendaBolosV2> encomendaBolosArrayList) {
        this.dia = dia;
        this.produtos = (ArrayList<String>) produtos.clone();
        this.quantidades = (ArrayList<Float>) quantidades.clone();
        this.encomendaBolosArrayList = encomendaBolosArrayList;
    }

    public String getDia() {
        return this.dia;
    }

    public float getQuantidade(String produto) {
        try {
            int index = this.produtos.indexOf(produto);
            return quantidades.get(index);

        } catch (Exception e) {
            return 0;
        }
    }

    public void addClienteEncomendaBolos(String s) {
        encomendaBolosArrayList.add(new EncomendaBolosV2(s));
    }

    public List<EncomendaBolosV2> getEncomendaBolosArrayList() {
        return this.encomendaBolosArrayList;
    }

    public void setQuantidade(String toString, float parseFloat) {
        int index = produtos.indexOf(toString);

        if (index == -1){
            produtos.add(toString);
            quantidades.add(parseFloat);

        } else {
            quantidades.set(index, parseFloat);
        }

    }

    public String toStringWithoutZeros() {
        String text = "";

        for (int i = 0; i < produtos.size(); i++) {
            if (quantidades.get(i) > 0) {
                if (quantidades.get(i)%1 == 0)
                    text += produtos.get(i) + " - " + String.format("%.0f", quantidades.get(i)) + "\n\n";
                else
                    text += produtos.get(i) + " - " + quantidades.get(i) + "\n\n";
            }
        }

        return text;
    }

    public String toStringEncomendaBolos() {

        String text = "";
        for (int i = 0; i < this.encomendaBolosArrayList.size(); i++) {
            text += this.encomendaBolosArrayList.get(i).toStringWithoutName();
            text += "--------------------------------------------------------\n\n";
        }
        return text;
    }

    @Override
    public String toString() {
        String text = this.dia + "\n";

        for (int i = 0; i < produtos.size(); i++) {
            text += this.produtos.get(i) + " - " + this.quantidades.get(i) + "\n";
        }

        return text;
    }

    public String toLog() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar c = Calendar.getInstance();
        Date date = new Date();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, 24);

        String info = "Encomenda para dia " + formatter.format(c.getTime()) + ",";

        for (int i = 0; i < produtos.size(); i++) {
            if (quantidades.get(i) > 0) {
                if (quantidades.get(i) % 1 == 0) {
                    info += "]" + produtos.get(i) + " ; " + String.format("%.0f", quantidades.get(i)) + ",";
                } else {
                    String number = String.format("%.3f", quantidades.get(i));
                    number = number.replace(",", "&");

                    info += "]" + produtos.get(i) + " ; " + number + ",";
                }
            }
        }

        c.setTime(date);
        info += "-" + formatter.format(c.getTime());

        return info;
    }

    public void removeClienteEncomendaBolos(EncomendaBolosV2 encomendaBolos) {
        this.encomendaBolosArrayList.remove(encomendaBolos);
    }
}
