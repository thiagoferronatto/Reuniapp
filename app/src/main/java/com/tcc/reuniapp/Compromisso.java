package com.tcc.reuniapp;

import android.support.annotation.NonNull;

class Compromisso {
  private String nome;
  private String data;
  private String horario;
  private String inicio;
  private String termino;

  Compromisso(String nome, String data, String horario) {
    setNome(nome);
    setData(data);
    setHorario(horario);
    setCor("nada");
  }

  public Compromisso(String nome, String data, String horario, String participantes) {
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

  private String participantes;

  String getCor() {
    return cor;
  }

  void setCor(String cor) {
    this.cor = cor;
  }

  private String cor;

  String getTermino() {
    return termino;
  }

  void setTermino(String termino) {
    this.termino = termino;
  }

  String getInicio() {
    return inicio;
  }

  void setInicio(String inicio) {
    this.inicio = inicio;
  }

  String getNome() {
    return nome;
  }

  void setNome(String nome) {
    this.nome = nome;
  }

  String getData() {
    return data;
  }

  void setData(String data) {
    this.data = data;
  }

  String getHorario() {
    return horario;
  }

  void setHorario(String horario) {
    this.horario = horario;
  }

  @NonNull
  @Override
  public String toString() {
    return getNome() + "; " + getData() + ", " + getHorario() + ", " + getCor();
  }
}
