package com.example.gestor_tab.clientes;

import java.util.ArrayList;

public class BolosManager {

    private ArrayList<String> listaProdutos;
    private ArrayList<Integer> produtoID;

    public BolosManager(final ArrayList<String> listaProdutos, final ArrayList<Integer> produtoID) {
        this.listaProdutos = listaProdutos;
        this.produtoID = produtoID;
    }

    public ArrayList<String> getListaProdutos() {
        return this.listaProdutos;
    }

    public String getProdutoID(int i) {
        return this.produtoID.get(i).toString();
    }
}
