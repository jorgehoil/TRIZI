package com.carloshoil.trizi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.carloshoil.trizi.Clases.Usuario;
import com.carloshoil.trizi.DTO.ConfigDTO;
import com.carloshoil.trizi.DTO.DatosPagoDTO;
import com.carloshoil.trizi.Global.Utilities;
import com.carloshoil.trizi.Global.Values;
import com.carloshoil.trizi.DB.FirebaseInstance;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class RegistroActivy extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference dbReferenceUsuarios;
    DatabaseReference dbReferenceDrivesCustomer;
    Button btnGuardar;
    Usuario usuario;
    EditText edNombre, edApellido, edDirección, edEdad;
    RadioButton rbFemenino, rbMasculino, rbIndefinido;
    ProgressBar pbCarga;
    ImageView imVCargaFoto;
    int REQUEST_IMAGE_CAPTURE=1;
    boolean lImagenCargada=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_activy);
        RegistroActivy.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseInstance.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference("usersimages");
        dbReferenceDrivesCustomer=firebaseDatabase.getReference().child("driverscustomers");
        dbReferenceUsuarios=firebaseDatabase.getReference("users");
        btnGuardar=findViewById(R.id.btnGuardar);
        pbCarga=findViewById(R.id.pbRegistroDatos);
        rbMasculino=findViewById(R.id.rbMasculino);
        rbFemenino=findViewById(R.id.rbFemenino);
        rbIndefinido=findViewById(R.id.rbIndefinido);
        edApellido=findViewById(R.id.edApellidos);
        edDirección=findViewById(R.id.edDireccion);
        edNombre=findViewById(R.id.edNombres);
        edEdad=findViewById(R.id.edEdad);
        imVCargaFoto=findViewById(R.id.imCargaFoto);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Guardar();
            }
        });
        imVCargaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarFoto();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        VerificaUsuario();

    }

    private void CargarFoto()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private void VerificaUsuario() {
        firebaseUser=firebaseAuth.getCurrentUser();
        dbReferenceUsuarios.child(firebaseUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pbCarga.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegistroActivy.this, "Ya existe un usuario registrado con este teléfono", Toast.LENGTH_SHORT).show();
                    Utilities.GuardarPreferencias("cEstatus", "C", RegistroActivy.this);
                    Utilities.GuardarPreferencias("cUID", firebaseUser.getUid(), RegistroActivy.this);
                    IniciarPrincipal(false);
                } else {
                    pbCarga.setVisibility(View.INVISIBLE);
                    btnGuardar.setEnabled(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                btnGuardar.setEnabled(true);
                pbCarga.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void IniciarPrincipal(boolean lPrimeraCarga)
    {

        Intent i= new Intent(RegistroActivy.this, MainActivity.class);
        if(lPrimeraCarga)
        {
            i.putExtra("lPrimeraCarga", false);
        }
        startActivity(i);
        finish();
    }
    private void Guardar() {
        int iTipoSexo=0;
        String cMensaje="";
        iTipoSexo=obtenerTipoSexo();
        usuario= new Usuario(
                edNombre.getText().toString(),
                edApellido.getText().toString(),
                edNombre.getText().toString()+" "+edApellido.getText().toString(),
                edDirección.getText().toString(),
                iTipoSexo,
                Utilities.TryParse(edEdad.getText().toString()),
                "",
                new ConfigDTO(false,
                        true,
                        false,
                        true,
                        0,
                        0,
                        0,
                        0,
                        0),
                new DatosPagoDTO(false,
                        "",
                        ""));
        cMensaje=ValidaUsuario(usuario);
        if(cMensaje.length()>1)
        {
            Toast.makeText(this, cMensaje, Toast.LENGTH_SHORT).show();
        }
        else
        {
            CargaImagen();
        }
    }
    public void GuardaUsuario(String cUID)
    {
        pbCarga.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);
        Utilities.GuardarPreferencias("cNombreCompletoUsuario", usuario.cNombreCompleto,this);
        dbReferenceUsuarios.child(cUID).setValue(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Utilities.GuardarPreferencias("cEstatus","C",RegistroActivy.this);
                IniciarPrincipal(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pbCarga.setVisibility(View.INVISIBLE);
                btnGuardar.setEnabled(true);
                Utilities.MostrarMensaje(RegistroActivy.this, "Error al guardar", "Se ha producido un error al guardar, verifica tu conexión a internet");
            }
        });
    }
    public int obtenerTipoSexo()
    {
        if(rbFemenino.isChecked())
        {
            return 1;
        }else if(rbMasculino.isChecked())
        {
            return 2;
        }
        return 3;
    }

    public String ValidaUsuario(Usuario _usuario)
    {
        String cMensaje="";
        if(_usuario!=null)
        {
            if(_usuario.cNombres.trim().isEmpty()||_usuario.cApellidos.trim().isEmpty()||_usuario.cDireccion.trim().isEmpty())
            {
                cMensaje="Por favor, verifica que todos los campos estén completos";
            }
            else if(_usuario.iTipoSexo==0)
            {
                cMensaje="Selecciona el género";
            }
            else if(_usuario.cDireccion.split(" ").length<2)
                cMensaje="Tu dirección requiere ser más preciso";
            else if(!lImagenCargada)
            {
                cMensaje="Es necesario una imagen de perfil";
            }
        }
        else
        {
            cMensaje="Usuario no válido";
        }
        return cMensaje;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap bmFinal;
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if(imageBitmap.getWidth()>=imageBitmap.getHeight())
            {
                bmFinal=Bitmap.createBitmap(imageBitmap,
                        imageBitmap.getWidth()/2-imageBitmap.getHeight()/2,
                        0,
                        imageBitmap.getHeight(),
                        imageBitmap.getHeight());
            }
            else
            {
                bmFinal=Bitmap.createBitmap(imageBitmap,
                        0,
                        imageBitmap.getHeight()/2-imageBitmap.getWidth()/2,
                        imageBitmap.getWidth(),
                        imageBitmap.getWidth());
            }
            imVCargaFoto.setImageBitmap(bmFinal);
            lImagenCargada=true;
        }

    }
    public void CargaImagen()
    {
        pbCarga.setVisibility(View.VISIBLE);
        btnGuardar.setEnabled(false);
        imVCargaFoto.setDrawingCacheEnabled(true);
        imVCargaFoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imVCargaFoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = storageReference.child("CP_"+firebaseUser.getUid()).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                pbCarga.setVisibility(View.INVISIBLE);
                btnGuardar.setEnabled(true);
                Utilities.MostrarMensaje(RegistroActivy.this,"Error al guardar", "Error al guardar. No se pudo subir tu foto, verifica tu conexión a internet");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                GuardaUsuario(firebaseUser.getUid());
            }
        });
    }
    /*private void VerificaEstatus(Intent intent) {
        Bundle bundle= intent.getExtras();
        boolean lPrimeraCarga=false;
        if(bundle!=null)
        {
            lPrimeraCarga=bundle.getBoolean("lPrimeraCarga",false);
            if(lPrimeraCarga){
                Toast.makeText(this, "Es primera carga", Toast.LENGTH_SHORT).show();
            }
        }
    }
*/


}
