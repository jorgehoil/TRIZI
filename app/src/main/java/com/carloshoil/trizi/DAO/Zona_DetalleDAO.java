package com.carloshoil.trizi.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.util.Log;

import com.carloshoil.trizi.DB.DataBaseHelper;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Zona_DetalleDAO {
    Context context;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabaseW;
    SQLiteDatabase sqLiteDatabaseR;
    public Zona_DetalleDAO(Context context)
    {
        dataBaseHelper= DataBaseHelper.getInstance(context);
       sqLiteDatabaseW = dataBaseHelper.getWritableDatabase();
        sqLiteDatabaseR = dataBaseHelper.getWritableDatabase();
        this.context=context;
    }
    public boolean Guardar(List<Zona_Detalle> lstZonaDetalle)
    {
        boolean lResultado=false;

        Object[] data;
        try{
            sqLiteDatabaseW.beginTransaction();
            sqLiteDatabaseW.execSQL("DELETE FROM tblZonaDetalle");
            String cQuery="INSERT INTO tblZonaDetalle VALUES(?,?,?,?)";
            for(Zona_Detalle entZonaDetalle:lstZonaDetalle)
            {
                data= new Object[]{entZonaDetalle.iIdZonaR,
                        entZonaDetalle.iOrden,
                        entZonaDetalle.dLatitud,
                        entZonaDetalle.dLongitud};
                sqLiteDatabaseW.execSQL(cQuery, data);
            }
            sqLiteDatabaseW.setTransactionSuccessful();
            lResultado=true;

        }catch(Exception e)
        {
            Log.e("Error", e.getMessage());
            lResultado=false;
        }
        finally {
            sqLiteDatabaseW.endTransaction();
            return  lResultado;
        }

    }
    public List<Zona_Detalle> Obtener(int iIdZonaR)
    {
        List<Zona_Detalle> lstZonaDetalle= new ArrayList<>();
        Zona_Detalle entZonaDetalle=null;
        Cursor cursor= sqLiteDatabaseR.rawQuery("SELECT*FROM tblZonaDetalle WHERE iIdZonaR=?",new String[]{iIdZonaR+""});
        while (cursor.moveToNext())
        {
            entZonaDetalle= new Zona_Detalle(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4));
            lstZonaDetalle.add(entZonaDetalle);

        }
        return lstZonaDetalle;
    }
    public List<com.mapbox.geojson.Point> ObtenerListaPoint(int iIdZonaR)
    {
        Log.d("DEBUG", iIdZonaR+"");
        List<com.mapbox.geojson.Point> lstZonaPoint= new ArrayList<>();
        com.mapbox.geojson.Point point=null;
        Cursor cursor= sqLiteDatabaseR.rawQuery("SELECT dLatitud, dLongitud FROM tblZonaDetalle WHERE iIdZonaR=? ORDER BY iOrden ASC",new String[]{iIdZonaR+""});
        Log.d("DEBUG", "ObtenerListaPoint"+ cursor.getCount());
        while (cursor.moveToNext())
        {
           point=  com.mapbox.geojson.Point.fromLngLat(
                   cursor.getDouble(1),
                   cursor.getDouble(0));
           lstZonaPoint.add(point);

        }
        return lstZonaPoint;
    }



}
