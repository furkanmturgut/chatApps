package com.kunai.chatappproject;

public class MesajModel {
    String alici, mesaj,imageUrl;

    public MesajModel() {
    }

    public MesajModel(String alici, String mesaj,String imageUrl) {
        this.alici = alici;
        this.mesaj = mesaj;
        this.imageUrl = imageUrl;
    }

    public String getAlici() {
        return alici;
    }

    public void setAlici(String alici) {
        this.alici = alici;
    }

    public String getMesaj() {
        return mesaj;
    }

    public void setMesaj(String mesaj) {
        this.mesaj = mesaj;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "MesajModel{" +
                "alici='" + alici + '\'' +
                ", mesaj='" + mesaj + '\'' +
                '}';
    }
}