package com.tcc.reuniapp;

import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class DecoderActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener {
  private QRCodeReaderView qrReaderView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_decoder);

    qrReaderView = findViewById(R.id.qr_reader_view);
    qrReaderView.setOnQRCodeReadListener(this);

    qrReaderView.setAutofocusInterval(500);
  }

  @Override
  public void onQRCodeRead(String text, PointF[] points) {
    Intent intent = new Intent(this, TempoDesejadoActivity.class);
    intent.putExtra("texto", text);
    startActivity(intent);
    this.finish();
  }

  @Override
  protected void onResume() {
    super.onResume();
    qrReaderView.startCamera();
  }

  @Override
  protected void onPause() {
    super.onPause();
    qrReaderView.stopCamera();
  }
}
