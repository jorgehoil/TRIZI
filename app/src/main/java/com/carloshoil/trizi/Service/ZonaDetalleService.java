package com.carloshoil.trizi.Service;

import android.content.Context;

import com.carloshoil.trizi.DAO.ZonaDAO;
import com.carloshoil.trizi.DAO.Zona_DetalleDAO;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.List;

public class ZonaDetalleService {
    Context context;

    public ZonaDetalleService(Context context) {
        this.context = context;
    }

    public List<Zona_Detalle> ObtenerZonasDetalles(int iIdZona)
    {
        Zona_DetalleDAO zona_detalle= new Zona_DetalleDAO(context);
        return zona_detalle.Obtener(iIdZona);
    }

    public List<com.mapbox.geojson.Point> ObtenerListaPoint(int iIdZonaR)
    {
        Zona_DetalleDAO zona_detalle= new Zona_DetalleDAO(context);
        return zona_detalle.ObtenerListaPoint(iIdZonaR);
    }


}
