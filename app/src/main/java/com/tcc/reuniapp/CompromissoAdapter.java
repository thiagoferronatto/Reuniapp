package com.tcc.reuniapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CompromissoAdapter extends RecyclerView.Adapter<CompromissoAdapter.CompromissoViewHolder> {
  private List<Compromisso> compromissos;
  private Context context;

  CompromissoAdapter(List<Compromisso> compromissos, Context context) {
    this.context = context;
    this.compromissos = compromissos;
  }

  @NonNull
  @Override
  public CompromissoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
    return new CompromissoViewHolder(v);
  }

  @Override
  public void onBindViewHolder(@NonNull CompromissoViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
    viewHolder.nome.setText(compromissos.get(i).getNome());
    viewHolder.data.setText(compromissos.get(i).getData());
    viewHolder.horario.setText(compromissos.get(i).getHorario());
    if (compromissos.get(i).getCor().equals("vermelho")) {
      viewHolder.cardView.setCardBackgroundColor(Color.rgb(255, 100, 100));
    }
    viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (context instanceof HorariosDisponiveisActivity) {
          Intent intent = new Intent(context, CompromissoActivity.class);

          String _inicio, _duracao, _termino;
          _duracao = ((HorariosDisponiveisActivity) context).getIntent().getStringExtra("duracao");

          int duracao = Integer.parseInt(_duracao.split(":")[0]) * 60 + Integer.parseInt(_duracao.split(":")[1]);

          int inicio, termino;
          if (compromissos.get(i).getNome().startsWith("Antes")) {
            termino =
              Integer.parseInt(compromissos.get(i).getNome().split(" ")[2].split(":")[0]) * 60 +
                Integer.parseInt(compromissos.get(i).getNome().split(" ")[2].split(":")[1]);
            inicio = termino - duracao;
          } else if (compromissos.get(i).getNome().endsWith("diante")) {
            inicio =
              Integer.parseInt(compromissos.get(i).getNome().split(" ")[0].split(":")[0]) * 60 +
                Integer.parseInt(compromissos.get(i).getNome().split(" ")[0].split(":")[1]);
            termino = inicio + duracao;
          } else {
            inicio =
              Integer.parseInt(compromissos.get(i).getNome().split(" até ")[0].split(":")[0]) * 60 +
                Integer.parseInt(compromissos.get(i).getNome().split(" até ")[0].split(":")[1]);
            termino = inicio + duracao;
          }
          _inicio = (inicio / 60) + ":" + (inicio % 60);
          _termino = (termino / 60) + ":" + (termino % 60);

          Log.i("inicio", _inicio);
          Log.i("termino", _termino);
          Log.i("data", compromissos.get(i).getData());
          Log.i("usuario", ((HorariosDisponiveisActivity) context).usuario);

          intent.putExtra("data", compromissos.get(i).getData());
          intent.putExtra("inicio", _inicio);
          intent.putExtra("termino", _termino);
          intent.putExtra("usuario", ((HorariosDisponiveisActivity) context).usuario);

          context.startActivity(intent);
          ((HorariosDisponiveisActivity) context).finish();
        } else {
          new AlertDialog.Builder(context)
            .setTitle(compromissos.get(i).getNome())
            .setMessage(
              compromissos.get(i).getData() + System.lineSeparator() +
                compromissos.get(i).getHorario() + System.lineSeparator() +
                compromissos.get(i).getParticipantes()
            ).show();
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return compromissos.size();
  }

  static class CompromissoViewHolder extends RecyclerView.ViewHolder {
    TextView nome, data, horario;
    CardView cardView;

    CompromissoViewHolder(View itemView) {
      super(itemView);
      nome = itemView.findViewById(R.id.nome_item);
      data = itemView.findViewById(R.id.data_item);
      horario = itemView.findViewById(R.id.horario_item);
      cardView = itemView.findViewById(R.id.cardview);
    }
  }
}
