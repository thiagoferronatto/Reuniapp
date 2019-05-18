package com.tcc.reuniapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    if (!(conta.getPhotoUrl() == null)) {
      new DownloadImageTask(foto, this).execute(conta.getPhotoUrl().toString());
    }

    Button qr = findViewById(R.id.qr);
    qr.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ContextCompat.checkSelfPermission(
          AgendaDrawerActivity.this, Manifest.permission.CAMERA)
          == PackageManager.PERMISSION_DENIED) {
          ActivityCompat.requestPermissions(AgendaDrawerActivity.this, new String[] {Manifest.permission.CAMERA}, 1804);
        } else {
          startActivity(new Intent(AgendaDrawerActivity.this, DecoderActivity.class));
        }
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    String e = conta.getEmail();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference df = db.collection("compromissos").document(e.substring(0, e.indexOf("@")));

    final AlertDialog carregando = new AlertDialog.Builder(this).setTitle("Aguarde").setMessage("Carregando compromissos").show();

    df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
      @SuppressWarnings("unchecked")
      @Override
      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
          DocumentSnapshot document = task.getResult();
          assert document != null;
          if (document.exists()) {
            List<Compromisso> l = new ArrayList<>();
            for (int i = 0; i < document.getData().size(); ++i) {
              Map<String, Object> x = (Map<String, Object>) document.getData().get(Integer.toString(i));
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
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case 1804: { // Câmera
        if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startActivity(new Intent(this, DecoderActivity.class));
        } else {
          // Permissão à câmera negada
        }
        return;
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
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_info) {
      startActivity(new Intent(this, InfoActivity.class));
    } else if (id == R.id.nav_problema) {
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
      intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"problemas@reuniapp.com"})
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
