package com.bjbarber.barber;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CardView Desconto, Catalogo, SaudeBeleza, chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Home");


        Desconto = findViewById(R.id.cardViewDesconto);
        Catalogo = findViewById(R.id.cardViewCatalogo);
        SaudeBeleza = findViewById(R.id.cardViewSaudeBeleza);
        chat = findViewById(R.id.cardViewChat);

        Desconto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DescontoActivity.class);
                startActivity(intent);
            }
        });

        Catalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DigitalCatalogueActivity.class);
                startActivity(intent);
            }
        });

        SaudeBeleza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SaudeBlazaActivity.class);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String contact = "+351 936994803"; // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = getBaseContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
