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

    Button selecionarDuracao = findViewById(R.id.btn_selecionar_duracao);

    tempoDesejado = findViewById(R.id.duracao_desejada);

    selecionarDuracao.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        _hora = c.get(Calendar.HOUR_OF_DAY);
        _minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(TempoDesejadoActivity.this,
          new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hora, int minuto) {
              TempoDesejadoActivity.this.tempoDesejado.setText(hora + ":" + minuto);
            }
          }, _hora, _minuto, true);
        timePickerDialog.show();
      }
    });

    Button verificar = findViewById(R.id.btn_verificar);

    final String dados = getIntent().getStringExtra("texto");

    verificar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(TempoDesejadoActivity.this, HorariosDisponiveisActivity.class);
        intent.putExtra("texto", dados);
        intent.putExtra("duracao", tempoDesejado.getText().toString());
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
