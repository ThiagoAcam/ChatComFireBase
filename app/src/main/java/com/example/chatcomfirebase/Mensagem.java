package com.example.chatcomfirebase;

import java.util.Date;

 public class  Mensagem implements Comparable<Mensagem> {
    private String texto;
    private Date data;
    private String email;
    private boolean dadosGPS = false;


    public Mensagem() {
    }

    public Mensagem(String texto, Date data, String email, boolean dadosGPS) {
        this.texto = texto;
        this.data = data;
        this.email = email;
        this.dadosGPS = dadosGPS;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public void setEmail(String email) {
        this.email = email;
    }

     public void setDadosGPS(boolean dadosGPS) {
         this.dadosGPS = dadosGPS;
     }

     public boolean getDadosGPS() {
         return dadosGPS;
     }

    public String getTexto() {
        return texto;
    }

    public Date getData() {
        return data;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int compareTo(Mensagem mensagem) {
        return this.data.compareTo(mensagem.data);
    }
}
