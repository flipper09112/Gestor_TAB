package com.example.gestor_tab.clientes;

import android.widget.Spinner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RegistosManager {

    private ArrayList<Registo> registos = new ArrayList<>();

    public RegistosManager(ArrayList<Registo> registos) {
        this.registos = registos;
        Collections.reverse(this.registos);
    }

    public ArrayList<Registo> getRegistosLocalizacao() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("LOCALIZACAO")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosInatividade() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("INATIVIDADE")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosEdicao() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("EDICAO")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosPagamentos() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("PAGAMENTO")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosRegisto() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("REGISTO")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosExtras() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            try {
                if (registo.getTipo().equals("EXTRA")) {
                    list.add(registo);
                }
            } catch (NullPointerException e) {
                System.out.println(registo);
            }

        }
        return list;
    }

    public ArrayList<Registo> getAllRegistos() {

        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {

            list.add(registo);

        }
        return list;
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

    public Registo getRegistoDia(final int clienteID, final Date date) {
        for (Registo registo : registos) {
            if (registo.getTipo().equals("REGISTO") && registo.getData().equals(date))
                return registo;
        }

        return null;
    }

    public boolean verificaRegistoRepetido(final int clienteID, final String dataRegisto) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        boolean registoRepetido = false;

        for (Registo registo: registos) {
            if (registo.getTipo().equals("REGISTO") && registo.getId() == clienteID) {
                try {
                    if (registo.getDateEncomenda().equals(dateFormat.parse(dataRegisto))) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return registoRepetido;
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

    public float getTotalDespesa(Cliente cliente, Spinner dia2Spiner) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Calendar calInicio = Calendar.getInstance();
        Calendar calFim = Calendar.getInstance();
        try {
            calInicio.setTime(sdf.parse(cliente.getPagamentoPlusOneDay()));
            calFim.setTime(sdf.parse(dia2Spiner.getSelectedItem().toString()));

        } catch (ParseException e) {
        }

        float total = 0;
        Registo registo = null;
        while (calInicio.before(calFim) || calInicio.equals(calFim)) {
            registo = getRegistoDia(cliente.getId(), calInicio.getTime());

            if (registo != null)
                total += registo.getTotal();

            calInicio.add(Calendar.DAY_OF_MONTH, 1);
        }

        return total;
    }

    public ArrayList<Registo> getRegistosSobras() {
        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("SOBRA")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosNewClients() {
        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("NOVOCLIENTE")) {
                list.add(registo);
            }
        }
        return list;
    }

    public ArrayList<Registo> getRegistosRemoveClients() {
        ArrayList<Registo> list = new ArrayList<>();
        for (Registo registo : registos) {
            if (registo.getTipo().equals("REMOVECLIENTE")) {
                list.add(registo);
            }
        }
        return list;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
                && calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH)
                && calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);
    }

    public Registo getRegistosSobrasSpecificDay(Date date) {
        for (Registo registo : registos) {
            if (registo.getTipo().equals("SOBRA")) {
                if (isSameDay(registo.getData(), date)) {
                    return registo;
                }
            }
        }
        return null;
    }
}
