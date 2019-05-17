package com.tcc.reuniapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_info);

    TextView texto = findViewById(R.id.info_texto);

    texto.setText(getIntent().getStringExtra("texto"));
  }
}
