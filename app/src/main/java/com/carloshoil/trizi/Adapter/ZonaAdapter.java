package com.carloshoil.trizi.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.R;

import java.util.List;

public class ZonaAdapter extends RecyclerView.Adapter<ZonaAdapter.ViewHolder> {
    Context context;
    List<Zona> listZona;
    public ZonaAdapter(Context context, List<Zona> listZona)
    {
        this.context=context;
        this.listZona=listZona;

    }
    @NonNull
    @Override
    public ZonaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_zona,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZonaAdapter.ViewHolder holder, int position) {
        Zona zona= listZona.get(position);
        if(zona!=null)
        {
            holder.tvNumItem.setText((position+1)+"");
            holder.tvNombreZona.setText(zona.cNombre);
        }
    }

    @Override
    public int getItemCount() {
        return listZona.size();
    }

    public void actualizaDatos(List<Zona> lstData)
    {
        Log.d("DEBUG", "ACTUALIZAdATOS"+lstData.size());
        this.listZona=lstData;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        TextView tvNombreZona, tvNumItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreZona=itemView.findViewById(R.id.tvNombreZona);
            tvNumItem=itemView.findViewById(R.id.tvNumItem);
        }
    }
}
