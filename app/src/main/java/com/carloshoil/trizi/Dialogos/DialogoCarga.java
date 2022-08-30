package com.carloshoil.trizi.Dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.trizi.R;

public class DialogoCarga extends DialogFragment {
    Context context;

    public DialogoCarga(Context context)
    {
        this.context=context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return creaDialogo();
    }

    private Dialog creaDialogo()
    {
        AlertDialog.Builder alert= new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= getActivity().getLayoutInflater();
        View view =layoutInflater.inflate(R.layout.layout_dialgocarga,null);
        alert.setView(view);
        return alert.create();
    }
}
