package com.example.gestor_tab.clientes;

import java.util.ArrayList;

public class TabelaPrecos {

    ArrayList<String> produtos;

    ArrayList<Integer> isUnity;

    ArrayList<Float> precos;

    ArrayList<Integer> clientes;

    ArrayList<ArrayList<Float>> listPrecosClientes;

    public TabelaPrecos(final ArrayList produtos, final ArrayList isUnity ,final ArrayList precos) {
        this.produtos = produtos;
        this.isUnity = isUnity;
        this.precos = precos;
        this.clientes = new ArrayList<>();
        this.listPrecosClientes = new ArrayList<>();
    }

    public void setNewClientInfo(int cliente, ArrayList<Float> precos) {
        this.clientes.add(cliente);
        this.listPrecosClientes.add(precos);
    }

    public ArrayList<Integer> getClientes() {
        return this.clientes;
    }

    public ArrayList<String> getProdutos() {
        return this.produtos;
    }

    public ArrayList<Float> getPrecos() {
        return this.precos;
    }

    public ArrayList<Float> getTabelaPrecosCliente(int cliente) {
        int position = this.clientes.indexOf(cliente);

        return this.listPrecosClientes.get(position);
    }

    public int getIdProduto(String toString) {
        return this.produtos.indexOf(toString);
    }

    public boolean verificaCliente(int id) {
        for (int clienteID : clientes) {
            if (clienteID == id)
                return true;
        }
        return false;
    }

    public boolean getProdutosIsUnit(int position) {
        if (this.isUnity.get(position) == 1) {
            return true;
        } else {
            return false;
        }
    }
}
