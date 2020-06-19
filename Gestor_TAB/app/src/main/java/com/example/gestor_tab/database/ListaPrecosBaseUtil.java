package com.example.gestor_tab.database;

import android.content.Context;

import com.example.gestor_tab.clientes.BolosManager;
import com.example.gestor_tab.clientes.TabelaPrecos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ListaPrecosBaseUtil extends DataBaseUtil {

    private static final String fileName = "TabelaPrecos.xls";

    private static int NOMES = 0;
    private static int IDS = 1;
    private static int UNIDADE = 2;
    private static int PVP = 3;
    private static int RESTANTES = 4;

    public ListaPrecosBaseUtil() {
        super();

        //TODO receive file and create if empty
    }

    public static TabelaPrecos getTabela(Context context) {
        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");

        TabelaPrecos tabelaPrecos = null;

        ArrayList<String> produtos = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        ArrayList<Integer> isUnity = new ArrayList<>();
        ArrayList<Float> precos = new ArrayList<>();

        String prt;
        int unity;
        int id;
        int cliente;
        float price;

        Workbook workbook;
        File file = new File(context.getExternalFilesDir(null), fileName);
        try {
            workbook = Workbook.getWorkbook(file, ws);
            Sheet sheet = workbook.getSheet(0);
            int linhas = sheet.getRows();
            int colunas = sheet.getColumns();

            //get precos clientes normais
            for (int i = 1; i < linhas; i++) {
                prt = sheet.getCell(NOMES,i).getContents();
                if (sheet.getCell(UNIDADE, i).getContents().equals(""))
                    break;
                unity = Integer.parseInt(sheet.getCell(UNIDADE, i).getContents());
                id = Integer.parseInt(sheet.getCell(IDS, i).getContents());
                price = Float.parseFloat(sheet.getCell(PVP,i).getContents().replace(",", "."));

                produtos.add(prt);
                isUnity.add(unity);
                precos.add(price);
                ids.add(id);
            }

            tabelaPrecos = new TabelaPrecos(produtos, isUnity, precos, ids);

            for (int col = RESTANTES; col < colunas; col++) {
                if (sheet.getCell(col, 0).getContents().equals("")) {
                    break;
                } else {
                    cliente = Integer.parseInt(sheet.getCell(col, 0).getContents());

                }
                precos = new ArrayList<>();

                for (int i = 1; i < linhas; i++) {
                    if (sheet.getCell(col, i).getContents().equals("")) {
                        break;
                    }
                    price = Float.parseFloat(sheet.getCell(col, i).getContents().replace(",", "."));
                    precos.add(price);
                }

                tabelaPrecos.setNewClientInfo(cliente, precos);
            }

            tabelaPrecos.setBolosManager(getListaBolosPastelaria(workbook));

            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

        return tabelaPrecos;
    }

    private static BolosManager getListaBolosPastelaria(Workbook workbook) {

        ArrayList listaProdutos = new ArrayList<String>();
        ArrayList produtosIDS = new ArrayList<String>();

        Sheet sheet = workbook.getSheet(1);
        int linhas = sheet.getRows();
        int colunas = sheet.getColumns();
        int id;

        for (int i = 1; i < linhas; i++) {
            listaProdutos.add(sheet.getCell(0,i).getContents());
            id = Integer.parseInt(sheet.getCell(1, i).getContents());
            produtosIDS.add(id);
        }

        return new BolosManager(listaProdutos, produtosIDS);
    }

    public void saveTabela(Context context, TabelaPrecos tabelaPrecos) {
        File file = new File(context.getExternalFilesDir(null), fileName);

        WritableWorkbook wb = null;
        try {
            wb = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wb.createSheet("Planilha", 0);
        WritableSheet plan = wb.getSheet(0);

        String cont;
        int count = 4;

        //completar a primeira linha
        Label label = new Label(0, 0, "Produto");
        Label label1 = new Label(1, 0, "ID");
        Label label2 = new Label(2, 0, "Unidade");
        Label label3 = new Label(3, 0, "Valor final");

        try {
            plan.addCell(label);
            plan.addCell(label1);
            plan.addCell(label2);
            plan.addCell(label3);
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
        ArrayList<Integer> unit = tabelaPrecos.getUnit();
        ArrayList<Integer> ids = tabelaPrecos.getIDs();
        ArrayList<Float> precos = tabelaPrecos.getPrecos();

        for (int i = 0; i < produtos.size(); i++) {
            label = new Label(0, i + 1, produtos.get(i));
            label1 = new Label(1, i + 1, ids.get(i).toString());
            label2 = new Label(2, i + 1, unit.get(i).toString());
            label3 = new Label(3, i + 1, String.format("%.2f", precos.get(i)));
            try {
                plan.addCell(label);
                plan.addCell(label1);
                plan.addCell(label2);
                plan.addCell(label3);
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }

        //completar as tabelas de precos dos clientes
        count = 4;
        int cliente;
        for (int i = 0; i < tabelaPrecos.getClientes().size(); i++) {
            cliente = tabelaPrecos.getClientes().get(i);
            precos = tabelaPrecos.getTabelaPrecosCliente(cliente);

            for (int row = 0; row < precos.size(); row++) {
                label = new Label(count, row + 1, String.format("%.2f", precos.get(row)));

                try {
                    plan.addCell(label);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }

            count++;
        }

        wb.createSheet("bolos", 1);
        plan = wb.getSheet(1);
        //completar a primeira linha
        label = new Label(0, 0, "Produto");
        label1 = new Label(1, 0, "ID");

        try {
            plan.addCell(label);
            plan.addCell(label1);
        } catch (WriteException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < tabelaPrecos.getListaBolos().size(); i++) {
            label = new Label(0, i + 1, tabelaPrecos.getListaBolos().get(i));
            label1 = new Label(1, i + 1, tabelaPrecos.getIdBolo(i));

            try {
                plan.addCell(label);
                plan.addCell(label1);
            } catch (WriteException e) {
                e.printStackTrace();
            }
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
}
