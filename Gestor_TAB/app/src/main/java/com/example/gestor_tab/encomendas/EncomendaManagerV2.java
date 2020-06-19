package com.example.gestor_tab.encomendas;

public class EncomendaManagerV2 {

    private int ITEMS = 18;

    EncomendaV2 encomendaSegunda;

    EncomendaV2 encomendaTerca;

    EncomendaV2 encomendaQuarta;

    EncomendaV2 encomendaQuinta;

    EncomendaV2 encomendaSexta;

    EncomendaV2 encomendaSabado;

    EncomendaV2 encomendaDomingo;

    public int numberOfItems() {
        return ITEMS;
    }

    public void setEncomenda(EncomendaV2 encomenda) {
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

    private void setEncomendaSegunda(EncomendaV2 encomendaSegunda) {
        this.encomendaSegunda = encomendaSegunda;
    }

    private void setEncomendaTerca(EncomendaV2 encomendaTerca) {
        this.encomendaTerca = encomendaTerca;
    }

    private void setEncomendaQuarta(EncomendaV2 encomendaQuarta) {
        this.encomendaQuarta = encomendaQuarta;
    }

    private void setEncomendaQuinta(EncomendaV2 encomendaQuinta) {
        this.encomendaQuinta = encomendaQuinta;
    }

    private void setEncomendaSexta(EncomendaV2 encomendaSexta) {
        this.encomendaSexta = encomendaSexta;
    }

    private void setEncomendaSabado(EncomendaV2 encomendaSabado) {
        this.encomendaSabado = encomendaSabado;
    }

    private void setEncomendaDomingo(EncomendaV2 encomendaDomingo) {
        this.encomendaDomingo = encomendaDomingo;
    }

    public EncomendaV2 getEncomendaSegunda() {
        return this.encomendaSegunda;
    }

    public EncomendaV2 getEncomendaTerca() {
        return encomendaTerca;
    }

    public EncomendaV2 getEncomendaQuarta() {
        return encomendaQuarta;
    }

    public EncomendaV2 getEncomendaQuinta() {
        return encomendaQuinta;
    }

    public EncomendaV2 getEncomendaSexta() {
        return encomendaSexta;
    }

    public EncomendaV2 getEncomendaSabado() {
        return encomendaSabado;
    }

    public EncomendaV2 getEncomendaDomingo() {
        return encomendaDomingo;
    }

    public EncomendaV2 getEncomenda(String s) {
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
