package com.example.gestor_tab.encomendas;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class EncomendaBolos {

    private String name;

    private boolean encomenda = true;

    int natas;

    int croissant;
    int croissantCreme;
    int croissantChocolate;

    int bolaDeBerlim;
    int rocha;

    int palmier;
    int palmierChocolate;

    int bispo;

    int tarteMaca;

    int massaFolhada;

    int tarteCoco;

    int caramujo;

    int almofadaMistaFolhada;

    int lancheCompridoQueijoChourico;

    int bolosVariados;

    public EncomendaBolos(String nome) {
        this.name = nome;

    }

    public void addBolos(int boloID, int quantidade) {
        switch (boloID) {
            case 1:
                this.natas = quantidade;
                break;
            case 2:
                this.croissant = quantidade;
                break;
            case 3:
                this.croissantCreme = quantidade;
                break;
            case 4:
                this.croissantChocolate = quantidade;
                break;
            case 5:
                this.bolaDeBerlim = quantidade;
                break;
            case 6:
                this.rocha = quantidade;
                break;
            case 7:
                this.palmier = quantidade;
                break;
            case 8:
                this.palmierChocolate = quantidade;
                break;
            case 9:
                this.bispo = quantidade;
                break;
            case 10:
                this.tarteMaca = quantidade;
                break;
            case 11:
                this.massaFolhada = quantidade;
                break;
            case 12:
                this.tarteCoco = quantidade;
                break;
            case 13:
                this.caramujo = quantidade;
                break;
            case 14:
                this.almofadaMistaFolhada = quantidade;
                break;
            case 15:
                this.lancheCompridoQueijoChourico = quantidade;
                break;
            case 16:
                this.bolosVariados = quantidade;
                break;
        }
    }

    public int getQuantidadeBolos(int id) {
        switch (id) {
            case 1:
                return this.natas;
            case 2:
                return this.croissant;
            case 3:
                return this.croissantCreme;
            case 4:
                return this.croissantChocolate;
            case 5:
                return this.bolaDeBerlim;
            case 6:
                return this.rocha;
            case 7:
                return this.palmier;
            case 8:
                return this.palmierChocolate;
            case 9:
                return this.bispo;
            case 10:
                return this.tarteMaca;
            case 11:
                return this.massaFolhada;
            case 12:
                return this.tarteCoco;
            case 13:
                return this.caramujo;
            case 14:
                return this.almofadaMistaFolhada;
            case 15:
                return this.lancheCompridoQueijoChourico;
            case 16:
                return this.bolosVariados;
        }

        return 0;
    }

    public ArrayList<Integer> getTipoBolos() {
        ArrayList<Integer> tipoBolos = new ArrayList<>();

        if (this.natas > 0)
            tipoBolos.add(1);

        if (this.croissant > 0)
            tipoBolos.add(2);

        if (this.croissantCreme > 0)
            tipoBolos.add(3);

        if (this.croissantChocolate > 0)
            tipoBolos.add(4);

        if (this.bolaDeBerlim > 0)
            tipoBolos.add(5);

        if (this.rocha > 0)
            tipoBolos.add(6);

        if (this.palmier > 0)
            tipoBolos.add(7);

        if (this.palmierChocolate > 0)
            tipoBolos.add(8);

        if (this.bispo > 0)
            tipoBolos.add(9);

        if (this.tarteMaca > 0)
            tipoBolos.add(10);

        if (this.massaFolhada > 0)
            tipoBolos.add(11);

        if (this.tarteCoco > 0)
            tipoBolos.add(12);

        if (this.caramujo > 0)
            tipoBolos.add(13);

        if (this.almofadaMistaFolhada > 0)
            tipoBolos.add(14);

        if (this.lancheCompridoQueijoChourico > 0)
            tipoBolos.add(15);

        if (this.bolosVariados> 0)
            tipoBolos.add(16);

        return tipoBolos;
    }

    public String toStringForSaveInDoc() {
        String text = "";
        text += this.name + "\n";

        if (this.natas > 0)
            text += "1 - " + this.natas + "\n";

        if (this.croissant > 0)
            text += "2 - " + this.croissant + "\n";

        if (this.croissantCreme > 0)
            text += "3 - " + this.croissantCreme + "\n";

        if (this.croissantChocolate > 0)
            text += "4 - " + this.croissantChocolate + "\n";

        if (this.bolaDeBerlim > 0)
            text += "5 - " + this.bolaDeBerlim + "\n";

        if (this.rocha > 0)
            text += "6 - " + this.rocha + "\n";

        if (this.palmier > 0)
            text += "7 - " + this.palmier + "\n";

        if (this.palmierChocolate > 0)
            text += "8 - " + this.palmierChocolate + "\n";

        if (this.bispo > 0)
            text += "9 - " + this.bispo + "\n";

        if (this.tarteMaca > 0)
            text += "10 - " + this.tarteMaca + "\n";

        if (this.massaFolhada > 0)
            text += "11 - " + this.massaFolhada + "\n";

        if (this.tarteCoco > 0)
            text += "12 - " + this.tarteCoco + "\n";

        if (this.caramujo > 0)
            text += "13 - " + this.caramujo + "\n";

        if (this.almofadaMistaFolhada > 0)
            text += "14 - " + this.almofadaMistaFolhada + "\n";

        if (this.lancheCompridoQueijoChourico > 0)
            text += "15 - " + this.lancheCompridoQueijoChourico + "\n";

        if (this.bolosVariados> 0)
            text += "16 - " + this.bolosVariados + "\n";

        return text;
    }

    @NonNull
    @Override
    public String toString() {
        String text = "";

        text += this.name;

        if (this.encomenda) {
            text +=  "\n";
        } else {
            text += " [Não será realizada esta encomenda!]\n";
        }

        if (this.natas > 0)
            text += "\tNatas - " + this.natas + "\n";

        if (this.croissant > 0)
            text += "\tCroissant normal - " + this.croissant + "\n";

        if (this.croissantCreme > 0)
            text += "\tCroissant c/creme - " + this.croissantCreme + "\n";

        if (this.croissantChocolate > 0)
            text += "\tCroissant c/Chocolate - " + this.croissantChocolate + "\n";

        if (this.bolaDeBerlim > 0)
            text += "\tBola de Berlim - " + this.bolaDeBerlim + "\n";

        if (this.rocha > 0)
            text += "\tRocha - " + this.rocha + "\n";

        if (this.palmier > 0)
            text += "\tPalmier - " + this.palmier + "\n";

        if (this.palmierChocolate > 0)
            text += "\tPalmier c/Chocolate - " + this.palmierChocolate + "\n";

        if (this.bispo > 0)
            text += "\tBispo - " + this.bispo + "\n";

        if (this.tarteMaca > 0)
            text += "\tTarte maca - " + this.tarteMaca + "\n";

        if (this.massaFolhada > 0)
            text += "\tMassa Folada - " + this.massaFolhada + "\n";

        if (this.tarteCoco > 0)
            text += "\tTarte Coco - " + this.tarteCoco + "\n";

        if (this.caramujo > 0)
            text += "\tCaramujo - " + this.caramujo + "\n";

        if (this.almofadaMistaFolhada > 0)
            text += "\tAlmofada Mista Folhada - " + this.almofadaMistaFolhada + "\n";

        if (this.lancheCompridoQueijoChourico > 0)
            text += "\tLanche comprido de queijo e chouriço - " + this.lancheCompridoQueijoChourico + "\n";

        if (this.bolosVariados> 0)
            text += "\tBolos Variados - " + this.bolosVariados + "\n";

        return text;
    }

    public String toStringWithoutName() {
        String text = "Queria\n\n";

        if (this.natas > 0)
            text += "\tNatas - " + this.natas + "\n";

        if (this.croissant > 0)
            text += "\tCroissant normal - " + this.croissant + "\n";

        if (this.croissantCreme > 0)
            text += "\tCroissant c/creme - " + this.croissantCreme + "\n";

        if (this.croissantChocolate > 0)
            text += "\tCroissant c/Chocolate - " + this.croissantChocolate + "\n";

        if (this.bolaDeBerlim > 0)
            text += "\tBola de Berlim - " + this.bolaDeBerlim + "\n";

        if (this.rocha > 0)
            text += "\tRocha - " + this.rocha + "\n";

        if (this.palmier > 0)
            text += "\tPalmier - " + this.palmier + "\n";

        if (this.palmierChocolate > 0)
            text += "\tPalmier c/Chocolate - " + this.palmierChocolate + "\n";

        if (this.bispo > 0)
            text += "\tBispo - " + this.bispo + "\n";

        if (this.tarteMaca > 0)
            text += "\tTarte maca - " + this.tarteMaca + "\n";

        if (this.massaFolhada > 0)
            text += "\tMassa Folada - " + this.massaFolhada + "\n";

        if (this.tarteCoco > 0)
            text += "\tTarte Coco - " + this.tarteCoco + "\n";

        if (this.caramujo > 0)
            text += "\tCaramujo - " + this.caramujo + "\n";

        if (this.almofadaMistaFolhada > 0)
            text += "\tAlmofada Mista Folhada - " + this.almofadaMistaFolhada + "\n";

        if (this.lancheCompridoQueijoChourico > 0)
            text += "\tLanche comprido de queijo e chouriço - " + this.lancheCompridoQueijoChourico + "\n";

        if (this.bolosVariados> 0)
            text += "\tBolos Variados - " + this.bolosVariados + "\n";

        return text;
    }

    public String getName() {
        return this.name;
    }

    public void naoEncomenda() {
        this.encomenda = false;
    }

    public boolean isEncomenda() {
        return encomenda;
    }

    public void Encomenda() {
        this.encomenda = true;
    }

    public boolean isEmpty() {
        boolean empty = true;
        if (this.natas > 0)
            return false;

        if (this.croissant > 0)
            return false;

        if (this.croissantCreme > 0)
            return false;

        if (this.croissantChocolate > 0)
            return false;

        if (this.bolaDeBerlim > 0)
            return false;

        if (this.rocha > 0)
            return false;

        if (this.palmier > 0)
            return false;

        if (this.palmierChocolate > 0)
            return false;

        if (this.bispo > 0)
            return false;

        if (this.tarteMaca > 0)
            return false;

        if (this.massaFolhada > 0)
            return false;

        if (this.tarteCoco > 0)
            return false;

        if (this.caramujo > 0)
            return false;

        if (this.almofadaMistaFolhada > 0)
            return false;

        if (this.lancheCompridoQueijoChourico > 0)
            return false;

        if (this.bolosVariados> 0)
            return false;

        return empty;
    }
}
