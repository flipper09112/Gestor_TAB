package com.example.gestor_tab.clientes;

import android.widget.TextView;

import com.example.gestor_tab.Date.DateUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.example.gestor_tab.database.DataBaseUtil.replaceOccurance;


@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Cliente implements Serializable{

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    private String name;

    private int id;

    private Date pagamento;

    private String tipoPagamento;

    private float despesa[];

    private double lat = 0;
    private double lng = 0;

    private int noPorta;

    public float[] getDespesa() {
        return despesa;
    }

    public Cliente(final String name, final int description, final Date pagamento, final String tipoPagamento, final float[] despesa) {
        this.name = name;
        this.id = description;
        this.pagamento = pagamento;
        this.tipoPagamento = tipoPagamento;
        this.despesa = Arrays.copyOf(despesa, despesa.length);
    }

    public void setCoordenadas(final String coordenadas) {
        String temp = coordenadas;

        if (countOccurencesChar(temp, ',') > 1) {
            temp = replaceOccurance(temp, ",", ".", 1);
            temp = replaceOccurance(temp, ",", ".", 2);
        }

        this.lat = Double.parseDouble(temp.split(",")[0]);
        this.lng = Double.parseDouble(temp.split(",")[1]);

    }

    private int countOccurencesChar(String someString, char someChar) {
        int count = 0;

        for (int i = 0; i < someString.length(); i++) {
            if (someString.charAt(i) == someChar) {
                count++;
            }
        }

        return count;
    }

    @Override
    public String toString() {
        return "Nome:   " + this.name.toUpperCase() + "\n" + "ID:  " + this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPagamento() {
        return formatter.format(this.pagamento);
    }

    public String getPagamentoPlusOneDay() {
        Calendar temp = Calendar.getInstance();
        temp.setTime(this.pagamento);
        temp.set(temp.HOUR,24);

        return formatter.format(temp.getTime());
    }

    public double getDespesa(final String payAteEstaData) {
        try {
            final Date date = formatter.parse(payAteEstaData);

            if (this.tipoPagamento.equals("S") || this.tipoPagamento.equals("LS")) {
                return calculaDespesaSemanal(date);
            } else if (this.tipoPagamento.equals("M")){
                return calculaDespesaMensal(date);
            } else if (this.tipoPagamento.equals("D")){
                return calculaDespesaDiaria(date);
            }
            return 0;
        } catch (ParseException e) {
            return -1;
        }
    }

    private double calculaDespesaDiaria(Date date) {
        Calendar temp = Calendar.getInstance();
        temp.setTime(date);

        double total = 0;

        if (temp.get(temp.DAY_OF_WEEK)==1){
            total += this.despesa[6];
        }
        if (temp.get(temp.DAY_OF_WEEK)==2){
            total += this.despesa[0];
        }
        if (temp.get(temp.DAY_OF_WEEK)==3){
            total += this.despesa[1];
        }
        if (temp.get(temp.DAY_OF_WEEK)==4){
            total += this.despesa[2];
        }
        if (temp.get(temp.DAY_OF_WEEK)==5){
            total += this.despesa[3];
        }
        if (temp.get(temp.DAY_OF_WEEK)==6){
            total += this.despesa[4];
        }
        if (temp.get(temp.DAY_OF_WEEK)==7){
            total += this.despesa[5];
        }

        return total;
    }

    private double calculaDespesaSemanal(final Date payUpTo) {
        long diff = payUpTo.getTime() - this.pagamento.getTime();
        int diffDays = (int) (diff / (24 * 1000 * 60 * 60));

        if (diffDays == 0) {
            return 0;
        }

        int diffweeks = diffDays / 7 + 1;
        if (diffDays % 7 == 0)
            diffweeks -= 1;

        final float total = sumArrayDespesa();

        return total * diffweeks + this.despesa[this.despesa.length - 1];
    }

    public ArrayList<String> getNextDate() {
        if (this.tipoPagamento.equals("S") || this.tipoPagamento.equals("LS")) {
            return new DateUtil().getNextSunday(this.pagamento);

        } else if (this.tipoPagamento.equals("M")) {
            return new DateUtil().getNextMonth(this.pagamento);

        } else if (this.tipoPagamento.equals("D")) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            ArrayList<String> lista = new ArrayList<String>();
            lista.add(dateFormat.format(date));
            return lista;
        }

        return null;
    }

    private float sumArrayDespesa() {
        float count = 0;
        for (int i = 0; i < this.despesa.length-1; i++) {
            count += this.despesa[i];
        }
        return count;
    }

    public void addExtra(final float add) {
        despesa[this.despesa.length - 1] = despesa[this.despesa.length - 1] + add;
    }

    private double calculaDespesaMensal(final Date payUpTo) {

        double total = 0;
        Calendar temp = Calendar.getInstance();
        temp.setTime(this.pagamento);
        if (!(temp.get(Calendar.DAY_OF_MONTH) == 1)) {
            temp.set(temp.HOUR,24);
        }

        if (this.pagamento.equals(payUpTo)) {
            return 0;
        }

        while (!temp.getTime().after(payUpTo)) {
            if (temp.get(temp.DAY_OF_WEEK)==1){
                total += this.despesa[6];
            }
            if (temp.get(temp.DAY_OF_WEEK)==2){
                total += this.despesa[0];
            }
            if (temp.get(temp.DAY_OF_WEEK)==3){
                total += this.despesa[1];
            }
            if (temp.get(temp.DAY_OF_WEEK)==4){
                total += this.despesa[2];
            }
            if (temp.get(temp.DAY_OF_WEEK)==5){
                total += this.despesa[3];
            }
            if (temp.get(temp.DAY_OF_WEEK)==6){
                total += this.despesa[4];
            }
            if (temp.get(temp.DAY_OF_WEEK)==7){
                total += this.despesa[5];
            }
            temp.set(temp.HOUR,24);
        }

        return total + this.despesa[this.despesa.length - 1];
    }

    public int getId() {
        return this.id;
    }

    public void setNewPagamento(final String date, final boolean extra) {
        try {
            this.pagamento = formatter.parse(date);

            if (extra) {
                this.despesa[7] = 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getTipoPagamento() {
        return this.tipoPagamento;
    }

    public String updateCliente(final String name, final Date pagamento, final String tipoPagamento, final float[] despesa) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

        String alteracoes = "";

        if (name != null) {
            if (!this.name.equals(name)) {
                alteracoes = alteracoes + "Alteração do nome de " + this.name + " para " + name + ";,";
            }
            this.name = name;
        }
        if (pagamento != null) {

            if (!this.pagamento.equals(pagamento)) {
                alteracoes = alteracoes + "Alteração da data de pagamento de " + dateFormat.format(this.pagamento) + " para " + dateFormat.format(pagamento)+ ";,";
            }

            this.pagamento = pagamento;
        }
        if (tipoPagamento != null) {

            if (!this.tipoPagamento.equals(tipoPagamento)) {
                alteracoes = alteracoes + "Alteração do tipo de pagamento de " + getWord(this.tipoPagamento) + " para " + getWord(tipoPagamento) + ";,";
            }

            this.tipoPagamento = tipoPagamento;
        }
        if (despesa != null) {

            if (!Arrays.equals(this.despesa, despesa)) {
                alteracoes = alteracoes + "Alteração de valores da despesa do cliente;," + lastDespesas(this.despesa);
            }
            this.despesa = Arrays.copyOf(despesa, despesa.length);
        }

        alteracoes+="-" + dateFormat.format(date);

        return alteracoes;
    }

    private String lastDespesas(float[] despesa) {
        String text = "";

        text += String.format("SEG  " + Float.toString(despesa[0]).replace(",", ".") + ",");
        text += String.format("TER  " + Float.toString(despesa[1]).replace(",", ".") + ",");
        text += String.format("QUA  " + Float.toString(despesa[2]).replace(",", ".") + ",");
        text += String.format("QUI   " + Float.toString(despesa[3]).replace(",", ".") + ",");
        text += String.format("SEX  " + Float.toString(despesa[4]).replace(",", ".") + ",");
        text += String.format("SAB  " + Float.toString(despesa[5]).replace(",", ".") + ",");
        text += String.format("DOM  " + Float.toString(despesa[6]).replace(",", ".") + ",");
        text += String.format("EXT  " + Float.toString(despesa[7]).replace(",", "."));

        return text;
    }

    private String getWord(String tipoPagamento) {
        if (tipoPagamento.equals("S"))
            return "Semanal";
        else if (tipoPagamento.equals("M"))
            return "Mensal";
        else if (tipoPagamento.equals("D"))
            return "Diário";
        else
            return "error";
    }

    public int[] getDaysToPrint(String toDate) {
        int [] countDays = new int[7];
        Arrays.fill(countDays, 0);

        try {
            final Date date = formatter.parse(toDate);

            if (this.tipoPagamento.equals("S")) {
                return calculaDaysSemanal(date, countDays);
            } else if (this.tipoPagamento.equals("M")){
                return calculaDaysMensal(date, countDays);
            } else if (this.tipoPagamento.equals("D")){
                new UnsupportedOperationException("ERRO");
            }
            return null;
        } catch (ParseException e) {
            return null;
        }
    }

    private int[] calculaDaysMensal(Date date, int[] countDays) {

        Calendar temp = Calendar.getInstance();
        temp.setTime(this.pagamento);
        if (!(temp.get(Calendar.DAY_OF_MONTH) == 1)) {
            temp.set(temp.HOUR,24);
        }

        if (this.pagamento.equals(date)) {
            countDays[0] = 0;
            countDays[1] = 0;
            countDays[2] = 0;
            countDays[3] = 0;
            countDays[4] = 0;
            countDays[5] = 0;
            countDays[6] = 0;
            return countDays;
        }

        while (!temp.getTime().after(date)) {
            if (temp.get(temp.DAY_OF_WEEK)==1){
                countDays[6] += 1;
            }
            if (temp.get(temp.DAY_OF_WEEK)==2){
                countDays[0] += 1;

            }
            if (temp.get(temp.DAY_OF_WEEK)==3){
                countDays[1] += 1;

            }
            if (temp.get(temp.DAY_OF_WEEK)==4){
                countDays[2] += 1;

            }
            if (temp.get(temp.DAY_OF_WEEK)==5){
                countDays[3] += 1;

            }
            if (temp.get(temp.DAY_OF_WEEK)==6){
                countDays[4] += 1;

            }
            if (temp.get(temp.DAY_OF_WEEK)==7){
                countDays[5] += 1;

            }
            temp.set(temp.HOUR,24);
        }

        return countDays;
    }

    private int[] calculaDaysSemanal(Date date, int[] countDays) {
        long diff = date.getTime() - this.pagamento.getTime();
        int diffDays = (int) (diff / (24 * 1000 * 60 * 60));

        if (diffDays == 0) {
            countDays[0] = 0;
            countDays[1] = 0;
            countDays[2] = 0;
            countDays[3] = 0;
            countDays[4] = 0;
            countDays[5] = 0;
            countDays[6] = 0;
            return countDays;
        }

        int diffweeks = diffDays / 7 + 1;
        if (diffDays % 7 == 0)
            diffweeks -= 1;

        countDays[0] = diffweeks;
        countDays[1] = diffweeks;
        countDays[2] = diffweeks;
        countDays[3] = diffweeks;
        countDays[4] = diffweeks;
        countDays[5] = diffweeks;
        countDays[6] = diffweeks;

        return countDays;
    }

    public void updateDay(float total, String data) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar temp = Calendar.getInstance();

        try {
            temp.setTime(dateFormat.parse(data));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (temp.get(temp.DAY_OF_WEEK)==1){
            despesa[6] = total;
        }
        if (temp.get(temp.DAY_OF_WEEK)==2){
            despesa[0] = total;

        }
        if (temp.get(temp.DAY_OF_WEEK)==3){
            despesa[1] = total;

        }
        if (temp.get(temp.DAY_OF_WEEK)==4){
            despesa[2] = total;

        }
        if (temp.get(temp.DAY_OF_WEEK)==5){
            despesa[3] = total;

        }
        if (temp.get(temp.DAY_OF_WEEK)==6){
            despesa[4] = total;

        }
        if (temp.get(temp.DAY_OF_WEEK)==7){
            despesa[5] = total;

        }
    }

    public String getCoordenadas() {
        if (this.lat == 0 || this.lng == 0) {
            return null;
        }
        return String.format("%f, %f", this.lat, this.lng);
    }

    public double getLat() {
        return this.lat;
    }

    public double getLng() {
        return this.lng;
    }
}
