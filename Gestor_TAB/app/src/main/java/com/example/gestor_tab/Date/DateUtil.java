package com.example.gestor_tab.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class DateUtil {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    //System.out.println(formatter.format(date));

    public ArrayList<String> getNextSunday(Date ultimoPagamento) {
        ArrayList<String> listaDatas = new ArrayList<>();
        boolean antesQuarta = false;

        final Date hj = new Date();
        Calendar calendar = Calendar.getInstance();
        Calendar ultimoPag = Calendar.getInstance();
        calendar.setTime(hj);
        ultimoPag.setTime(ultimoPagamento);
        ultimoPag.add(Calendar.DATE, 1);

        if (calendar.get(Calendar.DAY_OF_WEEK) <= Calendar.WEDNESDAY && !(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
            antesQuarta = true;
        }

        while (ultimoPag.before(calendar)) {
            if (ultimoPag.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                listaDatas.add(formatter.format(ultimoPag.getTime()));
            }
            ultimoPag.add(Calendar.DATE, 1);
        }

        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        if (!listaDatas.contains(formatter.format(calendar.getTime()))) {
            listaDatas.add(formatter.format(calendar.getTime()));
        }

        Collections.reverse(listaDatas);

        //Change 2 first items
        if (antesQuarta && listaDatas.size() > 1) {
            String toMove = listaDatas.get(1);
            listaDatas.set(1, listaDatas.get(0));
            listaDatas.set(0, toMove);
        }

        //condição de quando a data de pagamento está á frente do dia de hj
        if (listaDatas.isEmpty()) {
            calendar.setTime(hj);
            listaDatas.add(formatter.format(calendar.getTime()));
        }

        return listaDatas;
    }

    public ArrayList<String> getNextMonth(Date ultimoPagamento) {
        ArrayList<String> listaDatas = new ArrayList<>();

        final Date hj = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(hj);
        Calendar ultimoPag = Calendar.getInstance();
        ultimoPag.setTime(ultimoPagamento);

        Calendar temp = Calendar.getInstance();


        while (ultimoPag.before(calendar)) {

            ultimoPag.add(Calendar.MONTH, 1);
            ultimoPag.set(Calendar.DAY_OF_MONTH, 1);

            ultimoPag.add(Calendar.DATE, -1);
            listaDatas.add(formatter.format(ultimoPag.getTime()));
            ultimoPag.add(Calendar.DATE, 1);

        }

        Collections.reverse(listaDatas);

        //condição de quando a data de pagamento está á frente do dia de hj
        if (listaDatas.isEmpty()) {
            calendar.setTime(ultimoPagamento);
            listaDatas.add(formatter.format(calendar.getTime()));
            return listaDatas;
        }

        if (calendar.get(Calendar.DAY_OF_MONTH) < 15) {
            String toMove = listaDatas.get(1);
            listaDatas.set(1, listaDatas.get(0));
            listaDatas.set(0, toMove);
        }

        return listaDatas;

    }
}
