package com.carloshoil.trizi.Dialogos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.trizi.DB.FirebaseInstance;
import com.carloshoil.trizi.DTO.ConfigDTO;
import com.carloshoil.trizi.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

public class DialogoCalificacion extends DialogFragment {
    Context context;
    String cTaxista;
    private Button btnCalificar;
    private RatingBar ratingBar;
    private TextView tvPersona;
    private ProgressBar pbCargaCalif;
    private Activity MainActivity;
    AlertDialog.Builder alert;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceUser;
    public DialogoCalificacion(Context context, String cTaxista)
    {
        this.context=context;
        this.cTaxista=cTaxista;
        firebaseDatabase=FirebaseInstance.getInstance();
        databaseReferenceUser=firebaseDatabase.getReference().child("users").child(cTaxista).child("configDTO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof Activity)
        {
            this.MainActivity=(Activity)context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return CreaDialogo();
    }

    private Dialog CreaDialogo()
    {
        alert= new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= MainActivity.getLayoutInflater();
        View view =layoutInflater.inflate(R.layout.dialogo_calificacion,null);
        alert.setView(view);
        btnCalificar=view.findViewById(R.id.btnCalificar);
        ratingBar=view.findViewById(R.id.ratingBar);
        tvPersona=view.findViewById(R.id.tvPersonaCalif);
        pbCargaCalif=view.findViewById(R.id.pbCargaCalif);
        btnCalificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calificar();
            }
        });
        return alert.create();

    }

    private void Calificar() {

        if(cTaxista.isEmpty())
        {
            Toast.makeText(context, "Error al calificar", Toast.LENGTH_SHORT).show();
        }
        else
        {
            pbCargaCalif.setVisibility(View.VISIBLE);
            ratingBar.setIsIndicator(true);
            if(ratingBar.getRating()>=1.0)
            {
                btnCalificar.setEnabled(false);
                databaseReferenceUser.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        ConfigDTO configDTO=currentData.getValue(ConfigDTO.class);
                        if(configDTO!=null)
                        {
                            Float fTotalEstrellas = configDTO.fTotalEstrellas;
                            Float fCalificadoresTotales = configDTO.fTotalCalifcadores;
                            Float fCalificacion = ratingBar.getRating();
                            fTotalEstrellas = fTotalEstrellas + (fCalificacion.intValue());
                            fCalificadoresTotales = fCalificadoresTotales + 1;
                            Float fCalificacionFinal = (fTotalEstrellas / fCalificadoresTotales);
                            configDTO.fTotalEstrellas=fTotalEstrellas;
                            configDTO.fTotalCalifcadores=fCalificadoresTotales;
                            configDTO.fCalif=fCalificacionFinal;
                            currentData.setValue(configDTO);

                        }
                        return Transaction.success(currentData);

                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        if(error==null)
                        {
                            if(committed)
                            {
                                dismiss();
                                Toast.makeText(context, "¡Gracias por calificar :)!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            ratingBar.setIsIndicator(false);
                            pbCargaCalif.setVisibility(View.INVISIBLE);
                            btnCalificar.setEnabled(true);
                            Log.e("DEBUG",error.getMessage());
                            Toast.makeText(context, "Se presentó un error al calificar, intenta de nuevo" +error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(context, "La calificacion debe ser mayor a 1 estrella", Toast.LENGTH_SHORT).show();
            }


        }
    }
}
