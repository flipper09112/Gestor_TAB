package com.example.gestor_tab.clientes;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class RegistosManager {

    private ArrayList<Registo> registos;

    public RegistosManager(ArrayList<Registo> registos) {
        this.registos = registos;
        Collections.reverse(this.registos);
    }

    public ArrayList<Registo> getRegistos() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (!registo.getTipo().equals("ENCOMENDA")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosEncomendas() {
        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("ENCOMENDA")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosEncomendasThisDay(Date nextDay) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("ENCOMENDA") && dateFormat.format(registo.getDateEncomenda()).equals(dateFormat.format(nextDay))) {
                list.add(registo);
            }
        }
        return list;
    }

}
