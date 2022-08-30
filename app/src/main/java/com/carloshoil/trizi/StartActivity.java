package com.carloshoil.trizi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.widget.ImageView;

import com.carloshoil.trizi.Global.Utilities;
import com.carloshoil.trizi.Global.Values;

public class StartActivity extends AppCompatActivity {
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        imageView= findViewById(R.id.imIconoInicio);
        StartActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Inicia();
    }

    public void Inicia()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String cEstatus= Utilities.RecuperaPreferencia("cEstatus",StartActivity.this);
                Intent intent=null;
                switch (cEstatus)
                {
                    case Values.COMPLETO:
                        intent= new Intent(StartActivity.this, MainActivity.class);
                        break;
                    case Values.REGISTRADATOS:
                        intent= new Intent(StartActivity.this, RegistroActivy.class);
                        break;
                    default:
                        intent= new Intent(StartActivity.this, TelefonoActivity.class);
                        break;
                }
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

}
