package com.tcc.reuniapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CompromissoActivity extends AppCompatActivity {
  Button selecionarData, selecionarInicio, selecionarTermino, salvar;
  EditText _participantes, _data, _inicio, _termino, _nome;
  int _ano, _mes, _dia, _hora, _minuto, numeroDoCompromisso = 0;
  String email;
  FirebaseFirestore db;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_compromisso);

    email = GoogleSignIn.getLastSignedInAccount(this).getEmail();

    db = FirebaseFirestore.getInstance();

    assert email != null;
    DocumentReference df = db.collection("compromissos").document(email.substring(0, email.indexOf("@")));
    df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          assert document != null;
          if (document.exists()) {
            numeroDoCompromisso = document.getData().size();
            // compromisso.put(Integer.toString(numeroDoCompromisso), dados);
          }
        }
      }
    });

    selecionarData = findViewById(R.id.btnSelecionarData);
    selecionarInicio = findViewById(R.id.btnSelecionarInicio);
    selecionarTermino = findViewById(R.id.btnSelecionarTermino);
    salvar = findViewById(R.id.btnSalvarCompromisso);

    _participantes = findViewById(R.id.participantes);
    _data = findViewById(R.id.data);
    _data.setEnabled(false);
    _data.setVisibility(View.GONE);
    _inicio = findViewById(R.id.inicio);
    _inicio.setEnabled(false);
    _inicio.setVisibility(View.GONE);
    _termino = findViewById(R.id.termino);
    _termino.setEnabled(false);
    _termino.setVisibility(View.GONE);
    _nome = findViewById(R.id.nome);

    selecionarData.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        _ano = c.get(Calendar.YEAR);
        _mes = c.get(Calendar.MONTH);
        _dia = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(CompromissoActivity.this,
          new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int ano, int mes, int dia) {
              CompromissoActivity.this._data.setText(dia + "/" + (mes + 1) + "/" + ano);
              CompromissoActivity.this._data.setVisibility(View.VISIBLE);
            }
          }, _ano, _mes, _dia);
        datePickerDialog.show();
      }
    });

    selecionarInicio.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        _hora = c.get(Calendar.HOUR_OF_DAY);
        _minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CompromissoActivity.this,
          new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hora, int minuto) {
              CompromissoActivity.this._inicio.setText(hora + ":" + minuto);
              if (CompromissoActivity.this._termino.getVisibility() == View.GONE)
                CompromissoActivity.this._termino.setVisibility(View.INVISIBLE);
              CompromissoActivity.this._inicio.setVisibility(View.VISIBLE);
            }
          }, _hora, _minuto, true);
        timePickerDialog.show();
      }
    });

    selecionarTermino.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final Calendar c = Calendar.getInstance();
        _hora = c.get(Calendar.HOUR_OF_DAY);
        _minuto = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(CompromissoActivity.this,
          new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTimeSet(TimePicker view, int hora, int minuto) {
              CompromissoActivity.this._termino.setText(hora + ":" + minuto);
              if (CompromissoActivity.this._inicio.getVisibility() == View.GONE)
                CompromissoActivity.this._inicio.setVisibility(View.INVISIBLE);
              CompromissoActivity.this._termino.setVisibility(View.VISIBLE);
            }
          }, _hora, _minuto, true);
        timePickerDialog.show();
      }
    });

    salvar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String
          participantes = _participantes.getEditableText().toString(),
          data = _data.getEditableText().toString(),
          inicio = _inicio.getEditableText().toString(),
          termino = _termino.getEditableText().toString(),
          nome = _nome.getEditableText().toString();
        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount conta = GoogleSignIn.getLastSignedInAccount(CompromissoActivity.this);
        assert conta != null;
        email = conta.getEmail();


        final Map<String, Object> compromisso = new HashMap<>();
        final Map<String, String> dados = new HashMap<>();
        dados.put("participantes", participantes);
        dados.put("data", data);
        dados.put("inicio", inicio);
        dados.put("termino", termino);
        dados.put("nome", nome);

        compromisso.put(Integer.toString(numeroDoCompromisso), dados);

        if (numeroDoCompromisso != 0) {
          assert email != null;
          DocumentReference df = db.collection("compromissos").document(email.substring(0, email.indexOf("@")));
          df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                assert document != null;
                if (document.exists()) {
                  numeroDoCompromisso = document.getData().size();
                }
              }
            }
          });
        }

        if (numeroDoCompromisso != 0)
          db.collection("compromissos").document(email.substring(0, email.indexOf("@"))).update(compromisso);
        else
          db.collection("compromissos").document(email.substring(0, email.indexOf("@"))).set(compromisso);

        startActivity(new Intent(CompromissoActivity.this, AgendaDrawerActivity.class));
        CompromissoActivity.this.finish();
      }
    });
  }
}
