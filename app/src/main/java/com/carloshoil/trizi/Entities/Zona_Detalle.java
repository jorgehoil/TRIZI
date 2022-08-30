package com.carloshoil.trizi.Entities;

import java.util.zip.ZipEntry;

public class Zona_Detalle {
    public  int iIdZona_Detalle;
    public int iIdZonaR;
    public int iOrden;
    public double dLatitud;
    public double dLongitud;

    public Zona_Detalle(int iIdZona_Detalle, int iIdZonaR, int iOrden, double dLatitud, double dLongitud)
    {
        this.iIdZona_Detalle=iIdZona_Detalle;
        this.iIdZonaR=iIdZonaR;
        this.iOrden=iOrden;
        this.dLatitud=dLatitud;
        this.dLongitud=dLongitud;
    }
    public Zona_Detalle()
    {

    }
}
