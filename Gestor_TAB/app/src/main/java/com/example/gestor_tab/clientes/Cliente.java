package com.example.gestor_tab.clientes;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.gestor_tab.Date.DateUtil;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.example.gestor_tab.Date.DateUtil.addDaysToDate;
import static com.example.gestor_tab.Date.DateUtil.getNextDomingo;
import static com.example.gestor_tab.database.DataBaseUtil.replaceOccurance;


@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class Cliente implements Parcelable {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    public static String[] dias = {"SEG", "TER", "QUA", "QUI", "SEX", "SAB", "DOM"};

    private String name;
    private String alcunha = "Sem alcunha definida";
    private int noPorta = 0;
    private String morada = "Sem morada definida";
    private HashMap<String, HashMap<String, Integer>> quantidades = new HashMap<>();

    private int id;

    private Date pagamento;

    private String tipoPagamento;

    private float despesa[];

    private double lat = 0;
    private double lng = 0;

    private boolean ativo = true;

    private Date inicioInatividade = null;
    private Date fimInatividade = null;


    protected Cliente(Parcel in) {
        name = in.readString();
        id = in.readInt();
        pagamento = (java.util.Date) in.readSerializable();
        tipoPagamento = in.readString();
        despesa = in.createFloatArray();
        lat = in.readDouble();
        lng = in.readDouble();
        ativo = in.readByte() != 0;
        noPorta = in.readInt();
        alcunha = in.readString();
        morada = in.readString();
        quantidades = (HashMap<String, HashMap<String, Integer>>) in.readSerializable();
    }

    public static final Creator<Cliente> CREATOR = new Creator<Cliente>() {
        @Override
        public Cliente createFromParcel(Parcel in) {
            return new Cliente(in);
        }

        @Override
        public Cliente[] newArray(int size) {
            return new Cliente[size];
        }
    };

    public HashMap<String, HashMap<String, Integer>> getQuantidades() {
        return quantidades;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeInt(id);
        dest.writeSerializable(pagamento);
        dest.writeString(tipoPagamento);
        dest.writeFloatArray(despesa);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeByte((byte) (ativo ? 1 : 0));
        dest.writeInt(noPorta);
        dest.writeString(alcunha);
        dest.writeString(morada);
        dest.writeSerializable(quantidades);
    }

    public float[] getDespesa() {
        return despesa;
    }

    public Cliente(
            final String name,
            final int description,
            final Date pagamento,
            final String tipoPagamento,
            final float[] despesa,
            final String morada,
            final String alcunha,
            final int noPorta,
            HashMap<String, HashMap<String, Integer>> quantidadesDescricao) {

        this.name = name;
        this.id = description;
        this.pagamento = pagamento;
        this.tipoPagamento = tipoPagamento;
        this.despesa = Arrays.copyOf(despesa, despesa.length);

        if (quantidadesDescricao != null)
            this.quantidades = quantidadesDescricao;
        else {
            this.quantidades.put(dias[0], new HashMap<String, Integer>());
            this.quantidades.put(dias[1], new HashMap<String, Integer>());
            this.quantidades.put(dias[2], new HashMap<String, Integer>());
            this.quantidades.put(dias[3], new HashMap<String, Integer>());
            this.quantidades.put(dias[4], new HashMap<String, Integer>());
            this.quantidades.put(dias[5], new HashMap<String, Integer>());
            this.quantidades.put(dias[6], new HashMap<String, Integer>());
        }

        if (morada != null) {
            this.morada = morada;
        }

        if (alcunha != null) {
            this.alcunha = alcunha;
        }

        this.noPorta = noPorta;

    }

    public String getInicioInatividade() {
        if (this.inicioInatividade != null) {
            return formatter.format(this.inicioInatividade);
        } else {
            return "-";
        }
    }

    public String getFimInatividade() {
        if (this.fimInatividade != null) {
            return formatter.format(this.fimInatividade);
        } else {
            return "-";
        }
    }

    public void setAtivo(final String date1, final String date2) {
        Date date = new Date();
        String dateSemHoras = formatter.format(date);
        try {
            date = formatter.parse(dateSemHoras);
            if (!date1.equals("-") && !date2.equals("-")) {

                this.inicioInatividade = formatter.parse(date1);
                this.fimInatividade = formatter.parse(date2);

                if ((date.after(this.inicioInatividade) || date.equals(this.inicioInatividade)) && date.before(this.fimInatividade)) {
                    this.ativo = false;
                } else {
                    this.ativo = true;
                    this.inicioInatividade = null;
                    this.fimInatividade = null;
                    //TODO notification
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setAtivo(final boolean ativo) {
        this.ativo = ativo;
    }

    public boolean getAtivo() {
        return this.ativo;
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
        if (this.getCoordenadas() != null) {
            String toString = "";

            toString += "Nome:   " + this.name.toUpperCase() + "\n";

            if (!this.morada.equals("Sem morada definida")) {
                toString += "Morada: " + this.morada + "\n";
            }

            if (this.noPorta != 0)
                toString += "Nº porta: " + this.noPorta + "\n";


            if (!this.alcunha.equals("Sem alcunha definida")) {
                toString += "Alcunha: " + this.alcunha + "\n";
            }

            toString += "ID: " + this.id;

            return toString;
        } else {
            return "Nome:   " + this.name.toUpperCase() + "\nLocalização:   SEM LOCALIZAÇÃO\n" + "ID:  " + this.id;
        }
    }

    public String getName() {
        return this.name;
    }

    public String getPagamento() {
        return formatter.format(this.pagamento);
    }

    public Date getPagamentoDate() {
        return this.pagamento;
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
            } else if (this.tipoPagamento.equals("JD")) {
                return calculaDespesaJuntaDias(date);
            }
            return 0;
        } catch (ParseException e) {
            return -1;
        }
    }

    private double calculaDespesaJuntaDias(Date date) {
        double total = 0;
        Calendar temp = Calendar.getInstance();
        temp.setTime(this.pagamento);
        temp.add(Calendar.HOUR, 24);

        while (!temp.getTime().after(date)) {
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
            return 0 + this.despesa[this.despesa.length - 1];
        }

        int diffweeks = diffDays / 7 + 1;
        if (diffDays % 7 == 0)
            diffweeks -= 1;

        final float total = sumArrayDespesa();

        return total * diffweeks + this.despesa[this.despesa.length - 1];
    }

    public ArrayList<String> getNextDate() {
        if (this.tipoPagamento.equals("S") || this.tipoPagamento.equals("LS")) {
            return new DateUtil().getNextSunday(this.pagamento, this.despesa[this.despesa.length - 1]);

        } else if (this.tipoPagamento.equals("M")) {
            return new DateUtil().getNextMonth(this.pagamento);

        } else if (this.tipoPagamento.equals("D") || this.tipoPagamento.equals("JD")) {
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
            return 0 + this.despesa[this.despesa.length - 1];
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

    private double calculaDespesaSemanaMiddle(final Date payUpTo) {
        double total = 0;
        Calendar temp = Calendar.getInstance();
        temp.setTime(this.pagamento);
        if ((temp.get(Calendar.DAY_OF_WEEK) == temp.SUNDAY)) {
            temp.set(temp.HOUR, 24);
        }

        if (this.pagamento.equals(payUpTo)) {
            return this.despesa[6] + this.despesa[this.despesa.length - 1];
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

    public String updateCliente(
            final String name,
            final Date pagamento,
            final String tipoPagamento,
            final float[] despesa,
            final String morada,
            final int noPorta,
            final String alcunha,
            final Date dataAnterior,
            final float[] despesaAnterior) {

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
                if (dataAnterior != null) {
                    alteracoes = alteracoes + "Alteração da data de pagamento de " + dateFormat.format(dataAnterior) + " para " + dateFormat.format(pagamento)+ ";,";

                } else {

                    alteracoes = alteracoes + "Alteração da data de pagamento de " + dateFormat.format(this.pagamento) + " para " + dateFormat.format(pagamento)+ ";,";
                }
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
                if (despesaAnterior != null) {
                    alteracoes = alteracoes + "Alteração de valores da despesa do cliente;," + lastDespesas(despesaAnterior);

                } else {

                    alteracoes = alteracoes + "Alteração de valores da despesa do cliente;," + lastDespesas(this.despesa);
                }
            }
            this.despesa = Arrays.copyOf(despesa, despesa.length);
        }

        if (morada != null) {
            if (!this.morada.equals(morada)) {
                alteracoes = alteracoes + "Alteração da morada de " + this.morada.replace(",", "|") + " para " + morada.replace(",", "|") + ";,";
            }
            this.morada = morada;
        }

        if (noPorta != 0) {
            if (this.noPorta != noPorta) {
                alteracoes = alteracoes + "Alteração do número da porta de " + this.noPorta + " para " + noPorta + ";,";
            }
            this.noPorta = noPorta;
        }

        if (alcunha != null) {
            if (!this.alcunha.equals(alcunha)) {
                alteracoes = alteracoes + "Alteração da alcunha de " + this.alcunha + " para " + alcunha + ";,";
            }
            this.alcunha = alcunha;
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
        text += String.format("EXT  " + Float.toString(despesa[7]).replace(",", ".").replace("-", "&"));

        return text;
    }

    private String getWord(String tipoPagamento) {
        if (tipoPagamento.equals("S"))
            return "Semanal";
        else if (tipoPagamento.equals("M"))
            return "Mensal";
        else if (tipoPagamento.equals("D"))
            return "Diário";
        else if (tipoPagamento.equals("LS"))
            return "Loja Semanal";
        else if (tipoPagamento.equals("JD"))
            return "Junta Dias";
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

    public void setAtivo() {
        this.ativo = !this.ativo;
    }



    public String getMorada() {
        return this.morada.replaceAll(",", "|");
    }

    public int getNPorta() {
        return this.noPorta;
    }

    public String getAlcunha() {
        return this.alcunha;
    }

    public String alteracaoQuantidade(float[] despesa, String date) {
        String log = null;
        if (this.tipoPagamento.equals("M")) {
            try {
                Date data = formatter.parse(date);
                data = addDaysToDate(data, -1);
                double valorExtra = this.calculaDespesaMensal(data);

                despesa[despesa.length - 1] = (float) valorExtra;

                log = this.updateCliente(null, data, null, despesa, null, 0, null, null, this.despesa);


            } catch (ParseException e) {
                e.printStackTrace();
            }

        } else if (this.tipoPagamento.equals("S")) {
            /*try {
                Date data = formatter.parse(date);
                data = addDaysToDate(data, -1);

                Date ultimoPagamento = this.pagamento;
                float [] ultimaDespesa = Arrays.copyOf(this.despesa, this.despesa.length);

                double valorExtra = this.calculaDespesaSemanaMiddle(data);
                despesa[despesa.length - 1] = (float) valorExtra;
                data = addDaysToDate(data, 1);
                log = this.updateCliente(null, data, null, despesa, null, 0, null, null, null);

                data = getNextDomingo(data);

                valorExtra = this.calculaDespesaSemanaMiddle(data);
                despesa[despesa.length - 1] = (float) valorExtra;
                log = this.updateCliente(null, data, null, despesa, null, 0, null, ultimoPagamento, ultimaDespesa);


            } catch (ParseException e) {
                e.printStackTrace();
            }

             */

        }else {
            return log;
        }

        return log;
    }

    public String getQuantidadesString(int i, final TabelaPrecos tabelaPrecos) {
        HashMap<String, Integer> produtos = new HashMap<>();
        String somaString = "";

        for (String key : this.quantidades.keySet()) {
            if (dias[i].equals(key)) {

                produtos = this.quantidades.get(key);

                for (String produto : produtos.keySet()) {
                    somaString += tabelaPrecos.getNomeProdutoFromID(produto) + ": " + produtos.get(produto) + "\n";
                }
            }
        }

        return somaString;

        //return "Not Found";
    }

    public String getDescicaoProfutosString(int i) {
        boolean first = true;
        String somaString = "";


        for (String produto : this.quantidades.get(dias[i]).keySet()) {
            if (first) {
                first = false;
                somaString += produto + "-" + this.quantidades.get(dias[i]).get(produto);
            } else {
                somaString += ";";
                somaString += produto + "-" + this.quantidades.get(dias[i]).get(produto);
            }
        }

        if (somaString.isEmpty()) {
            return "-";
        }

        return somaString;
    }

    public HashMap<String, HashMap<String, Integer>> getDescricaoHashMap() {
        return this.quantidades;
    }

    public void replaceDescricaoQuantidades(int dia, final HashMap<String, Integer> descricao, final TabelaPrecos tabelaPrecos) {
        DecimalFormat df = new DecimalFormat("0.00");

        this.quantidades.replace(dias[dia], descricao);
        float totalNovaDespesa = (float) ((float) Math.round(getTotalfromDescricaoProdutos(descricao, tabelaPrecos) * 100.0) / 100.0);
        //float totalNovaDespesa = getTotalfromDescricaoProdutos(descricao, tabelaPrecos);

        if (totalNovaDespesa != this.despesa[dia]) {
            this.despesa[dia] = totalNovaDespesa;
        }

    }

    private float getTotalfromDescricaoProdutos(final HashMap<String, Integer> descricao, final TabelaPrecos tabelaPrecos) {
        float total = 0;

        for (String produtoId : descricao.keySet()) {
            total += tabelaPrecos.getValorFromProdutoID(produtoId, descricao.get(produtoId));
        }

        return total;
    }


}
