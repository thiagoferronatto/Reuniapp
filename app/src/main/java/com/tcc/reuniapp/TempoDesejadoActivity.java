package com.tcc.reuniapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TempoDesejadoActivity extends AppCompatActivity {
  int _hora, _minuto;

  EditText tempoDesejado;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_tempo_desejado);

    tempoDesejado = findViewById(R.id.duracao_desejada);

    Button verificar = findViewById(R.id.btn_verificar);

    final String dados = getIntent().getStringExtra("texto");

    verificar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(TempoDesejadoActivity.this, HorariosDisponiveisActivity.class);
        intent.putExtra("texto", dados);
        String duracao = tempoDesejado.getEditableText().toString();
        duracao = (Integer.parseInt(duracao) / 60) + ":" + (Integer.parseInt(duracao) % 60);
        intent.putExtra("duracao", duracao);
        startActivity(intent);
        TempoDesejadoActivity.this.finish();
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    this.finish();
  }
}
