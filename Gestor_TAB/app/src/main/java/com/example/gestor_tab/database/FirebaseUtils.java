package com.example.gestor_tab.database;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseUtils {

    public static void saveDocsInCloud(Context context, StorageReference mStorageRef) {
        File clientes = new File(context.getExternalFilesDir(null), "MeuArquivoXLS.xls");
        File encomendas = new File(context.getExternalFilesDir(null), "EncomendasPanilima.txt");
        File logs = new File(context.getExternalFilesDir(null), "logs/logs.txt");
        File tabelaPrecos = new File(context.getExternalFilesDir(null), "TabelaPrecos.xls");
        File encomendasV2 = new File(context.getExternalFilesDir(null), "EncomendasV2.txt");

        Uri file = Uri.fromFile(clientes);
        Uri file1 = Uri.fromFile(encomendas);
        Uri file2 = Uri.fromFile(logs);
        Uri file3 = Uri.fromFile(tabelaPrecos);
        Uri file4 = Uri.fromFile(encomendasV2);

        StorageReference riversRef = mStorageRef.child("docs/MeuArquivoXLS.xls");
        StorageReference riversRef1 = mStorageRef.child("docs/EncomendasPanilima.txt");
        StorageReference riversRef2 = mStorageRef.child("docs/logs.txt");
        StorageReference riversRef3 = mStorageRef.child("docs/TabelaPrecos.xls");
        StorageReference riversRef4 = mStorageRef.child("docs/EncomendasV2.txt");

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                //Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                //Toast.makeText(MainActivity.this, "NOT OK", Toast.LENGTH_SHORT).show();
            }
        });
        riversRef1.putFile(file1);
        riversRef2.putFile(file2);
        riversRef3.putFile(file3);
        riversRef4.putFile(file4);

    }

    public static void saveDocsInCloudDevelop(Context context, StorageReference mStorageRef) {
        File clientes = new File(context.getExternalFilesDir(null), "MeuArquivoXLS.xls");
        File encomendas = new File(context.getExternalFilesDir(null), "EncomendasPanilima.txt");
        File logs = new File(context.getExternalFilesDir(null), "logs/logs.txt");
        File tabelaPrecos = new File(context.getExternalFilesDir(null), "TabelaPrecos.xls");
        File encomendasV2 = new File(context.getExternalFilesDir(null), "EncomendasV2.txt");

        Uri file = Uri.fromFile(clientes);
        Uri file1 = Uri.fromFile(encomendas);
        Uri file2 = Uri.fromFile(logs);
        Uri file3 = Uri.fromFile(tabelaPrecos);
        Uri file4 = Uri.fromFile(encomendasV2);

        StorageReference riversRef = mStorageRef.child("develop/MeuArquivoXLS.xls");
        StorageReference riversRef1 = mStorageRef.child("develop/EncomendasPanilima.txt");
        StorageReference riversRef2 = mStorageRef.child("develop/logs.txt");
        StorageReference riversRef3 = mStorageRef.child("develop/TabelaPrecos.xls");
        StorageReference riversRef4 = mStorageRef.child("develop/EncomendasV2.txt");

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                //Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                //Toast.makeText(MainActivity.this, "NOT OK", Toast.LENGTH_SHORT).show();
            }
        });
        riversRef1.putFile(file1);
        riversRef2.putFile(file2);
        riversRef3.putFile(file3);
        riversRef4.putFile(file4);

    }

    public static void saveDocInCloud(final Context context, StorageReference mStorageRef, File f) {
        Uri file = Uri.fromFile(f);

        StorageReference riversRef = mStorageRef.child("docsTest/" + f.getName());

        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Get a URL to the uploaded content
                Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                Toast.makeText(context, "NOT Uploaded", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static int restoreSavedDocsInCloud(final Context context, StorageReference mStorageRef, final Intent intent) {
        final int[] finish = {0};

        File clientes = new File(context.getExternalFilesDir(null), "MeuArquivoXLS.xls");
        File encomendas = new File(context.getExternalFilesDir(null), "EncomendasPanilima.txt");
        File encomendasV2 = new File(context.getExternalFilesDir(null), "EncomendaV2.txt");
        File logs = new File(context.getExternalFilesDir(null), "logs");
        if (!logs.exists()) {
            logs.mkdirs();
        }
        logs = new File(context.getExternalFilesDir(null), "logs/logs.txt");
        File tabelaPrecos = new File(context.getExternalFilesDir(null), "TabelaPrecos.xls");

        Uri file = Uri.fromFile(clientes);
        final Uri file1 = Uri.fromFile(encomendas);
        final Uri file4 = Uri.fromFile(encomendasV2);
        final Uri file2 = Uri.fromFile(logs);
        final Uri file3 = Uri.fromFile(tabelaPrecos);

        StorageReference riversRef = mStorageRef.child("docs/MeuArquivoXLS.xls");
        final StorageReference riversRef1 = mStorageRef.child("docs/EncomendasPanilima.txt");
        final StorageReference riversRef4 = mStorageRef.child("docs/EncomendasV2.txt");
        final StorageReference riversRef2 = mStorageRef.child("docs/logs.txt");
        final StorageReference riversRef3 = mStorageRef.child("docs/TabelaPrecos.xls");

        riversRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(context, "File 0 success", Toast.LENGTH_SHORT).show();
                riversRef1.getFile(file1).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(context, "File 1 success", Toast.LENGTH_SHORT).show();

                        riversRef2.getFile(file2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                //Toast.makeText(context, "File 2 success", Toast.LENGTH_SHORT).show();

                                riversRef3.getFile(file3).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        //Toast.makeText(context, "File 3 success", Toast.LENGTH_SHORT).show();
                                        riversRef4.getFile(file4).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                Toast.makeText(context, "Todos os ficheiros atualizados", Toast.LENGTH_SHORT).show();
                                                context.startActivity(intent);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Handle any errors
                                                Toast.makeText(context, "File 4 error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                        Toast.makeText(context, "File 3 error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle any errors
                                Toast.makeText(context, "File 2 error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Toast.makeText(context, "File 1 error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(context, "File 0 error", Toast.LENGTH_SHORT).show();
            }
        });



        return finish[0];
    }

}
