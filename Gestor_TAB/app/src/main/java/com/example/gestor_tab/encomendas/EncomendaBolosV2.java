package com.example.gestor_tab.encomendas;


import java.util.ArrayList;

public class EncomendaBolosV2 {

    private String clienteName;

    private ArrayList<String> listaBolos;

    private ArrayList<Integer> quantidades;

    public EncomendaBolosV2(final String nome) {
        this.clienteName = nome;
        this.listaBolos = new ArrayList<>();
        this.quantidades = new ArrayList<>();
    }

    public ArrayList<String> getListaBolos() {
        return this.listaBolos;
    }

    public ArrayList<Integer> getQuantidades() {
        return quantidades;
    }

    public void addBolos(String nome, int total) {
        this.listaBolos.add(nome);
        this.quantidades.add(total);
    }

    public String toStringWithoutName() {
        String text = "";
        for (int i = 0; i < listaBolos.size(); i++) {
            text += listaBolos.get(i) + " - " + quantidades.get(i) + "\n";
        }
        return text;
    }


    public String toStringForSaveInDoc() {
        String text = "";
        text += this.clienteName + "\n";

        for (int i = 0; i < listaBolos.size(); i++) {
            if (quantidades.get(i) > 0) {
                text += this.listaBolos.get(i) + " - " + this.quantidades.get(i) + "\n";
            }
        }

        return text;

    }

    @Override
    public String toString() {
        String text = "";
        text += this.clienteName + "\n";

        for (int i = 0; i < listaBolos.size(); i++) {
            if (quantidades.get(i) > 0) {
                text += this.listaBolos.get(i) + " - " + this.quantidades.get(i) + "\n";
            }
        }

        return text;
    }

    public int getTotalBolosDaEncomenda() {
        return this.listaBolos.size();
    }

    public String getClienteName() {
        return this.clienteName;
    }

    public void reset() {
        this.quantidades.clear();
        this.listaBolos.clear();
    }
}
