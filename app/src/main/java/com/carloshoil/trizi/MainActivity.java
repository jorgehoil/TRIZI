package com.carloshoil.trizi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.trizi.DAO.ZonaDAO;
import com.carloshoil.trizi.DTO.Location;
import com.carloshoil.trizi.Dialogos.DialogoCalificacion;
import com.carloshoil.trizi.Dialogos.DialogoCarga;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;
import com.carloshoil.trizi.Global.Utilities;
import com.carloshoil.trizi.Global.Values;
import com.carloshoil.trizi.DB.FirebaseInstance;
import com.carloshoil.trizi.Service.ZonaDetalleService;
import com.carloshoil.trizi.Service.ZonaService;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.persistence.PersistenceManager;
import com.google.maps.android.PolyUtil;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.MultiPolygon;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.turf.TurfJoins;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {
    private FirebaseDatabase mDatabase;
    private MapView mapView;
    private DatabaseReference reftaxisDisponibles, dbReferenceCustomer, geoFireTaxisWorkin, refTaxista;
    private GeoFire geoFireTaxisDisponibles;
    private FirebaseAuth firebaseAuth;
    private MapboxMap gMapboxMap;
    private PermissionsManager permissionsManager;
    private Button btnSolicitar, btnOpciones, btnCerrarSesion;
    private boolean lTaxiEncontrado=false, lServicio=false, lMapaCargado=false, lCargaPrimera=true;
    private double dRadio=0.2;
    private String cKeyTaxi;
    private final String LSERVICIO="lServicioTaxi", CTAXI="cTaxi", LLEGADO="lLlegado";
    private ValueEventListener listenerEstatus, listenerTaxiWorking;
    private DialogoCarga dialogoCarga;
    private int PERMISSION_REQUEST_LOCATION=4;
    private SymbolManager symbolManager;
    private Symbol syTaxi, syUbicacion;
    private CountDownTimer timer;
    private LinearLayout linearLayoutTaxista;
    private ProgressBar pbCargaTaxista;
    private LocationComponent locationComponent;
    private TextView tvTaxista;
    private GeoQuery geoQuery;
    private LocationStatusReceiver locationStatusReceiver;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private android.location.Location gLocation;
    private int iIntentos=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("DEBUG", "onCreate");
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        mapView= findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        pbCargaTaxista=findViewById(R.id.pbCargaTaxista);
        btnSolicitar= findViewById(R.id.btnSolicitar);
        btnOpciones=findViewById(R.id.btnOpciones);
        btnCerrarSesion=findViewById(R.id.btnCerrarSesion);
        tvTaxista=findViewById(R.id.tvTaxista);
        btnSolicitar.setOnClickListener(this);
        btnOpciones.setOnClickListener(this);
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });
        PreparaListerners();
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerGlobales();
        InitObj();

    }
    private void ObtenerGlobales()
    {
        Log.d("DEBUG", "ObtenerGlobales");
        lServicio=Utilities.RecuperaPreferencia(LSERVICIO,this).equals("1");
        cKeyTaxi=Utilities.RecuperaPreferencia(CTAXI,this);
    }
    private void InitObj()
    {
        mDatabase=FirebaseInstance.getInstance();
        Log.d("DEBUG", "InitObj");
        geoFireTaxisDisponibles=new GeoFire(reftaxisDisponibles);
        dbReferenceCustomer=mDatabase.getReference("driverscustomers");
        dbReferenceCustomer.keepSynced(true);
        firebaseAuth=FirebaseAuth.getInstance();
        geoFireTaxisWorkin= mDatabase.getReference("driversworking");
        geoFireTaxisWorkin.keepSynced(true);
        linearLayoutTaxista=findViewById(R.id.linearLayoutTaxista);
        reftaxisDisponibles= mDatabase.getReference("availabletaxis");
        refTaxista=mDatabase.getReference("users");
        geoFireTaxisDisponibles=new GeoFire(reftaxisDisponibles);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        locationStatusReceiver= new LocationStatusReceiver();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
               if(locationResult==null)
               {
                   return;
               }
                for(android.location.Location location: locationResult.getLocations())
                {
                    if(location!=null)
                    {
                        Log.d("DEBUG", "location null");
                        gLocation=location;
                        if(gLocation==null)
                        {
                           Log.d("DEBUG", "ABC");
                        }
                        UbicaSimboloUbicacionActual(location);
                        AjustaCamara(new LatLng(gLocation.getLatitude(), gLocation.getLongitude()));
                    }

                }
            }
        };
        PermisoLocalizacion();

    }

    private void PermisoLocalizacion()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            }
        }
    }
    private void UbicaSimboloUbicacionActual(android.location.Location location) {
        if(location!=null&&lMapaCargado)
        {
            if(syUbicacion==null)
            {
                syUbicacion = symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(location.getLatitude(), location.getLongitude()))
                        .withIconImage("ubicacion")
                        .withIconSize(0.5f));
            }
            else
            {
                syUbicacion.setLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                symbolManager.update(syUbicacion);
            }
        }


    }

    @Override
    protected void onStart() {
        Log.d("DEBUG", "onStart");
        super.onStart();
        mapView.onStart();
        Init();
    }

    private void Init() {
        Log.d("DEBUG", "Init");
        if(lServicio)
        {
            MuestraTaxista(true);
            IniciaEscuchaEstatus();
            IniciaSeguimientoTaxista();
        }
    }
    private void Stop()
    {
        Log.d("DEBUG", "Stop");
        if(lServicio)
        {
            MuestraTaxista(false);
            TerminaSeguimientoTaxista();
            DetenerEscuchaEstatus();
        }
    }

    @Override
    protected void onStop() {
        Log.d("DEBUG", "onStop");
        super.onStop();
        mapView.onStop();

    }

    @Override
    protected void onPause() {
        Log.d("DEBUG", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationStatusReceiver);
        Stop();
        DetenerSolicitudUbicaciones();
        super.onPause();
        mapView.onPause();

    }

    @Override
    protected void onResume() {
        Log.d("DEBUG", "onResume");
        super.onResume();
        mapView.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(locationStatusReceiver,
                new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));
        IniciaSolicitudUbicaciones();

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        Stop();
        DetenerSolicitudUbicaciones();
        super.onDestroy();
        mapView.onDestroy();

    }


    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        Log.d("DEBUG", "onMapReady");
        mapboxMap.setStyle(getStyleMap(), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                gMapboxMap=mapboxMap;
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                symbolManager.setTextAllowOverlap(true);
                style.addImage("ubicacion_trizi", BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_icono_ubicacion_trizi)));
                style.addImage("ubicacion", BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_icono_ubicacion)));
                lMapaCargado=true;
                ObtenerUltimaUbicacion();
            }
        });
    }

    private void ObtenerUltimaUbicacion() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(fusedLocationProviderClient!=null&&fusedLocationProviderClient!=null)
            {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
                    @Override
                    public void onSuccess(android.location.Location location) {
                        if(location!=null)
                        {
                            gLocation=location;
                            AjustaCamara(new LatLng(location.getLatitude(), location.getLongitude()));
                            UbicaSimboloUbicacionActual(location);
                        }

                    }
                });
            }

        }


    }


    private void moverCamara(LatLng location)
    {
        Log.d("DEBUG", "moverCamara");
        CameraPosition position= new CameraPosition.Builder()
                .target(location)
                .zoom(14.5)
                .tilt(10)
                .build();
        gMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position),2000);
    }

    public void ValidaSolicitar()
    {
        if(Utilities.VerificaLocalizacion(MainActivity.this)){
            if(Utilities.VerificaInternet(MainActivity.this))
            {
                ConfirmaSolicitar();
            }
            else
            {
                Utilities.MostrarMensaje(MainActivity.this, "Sin conexión a internet", "Por favor, asegúrate de estar conectado a internet");
            }

        }
        else
        {
            Utilities.MostrarMensaje(MainActivity.this, "Activar ubicación", "Por favor, asegúrate de activa la ubicación para solicitar");
        }
    }
    @Override
    public void onClick(View v) {
        int c= v.getId();
        Log.d("TEST", c+"");
        switch (c)
        {
            case R.id.btnSolicitar:
               ValidaSolicitar();
                break;
            case R.id.btnOpciones:
                ActivaDesactivaBotones(true);
                MostrarOpciones();
                break;

        }
    }

    private void ConfirmaSolicitar() {
        Log.d("DEBUG", "ConfirmaSolicitar");
        android.app.AlertDialog.Builder alertConf= new AlertDialog.Builder(this);
        alertConf.setTitle("Confirmación");
        alertConf.setMessage("¿Solicitar taxi?");
        alertConf.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Solicitar();

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertConf.show();
    }

    private void ActivaDesactivaBotones(boolean lActivar)
    {
        Log.d("DEBUG", "ActivaDesactivaBotones: " +lActivar);
        if(lActivar)
        {
            btnSolicitar.setEnabled(true);
            btnSolicitar.setText("Solicitar");
            btnOpciones.setEnabled(true);
            btnSolicitar.setTextColor(Color.argb(255,88,40,65));
        }
        else
        {
            btnSolicitar.setEnabled(false);
            btnSolicitar.setText("Buscando...");
            btnOpciones.setEnabled(false);
            btnSolicitar.setTextColor(Color.argb(127,88,40,65));
        }


    }
    private void BuscarTaxi(final LatLng ubicacionActual)
    {
        Log.d("DEBUG", "BuscarTaxi "+dRadio);

        geoQuery= geoFireTaxisDisponibles.queryAtLocation(new GeoLocation(ubicacionActual.getLatitude(), ubicacionActual.getLongitude()),dRadio);
        geoQuery.removeAllListeners();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!lTaxiEncontrado)
                {
                    Log.d("DEBUG", "Taxi encontrado: "+key);
                    cKeyTaxi=key;
                    lTaxiEncontrado=true;
                }
            }

            @Override
            public void onKeyExited(String key) {

            }
            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                //No action
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("DEBUG", "onGeoQueryReady:  " +lTaxiEncontrado + "  cKey:  "+cKeyTaxi);
                if(!lTaxiEncontrado) // Si no se encontro el taxi
                {
                    dRadio=dRadio+0.2;
                    if(dRadio<= Values.MAXIMABUSQUEDA)
                    {
                        geoQuery.removeAllListeners();
                        BuscarTaxi(ubicacionActual);
                    }
                    else
                    {
                        geoQuery.removeAllListeners();
                        ActivaDesactivaBotones(true);
                        Utilities.MostrarMensaje(MainActivity.this, "Información", "No se encontró ningún taxi, intenta de nuevo");
                    }
                }
                else
                {
                    geoQuery.removeAllListeners();
                    btnSolicitar.setText("Espere...");
                    lTaxiEncontrado=false;
                    dRadio=0.2;
                    SolicitaSerivicio();
                }
            }
            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error al buscar", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void SolicitaSerivicio() {
        Log.d("DEBUG", "SolicitaServicio");
        if(gLocation!=null)
        {
            dbReferenceCustomer.child(cKeyTaxi).runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    String cDato=currentData.child("customerID").getValue(String.class);
                    if(cDato!=null)
                    {
                        if(cDato.isEmpty())
                        {
                            currentData.child("customerID").setValue(firebaseAuth.getUid());
                            currentData.child("iEstatus").setValue(Values.SOLICITADO);
                            currentData.child("location").setValue(new Location(gLocation.getLatitude(), gLocation.getLongitude()));
                            return Transaction.success(currentData);
                        }
                        else
                        {
                            return Transaction.abort();
                        }
                    }
                    else
                    {
                        return Transaction.success(currentData);
                    }
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    if(error==null)
                    {
                        if(committed) {
                            iIntentos=0;
                            IniciaTimerEspera();
                            IniciaEscuchaEstatus();
                        }
                        else {
                            if(iIntentos<3)
                            {
                                iIntentos++;
                                BuscarTaxi(new LatLng(gLocation.getLatitude(), gLocation.getLongitude()));
                            }
                            else
                            {
                                iIntentos=0;
                                Utilities.MostrarMensaje(MainActivity.this, "Taxis no disponibles", "Lo sentimos, ningún taxi está disponible");
                                cKeyTaxi="";
                                ActivaDesactivaBotones(true);
                                Utilities.GuardarPreferencias(CTAXI, cKeyTaxi,MainActivity.this);
                            }

                        }

                    }
                    else {
                        Utilities.MostrarMensaje(MainActivity.this, "Error", "Ha ocurrido un error, intenta de nuevo");
                        cKeyTaxi="";
                        ActivaDesactivaBotones(true);
                        Utilities.GuardarPreferencias(CTAXI, cKeyTaxi,MainActivity.this);
                    }

                }
            });
        }
        else
        {
            Utilities.MostrarMensaje(this, "No existe ubicacion", "Por favor, asegúrate de tener activo la localizacion al solicitar servico");
        }

    }
    private void RestauraEstatus()
    {

        Log.d("DEBUG", "RestauraEstatus"+ cKeyTaxi);
        dbReferenceCustomer.child(cKeyTaxi).child("iEstatus").setValue(Values.SIN_ESTATUS).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful())
                {
                    RestauraEstatus();
                }
            }
        });
    }
    private void PreparaListerners()
    {
        Log.d("DEBUG", "PreparaListeners");

        if(listenerEstatus==null)
        {
            listenerEstatus= new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        String cIdUsuario = snapshot.child("customerID").getValue().toString();
                        int iTipo= Utilities.TryParse(snapshot.child("iEstatus").getValue().toString());
                        Log.d("DEBUG","cIdUsuario "+cIdUsuario+" iTipo"+iTipo);
                        if(cIdUsuario.equals(firebaseAuth.getUid()))
                        {
                            ProcesaRespuesta(iTipo);
                        }
                        else
                        {
                            if(lServicio&&!cKeyTaxi.isEmpty())
                                ReiniciaDatos();
                        }
                    }
                    else
                    {
                        Utilities.MostrarMensaje(MainActivity.this,"Error", "Error al procesar solicitud");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("DEBUG", "ERROR"+error.getMessage());
                }
            };
        }
        if(listenerTaxiWorking==null)
        {
            listenerTaxiWorking= new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Double dLatitud=Double.parseDouble(snapshot.child("0").getValue().toString());
                        Double dLongitud=Double.parseDouble(snapshot.child("1").getValue().toString());
                        LatLng location= new LatLng(dLatitud,dLongitud);
                        UbicaTaxi(location);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }

    }

    private void ReiniciaDatos() {
        retiraSimboloTaxi();
        Utilities.MostrarMensaje(this, "Información", "El taxista ha finalizado o cancelado el servicio");
        MuestraTaxista(false);
        lServicio=false;
        Utilities.GuardarPreferencias(LSERVICIO,"0",this);
        DetenerEscuchaEstatus();
        ActivaDesactivaBotones(true);
        cKeyTaxi="";
        Utilities.GuardarPreferencias(CTAXI,cKeyTaxi,this);
    }

    private void IniciaEscuchaEstatus()
    {
        Log.d("DEBUG", "IniciaEscucha:"+ cKeyTaxi);
        dbReferenceCustomer.child(cKeyTaxi).addValueEventListener(listenerEstatus);
    }

    private void DetenerEscuchaEstatus()
    {
        Log.d("DEBUG", "DetenerEscucha:"+ cKeyTaxi);
        dbReferenceCustomer.child(cKeyTaxi).removeEventListener(listenerEstatus);
    }

    private void ProcesaRespuesta(int iTipo)
    {
        Log.d("DEBUG", "Procesa respuesta:" +iTipo);
        switch (iTipo)
        {
            case Values.ACEPTADO:
                IniciaServicio();
                break;
            case Values.CANCELADO:
                ServicioCancelado();
                break;
            case Values.FINALIZADO:
                ServicioFinalizado();
                break;
            case Values.RECHAZADO:
                ServicioRechazado();
                break;
            case Values.TIME_OUT:
                break;
            case Values.LLEGADO:
                MarcaLlegado();
        }

    }

    private void MarcaLlegado() {
        TerminaSeguimientoTaxista();
        retiraSimboloTaxi();
        RestauraEstatus();
    }

    private void ServicioFinalizado() {
        Log.d("DEBUG", "Servicio Finalizado");
        MuestraTaxista(false);
        Utilities.MostrarMensaje(MainActivity.this, "Servicio Finalizado", "El servicio fue finalizado");
        DetenerEscuchaEstatus();
        btnSolicitar.setVisibility(View.VISIBLE);
        Utilities.GuardarPreferencias(LSERVICIO, "0",this);
        ActivaDesactivaBotones(true);
        slideDown(linearLayoutTaxista);
        MuestraDialogoCalificacion(cKeyTaxi);
        cKeyTaxi="";
        Utilities.GuardarPreferencias(CTAXI,cKeyTaxi,this);
        lServicio=false;
    }

    private void MuestraDialogoCalificacion(String cKeyTaxi) {
       DialogoCalificacion dialogoCalificacion= new DialogoCalificacion(this,cKeyTaxi);
       dialogoCalificacion.setCancelable(false);
       dialogoCalificacion.show(getSupportFragmentManager(), "DialogoCalfificacion");

    }

    private void ServicioCancelado() {
        Log.d("DEBUG", "ServicioCancelado");
        DetenerEscuchaEstatus();
        MuestraTaxista(false);
        Utilities.MostrarMensaje(MainActivity.this, "Servicio cancelado", "Lo sentimos, el taxista ha cancelado el servicio, intenta nuevamente");
        TerminaSeguimientoTaxista();
        cKeyTaxi="";
        btnSolicitar.setVisibility(View.VISIBLE);
        Utilities.GuardarPreferencias(LSERVICIO, "0",this);
        ActivaDesactivaBotones(true);
        retiraSimboloTaxi();
        Utilities.GuardarPreferencias(CTAXI,cKeyTaxi,this);
        lServicio=false;
    }
    private void IniciaServicio() {
        Log.d("DEBUG", "IniciaServicio");
        MuestraTaxista(true);
        timer.cancel();
        IniciaSeguimientoTaxista();
        lServicio=true;
        Utilities.GuardarPreferencias(LSERVICIO, "1",this);
        Utilities.GuardarPreferencias(CTAXI,cKeyTaxi,this);
        RestauraEstatus();
    }
    private void IniciaSeguimientoTaxista()
    {
        Log.d("DEBUG", "IniciaSeguimientoTaxista");
        geoFireTaxisWorkin.child(cKeyTaxi).child("l").addValueEventListener(listenerTaxiWorking);
    }
    private void TerminaSeguimientoTaxista()
    {
        geoFireTaxisWorkin.child(cKeyTaxi).child("l").removeEventListener(listenerTaxiWorking);
    }
    public void MuestraTaxista(boolean lMostrar)
    {
        Log.d("DEBUG", "MuestraTaxista:"+ lMostrar);
        if(lMostrar)
        {
            btnSolicitar.setVisibility(View.GONE);
            slideUp(linearLayoutTaxista);
            CargaDatosTaxista();

        }
        else
        {
            btnSolicitar.setVisibility(View.VISIBLE);
            slideDown(linearLayoutTaxista);

        }

    }

    private void CargaDatosTaxista() {
        Log.d("DEBUG", "CargaDatosTaxista");
        refTaxista.child(cKeyTaxi).child("cNombreCompleto").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                pbCargaTaxista.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                        tvTaxista.setText(task.getResult().getValue().toString());
                }

            }
        });

    }

    private void UbicaTaxi(LatLng location) {
        Log.d("DEBUG", "UbicaTaxi: "+location.getLatitude()+" "+ location.getLongitude());
        if(location!=null&&lMapaCargado)
        {
            if(syTaxi==null)
            {
                syTaxi = symbolManager.create(new SymbolOptions()
                        .withLatLng(location)
                        .withIconImage("ubicacion_trizi")
                        .withIconSize(0.5f));
            }
            else
            {
                syTaxi.setLatLng(location);
                symbolManager.update(syTaxi);
            }
        }
    }

    public void retiraSimboloTaxi()
    {
        Log.d("DEBUG", "retiraSimboloTaxi");
        if(syTaxi!=null)
        {
            symbolManager.delete(syTaxi);
            syTaxi=null;
        }

    }

    private void ServicioRechazado()
    {
        Log.d("DEBUG", "ServicioRechazado");
        DetenerEscuchaEstatus();
        MuestraTaxista(false);
        Utilities.MostrarMensaje(MainActivity.this, "Servicio no aceptado", "Lo sentimos, el taxista no acepto el servicio, intenta nuevamente");
        cKeyTaxi="";
        timer.cancel();
        btnSolicitar.setVisibility(View.VISIBLE);
        ActivaDesactivaBotones(true);
        retiraSimboloTaxi();
        Utilities.GuardarPreferencias(CTAXI, cKeyTaxi,MainActivity.this);
    }
    private void IniciaTimerEspera() {
        Log.d("DEBUG", "IniciaTimerEspera");
        timer = new CountDownTimer(15000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                ProcesaSinRespuesta();
            }
        };
        timer.start();
    }

    private void Solicitar() {
        Log.d("DEBUG", "Solicitar");
        if(gLocation!=null)
        {
            Handler handler= new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if((Boolean)msg.obj)
                    {
                        ActivaDesactivaBotones(false);
                        BuscarTaxi(new LatLng(gLocation.getLatitude(), gLocation.getLongitude()));
                    }
                    else
                    {
                        Utilities.MostrarMensaje(MainActivity.this, "Zona no disponible", "El servicio no está disponible en tu zona actualmente");
                    }
                }
            };

            new Thread(() -> {
                boolean lExiste=false;
                Message message= new Message();
                ZonaService zonaService= new ZonaService(MainActivity.this);
                ZonaDetalleService zonaDetalleService= new ZonaDetalleService(MainActivity.this);
                List<Zona> lstZona=zonaService.ObtenerZonas();
                List<List<Point>> poligono= new ArrayList<>();
                List<List<List<Point>>> multipoligono=new ArrayList<>();
                for(Zona entZona:lstZona)
                {
                    poligono= new ArrayList<>();
                    Log.d("DEBUG",""+entZona.cNombre+entZona.iIdZona+"" +entZona.iIdZonaR);
                    List<Point> lstPuntos=zonaDetalleService.ObtenerListaPoint(entZona.iIdZonaR);
                    Log.d("DEBUG","tamano"+lstPuntos.size());
                    for(Point point: lstPuntos)
                    {
                        Log.d("DEBUG",""+point.latitude()+ point.longitude());
                    }
                    poligono.add(lstPuntos);
                    multipoligono.add(poligono);
                }

                lExiste=TurfJoins.inside(Point.fromLngLat(  gLocation.getLongitude(), gLocation.getLatitude()), MultiPolygon.fromLngLats(multipoligono));
                message.obj=lExiste;
                handler.sendMessage(message);

            }).start();

        }
        else
        {
            Utilities.MostrarMensaje(MainActivity.this, "Error", "No se logró obtener su ubicación, intente de nuevo");
        }
    }

    private void ProcesaSinRespuesta()
    {
        Log.d("DEBUG", "ProcesaSinRespuesta");
        DetenerEscuchaEstatus();
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("customerID", "");
        hashMap.put("iEstatus", Values.TIME_OUT);
        hashMap.put("location", new Location(0.0,0.0));
        dbReferenceCustomer.child(cKeyTaxi).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utilities.MostrarMensaje(MainActivity.this, "Tiempo agotado", "El taxista no acepto la solicitud a tiempo, intenta de nuevo");
                ActivaDesactivaBotones(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ProcesaSinRespuesta();
                Toast.makeText(MainActivity.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void MostrarOpciones() {
       Intent i= new Intent(MainActivity.this, MenuActivity.class);
       startActivity(i);
    }
    public void slideUp(View view){
        Log.d("DEBUG", "slideUp");
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view){
        Log.d("DEBUG", "slideDown");
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(1000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        view.setVisibility(View.GONE);
    }
    public String getStyleMap()
    {
        Calendar time= Calendar.getInstance();
        int hour=time.get(Calendar.HOUR_OF_DAY);
        if(hour>=6&&hour<=18)
            return Style.OUTDOORS;
        else
            return Style.DARK;
    }
    private void CerrarSesion()
    {

        AlertDialog.Builder alert= new AlertDialog.Builder(this);
        if(lServicio)
        {
            Utilities.MostrarMensaje(this, "Error", "No se puede cerrar la sesión ya que existe un servicio actual");
        }
        else
        {
            alert.setTitle("Cerrar Sesión");
            alert.setMessage("¿Estás seguro que deseas cerrar la sesión?");
            alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    CierraSesion();
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.create().show();
        }

    }

    private void CierraSesion() {
        Intent intent= new Intent(MainActivity.this, TelefonoActivity.class);
        firebaseAuth.signOut();
        startActivity(intent);
        finish();
    }





    private void ValidaLocalizacion(LatLng location)
    {
        Handler handler= new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                PreparaUbicacionValida((Boolean)msg.obj);
            }
        };

        new Thread(() -> {
            boolean lExiste=false;
            Message message= new Message();
            Log.d("DEBUG", location.getLatitude()+" "+location.getLongitude());
            ZonaService zonaService= new ZonaService(MainActivity.this);
            ZonaDetalleService zonaDetalleService= new ZonaDetalleService(MainActivity.this);
            List<Zona> lstZona=zonaService.ObtenerZonas();
            List<Point> lstZonaDetalle;
            List<List<Point>> lstDatos;
            for(Zona entZona:lstZona)
            {
                lstDatos= new ArrayList<>();
                lstZonaDetalle=zonaDetalleService.ObtenerListaPoint(entZona.iIdZona);
                for(Point point:lstZonaDetalle)
                {
                    Log.d("DEBUG", "PUNTO: "+ point.latitude()+ point.longitude());
                }
                lstDatos.add(lstZonaDetalle);
                Log.d("DEBUG","TAMAÑOS "+lstZonaDetalle.size());
                lExiste=TurfJoins.inside(Point.fromLngLat(location.getLongitude(), location.getLatitude()),Polygon.fromLngLats(lstDatos));
                Log.d("DEBUG", " Result"+lExiste);
            }
            message.obj=lExiste;
            handler.sendMessage(message);

        }).start();
    }

    private void PreparaUbicacionValida(boolean lExiste) {
        if(!lExiste)
        {
            btnSolicitar.setVisibility(View.GONE);
            Utilities.MostrarMensaje(this, "Zona no disponible", "El servicio no está disponible en tu zona actualmente");
        }
    }

    private void MuestraCarga()
    {
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getSupportFragmentManager(), "dialogocarga");
    }
    private void OcultaCarga()
    {
        dialogoCarga.dismiss();
    }
    public class LocationStatusReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("DEBUG", "mmmm:" +intent.getAction());
            if(LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction()))
            {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean lGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean lNetWork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (lGPS || lNetWork) {
                    ConfiguraUbicacion(true);
                } else {
                    ConfiguraUbicacion(false);
                }

            }

        }
    }

    private void ConfiguraUbicacion(boolean b) {
        if(!b)
        {
            btnSolicitar.setVisibility(View.GONE);
            Utilities.MostrarMensaje(this, "Ubicacion desactivada","Es necesario activar la ubicación");
        }
        else
        {
            btnSolicitar.setVisibility(View.VISIBLE);
        }

    }

    private void IniciaSolicitudUbicaciones()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(Utilities.VerificaLocalizacion(MainActivity.this))
            {
                fusedLocationProviderClient.requestLocationUpdates(obtenerLocationRequest(), locationCallback, Looper.myLooper());
            }
            else
            {
                Toast.makeText(this, "¡Es necesario activar la localización!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "¡Es necesario conceder permisos de la ubicación!", Toast.LENGTH_SHORT).show();
        }

    }
    private void DetenerSolicitudUbicaciones()
    {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    private LocationRequest obtenerLocationRequest()
    {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "!El permiso es necesario para continuar!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    public void AjustaCamara(LatLng location)
    {
        Log.d("DEBUG", "AjustaCamara");
        if(location!=null)
        {
            CameraPosition cameraPosition;
            if(lCargaPrimera&&gMapboxMap!=null)
            {
                cameraPosition= new CameraPosition.Builder()
                        .target(location)
                        .zoom(15.5)
                        .build();
                gMapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),1500);
                lCargaPrimera=false;
            }

        }
    }

}
