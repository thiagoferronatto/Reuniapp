package com.tcc.reuniapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class AgendaDrawerActivity extends AppCompatActivity
  implements NavigationView.OnNavigationItemSelectedListener {
  GoogleSignInClient mGoogleSignInClient;
  GoogleSignInAccount conta;

  List<Compromisso> compromissos;

  RecyclerView rv;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_agenda_drawer);

    android.support.v7.widget.Toolbar toolbarAgenda = findViewById(R.id.toolbar_agenda);
    toolbarAgenda.setTitle("Agenda");
    setSupportActionBar(toolbarAgenda);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .build();
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    conta = GoogleSignIn.getLastSignedInAccount(this);


    compromissos = new ArrayList<>();


    // loading alert


    Button criarCompromisso = findViewById(R.id.btn_novo_compromisso);
    criarCompromisso.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startActivity(new Intent(AgendaDrawerActivity.this, CompromissoActivity.class));
      }
    });

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    NavigationView navigationView = findViewById(R.id.nav_view);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbarAgenda, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();
    navigationView.setNavigationItemSelectedListener(this);

    View headerView = navigationView.getHeaderView(0);

    TextView nome = headerView.findViewById(R.id.nome_conta);
    nome.setText(conta.getDisplayName());

    TextView email = headerView.findViewById(R.id.email_conta);
    email.setText(conta.getEmail());

    ImageView foto = headerView.findViewById(R.id.imagem_conta);

    ConnectivityManager cm =
      (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null &&
      activeNetwork.isConnectedOrConnecting();

    if (isConnected && conta.getPhotoUrl() != null) {
      new DownloadImageTask(foto, this).execute(conta.getPhotoUrl().toString());
    }

    Button qr = findViewById(R.id.qr);
    qr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(
          AgendaDrawerActivity.this, Manifest.permission.CAMERA)
          == PackageManager.PERMISSION_DENIED) {
          ActivityCompat.requestPermissions(AgendaDrawerActivity.this, new String[]{Manifest.permission.CAMERA}, 1804);
        } else {
          startActivity(new Intent(AgendaDrawerActivity.this, DecoderActivity.class));
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    final String e = conta.getEmail();

    final FirebaseFirestore db = FirebaseFirestore.getInstance();

    assert e != null;
    DocumentReference df = db.collection("compromissos").document(e.substring(0, e.indexOf("@")));

    final AlertDialog carregando = new AlertDialog.Builder(this)
      .setTitle("Aguarde")
      .setMessage("Carregando compromissos")
      .show();

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
                horario = x.get("inicio") + " até " + x.get("termino"),
                participantes = "Com: " + x.get("participantes");
              Compromisso c = new Compromisso(nome, data, horario, participantes);
              l.add(c);
            }
            compromissos = l;

            Collections.sort(l, new Comparator<Compromisso>() {
              @Override
              public int compare(Compromisso o1, Compromisso o2) {
                long _t1 = Long.parseLong(
                  o1.getData().split("/")[2]
                ) * 31536000 + Long.parseLong(
                  o1.getData().split("/")[1]
                ) * 2592000 + Long.parseLong(
                  o1.getData().split("/")[0]
                ) * 86400 + Long.parseLong(
                  o1.getHorario().split(" até ")[0].split(":")[0]
                ) * 3600 + Long.parseLong(
                  o1.getHorario().split(" até ")[0].split(":")[1]
                ) * 60;

                String t1 = Long.toString(_t1);

                long _t2 = Long.parseLong(
                  o2.getData().split("/")[2]
                ) * 31536000 + Long.parseLong(
                  o2.getData().split("/")[1]
                ) * 2592000 + Long.parseLong(
                  o2.getData().split("/")[0]
                ) * 86400 + Long.parseLong(
                  o2.getHorario().split(" até ")[0].split(":")[0]
                ) * 3600 + Long.parseLong(
                  o2.getHorario().split(" até ")[0].split(":")[1]
                ) * 60;

                String t2 = Long.toString(_t2);

                return t1.compareToIgnoreCase(t2);
              }
            });

            rv = findViewById(R.id.lista_usuarios);
            LinearLayoutManager lm = new LinearLayoutManager(AgendaDrawerActivity.this);
            lm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(lm);
            CompromissoAdapter adapter = new CompromissoAdapter(compromissos, AgendaDrawerActivity.this);
            rv.setAdapter(adapter);
          }
        }

        carregando.hide();
        carregando.dismiss();
      }
    });

    String email = conta.getEmail();

    final DocumentReference docRef =
      db.collection("compromissos").document(email.substring(0, email.indexOf("@")));

    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
      @Override
      public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
        final AlertDialog carregando = new AlertDialog.Builder(AgendaDrawerActivity.this)
          .setTitle("Aguarde")
          .setMessage("Carregando compromissos")
          .show();

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
          @Override
          public void onSuccess(DocumentSnapshot document) {
            assert document != null;
            if (document.exists()) {
              List<Compromisso> l = new ArrayList<>();
              for (int i = 0; i < Objects.requireNonNull(document.getData()).size(); ++i) {
                Map<String, Object> x = (Map<String, Object>) document.getData().get(Integer.toString(i));
                assert x != null;
                String
                  nome = (String) x.get("nome"),
                  data = (String) x.get("data"),
                  horario = x.get("inicio") + " até " + x.get("termino"),
                  participantes = "Com: " + x.get("participantes");
                Compromisso c = new Compromisso(nome, data, horario, participantes);
                l.add(c);
              }
              compromissos = l;

              Collections.sort(l, new Comparator<Compromisso>() {
                @Override
                public int compare(Compromisso o1, Compromisso o2) {
                  long _t1 = Long.parseLong(
                    o1.getData().split("/")[2]
                  ) * 31536000 + Long.parseLong(
                    o1.getData().split("/")[1]
                  ) * 2592000 + Long.parseLong(
                    o1.getData().split("/")[0]
                  ) * 86400 + Long.parseLong(
                    o1.getHorario().split(" até ")[0].split(":")[0]
                  ) * 3600 + Long.parseLong(
                    o1.getHorario().split(" até ")[0].split(":")[1]
                  ) * 60;

                  String t1 = Long.toString(_t1);

                  long _t2 = Long.parseLong(
                    o2.getData().split("/")[2]
                  ) * 31536000 + Long.parseLong(
                    o2.getData().split("/")[1]
                  ) * 2592000 + Long.parseLong(
                    o2.getData().split("/")[0]
                  ) * 86400 + Long.parseLong(
                    o2.getHorario().split(" até ")[0].split(":")[0]
                  ) * 3600 + Long.parseLong(
                    o2.getHorario().split(" até ")[0].split(":")[1]
                  ) * 60;

                  String t2 = Long.toString(_t2);

                  return t1.compareToIgnoreCase(t2);
                }
              });

              rv = findViewById(R.id.lista_usuarios);
              LinearLayoutManager lm = new LinearLayoutManager(AgendaDrawerActivity.this);
              lm.setOrientation(LinearLayoutManager.VERTICAL);
              rv.setLayoutManager(lm);
              CompromissoAdapter adapter = new CompromissoAdapter(compromissos, AgendaDrawerActivity.this);
              rv.setAdapter(adapter);

              carregando.hide();
              carregando.dismiss();
            }
          }
        });
      }
    });

    db.collection("compromissos").addSnapshotListener(new EventListener<QuerySnapshot>() {
      @Override
      public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException ex) {
        if (queryDocumentSnapshots != null) {
          DocumentReference df = db.collection("compromissos").document(e.substring(0, e.indexOf("@")));


          df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

            }
          });
        }
      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // Câmera
    if (requestCode == 1804) {
      if (grantResults.length > 0
        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startActivity(new Intent(this, DecoderActivity.class));
      }
    }
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.agenda_drawer, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_agenda_sair) {
      sair();
      return true;
    } else if (item.getItemId() == R.id.menu_agenda_atualizar) {
      startActivity(new Intent(this, AgendaDrawerActivity.class));
      this.finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_problema) {
      final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
      intent.setType("text/plain");
      final PackageManager pm = getPackageManager();
      final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
      ResolveInfo best = null;
      for (final ResolveInfo info : matches)
        if (info.activityInfo.packageName.endsWith(".gm") ||
          info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
      if (best != null)
        intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
      intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"reuniapp@outlook.com.br"})
        .putExtra(Intent.EXTRA_SUBJECT, "Problema com o Reuniapp")
        .putExtra(Intent.EXTRA_TEXT, "\n\n--\nAtenciosamente, " + conta.getGivenName() + ".");
      startActivity(intent);
    } else if (id == R.id.nav_meu_qr) {
      startActivity(new Intent(this, QRActivity.class));
    }

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void sair() {
    mGoogleSignInClient.signOut()
      .addOnCompleteListener(this, new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          startActivity(new Intent(AgendaDrawerActivity.this, LoginActivity.class));
        }
      });
  }
}
