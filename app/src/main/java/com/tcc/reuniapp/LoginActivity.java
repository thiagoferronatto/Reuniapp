package com.tcc.reuniapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
  GoogleSignInClient mGoogleSignInClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .build();
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
  }

  @Override
  protected void onStart() {
    super.onStart();
    GoogleSignInAccount conta = GoogleSignIn.getLastSignedInAccount(this);
    if (conta == null) {
      setContentView(R.layout.activity_login);
      SignInButton btnLogin = findViewById(R.id.btnLoginGoogle);
      btnLogin.setOnClickListener(v -> startActivityForResult(mGoogleSignInClient.getSignInIntent(), 1));
    } else {
      startActivity(new Intent(this, AgendaDrawerActivity.class));
      this.finish();
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1) {
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      try {
        GoogleSignInAccount conta = task.getResult(ApiException.class);
        assert conta != null;
        String e = conta.getEmail();
        assert e != null;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> usuario = new HashMap<>();
        usuario.put("email", conta.getEmail());
        usuario.put("nome", conta.getDisplayName());
        db.collection("usuarios").document(e.substring(0, e.indexOf("@"))).set(usuario);
        startActivity(new Intent(this, AgendaDrawerActivity.class));
      } catch (/*Api*/Exception e) {
        // vish...
      }
    }
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    finishAffinity();
  }
}
