package com.carloshoil.trizi.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.carloshoil.trizi.DB.DataBaseHelper;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;

import java.util.ArrayList;
import java.util.List;

public class ZonaDAO {
    Context context;
    DataBaseHelper dataBaseHelper;
    SQLiteDatabase sqLiteDatabaseW;
    SQLiteDatabase sqLiteDatabaseR;
    public ZonaDAO(Context context)
    {
       this.context=context;
       dataBaseHelper = DataBaseHelper.getInstance(context);
       sqLiteDatabaseW = dataBaseHelper.getWritableDatabase();
       sqLiteDatabaseR = dataBaseHelper.getReadableDatabase();

    }
    public boolean GuardaZonasZonasDetalles(List<Zona> lstZona, List<Zona_Detalle> lstZonaDetalle)
    {
        Log.d("DEBUG", "zonas:"+lstZona.size()+"" +" zonadetalle:"+ lstZonaDetalle.size()+"");
        boolean lResultado=false;
        Object[] objectZona;
        Object[] objectZonaDetalle;
        String cQueryZona="INSERT INTO tblZonas (iIdZonaR, cNombre) VALUES(?,?)";
        String cQueryZonaDetalle="INSERT INTO tblZonaDetalle (iIdZonaR,iOrden, dLatitud, dLongitud) VALUES(?,?,?,?)";
        try {
            sqLiteDatabaseW.beginTransaction();
            sqLiteDatabaseW.execSQL("DELETE FROM tblZonas");
            sqLiteDatabaseW.execSQL("DELETE FROM tblZonaDetalle");
            Log.d("DEBUG", "GuardaZonasDetalles");
            for(Zona entZona: lstZona)
            {
                objectZona= new Object[]{entZona.iIdZonaR, entZona.cNombre};
                sqLiteDatabaseW.execSQL(cQueryZona,objectZona);
            }
            for(Zona_Detalle zona_detalle:lstZonaDetalle)
            {
                objectZonaDetalle= new Object[]{zona_detalle.iIdZonaR,zona_detalle.iOrden, zona_detalle.dLatitud, zona_detalle.dLongitud};
                sqLiteDatabaseW.execSQL(cQueryZonaDetalle, objectZonaDetalle);
            }
            sqLiteDatabaseW.setTransactionSuccessful();
            lResultado=true;
        }
        catch (Exception e)
        {
            lResultado=false;
            Log.e("Error", e.getMessage());
        }
        finally {
            sqLiteDatabaseW.endTransaction();
            return lResultado;
        }
    }
    public boolean Existe(int iIdZona)
    {
        boolean lResultado=false;
        Cursor cursor=sqLiteDatabaseR.rawQuery("SELECT*FROM tblZonas WHERE iIdzona=?",new String[]{iIdZona+""});
        while (cursor.moveToNext()){
            lResultado=true;
        }
        return lResultado;
    }
    public Zona Obtener(int iIdZona)
    {
        boolean lResultado=false;
        Zona entZona=null;
        Cursor cursor=sqLiteDatabaseR.rawQuery("SELECT*FROM tblZonas WHERE iIdZona=?",new String[]{iIdZona+""});
        while (cursor.moveToNext()){
            entZona= new Zona(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));
        }
        return entZona;

    }
    public List<Zona> ObtenerTodos()
    {
        List<Zona> lstZona= new ArrayList<>();
        Zona entZona=null;
        Cursor cursor= sqLiteDatabaseR.rawQuery("SELECT*FROM tblZonas", null);
        while (cursor.moveToNext())
        {
            entZona= new Zona(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2)
                    );
            lstZona.add(entZona);
        }
        return lstZona;
    }

}
