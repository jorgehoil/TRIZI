package com.carloshoil.trizi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    Button btnInfo, btnZonas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btnInfo=findViewById(R.id.btnInfo);
        btnZonas=findViewById(R.id.btnZonas);
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambiaPantalla(1);
            }
        });
        btnZonas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CambiaPantalla(2);
            }
        });
        CambiaPantalla(1);
    }

    private void CambiaPantalla(int iOpcion)
    {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment newFragment=null;
        switch (iOpcion)
        {
            case 1:
                newFragment= new FragmentInfo();
                break;
            case 2:
                newFragment= new FragmentZonas();
                break;
        }
        transaction.replace(R.id.fragmentMenu, newFragment);
        transaction.commit();

    }
}