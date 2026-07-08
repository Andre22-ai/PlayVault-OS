package com.progetto.entita;

public class Recensione {
    private String username;
    private int idGioco;
    private int voto;
    private String commento;
    
    private String nomeGioco; 

    public Recensione(String username, int idGioco, int voto, String commento) {
        this.username = username;
        this.idGioco = idGioco;
        this.voto = voto;
        this.commento = commento;
    }

    public String getUsername() { return username; }
    public int getIdGioco() { return idGioco; }
    public int getVoto() { return voto; }
    public String getCommento() { return commento; }

    public String getNomeGioco() { return nomeGioco; }
    public void setNomeGioco(String nomeGioco) { this.nomeGioco = nomeGioco; }
    public void setVoto(int voto) {
        this.voto = voto;
    }

    public void setCommento(String commento) {
        this.commento = commento;
    }
}