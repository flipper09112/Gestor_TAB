package com.example.gestor_tab.encomendas;

import java.util.ArrayList;

public class EncomendaManager {

    private int ITEMS = 18;

    Encomenda encomendaSegunda;

    Encomenda encomendaTerca;

    Encomenda encomendaQuarta;

    Encomenda encomendaQuinta;

    Encomenda encomendaSexta;

    Encomenda encomendaSabado;

    Encomenda encomendaDomingo;

    public int numberOfItems() {
        return ITEMS;
    }

    public void setEncomenda(Encomenda encomenda) {
        switch (encomenda.getDia()) {
            case "SEGUNDA":
                this.setEncomendaSegunda(encomenda);
                break;
            case "TERCA":
                this.setEncomendaTerca(encomenda);
                break;
            case "QUARTA":
                this.setEncomendaQuarta(encomenda);
                break;
            case "QUINTA":
                this.setEncomendaQuinta(encomenda);
                break;
            case "SEXTA":
                this.setEncomendaSexta(encomenda);
                break;
            case "SABADO":
                this.setEncomendaSabado(encomenda);
                break;
            case "DOMINGO":
                this.setEncomendaDomingo(encomenda);
                break;
        }
    }

    private void setEncomendaSegunda(Encomenda encomendaSegunda) {
        this.encomendaSegunda = encomendaSegunda;
    }

    private void setEncomendaTerca(Encomenda encomendaTerca) {
        this.encomendaTerca = encomendaTerca;
    }

    private void setEncomendaQuarta(Encomenda encomendaQuarta) {
        this.encomendaQuarta = encomendaQuarta;
    }

    private void setEncomendaQuinta(Encomenda encomendaQuinta) {
        this.encomendaQuinta = encomendaQuinta;
    }

    private void setEncomendaSexta(Encomenda encomendaSexta) {
        this.encomendaSexta = encomendaSexta;
    }

    private void setEncomendaSabado(Encomenda encomendaSabado) {
        this.encomendaSabado = encomendaSabado;
    }

    private void setEncomendaDomingo(Encomenda encomendaDomingo) {
        this.encomendaDomingo = encomendaDomingo;
    }

    public Encomenda getEncomendaSegunda() {
        return this.encomendaSegunda;
    }

    public Encomenda getEncomendaTerca() {
        return encomendaTerca;
    }

    public Encomenda getEncomendaQuarta() {
        return encomendaQuarta;
    }

    public Encomenda getEncomendaQuinta() {
        return encomendaQuinta;
    }

    public Encomenda getEncomendaSexta() {
        return encomendaSexta;
    }

    public Encomenda getEncomendaSabado() {
        return encomendaSabado;
    }

    public Encomenda getEncomendaDomingo() {
        return encomendaDomingo;
    }

    public Encomenda getEncomenda(String s) {
        switch (s) {
            case "SEGUNDA":
                return getEncomendaSegunda();
            case "TERCA":
                return getEncomendaTerca();
            case "QUARTA":
                return getEncomendaQuarta();
            case "QUINTA":
                return getEncomendaQuinta();
            case "SEXTA":
                return getEncomendaSexta();
            case "SABADO":
                return getEncomendaSabado();
            case "DOMINGO":
                return getEncomendaDomingo();
        }
        return null;
    }
}
