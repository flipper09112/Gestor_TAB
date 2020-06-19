package com.example.gestor_tab.activitys.MainActivity_Functions;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gestor_tab.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowCatalogo {

    public static int PAO = 1;
    public static int BOLOS = 2;

    private int[] imagespao = {
            R.drawable.pao_1,
            R.drawable.pao_2,
            R.drawable.pao_3,
            R.drawable.pao_6,
            R.drawable.pao_7,
            R.drawable.cacetes_1,
            R.drawable.cacetes_2,
            R.drawable.cacetes_3,
            R.drawable.cacetes_4,
            R.drawable.cacetes_5,
            R.drawable.cacetes_6,
            R.drawable.broa_1,
            R.drawable.broa_2,
            R.drawable.broa_3,
            R.drawable.mini_1,
            R.drawable.mini_2,
            R.drawable.pao_forma_1,
            R.drawable.pao_forma_2,
            R.drawable.pao_forma_3,
    };

    private int[] imagesBolos = {
            R.drawable.pastelaria_1,
            R.drawable.pastelaria_2,
            R.drawable.pastelaria_3,
            R.drawable.pastelaria_4,
            R.drawable.pastelaria_5,
            R.drawable.pastelaria_6,
            R.drawable.pastelaria_7,
            R.drawable.pastelaria_8,
            R.drawable.pastelaria_9,
            R.drawable.pastelaria_10,
            R.drawable.pastelaria_11,
            R.drawable.pastelaria_12,
            R.drawable.pastelaria_13,
            R.drawable.pastelaria_14,
            R.drawable.pastelaria_15,
            R.drawable.pastelaria_16,
            R.drawable.pastelaria_17
    };

    private final Context context;
    private final Activity activity;

    private int count = 0;
    private int tipoCatalogo;

    public ShowCatalogo(final Context context, int tipo) {
        this.context = context;
        this.activity = (Activity) context;

        this.tipoCatalogo = tipo;
    }

    public void run() {
        activity.setContentView(R.layout.catalogo_view);

        setButtonsConfig();
        setImage();
    }

    private void setButtonsConfig() {
        Button anterior = this.activity.findViewById(R.id.anterior);
        Button seguinte = this.activity.findViewById(R.id.seguinte);

        anterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCount();
                setImage();
            }
        });

        seguinte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCount();
                setImage();
            }
        });

    }

    private void subCount() {
        count--;

        if (count == -1) {
            count = 0;
        }
    }

    private void addCount() {
        count++;

        int max;
        if (this.tipoCatalogo == BOLOS) {
            max = imagesBolos.length;
        } else {
            max = imagespao.length;
        }

        if (count == max) {
            count = 0;
        }
    }

    private void setImage() {

        ImageView img= (ImageView) this.activity.findViewById(R.id.imagem_catalogo);

        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(img);
        pAttacher.update();

        if (this.tipoCatalogo == PAO) {
            img.setImageResource(this.imagespao[this.count]);
        } else {
            img.setImageResource(this.imagesBolos[this.count]);
        }
    }
}
