package com.example.gestor_tab.activitys.ClienteActivity_Functions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.gestor_tab.R;
import com.example.gestor_tab.clientes.Cliente;
import com.example.gestor_tab.clientes.TabelaPrecos;

public class ShowInfoCliente {

    private final Context context;
    private final Activity activity;
    private final Cliente cliente;
    private final TabelaPrecos tabelaPrecos;

    public ShowInfoCliente(final Context context, final Cliente cliente, final TabelaPrecos tabelaPrecos) {
        this.cliente = cliente;
        this.context = context;
        this.activity = (Activity) context;
        this.tabelaPrecos = tabelaPrecos;
    }

    public void run() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

        // Get the layout inflater
        LayoutInflater inflater = this.activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.info_cliente, null);

        setInfoInView(view);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alertDialogBuilder.setView(view)
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setCancelable(false);



        alertDialogBuilder.show();

    }

    private void setInfoInView(final View view) {
        TextView nome = view.findViewById(R.id.nome);
        TextView morada = view.findViewById(R.id.morada);
        TextView nPorta = view.findViewById(R.id.porta);
        TextView alcunha = view.findViewById(R.id.alcunha);

        nome.setText(this.cliente.getName());
        morada.setText(this.cliente.getMorada());
        nPorta.setText(Integer.toString(this.cliente.getNPorta()));
        alcunha.setText(this.cliente.getAlcunha());

        TextView seg = view.findViewById(R.id.seg);
        TextView ter = view.findViewById(R.id.ter);
        TextView qua = view.findViewById(R.id.qua);
        TextView qui = view.findViewById(R.id.qui);
        TextView sex = view.findViewById(R.id.sex);
        TextView sab = view.findViewById(R.id.sab);
        TextView dom = view.findViewById(R.id.dom);

        seg.setText(this.cliente.getQuantidadesString(0, tabelaPrecos));
        ter.setText(this.cliente.getQuantidadesString(1, tabelaPrecos));
        qua.setText(this.cliente.getQuantidadesString(2, tabelaPrecos));
        qui.setText(this.cliente.getQuantidadesString(3, tabelaPrecos));
        sex.setText(this.cliente.getQuantidadesString(4, tabelaPrecos));
        sab.setText(this.cliente.getQuantidadesString(5, tabelaPrecos));
        dom.setText(this.cliente.getQuantidadesString(6, tabelaPrecos));
    }
}
