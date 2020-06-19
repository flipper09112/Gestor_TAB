package com.example.gestor_tab.clientes;

import java.util.ArrayList;
import java.util.BitSet;

public class TabelaPrecos {

    ArrayList<String> produtos;

    BolosManager bolosManager;

    ArrayList<Integer> ids;

    ArrayList<Integer> isUnity;

    ArrayList<Float> precos;

    ArrayList<Integer> clientes;

    ArrayList<ArrayList<Float>> listPrecosClientes;

    public TabelaPrecos(final ArrayList produtos, final ArrayList isUnity ,final ArrayList precos, final ArrayList ids) {
        this.produtos = produtos;
        this.isUnity = isUnity;
        this.precos = precos;
        this.ids = ids;
        this.clientes = new ArrayList<>();
        this.listPrecosClientes = new ArrayList<>();
    }

    public BolosManager getBolosManager() {
        return bolosManager;
    }

    public void setBolosManager(final BolosManager bolosManager) {
        this.bolosManager = bolosManager;
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

    public int getIDFromProduct(String toString) {
        int position = this.produtos.indexOf(toString);
        return this.ids.get(position);
    }

    public String getNomeProdutoFromID(final String produto) {
        int produtoId = Integer.parseInt(produto);
        int position = this.ids.indexOf(produtoId);

        return this.produtos.get(position);
    }

    public float getValorFromProdutoID(String produtoId, Integer integer) {
        int id = Integer.parseInt(produtoId);
        int position = this.ids.indexOf(id);

        Float valor = this.precos.get(position);

        return valor*integer;
    }

    public ArrayList<Integer> getIDs() {
        return this.ids;
    }

    public ArrayList<String> getListaBolos() {
        return this.bolosManager.getListaProdutos();
    }

    public ArrayList<Integer> getUnit() {
        return this.isUnity;
    }

    public String getIdBolo(int i) {
        return this.bolosManager.getProdutoID(i);
    }

    public void removeCliente(int id) {
        int pos = this.clientes.indexOf(id);

        this.clientes.remove(pos);
        this.listPrecosClientes.remove(pos);
    }
}
