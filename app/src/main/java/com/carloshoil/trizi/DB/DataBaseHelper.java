package com.carloshoil.trizi.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {
    Context context;
    final static String cName="Database.sql";
    final static int iVersion=1;
    static DataBaseHelper dataBaseHelper;
    public static synchronized DataBaseHelper getInstance(Context context)
    {
        if(dataBaseHelper==null)
        {
            dataBaseHelper= new DataBaseHelper(context);
        }
        return dataBaseHelper;
    }
    public DataBaseHelper(@Nullable Context context) {
        super(context,cName ,null, iVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tblZonas (iIdZona INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, iIdZonaR INTEGER NOT NULL, cNombre TEXT)");
        db.execSQL("CREATE TABLE tblZonaDetalle(iIdZonaDetalle INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " iIdZonaR INTEGER," +
                " iOrden INTEGER, " +
                "dLatitud DOUBLE, " +
                "dLongitud DOUBLE)");
        db.execSQL("INSERT INTO tblZonas VALUES (1,1,'CHUMAYEL' )");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (1, 1,1,20.44109,-89.30194)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (2, 1,2,20.43997,-89.29628)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (3, 1,3,20.43084,-89.29559)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (4, 1,4,20.42281,-89.29327)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (5, 1,5,20.42017,-89.30087)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (6, 1,6,20.42348,-89.30349)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (7, 1,7,20.42678,-89.30808)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (8, 1,8,20.43136,-89.31061)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (9, 1,9,20.43462,-89.30349)");
        db.execSQL("INSERT INTO tblZonaDetalle VALUES (10, 1,10,20.44109,-89.30194)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
