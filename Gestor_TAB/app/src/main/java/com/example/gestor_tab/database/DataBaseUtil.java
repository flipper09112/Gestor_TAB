package com.example.gestor_tab.database;

import android.content.Context;

import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;
import com.example.gestor_tab.clientes.TabelaPrecos;
import com.example.gestor_tab.encomendas.Encomenda;
import com.example.gestor_tab.encomendas.EncomendaBolos;
import com.example.gestor_tab.encomendas.EncomendaManager;
import com.example.gestor_tab.enumClasses.TablePosition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DataBaseUtil {

    private File file;
    private Workbook wb;

    public DataBaseUtil(File f) {
        this.file=f;
        //TODO create log file

        this.wb = null;
        try {
            this.wb = Workbook.getWorkbook(file);
        } catch (IOException | BiffException e) {
            //TODO create a empty file example
        }
        wb.close();
    }

    public static TabelaPrecos getTabela(Context context) {
        TabelaPrecos tabelaPrecos = null;

        ArrayList<String> produtos = new ArrayList<>();
        ArrayList<Float> precos = new ArrayList<>();

        String prt;
        int cliente;
        float price;

        Workbook workbook;
        File file = new File(context.getExternalFilesDir(null), "TabelaPrecos.xls");
        try {
            workbook = Workbook.getWorkbook(file);
            Sheet sheet = workbook.getSheet(0);
            int linhas = sheet.getRows();
            int colunas = sheet.getColumns();

            //get precos clientes normais
            for (int i = 1; i < linhas; i++) {
                prt = sheet.getCell(0,i).getContents();
                price = Float.parseFloat(sheet.getCell(1,i).getContents().replace(",", "."));

                produtos.add(prt);
                precos.add(price);
            }

            tabelaPrecos = new TabelaPrecos(produtos, precos);

            for (int col = 2; col < colunas; col++) {
                cliente = Integer.parseInt(sheet.getCell(col, 0).getContents());
                precos = new ArrayList<>();

                for (int i = 1; i < linhas; i++) {
                    price = Float.parseFloat(sheet.getCell(col, i).getContents().replace(",", "."));
                    precos.add(price);
                }

                tabelaPrecos.setNewClientInfo(cliente, precos);
            }

            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        return tabelaPrecos;
    }

    public void saveTabela(Context context, TabelaPrecos tabelaPrecos) {
        File file = new File(context.getExternalFilesDir(null), "TabelaPrecos.xls");

        WritableWorkbook wb = null;
        try {
            wb = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wb.createSheet("Planilha", 0);
        WritableSheet plan = wb.getSheet(0);

        String cont;
        int count = 2;

        //completar a primeira linha
        Label label = new Label(0, 0, "Produto");
        Label label1 = new Label(1, 0, "Valor final");

        try {
            plan.addCell(label);
            plan.addCell(label1);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        for (Integer cliente : tabelaPrecos.getClientes()) {
            label = new Label(count, 0, cliente.toString());
            try {
                plan.addCell(label);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            count ++;
        }


        //completar as tabelas gerais (produtos e precos normais)
        ArrayList<String> produtos = tabelaPrecos.getProdutos();
        ArrayList<Float> precos = tabelaPrecos.getPrecos();
        for (int i = 0; i < produtos.size(); i++) {
            label = new Label(0, i + 1, produtos.get(i));
            label1 = new Label(1, i + 1, String.format("%.2f", precos.get(i)));
            try {
                plan.addCell(label);
                plan.addCell(label1);
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }

        //completar as tabelas de precos dos clientes
        count = 2;
        int cliente;
        for (int i = 0; i < tabelaPrecos.getClientes().size(); i++) {
            cliente = tabelaPrecos.getClientes().get(i);
            precos = tabelaPrecos.getTabelaPrecosCliente(cliente);

            for (int row = 0; row < precos.size(); row++) {
                label = new Label(count, i + 1, String.format("%.2f", precos.get(i)));

                try {
                    plan.addCell(label);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }

            count++;
        }

        //close files
        try {
            wb.write();
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }


    public ClientsManager getData() throws IOException, BiffException {
        boolean empty = true;
        Date pagamento = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
        String name;
        int number;
        String tipoPagamento;
        float despesa[] = new float[8];
        String coordenadas;

        final ArrayList<Cliente> clienteArrayList = new ArrayList<>();

        this.wb = Workbook.getWorkbook(file);
        Sheet sheet = this.wb.getSheet(0);
        int linhas = sheet.getRows();

        /*coluna 1*/
        for (int i=1;i<linhas;i++){
            empty = false;
            if (sheet.getCell(0,i).getContents().compareTo("")!=0){

                name = sheet.getCell(TablePosition.NOME.getValue(),i).getContents();
                number = Integer.parseInt(sheet.getCell(TablePosition.ID.getValue(),i).getContents());
                try {
                    if (!sheet.getCell(TablePosition.PAGAMENTO.getValue(),i).getContents().equals("NULL")) {
                        pagamento = ft.parse(sheet.getCell(TablePosition.PAGAMENTO.getValue(),i).getContents());
                    }

                } catch (ParseException e) {
                    System.out.println("Erro de parse da data");
                    return null;
                }
                tipoPagamento = sheet.getCell(TablePosition.TIPO.getValue(),i).getContents();

                despesa[0] = Float.parseFloat(sheet.getCell(TablePosition.SEGUNDA.getValue(),i).getContents().replace(",","."));
                despesa[1] = Float.parseFloat(sheet.getCell(TablePosition.TERCA.getValue(),i).getContents().replace(",","."));
                despesa[2] = Float.parseFloat(sheet.getCell(TablePosition.QUARTA.getValue(),i).getContents().replace(",","."));
                despesa[3] = Float.parseFloat(sheet.getCell(TablePosition.QUINTA.getValue(),i).getContents().replace(",","."));
                despesa[4] = Float.parseFloat(sheet.getCell(TablePosition.SEXTA.getValue(),i).getContents().replace(",","."));
                despesa[5] = Float.parseFloat(sheet.getCell(TablePosition.SABADO.getValue(),i).getContents().replace(",","."));
                despesa[6] = Float.parseFloat(sheet.getCell(TablePosition.DOMINGO.getValue(),i).getContents().replace(",","."));
                despesa[7] = Float.parseFloat(sheet.getCell(TablePosition.EXTRAS.getValue(),i).getContents().replace(",","."));

                coordenadas = sheet.getCell(TablePosition.COORDENADAS.getValue(),i).getContents();

                clienteArrayList.add(new Cliente(name, number, pagamento, tipoPagamento, despesa));

                if (!coordenadas.equals("null")) {
                    /*coordenadas = replaceOccurance(coordenadas, ",", ".", 1);
                    coordenadas = replaceOccurance(coordenadas, ",", ".", 2);*/

                    clienteArrayList.get(clienteArrayList.size()-1).setCoordenadas(coordenadas);
                }
            }
        }
        wb.close();

        if (empty)
            return null;

        final ClientsManager clientsManager = new ClientsManager(clienteArrayList);
        return clientsManager;
    }

    public static String replaceOccurance(String text, String replaceFrom, String replaceTo, int occuranceIndex)
    {
        StringBuffer sb = new StringBuffer();
        Pattern p = Pattern.compile(replaceFrom);
        Matcher m = p.matcher(text);
        int count = 0;
        while (m.find())
        {
            if (count++ == occuranceIndex - 1)
            {
                m.appendReplacement(sb, replaceTo);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public void guarda_em_ficheiro(final ClientsManager clientsManager) {
        final ArrayList<Cliente> clientes = clientsManager.getClientsList();
        WritableWorkbook wb = null;
        try {
            wb = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wb.createSheet("Planilha", 0);
        WritableSheet plan = wb.getSheet(0);

        Label label = new Label(TablePosition.NOME.getValue(),0,"Nome");
        Label labe2 = new Label(TablePosition.ID.getValue(),0,"ID");
        Label labe3 = new Label(TablePosition.PAGAMENTO.getValue(),0,"Pagamento");
        Label labe4 = new Label(TablePosition.TIPO.getValue(),0,"Tipo");
        Label labe5 = new Label(TablePosition.SEGUNDA.getValue(),0,"SEG");
        Label labe6 = new Label(TablePosition.TERCA.getValue(),0,"TER");
        Label labe7 = new Label(TablePosition.QUARTA.getValue(),0,"QUA");
        Label labe8 = new Label(TablePosition.QUINTA.getValue(),0,"QUI");
        Label labe9 = new Label(TablePosition.SEXTA.getValue(),0,"SEX");
        Label labe10 = new Label(TablePosition.SABADO.getValue(),0,"SAB");
        Label labe11 = new Label(TablePosition.DOMINGO.getValue(),0,"DOM");
        Label labe12 = new Label(TablePosition.EXTRAS.getValue(),0,"EXTRAS");
        Label labe13 = new Label(TablePosition.COORDENADAS.getValue(),0,"Coordenadas");

        try {
            plan.addCell(label);
            plan.addCell(labe2);
            plan.addCell(labe3);
            plan.addCell(labe4);
            plan.addCell(labe5);
            plan.addCell(labe6);
            plan.addCell(labe7);
            plan.addCell(labe8);
            plan.addCell(labe9);
            plan.addCell(labe10);
            plan.addCell(labe11);
            plan.addCell(labe12);
            plan.addCell(labe13);

        } catch (RowsExceededException e1) {
            e1.printStackTrace();
        } catch (WriteException e1) {
            e1.printStackTrace();
        }


        for (int i=1;i<=clientes.size();i++){
            label = new Label(TablePosition.NOME.getValue(),i,clientes.get(i-1).getName());
            labe2 = new Label(TablePosition.ID.getValue(),i,Integer.toString(clientes.get(i-1).getId()));
            labe3 = new Label(TablePosition.PAGAMENTO.getValue(),i,clientes.get(i-1).getPagamento());
            labe4 = new Label(TablePosition.TIPO.getValue(),i,clientes.get(i-1).getTipoPagamento());

            float[] despesa = clientes.get(i-1).getDespesa();

            labe5 = new Label(TablePosition.SEGUNDA.getValue(),i,Float.toString(despesa[0]));
            labe6 = new Label(TablePosition.TERCA.getValue(),i,Float.toString(despesa[1]));
            labe7 = new Label(TablePosition.QUARTA.getValue(),i,Float.toString(despesa[2]));
            labe8 = new Label(TablePosition.QUINTA.getValue(),i,Float.toString(despesa[3]));
            labe9 = new Label(TablePosition.SEXTA.getValue(),i,Float.toString(despesa[4]));
            labe10 = new Label(TablePosition.SABADO.getValue(),i,Float.toString(despesa[5]));
            labe11 = new Label(TablePosition.DOMINGO.getValue(),i,Float.toString(despesa[6]));
            labe12 = new Label(TablePosition.EXTRAS.getValue(),i,Float.toString(despesa[7]));

            if (clientes.get(i-1).getCoordenadas() != null) {
                labe13 = new Label(TablePosition.COORDENADAS.getValue(), i, clientes.get(i-1).getCoordenadas());
            } else {
                labe13 = new Label(TablePosition.COORDENADAS.getValue(), i, "null");
            }

            try {
                plan.addCell(label);
                plan.addCell(labe2);
                plan.addCell(labe3);
                plan.addCell(labe4);

                plan.addCell(labe5);
                plan.addCell(labe6);
                plan.addCell(labe7);
                plan.addCell(labe8);
                plan.addCell(labe9);
                plan.addCell(labe10);
                plan.addCell(labe11);
                plan.addCell(labe12);

                plan.addCell(labe13);

            } catch (RowsExceededException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
        }
        try {
            wb.write();
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     *
     * @param context contexto da chamada da função
     * @param tipo
     *                <p>0 - adição de extras</p>
     *                <p>1 - pagamento</p>
     *                <p>2 - Edição</p>
     *                <p>3 - Encomenda</p>
     *                <p>4 - Localização</p>
     * @param id
     * @param info
     *                <p>0 - Estrutura da info: EXTRA [ID]: dados-data</p>
     *                <p>1 - Estrutura da info: PAGAMENTO [ID]: dados-data</p>
     *                <p>2 - Estrutura da info: EDITADO [ID]: dados-data</p>
     *                <p>2 - Estrutura da info: ENCOMENDA [ID]: dados-data</p>
     */
    public static void logs(Context context, int tipo, int id, String info) {
        String[] listaEventos = {"EXTRA [" + id + "]", "PAGAMENTO [" + id + "]", "EDICAO [" + id + "]", "ENCOMENDA [" + id + "]", "LOCALIZAÇÃO [" + id + "]"};

        String data = listaEventos[tipo] + ": " + info;

        File file = new File(context.getExternalFilesDir(null), "logs");
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, "logs.txt");
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception e) { }

    }

    public static RegistosManager getLogsClient(Context context, final int id) {
        ArrayList<Registo> registos = new ArrayList<>();

        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", "logs.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(":");
                String tipo = lineSplit[0].split(" ")[0];

                //get id
                String logId = lineSplit[0];
                logId = logId.substring(logId.indexOf("[") + 1);
                logId = logId.substring(0, logId.indexOf("]"));

                //get data
                String data = line.split("-")[1];

                String info = "";
                if (tipo.equals("PAGAMENTO") || tipo.equals("EXTRA")) {

                    //get info
                    info = lineSplit[1];
                    info = info.substring(1, info.indexOf("€")+1);
                }
                else if (tipo.equals("EDICAO")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(0, info.indexOf("-"));
                }

                else if (tipo.equals("ENCOMENDA")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(0, info.indexOf("-"));
                }

                else if (tipo.equals("LOCALIZAÇÃO")) {
                    info = lineSplit[1];
                    int ocur = countOccurencesChar(info, '-');
                    if (ocur > 1) {
                        info = replaceOccurance(info, "-", "&", ocur);
                        info = info.substring(0, info.indexOf("&"));

                        String temp = lineSplit[1];
                        temp = replaceOccurance(temp, "-", "&", ocur);

                        data = temp.split("&")[1];
                    }
                    else
                        info = info.substring(0, info.indexOf("-"));
                }

                else {
                    continue;
                }

                if (logId.equals(Integer.toString(id))) {
                    registos.add(new Registo(id, lineSplit[0].split(" ")[0], info, data));
                }

            }

            br.close();
            if (registos.isEmpty()) {
                registos.add(new Registo(id, "INFO", "Sem operações realizadas!", null));
            }
            return new RegistosManager(registos);
        } catch (IOException e) {
            return new RegistosManager(registos);
        }
    }

    public static int countOccurencesChar(String someString, char someChar) {
        int count = 0;

        for (int i = 0; i < someString.length(); i++) {
            if (someString.charAt(i) == someChar) {
                count++;
            }
        }

        return count;
    }

    public static RegistosManager getAllLogsEncomendas(Context context, Date hj) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date diaEncomenda;

        ArrayList<Registo> registos = new ArrayList<>();

        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", "logs.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                String[] lineSplit = line.split(":");
                String tipo = lineSplit[0].split(" ")[0];

                //get id
                String logId = lineSplit[0];
                logId = logId.substring(logId.indexOf("[") + 1);
                logId = logId.substring(0, logId.indexOf("]"));

                String info = "";
                String data = "";

                if (tipo.equals("ENCOMENDA")) {
                    data = lineSplit[1].split(" ")[4].split(",")[0];
                }

                if (tipo.equals("ENCOMENDA") && data.equals(dateFormat.format(hj))) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(0, info.indexOf("-"));

                    registos.add(new Registo(Integer.parseInt(logId), lineSplit[0].split(" ")[0], info, data));
                }

            }

            br.close();
            if (registos.isEmpty()) {
                registos.add(new Registo(-1, "INFO", "Sem operações realizadas!", null));
            }
            return new RegistosManager(registos);
        } catch (IOException e) {
            return new RegistosManager(registos);
        }
    }



    public static void deleteAllLogs(Context context) {
        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", "logs.txt");
        fileEvents.delete();

        File file = new File(context.getFilesDir(), "logs");
        if (!file.exists()) {
            file.mkdir();
        }
        File gpxfile = new File(file, "log.txt");
    }

    public static void saveEncomenda(File file, EncomendaManager encomendaManager) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(encomendaManager.getEncomendaSegunda().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaSegunda().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSegunda().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaTerca().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaTerca().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaTerca().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaQuarta().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaQuarta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaQuarta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaQuinta().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaQuinta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaQuinta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaSexta().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaSexta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSexta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaSabado().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaSabado().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSabado().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaDomingo().toString());
            for (int i = 0 ; i < encomendaManager.getEncomendaDomingo().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaDomingo().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.flush();
            writer.close();
        } catch (Exception e) { }
    }

    public static void getEncomenda(File file, EncomendaManager encomendaManager) {
        try {
            ArrayList<Integer> items = new ArrayList<>();
            String dia;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            int quantidade;
            int count = 1;

            while ((line = br.readLine()) != null) {
                dia = line;

                if (dia.equals(""))
                    break;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    quantidade = Integer.parseInt(line.split("-")[1].replaceAll(" ", ""));
                    items.add(quantidade);

                    if (count == encomendaManager.numberOfItems()) {
                        count = 1;
                        break;
                    }
                    count++;
                }

                ArrayList<EncomendaBolos> encomendaBolosArrayList = new ArrayList<>();
                while (!(line = br.readLine()).equals("FINAL")) {
                    EncomendaBolos encomendaBolos = new EncomendaBolos(line);
                    while (!(line = br.readLine()).equals("FINAL")) {
                        if (line.equals("END"))
                            break;

                        line = line.replaceAll(" ", "");
                        int id = Integer.parseInt(line.split("-")[0]);
                        int total  = Integer.parseInt(line.split("-")[1]);

                        encomendaBolos.addBolos(id, total);

                    }
                    encomendaBolosArrayList.add(encomendaBolos);
                }


                encomendaManager.setEncomenda(createEncomenda(dia, items, encomendaBolosArrayList));
                items.clear();

            }
            br.close();
        }catch (IOException e) { }

    }

    private static Encomenda createEncomenda(String dia, ArrayList<Integer> items, ArrayList<EncomendaBolos> encomendaBolos) {
        Encomenda encomenda =
                new Encomenda(dia,
                        items.get(0), items.get(1), items.get(2),
                        items.get(3), items.get(4), items.get(5),
                        items.get(6), items.get(7), items.get(8),
                        items.get(9), items.get(10), items.get(11),
                        items.get(12), items.get(13), items.get(14),
                        items.get(15), items.get(16), items.get(17)
                        );

        encomenda.setEncomendaBolosArrayList(encomendaBolos);

        return encomenda;
    }

}
