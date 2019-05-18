package com.tcc.reuniapp;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HorariosDisponiveisActivity extends AppCompatActivity {
  RecyclerView rv;
  List<Compromisso> sugestoes;
  String _terminoJ, _inicioJ;

  @SuppressLint("SetTextI18n")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_horarios_disponiveis);

    GoogleSignInAccount conta = GoogleSignIn.getLastSignedInAccount(this);

    List<Compromisso> compromissos = new ArrayList<>();
    List<Compromisso> meusCompromissos = new ArrayList<>();
    sugestoes = new ArrayList<>();

    assert conta != null;
    String e = conta.getEmail();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    assert e != null;
    DocumentReference df = db.collection("compromissos").document(e.substring(0, e.indexOf("@")));

    String qr = getIntent().getStringExtra("texto");

    Compromisso c;
    for (int i = 0; i < qr.split(";").length - 2; ++i) {
      String data = qr.split(";")[i].split("#")[0];
      String horario = qr.split(";")[i].split("#")[1];
      c = new Compromisso(data, horario.split("-")[0], horario.split("-")[1], true);
      compromissos.add(c);
    }

    TextView nome = findViewById(R.id.nome_outra_conta);
    nome.setText(
      "Agenda de " + qr.split(";")[qr.split(";").length - 2] + System.lineSeparator() +
        qr.split(";")[qr.split(";").length - 1]
    );

    Collections.sort(compromissos, new Comparator<Compromisso>() {
      @Override
      public int compare(Compromisso o1, Compromisso o2) {
        long _t1 = Long.parseLong(
          o1.getData().split("/")[2]
        ) * 31536000 + Long.parseLong(
          o1.getData().split("/")[1]
        ) * 2592000 + Long.parseLong(
          o1.getData().split("/")[0]
        ) * 86400 + Long.parseLong(
          o1.getHorario().split("-")[0].split(":")[0]
        ) * 3600 + Long.parseLong(
          o1.getHorario().split("-")[0].split(":")[1]
        ) * 60;

        String t1 = Long.toString(_t1);

        long _t2 = Long.parseLong(
          o2.getData().split("/")[2]
        ) * 31536000 + Long.parseLong(
          o2.getData().split("/")[1]
        ) * 2592000 + Long.parseLong(
          o2.getData().split("/")[0]
        ) * 86400 + Long.parseLong(
          o2.getHorario().split("-")[0].split(":")[0]
        ) * 3600 + Long.parseLong(
          o2.getHorario().split("-")[0].split(":")[0]
        ) * 60;

        String t2 = Long.toString(_t2);

        return t1.compareToIgnoreCase(t2);
      }
    });

    Compromisso compromisso = new Compromisso(
      "Antes das " + compromissos.get(0).getInicio(),
      compromissos.get(0).getData(),
      "Tempo livre"
    );
    sugestoes.add(compromisso);

    for (int i = 0; i < compromissos.size() - 1; ++i) {
      if (compromissos.get(i).getData().equals(compromissos.get(i + 1).getData())) {
        long termSecs =
          Long.parseLong(compromissos.get(i).getHorario().split("-")[1].split(":")[0]) * 3600 +
            Long.parseLong(compromissos.get(i).getHorario().split("-")[1].split(":")[1]) * 60;
        long inicSecs =
          Long.parseLong(compromissos.get(i + 1).getHorario().split("-")[0].split(":")[0]) * 3600 +
            Long.parseLong(compromissos.get(i + 1).getHorario().split("-")[0].split(":")[1]) * 60;
        String _duracao = HorariosDisponiveisActivity.this.getIntent().getStringExtra("duracao");
        long duracao =
          Long.parseLong(_duracao.split(":")[0]) * 3600 +
            Long.parseLong(_duracao.split(":")[1]) * 60;
        if (inicSecs - termSecs >= duracao) {
          compromisso = new Compromisso(
            compromissos.get(i).getTermino() + " até " + compromissos.get(i + 1).getInicio(),
            compromissos.get(i).getData(),
            "Tempo livre"
          );
          sugestoes.add(compromisso);
        }
      } else {
        Compromisso c1 = new Compromisso(
          "Antes das " + compromissos.get(i + 1).getInicio(),
          compromissos.get(i + 1).getData(),
          "Tempo livre"
        );
        sugestoes.add(c1);

        Compromisso c2 = new Compromisso(
          compromissos.get(i).getTermino() + " em diante",
          compromissos.get(i).getData(),
          "Tempo livre"
        );
        sugestoes.add(c2);
      }
    }

    compromisso = new Compromisso(
      compromissos.get(compromissos.size() - 1).getTermino() + " em diante",
      compromissos.get(compromissos.size() - 1).getData(),
      "Tempo livre"
    );
    sugestoes.add(compromisso);

    df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @SuppressWarnings("unchecked")
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          assert document != null;
          if (document.exists()) {
            List<Compromisso> l = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(document.getData()).size(); ++i) {
              Map<String, Object> x = (Map<String, Object>) document.getData().get(Integer.toString(i));
              assert x != null;
              String
                nome = (String) x.get("nome"),
                data = (String) x.get("data"),
                horario = x.get("inicio") + " até " + x.get("termino");
              Compromisso c = new Compromisso(nome, data, horario);
              l.add(c);
            }

            for (Compromisso i : l) {
              long inicioI = Long.parseLong(
                i.getData().split("/")[2]
              ) * 31536000 + Long.parseLong(
                i.getData().split("/")[1]
              ) * 2592000 + Long.parseLong(
                i.getData().split("/")[0]
              ) * 86400 + Long.parseLong(
                i.getHorario().split(" até ")[0].split(":")[0]
              ) * 3600 + Long.parseLong(
                i.getHorario().split(" até ")[0].split(":")[1]
              ) * 60;

              long terminoI = Long.parseLong(
                i.getData().split("/")[2]
              ) * 31536000 + Long.parseLong(
                i.getData().split("/")[1]
              ) * 2592000 + Long.parseLong(
                i.getData().split("/")[0]
              ) * 86400 + Long.parseLong(
                i.getHorario().split(" até ")[1].split(":")[0]
              ) * 3600 + Long.parseLong(
                i.getHorario().split(" até ")[1].split(":")[1]
              ) * 60;

              for (Compromisso j : sugestoes) {
                String nome = j.getNome();

                if (nome.startsWith("Antes")) {
                  _terminoJ = nome.split(" ")[2];
                } else if (!nome.endsWith("diante")) {
                  _terminoJ = nome.split(" até ")[1];
                } else {
                  _terminoJ = "23:59";
                }

                if (nome.endsWith("diante")) {
                  _inicioJ = nome.split(" ")[0];
                } else if (!nome.startsWith("Antes")) {
                  _inicioJ = nome.split(" até ")[0];
                } else {
                  _inicioJ = "0:0";
                }

                long inicioJ = Long.parseLong(
                  j.getData().split("/")[2]
                ) * 31536000 + Long.parseLong(
                  j.getData().split("/")[1]
                ) * 2592000 + Long.parseLong(
                  j.getData().split("/")[0]
                ) * 86400 + Long.parseLong(
                  _inicioJ.split(":")[0]
                ) * 3600 + Long.parseLong(
                  _inicioJ.split(":")[1]
                ) * 60;

                long terminoJ = Long.parseLong(
                  j.getData().split("/")[2]
                ) * 31536000 + Long.parseLong(
                  j.getData().split("/")[1]
                ) * 2592000 + Long.parseLong(
                  j.getData().split("/")[0]
                ) * 86400 + Long.parseLong(
                  _terminoJ.split(":")[0]
                ) * 3600 + Long.parseLong(
                  _terminoJ.split(":")[1]
                ) * 60;

                Log.i("fora", sugestoes.get(sugestoes.indexOf(j)).toString());
                if ((inicioI > inicioJ && inicioI < terminoJ) || (terminoI > inicioJ && terminoI < terminoJ)) {
                  sugestoes.get(sugestoes.indexOf(j)).setCor("vermelho");
                }

              }
            }

            rv = findViewById(R.id.horarios_disponiveis);
            LinearLayoutManager lm = new LinearLayoutManager(HorariosDisponiveisActivity.this);
            lm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(lm);
            CompromissoAdapter adapter = new CompromissoAdapter(sugestoes, HorariosDisponiveisActivity.this);
            rv.setAdapter(adapter);
          }
        }
      }
    });
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    this.finish();
  }
}
