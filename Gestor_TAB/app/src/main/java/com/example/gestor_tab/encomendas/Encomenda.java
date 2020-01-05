package com.example.gestor_tab.encomendas;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Encomenda {

    String dia;

    int paoParis;

    int bijuTrigo;

    int paoLata;

    int paoIntegral;

    int bijuEscuro;

    int bicas;

    int cacetes;

    int cacetinhosIntegral;

    int bolasTrigoKg;

    int bolasTrigoHalfKg;

    int broaMilhoHalfKg;

    int broaMilhoKg;

    int broaCenteioKg;

    int broaCenteioHalfKg;

    int paoQuadoKg;

    int paoQuadoHalfKg;

    int regueifa;

    int regueifaG;

    private ArrayList<EncomendaBolos> encomendaBolosArrayList;

    public Encomenda(
            String dia,
            int paoParis, int bijuTrigo, int paoLata,
            int paoIntegral, int bijuEscuro, int bicas,
            int cacetes, int cacetinhosIntegral, int bolasTrigoKg,
            int bolasTrigoHalfKg, int broaMilhoHalfKg, int broaMilhoKg,
            int broaCenteioKg, int broaCenteioHalfKg, int paoQuadoKg,
            int paoQuadoHalfKg, int regueifa, int regueifaG) {

        this.dia = dia;
        this.paoParis = paoParis ;
        this.bijuTrigo = bijuTrigo;
        this.paoLata = paoLata;
        this.paoIntegral = paoIntegral;
        this.bijuEscuro = bijuEscuro;
        this.bicas = bicas;
        this.cacetes = cacetes;
        this.cacetinhosIntegral = cacetinhosIntegral;
        this.bolasTrigoKg = bolasTrigoKg;
        this.bolasTrigoHalfKg = bolasTrigoHalfKg;
        this.broaMilhoHalfKg = broaMilhoHalfKg;
        this.broaMilhoKg = broaMilhoKg;
        this.broaCenteioKg = broaCenteioKg;
        this.broaCenteioHalfKg = broaCenteioHalfKg;
        this.paoQuadoKg = paoQuadoKg;
        this.paoQuadoHalfKg = paoQuadoHalfKg;
        this.regueifa = regueifa;
        this.regueifaG = regueifaG;

        this.encomendaBolosArrayList = new ArrayList<>();
    }

    public ArrayList<EncomendaBolos> getEncomendaBolosArrayList() {
        return encomendaBolosArrayList;
    }

    public void setEncomendaBolosArrayList(ArrayList<EncomendaBolos> encomendaBolosArrayList) {
        this.encomendaBolosArrayList = encomendaBolosArrayList;
    }

    public ArrayList<Integer> getListaItems() {
        ArrayList<Integer> lista = new ArrayList<Integer>();


        return lista;
    }

    @NonNull
    @Override
    public String toString() {
        String text = this.dia + "\n";

        text += "Pão Paris - " + this.paoParis + "\n";
        text += "Biju Trigo - " + this.bijuTrigo + "\n";
        text += "Pão Lata - " + this.paoLata + "\n";
        text += "Pão Integral - " + this.paoIntegral + "\n";
        text += "Biju Escuro - " + this.bijuEscuro + "\n";
        text += "Bicas massa fina - " + this.bicas + "\n";
        text += "Cacetes (peças normal) - " + this.cacetes + "\n";
        text += "Cacetes Integral (120g) - " + this.cacetinhosIntegral + "\n";

        text += "Bolas de Trigo (Kg) - " + this.bolasTrigoKg + "\n";
        text += "Bolas de Trigo (1/2 Kg) - " + this.bolasTrigoHalfKg + "\n";
        text += "Broa Milho (1/2 Kg) - " + this.broaMilhoHalfKg + "\n";
        text += "Broa Milho (Kg) - " + this.broaMilhoKg + "\n";
        text += "Broa Centeio (Kg) - " + this.broaCenteioKg + "\n";
        text += "Broa Centeio (1/2 Kg) - " + this.broaCenteioHalfKg + "\n";
        text += "Pão Quado (Kg) - " + this.paoQuadoKg + "\n";
        text += "Pão Quado (1/2 Kg) - " + this.paoQuadoHalfKg + "\n";
        text += "Regueifa - " + this.regueifa + "\n";
        text += "Regueifa Grande - " + this.regueifaG + "\n";

        return text;
    }

    public String toStringWithoutZeros() {
        String text = "";

        if (this.paoParis != 0)
            text += "Pão Paris - " + this.paoParis + "\n\n";

        if (this.bijuTrigo != 0)
            text += "Biju Trigo - " + this.bijuTrigo + "\n\n";

        if (this.paoLata != 0)
            text += "Pão Lata - " + this.paoLata + "\n\n";

        if (this.paoIntegral != 0)
            text += "Pão Integral - " + this.paoIntegral + "\n\n";

        if (this.bijuEscuro != 0)
            text += "Biju Escuro - " + this.bijuEscuro + "\n\n";

        if (this.bicas != 0)
            text += "Bicas massa fina - " + this.bicas + "\n\n";

        if (this.cacetes != 0)
            text += "Cacetes (peças normal) - " + this.cacetes + "\n\n";

        if (this.cacetinhosIntegral != 0)
            text += "Cacetes Integral (120g) - " + this.cacetinhosIntegral + "\n\n";

        if (this.bolasTrigoKg != 0)
            text += "Bolas de Trigo (Kg) - " + this.bolasTrigoKg + "\n\n";

        if (this.bolasTrigoHalfKg != 0)
            text += "Bolas de Trigo (1/2 Kg) - " + this.bolasTrigoHalfKg + "\n\n";

        if (this.broaMilhoHalfKg != 0)
            text += "Broa Milho (1/2 Kg) - " + this.broaMilhoHalfKg + "\n\n";

        if (this.broaMilhoKg != 0)
            text += "Broa Milho (Kg) - " + this.broaMilhoKg + "\n\n";

        if (this.broaCenteioKg != 0)
            text += "Broa Centeio (Kg) - " + this.broaCenteioKg + "\n\n";

        if (this.broaCenteioHalfKg != 0)
            text += "Broa Centeio (1/2 Kg) - " + this.broaCenteioHalfKg + "\n\n";

        if (this.paoQuadoKg != 0)
            text += "Pão Quado (Kg) - " + this.paoQuadoKg + "\n\n";

        if (this.paoQuadoHalfKg != 0)
            text += "Pão Quado (1/2 Kg) - " + this.paoQuadoHalfKg + "\n\n";

        if (this.regueifa != 0)
            text += "Regueifa - " + this.regueifa + "\n\n";

        if (this.regueifaG != 0)
            text += "Regueifa Grande - " + this.regueifaG + "\n\n";

        return text;
    }

    public String toStringEncomendaBolos() {
        String text = "";
        for (int i = 0; i < this.encomendaBolosArrayList.size(); i++) {
            if (this.encomendaBolosArrayList.get(i).isEncomenda() && !this.encomendaBolosArrayList.get(i).isEmpty()) {
                text += this.encomendaBolosArrayList.get(i).toStringWithoutName();
                text += "--------------------------------------------------------\n\n";
            }
        }
        return text;
    }


    public String getDia() {
        return this.dia;
    }

    public int getPaoParis() {
        return paoParis;
    }

    public int getBijuTrigo() {
        return bijuTrigo;
    }

    public int getPaoLata() {
        return paoLata;
    }

    public int getPaoIntegral() {
        return paoIntegral;
    }

    public int getBijuEscuro() {
        return bijuEscuro;
    }

    public int getBicas() {
        return bicas;
    }

    public int getCacetes() {
        return cacetes;
    }

    public int getCacetinhosIntegral() {
        return cacetinhosIntegral;
    }

    public int getBolasTrigoKg() {
        return bolasTrigoKg;
    }

    public int getBolasTrigoHalfKg() {
        return bolasTrigoHalfKg;
    }

    public int getBroaMilhoHalfKg() {
        return broaMilhoHalfKg;
    }

    public int getBroaMilhoKg() {
        return broaMilhoKg;
    }

    public int getBroaCenteioKg() {
        return broaCenteioKg;
    }

    public int getBroaCenteioHalfKg() {
        return broaCenteioHalfKg;
    }

    public int getPaoQuadoKg() {
        return paoQuadoKg;
    }

    public int getPaoQuadoHalfKg() {
        return paoQuadoHalfKg;
    }

    public int getRegueifa() {
        return regueifa;
    }

    public int getRegueifaG() {
        return regueifaG;
    }

    public void setPaoParis(int paoParis) {
        this.paoParis = paoParis;
    }

    public void setBijuTrigo(int bijuTrigo) {
        this.bijuTrigo = bijuTrigo;
    }

    public void setPaoLata(int paoLata) {
        this.paoLata = paoLata;
    }

    public void setPaoIntegral(int paoIntegral) {
        this.paoIntegral = paoIntegral;
    }

    public void setBijuEscuro(int bijuEscuro) {
        this.bijuEscuro = bijuEscuro;
    }

    public void setBicas(int bicas) {
        this.bicas = bicas;
    }

    public void setCacetes(int cacetes) {
        this.cacetes = cacetes;
    }

    public void setCacetinhosIntegral(int cacetinhosIntegral) {
        this.cacetinhosIntegral = cacetinhosIntegral;
    }

    public void setBolasTrigoKg(int bolasTrigoKg) {
        this.bolasTrigoKg = bolasTrigoKg;
    }

    public void setBolasTrigoHalfKg(int bolasTrigoHalfKg) {
        this.bolasTrigoHalfKg = bolasTrigoHalfKg;
    }

    public void setBroaMilhoHalfKg(int broaMilhoHalfKg) {
        this.broaMilhoHalfKg = broaMilhoHalfKg;
    }

    public void setBroaMilhoKg(int broaMilhoKg) {
        this.broaMilhoKg = broaMilhoKg;
    }

    public void setBroaCenteioKg(int broaCenteioKg) {
        this.broaCenteioKg = broaCenteioKg;
    }

    public void setBroaCenteioHalfKg(int broaCenteioHalfKg) {
        this.broaCenteioHalfKg = broaCenteioHalfKg;
    }

    public void setPaoQuadoKg(int paoQuadoKg) {
        this.paoQuadoKg = paoQuadoKg;
    }

    public void setPaoQuadoHalfKg(int paoQuadoHalfKg) {
        this.paoQuadoHalfKg = paoQuadoHalfKg;
    }

    public void setRegueifa(int regueifa) {
        this.regueifa = regueifa;
    }

    public void setRegueifaG(int regueifaG) {
        this.regueifaG = regueifaG;
    }
}
