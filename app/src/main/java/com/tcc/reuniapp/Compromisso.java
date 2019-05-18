package com.tcc.reuniapp;

import android.support.annotation.NonNull;

class Compromisso {
  private String nome;
  private String data;
  private String horario;
  private String inicio;
  private String termino;
  private String participantes;
  private String cor;

  Compromisso(String nome, String data, String horario) {
    setNome(nome);
    setData(data);
    setHorario(horario);
    setCor("nada");
  }

  Compromisso(String nome, String data, String horario, String participantes) {
    setParticipantes(participantes);
    setNome(nome);
    setData(data);
    setHorario(horario);
    setCor("nada");
  }

  Compromisso(String data, String inicio, String termino, boolean usarConstrutorAlternativo) {
    setNome(inicio + "-" + termino);
    setData(data);
    setHorario(getNome());
    setInicio(inicio);
    setTermino(termino);
    setCor("nada");
  }

  String getParticipantes() {
    return participantes;
  }

  private void setParticipantes(String participantes) {
    this.participantes = participantes;
  }

  String getCor() {
    return cor;
  }

  void setCor(String cor) {
    this.cor = cor;
  }

  String getTermino() {
    return termino;
  }

  private void setTermino(String termino) {
    this.termino = termino;
  }

  String getInicio() {
    return inicio;
  }

  private void setInicio(String inicio) {
    this.inicio = inicio;
  }

  String getNome() {
    return nome;
  }

  private void setNome(String nome) {
    this.nome = nome;
  }

  String getData() {
    return data;
  }

  private void setData(String data) {
    this.data = data;
  }

  String getHorario() {
    return horario;
  }

  private void setHorario(String horario) {
    this.horario = horario;
  }

  @NonNull
  @Override
  public String toString() {
    return getNome() + "; " + getData() + ", " + getHorario() + ", " + getCor();
  }
}
