package com.example.gestor_tab.database;

import android.content.Context;

import com.example.gestor_tab.clientes.Registo;
import com.example.gestor_tab.clientes.RegistosManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class LogsBaseUtil extends DataBaseUtil {

    private static final String fileName = "logs.txt";

    public static ArrayList<String> getTiposLogs() {
        ArrayList<String> tipos = new ArrayList<>();
        tipos.add("TODOS");
        tipos.add("EXTRAS");
        tipos.add("PAGAMENTOS");
        tipos.add("EDIÇÕES");
        tipos.add("ENCOMENDAS");
        tipos.add("LOCALIZAÇÕES");
        tipos.add("INATIVIDADE");
        tipos.add("REGISTOS");
        tipos.add("SOBRAS");
        tipos.add("NOVO CLIENTE");
        tipos.add("CLIENTES REMOVIDOS");

        return tipos;
    }

    /**
     * NOTAS: Nao usar ':' que serve para separar em 2 a string
     *
     *
     * @param context contexto da chamada da função
     * @param tipo
     *                <p>0 - adição de extras</p>
     *                <p>1 - pagamento</p>
     *                <p>2 - Edição</p>
     *                <p>3 - Encomenda</p>
     *                <p>4 - Localização</p>
     *                <p>5 - Inatividade</p>
     *                <p>6 - Registo Lojas</p>
     *                <p>7 - Sobras do dia</p>
     *                <p>8 - Criação de um novo Cliente</p>
     *                <p>8 - Remoção de um Cliente</p>
     */
    public static void logs(Context context, int tipo, int id, String info) {
        String[] listaEventos = {
                "EXTRA [" + id + "]",
                "PAGAMENTO [" + id + "]",
                "EDICAO [" + id + "]",
                "ENCOMENDA [" + id + "]",
                "LOCALIZAÇÃO [" + id + "]",
                "INATIVIDADE [" + id + "]",
                "REGISTO [" + id + "]",
                "SOBRA [" + id + "]",
                "NOVOCLIENTE [" + id + "]",
                "REMOVECLIENTE [" + id + "]"
        };

        String data = listaEventos[tipo] + ": " + info;

        File file = new File(context.getExternalFilesDir(null), "logs");

        if (!file.exists()) {
            file.mkdir();
        }
        try {
            File gpxfile = new File(file, fileName);
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.append(data + "\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {

        }

    }

    public static RegistosManager getLogsClient(Context context, final int id) {
        ArrayList<Registo> registos = new ArrayList<>();

        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] lineSplit = line.split(":");
                String tipo = lineSplit[0].split(" ")[0];

                //get id
                String logId = lineSplit[0];
                logId = logId.substring(logId.indexOf("[") + 1);
                logId = logId.substring(0, logId.indexOf("]"));

                //get data
                String data = line.split("-")[1];

                String info;
                if (tipo.equals("PAGAMENTO") || tipo.equals("EXTRA")) {

                    //get info
                    info = lineSplit[1];
                    info = info.substring(1, info.indexOf("€")+1);
                }
                else if (tipo.equals("EDICAO") || tipo.equals("SOBRA")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(1, info.indexOf("-"));
                }

                else if (tipo.equals("ENCOMENDA") || tipo.equals("REGISTO")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(1, info.indexOf("-"));
                    if (tipo.equals("REGISTO")) {
                        info = info.replaceAll("]", "\t");
                        info = info.replaceAll(";", "-");
                    }
                }

                else if (tipo.equals("LOCALIZAÇÃO")) {
                    info = lineSplit[1];
                    int ocur = countOccurencesChar(info, '-');
                    if (ocur > 1) {
                        info = replaceOccurance(info, "-", "&", ocur);
                        info = info.substring(1, info.indexOf("&"));

                        String temp = lineSplit[1];
                        temp = replaceOccurance(temp, "-", "&", ocur);

                        data = temp.split("&")[1];
                    }
                    else
                        info = info.substring(1, info.indexOf("-"));

                } else if (tipo.equals("INATIVIDADE") || tipo.equals("NOVOCLIENTE") || tipo.equals("REMOVECLIENTE")) {
                    info = lineSplit[1];
                    info = info.substring(1, info.indexOf("-"));
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

    public static RegistosManager getAllLogs(Context context) {
        ArrayList<Registo> registos = new ArrayList<>();

        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", fileName);

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

                String info;
                if (tipo.equals("PAGAMENTO") || tipo.equals("EXTRA")) {

                    //get info
                    info = lineSplit[1];
                    info = info.substring(1, info.indexOf("€")+1);
                }
                else if (tipo.equals("EDICAO") || tipo.equals("SOBRA")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(1, info.indexOf("-"));
                }

                else if (tipo.equals("ENCOMENDA") || tipo.equals("REGISTO")) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(1, info.indexOf("-"));
                    if (tipo.equals("REGISTO")) {
                        info = info.replaceAll("]", "\t");
                        info = info.replaceAll(";", "-");
                    }
                }

                else if (tipo.equals("LOCALIZAÇÃO")) {
                    info = lineSplit[1];
                    int ocur = countOccurencesChar(info, '-');
                    if (ocur > 1) {
                        info = replaceOccurance(info, "-", "&", ocur);
                        info = info.substring(1, info.indexOf("&"));

                        String temp = lineSplit[1];
                        temp = replaceOccurance(temp, "-", "&", ocur);

                        data = temp.split("&")[1];
                    }
                    else
                        info = info.substring(1, info.indexOf("-"));

                } else if (tipo.equals("INATIVIDADE") || tipo.equals("NOVOCLIENTE") || tipo.equals("REMOVECLIENTE")) {
                    info = lineSplit[1];
                    info = info.substring(1, info.indexOf("-"));
                }

                else {
                    continue;
                }

                registos.add(new Registo(Integer.parseInt(logId), lineSplit[0].split(" ")[0], info, data));

            }

            br.close();
            if (registos.isEmpty()) {
                registos.add(new Registo(0, "INFO", "Sem operações realizadas!", null));
            }
            return new RegistosManager(registos);
        } catch (IOException e) {
            return new RegistosManager(registos);
        }
    }

    public static RegistosManager getAllLogsEncomendas(Context context, Date hj) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date diaEncomenda;

        ArrayList<Registo> registos = new ArrayList<>();

        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", fileName);

        try {
            BufferedReader br = new BufferedReader(new FileReader(fileEvents));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                String[] lineSplit = line.split(":");
                String tipo = lineSplit[0].split(" ")[0];

                //get id
                String logId = lineSplit[0];
                logId = logId.substring(logId.indexOf("[") + 1);
                logId = logId.substring(0, logId.indexOf("]"));

                String info = "";
                Date data = null;

                if (tipo.equals("ENCOMENDA")) {
                    try {
                        data = dateFormat.parse(lineSplit[1].split(" ")[4].split(",")[0]);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (tipo.equals("ENCOMENDA") && dateFormat.format(data).equals(dateFormat.format(hj))) {
                    info = lineSplit[1].replaceAll(",", "\n");
                    info = info.substring(0, info.indexOf("-"));

                    registos.add(new Registo(Integer.parseInt(logId), lineSplit[0].split(" ")[0], info, dateFormat.format(data)));
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
        File fileEvents = new File(context.getExternalFilesDir(null)+"/logs", fileName);
        fileEvents.delete();

        File file = new File(context.getFilesDir(), "logs");
        if (!file.exists()) {
            file.mkdir();
        }
        new File(file, "log.txt");
    }

    public static void saveAllLogs(final Context context, final RegistosManager registosManager) {
        try {
            File fileEvents = new File(context.getExternalFilesDir(null)+"/logsTest", fileName);
            FileWriter writer = new FileWriter(fileEvents);

            ArrayList<Registo> registos = registosManager.getAllRegistos();
            Collections.reverse(registos);

            for (int i = 0; i < registos.size(); i++) {
                writer.write(registos.get(i).toFileTxt() + "\n");
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
