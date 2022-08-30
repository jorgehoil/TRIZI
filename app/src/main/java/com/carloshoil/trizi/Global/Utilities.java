package com.carloshoil.trizi.Global;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.carloshoil.trizi.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utilities {
    public static void GuardarPreferencias(String cClave, String cValor, Context context)
    {
        SharedPreferences sharedPreferences= context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(cClave, cValor);
        editor.commit();
    }
    public static String RecuperaPreferencia(String cClave, Context context)
    {
        String cRespuesta;
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        cRespuesta=sharedPreferences.getString(cClave, "");
        return cRespuesta;
    }
    public static int TryParse(String cCantidad)
    {
        cCantidad=cCantidad==null?"0":cCantidad;
        int iRetorno=0;
        try{
            iRetorno=Integer.parseInt(cCantidad);
        }
        catch (Exception ex)
        {
            return 0;
        }
        return iRetorno;
    }
    public static String formatDate(Date _date)
    {
        String cResultado;
        try{
            SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            cResultado=simpleDateFormat.format(_date);
            return cResultado;

        }catch (Exception ex)
        {
            return "00-0000-0000 00:00:00";
        }
    }
    public static Date obtenerFechaActual()
    {
        return Calendar.getInstance().getTime();
    }
    public static String miliSecondsToDate(long milliSeconds)
    {
        SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((int) milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
    public static String obtenerFechaActualCadena()
    {
        String cRetorno="";
        Date dtFechaActual;
        dtFechaActual=obtenerFechaActual();
        cRetorno=formatDate(dtFechaActual);
        return cRetorno;
    }
    public static String obtenerFechaMasDias(int iDias)
    {
        String cRetorno="";
        Calendar calendarf= Calendar.getInstance();
        calendarf.add(Calendar.DATE,iDias);
        cRetorno=formatDate(calendarf.getTime());
        return cRetorno;
    }
    public static long obtenerSegundos(String cDateAntigua, String cDateActual)
    {
        Log.d("DEBUG", "FECHA ACTUAL:"+cDateActual+" FECHA ANTIGUA:"+cDateAntigua);
        long lSegundosRetorno=0, lDifference=0;
        Date dtFechaInicio;
        Date dtFechaFinal;
        try{
            SimpleDateFormat cDateFormat=new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
            dtFechaInicio= cDateFormat.parse(cDateAntigua);
            dtFechaFinal=cDateFormat.parse(cDateActual);
            lDifference=dtFechaFinal.getTime()-dtFechaInicio.getTime();
            Log.d("DEBUG", "Tiempo"+lDifference);
            if(lDifference>0)
            {
                Log.d("DEBUG", "Tiempo:"+lDifference);
                lSegundosRetorno=lDifference/1000;
            }
            return lSegundosRetorno;
        }
        catch (Exception e)
        {
            Log.e("DEBUG", e.getMessage());
            return 0;
        }

    }
    public static boolean VerificaLocalizacion(Context context)
    {
        LocationManager locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
    public static boolean VerificaInternet(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public static void MostrarMensaje(Context context, String cTitulo, String cMensaje)
    {
        AlertDialog.Builder alert= new AlertDialog.Builder(context);
        alert.setTitle(cTitulo);
        alert.setMessage(cMensaje);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
