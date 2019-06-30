package com.tcc.reuniapp;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRActivity extends AppCompatActivity {
  String TAG = "GenerateQRCode";
  ImageView qrImage;
  String inputValue;
  Bitmap bitmap;
  QRGEncoder qrgEncoder;
  GoogleSignInAccount conta;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qr);

    qrImage = findViewById(R.id.img_qr);

    conta = GoogleSignIn.getLastSignedInAccount(this);

    assert conta != null;
    String e = conta.getEmail();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    assert e != null;
    DocumentReference df = db.collection("compromissos").document(e.substring(0, e.indexOf("@")));

    final AlertDialog carregando = new AlertDialog.Builder(this)
      .setTitle("Aguarde")
      .setMessage("Gerando QR")
      .setCancelable(false)
      .show();

    df.get().addOnCompleteListener(task -> {
      if (task.isSuccessful()) {
        DocumentSnapshot document = task.getResult();
        assert document != null;
        if (document.exists() && document.getData().size() > 0) {
          List<Compromisso> l = new ArrayList<>();
          for (int i = 0; i < Objects.requireNonNull(document.getData()).size(); ++i) {
            @SuppressWarnings("unchecked")
            Map<String, Object> x = (Map<String, Object>) document.getData().get(Integer.toString(i));
            assert x != null;
            String
              nome = (String) x.get("nome"),
              data = (String) x.get("data"),
              horario = x.get("inicio") + "-" + x.get("termino");
            Compromisso c = new Compromisso(nome, data, horario);
            l.add(c);
          }

          StringBuilder horarios = new StringBuilder();
          for (Compromisso i : l) {
            horarios.append(i.getData()).append("#");
            horarios.append(i.getHorario());
            if (!(l.indexOf(i) == l.size() - 1))
              horarios.append(";");
          }

          horarios.append(";").append(conta.getDisplayName()).append(";").append(conta.getEmail());

          inputValue = horarios.toString();
          if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
              inputValue, null,
              QRGContents.Type.TEXT,
              smallerDimension);
            try {
              bitmap = qrgEncoder.encodeAsBitmap();
              qrImage.setImageBitmap(bitmap);
            } catch (WriterException e1) {
              Log.v(TAG, e1.toString());
            }
          }
        } else {
          TextView textoQR = findViewById(R.id.texto_qr);
          textoQR.setText("Crie compromissos para ter um QR!\n\n(O código acima não é válido)");
        }
      }
      carregando.hide();
      carregando.dismiss();
    });
  }
}
