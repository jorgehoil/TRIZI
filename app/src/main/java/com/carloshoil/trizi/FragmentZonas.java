package com.carloshoil.trizi;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.trizi.Adapter.ZonaAdapter;
import com.carloshoil.trizi.DB.FirebaseInstance;
import com.carloshoil.trizi.Dialogos.DialogoCarga;
import com.carloshoil.trizi.Entities.Zona;
import com.carloshoil.trizi.Entities.Zona_Detalle;
import com.carloshoil.trizi.Global.Utilities;
import com.carloshoil.trizi.Service.ZonaDetalleService;
import com.carloshoil.trizi.Service.ZonaService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentZonas#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentZonas extends Fragment {
    private FirebaseDatabase firebaseDatabase;
    Button btnActualizar;
    TextView tvZonasNuevas;
    RecyclerView recyclerView;
    ZonaAdapter zonaAdapter;
    DialogoCarga dialogoCarga;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentZonas() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentZonas.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentZonas newInstance(String param1, String param2) {
        FragmentZonas fragment = new FragmentZonas();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_zonas, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    private void MostrarDialogoCarga()
    {
        dialogoCarga= new DialogoCarga(getActivity());
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getActivity().getSupportFragmentManager(),"dialogocarga");

    }
    private void OcultarDialogoCarga()
    {
        if(dialogoCarga!=null)
            dialogoCarga.dismiss();
    }
    private void PreparaDatosApapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    private void CargaDatosInicio()
    {
        MostrarDialogoCarga();
        List<Zona> lstZona= ObtenerZonasDBLocal();
        zonaAdapter= new ZonaAdapter(getActivity(), lstZona);
        recyclerView.setAdapter(zonaAdapter);
        VerificaNuevosDatos(lstZona.size());
    }
    private void RecargaDatosZonas()
    {
        List<Zona> lstZona=ObtenerZonasDBLocal();
        zonaAdapter.actualizaDatos(lstZona);
    }
    private void VerificaNuevosDatos(int iActual)
    {
        firebaseDatabase.getReference().child("zonasvalidas").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                List<Zona> lstZonas= new ArrayList<>();
                Zona entZona;
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        entZona= new Zona();
                        entZona.iIdZonaR=Utilities.TryParse(dataSnapshot1.child("iIdZona").getValue().toString());
                        entZona.cNombre=dataSnapshot1.child("cNombre").toString();
                        lstZonas.add(entZona);
                    }
                    OcultarDialogoCarga();
                    if(lstZonas.size()!=iActual)
                    {
                        tvZonasNuevas.setVisibility(View.VISIBLE);
                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OcultarDialogoCarga();
                Toast.makeText(getActivity(), "Error al cargar al verificar", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void Init(View view)
    {
        firebaseDatabase= FirebaseInstance.getInstance();
        btnActualizar=view.findViewById(R.id.btnActualizar);
        tvZonasNuevas=view.findViewById(R.id.tvZonasNuevas);
        recyclerView=view.findViewById(R.id.listZonas);
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObtenerZonas_Detalles();
            }
        });
        PreparaDatosApapter();
        CargaDatosInicio();
    }

    public void slideUp(View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(3000);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }
    public void slideDown(View view){
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
    private void ObtenerZonas_Detalles()
    {
        MostrarDialogoCarga();
        Log.d("DEBUG", "ObtenerZonas_Detalles()");
        firebaseDatabase.getReference().child("zonavalidadet").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    List<Zona_Detalle> lstZona= new ArrayList<>();
                    Zona_Detalle zona_detalle;
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        zona_detalle= new Zona_Detalle();
                        zona_detalle.dLongitud=Double.parseDouble(dataSnapshot1.child("dLongitud").getValue().toString().isEmpty()?"0":dataSnapshot1.child("dLongitud").getValue().toString());
                        zona_detalle.dLatitud=Double.parseDouble(dataSnapshot1.child("dLatitud").getValue().toString().isEmpty()?"0":dataSnapshot1.child("dLatitud").getValue().toString());
                        zona_detalle.iOrden= Utilities.TryParse(dataSnapshot1.child("iOrden").getValue().toString());
                        zona_detalle.iIdZonaR=Utilities.TryParse(dataSnapshot1.child("iIdZona").getValue().toString());
                        lstZona.add(zona_detalle);
                        Log.d("DEBUG", zona_detalle.iIdZonaR+"");

                    }
                    ObtenerZonas(lstZona);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OcultarDialogoCarga();
                Toast.makeText(getActivity(), "Error al actualizar, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void ObtenerZonas(List<Zona_Detalle> lstZonaDet) {
        Log.d("DEBUG", "ObtenerZonas()");
        firebaseDatabase.getReference().child("zonasvalidas").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                List<Zona> lstZonas= new ArrayList<>();
                Zona entZona;
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {
                        entZona= new Zona();
                        entZona.iIdZonaR=Utilities.TryParse(dataSnapshot1.child("iIdZona").getValue().toString());
                        entZona.cNombre=dataSnapshot1.child("cNombre").getValue().toString();
                        lstZonas.add(entZona);
                        Log.d("DEBUG", entZona.iIdZonaR+""+ entZona.iIdZona);
                    }
                    Log.d("DEBUG", "ONSUCCESS"+ lstZonas.size());
                    ActualizaZonas(lstZonas,lstZonaDet);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OcultarDialogoCarga();
                Toast.makeText(getActivity(), "Error al actualizar, intenta de nuevo", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private List<Zona> ObtenerZonasDBLocal()
    {
        ZonaService zonaService= new ZonaService(getActivity());
        return zonaService.ObtenerZonas();
    }

    public void ActualizaZonas(List<Zona> listZonas, List<Zona_Detalle> lstZonaDetalle)
    {
        Handler handler= new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                OcultarDialogoCarga();
                if((boolean)msg.obj)
                {
                    Log.d("DEBUG","Respuesta"+ (boolean)msg.obj+"");
                    tvZonasNuevas.setVisibility(View.GONE);
                    RecargaDatosZonas();
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("DEBUG", "ActualizaZonas-Thread");
                Message message= new Message();
                ZonaService zonaService= new ZonaService(getActivity());
                boolean lResult=zonaService.GuardarZonaZonaDetalles(listZonas, lstZonaDetalle);
                message.obj=lResult;
                handler.sendMessage(message);
            }
        }).start();



    }
}