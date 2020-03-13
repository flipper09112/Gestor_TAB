package com.example.gestor_tab.clientes;

import android.content.Context;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.gestor_tab.database.LogsBaseUtil.logs;

public class ClientsManager {

    private ArrayList<Cliente> clientsList;

    public ClientsManager(final ArrayList<Cliente> list) {
        this.clientsList = list;
    }

    public ArrayList<Cliente> getClientsList() {
        return this.clientsList;
    }

    public Cliente getCliente(final int position) {
        return this.clientsList.get(position);
    }

    public int getPosition(final Cliente cliente) {
        for (int i = 0; i < clientsList.size(); i++) {
            if (clientsList.get(i).getId() == cliente.getId())
                return i;
        }
        return 0;
    }

    public void replaceCliente(int position, Cliente cliente) {
        clientsList.set(position, cliente);
    }

    public int getId() {
        int max = 0;
        for (int i = 0; i < clientsList.size(); i++) {
            if (clientsList.get(i).getId() > max) {
                max = clientsList.get(i).getId();
            }
        }
        return max + 1;
    }

    public void addCliente(int pos, Cliente newCliente) {
        clientsList.add(pos, newCliente);
    }

    public void removeCliente(Context applicationContext, int pos) {
        Cliente cliente = clientsList.get(pos);
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        String date = mDay + "/" + (mMonth + 1) + "/" + mYear;

        String info = "Removido um cliente ("+cliente.getName()+") " + "com id ("+cliente.getId()+")-" + date;

        logs(applicationContext, 9, cliente.getId(), info);
        clientsList.remove(pos);
    }

    public String getClienteName(int id) {
        for (Cliente cliente : clientsList) {
            if (cliente.getId() == id) {
                return cliente.getName();
            }
        }
        return "Not Found";
    }

    public Cliente getClienteId(int id) {
        for (Cliente cliente : this.clientsList) {
            if (cliente.getId() == id) {
                return cliente;
            }
        }
        return null;
    }
}
