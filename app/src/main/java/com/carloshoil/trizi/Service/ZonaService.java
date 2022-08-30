package com.carloshoil.trizi.Service;

import android.content.Context;

import com.carloshoil.trizi.DAO.ZonaDAO;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;

import java.util.List;

public class ZonaService {
    Context context;
    public ZonaService(Context context)
    {
        this.context=context;
    }
    public boolean GuardarZonaZonaDetalles(List<Zona> lstZona, List<Zona_Detalle> lstZonaDetalle) {
        ZonaDAO zonaDAO = new ZonaDAO(context);
        return zonaDAO.GuardaZonasZonasDetalles(lstZona, lstZonaDetalle);
    }
    public List<Zona> ObtenerZonas()
    {
        ZonaDAO zonaDAO= new ZonaDAO(context);
        return zonaDAO.ObtenerTodos();
    }

}
