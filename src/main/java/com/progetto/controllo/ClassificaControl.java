package com.progetto.controllo;

import java.util.List;
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Utente;

public class ClassificaControl {
    
    private final UtenteDAO utenteDAO;

    public ClassificaControl(UtenteDAO utenteDAO) {
        this.utenteDAO = utenteDAO;
    }

    public List<Utente> ottieniTopPlayers() {
        return utenteDAO.recuperaClassifica();
    }
}