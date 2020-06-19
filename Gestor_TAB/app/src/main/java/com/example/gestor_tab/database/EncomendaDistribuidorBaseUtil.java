package com.example.gestor_tab.database;

import com.example.gestor_tab.encomendas.Encomenda;
import com.example.gestor_tab.encomendas.EncomendaBolos;
import com.example.gestor_tab.encomendas.EncomendaBolosV2;
import com.example.gestor_tab.encomendas.EncomendaManager;
import com.example.gestor_tab.encomendas.EncomendaManagerV2;
import com.example.gestor_tab.encomendas.EncomendaV2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class EncomendaDistribuidorBaseUtil extends DataBaseUtil {


    public static void saveEncomendaV2(File file, EncomendaManagerV2 encomendaManager) {
        try {
            FileWriter writer = new FileWriter(file);
            writer.append(encomendaManager.getEncomendaSegunda().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaSegunda().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSegunda().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaTerca().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaTerca().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaTerca().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaQuarta().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaQuarta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaQuarta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaQuinta().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaQuinta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaQuinta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaSexta().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaSexta().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSexta().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaSabado().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaSabado().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaSabado().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.append(encomendaManager.getEncomendaDomingo().toString());
            writer.append("END\n");
            for (int i = 0 ; i < encomendaManager.getEncomendaDomingo().getEncomendaBolosArrayList().size(); i++) {
                writer.append(encomendaManager.getEncomendaDomingo().getEncomendaBolosArrayList().get(i).toStringForSaveInDoc());
                writer.append("END\n");
            }
            writer.append("FINAL\n");

            writer.flush();
            writer.close();
        } catch (Exception e) { }
    }


    public static void getEncomendav2(File file, EncomendaManagerV2 encomendaManager, int n_produtos) {
        try {
            ArrayList<Float> items = new ArrayList<>();
            ArrayList<String> products = new ArrayList<>();
            String dia;

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            float quantidade;
            boolean first = true;

            while ((line = br.readLine()) != null) {

                dia = line;

                if (dia.equals(""))
                    break;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);

                    if (line.equals("END"))
                        break;

                    quantidade = Float.parseFloat(line.split("-")[1].replaceAll(" ", ""));
                    items.add(quantidade);
                    products.add(line.split("-")[0].trim());

                }

                ArrayList<EncomendaBolosV2> encomendaBolosArrayList = new ArrayList<>();

                while (!(line = br.readLine()).equals("FINAL")) {
                    EncomendaBolosV2 encomendaBolos = new EncomendaBolosV2(line);
                    while (!(line = br.readLine()).equals("FINAL")) {
                        if (line.equals("END"))
                            break;

                        try {

                            //line = line.replaceAll(" ", "");
                            String nome = line.split("-")[0].trim();
                            int total  = Integer.parseInt(line.split("-")[1].trim());

                            encomendaBolos.addBolos(nome, total);

                        } catch (Exception e) {
                            continue;
                        }

                    }
                    encomendaBolosArrayList.add(encomendaBolos);
                }


                encomendaManager.setEncomenda(new EncomendaV2(dia, products, items, encomendaBolosArrayList));
                items.clear();
                products.clear();

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
