package com.tcc.reuniapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
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
          Log.i("instanceof", "HorariosDisponiveisActivity");
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
}
