package com.example.chatcomfirebase;

import java.util.Date;

 public class  Mensagem implements Comparable<Mensagem> {
    private String texto;
    private Date data;
    private String email;
    private boolean dadosGPS = false;
    private boolean isImage = false;


    public Mensagem() {
    }

    public Mensagem(String texto, Date data, String email, boolean dadosGPS, boolean isImage) {
        this.texto = texto;
        this.data = data;
        this.email = email;
        this.dadosGPS = dadosGPS;
        this.isImage = isImage;
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

     public void setIsImage(boolean isImage) {
         this.isImage = isImage;
     }

     public void setDadosGPS(boolean dadosGPS) {
         this.dadosGPS = dadosGPS;
     }

     public boolean getIsImage() {
         return isImage;
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
        return this.data.compareTo(mensagem.data) * -1;
    }
}
