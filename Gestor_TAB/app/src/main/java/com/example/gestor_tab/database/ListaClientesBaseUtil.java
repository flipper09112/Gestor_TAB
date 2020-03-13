package com.example.gestor_tab.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.ClientsManager;
import com.example.gestor_tab.enumClasses.TablePosition;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ListaClientesBaseUtil extends DataBaseUtil {

    private final String fileName = "MeuArquivoXLS.xls";

    private File file;

    private Workbook wb;

    private Activity activity;

    public ListaClientesBaseUtil(final Activity applicationActivity) {
        super();

        this.file=new File(applicationActivity.getExternalFilesDir(null), fileName);

        this.activity = applicationActivity;

        this.wb = null;
        try {
            this.wb = Workbook.getWorkbook(file);
            wb.close();
        } catch (IOException | BiffException e) {
            applicationActivity.setContentView(R.layout.error_template);

            TextView msgErro = applicationActivity.findViewById(R.id.textView27);
            msgErro.setText("Ficheiro com lista de clientes não encontrado!");

            Button button = applicationActivity.findViewById(R.id.button14);
            button.setVisibility(View.VISIBLE);
            button.setText("Criar ficheiro exemplo");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    file = new File(applicationActivity.getApplicationContext().getExternalFilesDir(null), "MeuArquivoXLS.xls");
                    createTemplate();
                    Intent intent = applicationActivity.getIntent();
                    applicationActivity.finish();
                    applicationActivity.startActivity(intent);
                }
            });

        }
    }

    public ClientsManager getData() throws IOException, BiffException {
        if (this.wb == null)
            throw new Resources.NotFoundException();
        boolean empty = true;
        Date pagamento = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy");
        String name;
        int number;
        String tipoPagamento;
        float despesa[] = new float[8];
        String coordenadas;
        String ativo;
        String dataInicio;
        String dataFim;

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
                    this.activity.setContentView(R.layout.error_template);

                    TextView msgErro = this.activity.findViewById(R.id.textView27);
                    msgErro.setText("Erro de parse da data da linha: " + i + " coluna: " + TablePosition.PAGAMENTO.getValue());
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

                ativo = sheet.getCell(TablePosition.ATIVO.getValue(),i).getContents();
                dataInicio = sheet.getCell(TablePosition.INICIO_INITAVIDADE.getValue(),i).getContents();
                dataFim = sheet.getCell(TablePosition.FIM_INITAVIDADE.getValue(),i).getContents();

                clienteArrayList.add(new Cliente(name, number, pagamento, tipoPagamento, despesa));

                if (!coordenadas.equals("null")) {
                    clienteArrayList.get(clienteArrayList.size()-1).setCoordenadas(coordenadas);
                }

                if (ativo.equals("1"))
                    clienteArrayList.get(clienteArrayList.size()-1).setAtivo(true);
                else
                    clienteArrayList.get(clienteArrayList.size()-1).setAtivo(false);

                if (!dataInicio.equals("-") && !dataFim.equals("-")) {
                    clienteArrayList.get(clienteArrayList.size()-1).setAtivo(dataInicio, dataFim);
                }
            }
        }
        wb.close();

        if (empty)
            return null;

        return new ClientsManager(clienteArrayList);

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
        Label labe14 = new Label(TablePosition.ATIVO.getValue(),0,"Ativo");
        Label labe15 = new Label(TablePosition.INICIO_INITAVIDADE.getValue(),0,"Inicio Inatividade");
        Label labe16 = new Label(TablePosition.FIM_INITAVIDADE.getValue(),0,"Fim Inatividade");


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
            plan.addCell(labe14);
            plan.addCell(labe15);
            plan.addCell(labe16);

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

            if (clientes.get(i-1).getAtivo()) {
                labe14 = new Label(TablePosition.ATIVO.getValue(), i, "1");
            } else {
                labe14 = new Label(TablePosition.ATIVO.getValue(), i, "0");
            }

            labe15 = new Label(TablePosition.INICIO_INITAVIDADE.getValue(), i, clientes.get(i-1).getInicioInatividade());
            labe16 = new Label(TablePosition.FIM_INITAVIDADE.getValue(), i, clientes.get(i-1).getFimInatividade());

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
                plan.addCell(labe14);

                plan.addCell(labe15);
                plan.addCell(labe16);

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

    public void createTemplate() {
        int numberClients = 10;
        String tiposPagamento[] = {"D", "S", "M", "LS"};
        String coordenadas[] = {"41,785989, -8,759839",
                "41,788432, -8,734293",
                "41,785732, -8,726119",
                "41,790108, -8,743985",
                "41,801439, -8,745848",
                "41,816218, -8,755430",
                "41,829212, -8,762549",
                "41,817726, -8,778967",
                "41,793669, -8,765681",
                "41,790784, -8,768020"
        };

        WritableWorkbook wb = null;
        try {
            wb = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        wb.createSheet("Planilha", 0);
        WritableSheet plan = wb.getSheet(0);

        Label label = new Label(TablePosition.NOME.getValue(), 0, "Nome");
        Label labe2 = new Label(TablePosition.ID.getValue(), 0, "ID");
        Label labe3 = new Label(TablePosition.PAGAMENTO.getValue(), 0, "Pagamento");
        Label labe4 = new Label(TablePosition.TIPO.getValue(), 0, "Tipo");
        Label labe5 = new Label(TablePosition.SEGUNDA.getValue(), 0, "SEG");
        Label labe6 = new Label(TablePosition.TERCA.getValue(), 0, "TER");
        Label labe7 = new Label(TablePosition.QUARTA.getValue(), 0, "QUA");
        Label labe8 = new Label(TablePosition.QUINTA.getValue(), 0, "QUI");
        Label labe9 = new Label(TablePosition.SEXTA.getValue(), 0, "SEX");
        Label labe10 = new Label(TablePosition.SABADO.getValue(), 0, "SAB");
        Label labe11 = new Label(TablePosition.DOMINGO.getValue(), 0, "DOM");
        Label labe12 = new Label(TablePosition.EXTRAS.getValue(), 0, "EXTRAS");
        Label labe13 = new Label(TablePosition.COORDENADAS.getValue(), 0, "Coordenadas");
        Label labe14 = new Label(TablePosition.ATIVO.getValue(), 0, "Ativo");
        Label labe15 = new Label(TablePosition.INICIO_INITAVIDADE.getValue(), 0, "Inicio Inatividade");
        Label labe16 = new Label(TablePosition.FIM_INITAVIDADE.getValue(), 0, "Fim Inatividade");

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
            plan.addCell(labe14);
            plan.addCell(labe15);
            plan.addCell(labe16);

        } catch (RowsExceededException e1) {
            e1.printStackTrace();
        } catch (WriteException e1) {
            e1.printStackTrace();
        }

        int count = 0;
        for (int i=1;i<=numberClients;i++){
            count++;
            label = new Label(TablePosition.NOME.getValue(),i,"Cliente " + count);
            labe2 = new Label(TablePosition.ID.getValue(),i,Integer.toString(count*1000));
            labe3 = new Label(TablePosition.PAGAMENTO.getValue(),i,"16/01/2020");
            labe4 = new Label(TablePosition.TIPO.getValue(),i,tiposPagamento[new Random().nextInt(4)]);

            labe5 = new Label(TablePosition.SEGUNDA.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe6 = new Label(TablePosition.TERCA.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe7 = new Label(TablePosition.QUARTA.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe8 = new Label(TablePosition.QUINTA.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe9 = new Label(TablePosition.SEXTA.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe10 = new Label(TablePosition.SABADO.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe11 = new Label(TablePosition.DOMINGO.getValue(),i,Integer.toString(new Random().nextInt(100)));
            labe12 = new Label(TablePosition.EXTRAS.getValue(),i,Integer.toString(new Random().nextInt(100)));


            labe13 = new Label(TablePosition.COORDENADAS.getValue(), i, coordenadas[i-1]);


            if (count != 7) {
                labe14 = new Label(TablePosition.ATIVO.getValue(), i, "1");
            } else {
                labe14 = new Label(TablePosition.ATIVO.getValue(), i, "0");
            }

            labe15 = new Label(TablePosition.INICIO_INITAVIDADE.getValue(), i, "-");
            labe16 = new Label(TablePosition.FIM_INITAVIDADE.getValue(), i, "-");

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
                plan.addCell(labe14);

                plan.addCell(labe15);
                plan.addCell(labe16);

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

}
