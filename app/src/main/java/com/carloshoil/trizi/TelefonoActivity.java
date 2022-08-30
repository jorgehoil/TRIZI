package com.carloshoil.trizi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.trizi.Global.Utilities;
import com.carloshoil.trizi.Global.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TelefonoActivity extends AppCompatActivity {

    boolean lSMSEnVerificacion=false;
    ProgressBar pbCarga;
    Button btnVerificar;
    EditText edNumTelefono, edCodigoSMS;
    TextView tvMensaje,tvCambiarNumero, tvReenviarSMS;
    String cClaveIDCodigo="cClaveIDCodigo", cClaveEnvioSMS="cSMS",cClaveTelefono="cTelefono", cClaveSMSFecha="cFechaSMS", cTelefono="",cCodigoSMS="";
    private FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telefono);
        Init();
        TelefonoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ObtenerGlobales();
        Iniciar(firebaseAuth.getCurrentUser());
    }


    public void Iniciar(FirebaseUser user)
    {
        IniciaPantalla(user);
    }
    public void ActivaDesactivaReenviarCambiar(boolean lActivo)
    {
        tvCambiarNumero.setEnabled(lActivo);
        tvReenviarSMS.setEnabled(lActivo);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "OnResume", Toast.LENGTH_SHORT).show();
    }

    public void Init() {
        firebaseAuth= FirebaseAuth.getInstance();
        pbCarga=findViewById(R.id.pbCarga);
        tvMensaje=findViewById(R.id.tvMensaje);
        tvCambiarNumero=findViewById(R.id.tvCambiarNumero);
        tvReenviarSMS=findViewById(R.id.tvReenviarSMS);
        edNumTelefono=findViewById(R.id.edNumTelefono);
        edCodigoSMS=findViewById(R.id.edCodigoSMS);
        btnVerificar = findViewById(R.id.btnVerificar);
        tvReenviarSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReenviarSMS();
            }
        });
        tvCambiarNumero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambiarNumero();
            }
        });
        btnVerificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Verificar();

            }
        });
        mCallBacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                IniciaSesion(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pbCarga.setVisibility(View.GONE);
                Toast.makeText(TelefonoActivity.this, "Error, vuelva a intentarlo."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                pbCarga.setVisibility(View.GONE);
                lSMSEnVerificacion=true;
                cCodigoSMS=s;
                ActualizaVariablesPreferences("1",Utilities.obtenerFechaActualCadena(),s,cTelefono);
                CamposSMS(true);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                ActivaDesactivaReenviarCambiar(true);
            }
        };
    }

    private void Verificar() {
        String cAuxiliar="";
        Log.d("DEBUG", "Verificar "+"lSMSEnverificacion"+lSMSEnVerificacion);
        if(lSMSEnVerificacion)
        {
            cAuxiliar=edCodigoSMS.getText().toString();
            if(ValidaCodigoSMS(cAuxiliar))
            {
                VerificarCodigoSMS(cAuxiliar,cCodigoSMS);
            }
            else
            {
                Toast.makeText(this, "Verifica que el código contenga 6 dígitos", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            cAuxiliar=edNumTelefono.getText().toString();
            if(ValidaNumeroTelefono(cAuxiliar))
            {
                cTelefono=cAuxiliar;
                VerificarTelefono("+52"+cAuxiliar);
            }
            else
            {
                Toast.makeText(this, "Verifica que el número de teléfono contenga 10 dígitos", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean ValidaCodigoSMS(String cCodigoSMS)
    {
        if(cCodigoSMS.isEmpty())
        {
            return false;
        }
        if(cCodigoSMS.length()!=6)
        {
            return false;
        }
        if(cCodigoSMS.contains(".")||cCodigoSMS.contains(","))
        {
            return false;
        }
        return true;
    }
    public boolean ValidaNumeroTelefono(String cTelefono)
    {
        String temp="";
        if(cTelefono.isEmpty())
        {
            return false;
        }
        if(cTelefono.length()!=10)
        {
            return false;
        }
        if(cTelefono.contains(".")||cTelefono.contains(","))
        {
            return false;
        }
        return true;
    }
    public void VerificarCodigoSMS(String cCodigoSMS, String cId)
    {
        if(!cTelefono.isEmpty()&&!cCodigoSMS.isEmpty()&&!cId.isEmpty()) {
            pbCarga.setVisibility(View.VISIBLE);
            PhoneAuthCredential phoneAuthCredential=  PhoneAuthProvider.getCredential(cId,cCodigoSMS);
            IniciaSesion(phoneAuthCredential);
        }
    }


    public void CamposSMS(boolean lCodigoSMS)
    {
        if(lCodigoSMS) {
            tvCambiarNumero.setVisibility(View.VISIBLE);
            edNumTelefono.setVisibility(View.GONE);
            edCodigoSMS.setVisibility(View.VISIBLE);
            tvMensaje.setText("Te hemos enviado un SMS a tu número telefónico, ingresa el código que has recibido.");
            tvReenviarSMS.setVisibility(View.VISIBLE);
            tvCambiarNumero.setVisibility(View.VISIBLE);
        }
        else
        {
            tvCambiarNumero.setVisibility(View.GONE);
            edCodigoSMS.setVisibility(View.GONE);
            edNumTelefono.setVisibility(View.VISIBLE);
            tvMensaje.setText("Ingresa tu número telefónico");
            tvReenviarSMS.setVisibility(View.GONE);
            tvCambiarNumero.setVisibility(View.GONE);
        }

    }
    public void ReenviarSMS()
    {
        if(!cTelefono.trim().isEmpty())
        {
            Log.d("DEBUG","Telefono "+ cTelefono);
            ActivaDesactivaReenviarCambiar(false);
            ActualizaVariablesPreferences("1","","", cTelefono);
            VerificarTelefono("+52"+cTelefono);
        }

    }
    public void ActualizaVariablesPreferences(String _cClaveEnvioSMS,String  _cClaveSMSFecha, String _cClaveIDCodigo, String _cClaveTelefono)
    {

        Utilities.GuardarPreferencias(cClaveEnvioSMS, _cClaveEnvioSMS,this);
        Utilities.GuardarPreferencias(cClaveSMSFecha, _cClaveSMSFecha, TelefonoActivity.this);
        Utilities.GuardarPreferencias(cClaveIDCodigo, _cClaveIDCodigo,TelefonoActivity.this);

    }
    public void VerificarTelefono(String _cTelefono)
    {
        pbCarga.setVisibility(View.VISIBLE);
        PhoneAuthOptions phoneAuthOptions= PhoneAuthOptions.newBuilder()
                .setPhoneNumber(_cTelefono)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

    }
    public void IniciaSesion(PhoneAuthCredential credential)
    {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Utilities.GuardarPreferencias("cEstatus",Values.REGISTRADATOS, TelefonoActivity.this);
                    IniciaPantalla(task.getResult().getUser());
                }
                else
                {
                    Toast.makeText(TelefonoActivity.this, "Error, verifica el código ingresado", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    public void IniciaPantalla(FirebaseUser user)
    {
        if(user!=null)
        {
            String cEstatus= Utilities.RecuperaPreferencia("cEstatus",this);
            Intent intent=null;
            switch (cEstatus)
            {
                case Values.COMPLETO:
                    intent= new Intent(TelefonoActivity.this, MainActivity.class);
                    break;
                case Values.REGISTRADATOS:
                    intent= new Intent(TelefonoActivity.this, RegistroActivy.class);
                    break;
            }
            if(intent!=null)
            {
                startActivity(intent);
                finish();
            }
        }
        else
        {
            long lTiempoDesdeEnvioSMS;
            if(lSMSEnVerificacion)
            {
                CamposSMS(true);
                lTiempoDesdeEnvioSMS=Utilities.obtenerSegundos(Utilities.RecuperaPreferencia(cClaveSMSFecha,TelefonoActivity.this),Utilities.obtenerFechaActualCadena());
                if(lTiempoDesdeEnvioSMS>=30&&lTiempoDesdeEnvioSMS<=60)
                {
                    ActivaDesactivaReenviarCambiar(true);
                }
                else if(lTiempoDesdeEnvioSMS>60)
                {
                    lSMSEnVerificacion=false;
                    ActualizaVariablesPreferences("0","","","");
                    CamposSMS(false);
                }
            }
        }
    }

    private void ObtenerGlobales() {
        lSMSEnVerificacion=Utilities.RecuperaPreferencia(cClaveEnvioSMS,this).equals("1");
        cCodigoSMS=Utilities.RecuperaPreferencia(cClaveIDCodigo, this);
        cTelefono=Utilities.RecuperaPreferencia(cClaveTelefono, this);
    }

    private void CambiarNumero()
    {
        CamposSMS(false);
        lSMSEnVerificacion=false;
        ActivaDesactivaReenviarCambiar(false);
        ActualizaVariablesPreferences("0","","","");
    }


}
