package com.progetto.controllo;

// FIX 1: Importiamo le interfacce, non le classi MySQL!
import com.progetto.database.RecensioneDAO;
import com.progetto.database.UtenteDAO;
import com.progetto.entita.Recensione;
import com.progetto.entita.Sessione;
import com.progetto.entita.Utente;

public class RecensioneControl {

    // FIX SonarCloud: Usiamo interfacce e aggiungiamo "final"
    private final RecensioneDAO recensioneDAO;
    private final UtenteDAO utenteDAO;

    // FIX 2: Dependency Injection! Il Control riceve i database dall'esterno
    public RecensioneControl(RecensioneDAO recensioneDAO, UtenteDAO utenteDAO) {
        this.recensioneDAO = recensioneDAO;
        this.utenteDAO = utenteDAO;
    }

    public String elaboraRecensione(Recensione recensione) {
        // 1. Tentiamo di salvare la recensione nel DB
        boolean salvata = recensioneDAO.salvaRecensione(recensione);
        
        if (!salvata) {
            return "ALREADY_REVIEWED"; // Il DB ci ha bloccato (vincolo UNIQUE)
        }

        // 2. Se salvata con successo, eroghiamo la ricompensa di 15 crediti!
        boolean accreditati = utenteDAO.aggiungiCreditiAlDB(recensione.getUsername(), 15);
        
        if (accreditati) {
            // Aggiorniamo anche la RAM, così la Dashboard aggiorna subito il numerino!
            Utente corrente = Sessione.getIstanza().getUtenteCorrente();
            corrente.setCrediti(corrente.getCrediti() + 15);
            return "SUCCESS";
        } else {
            return "SYSTEM_ERROR";
        }
    }

    // Metodo per estrarre le recensioni e mostrarle nel negozio
    public java.util.List<Recensione> ottieniRecensioniGioco(int idGioco) {
        return recensioneDAO.recuperaRecensioniPerGioco(idGioco);
    }

    // Recupera lo storico personale dell'utente
    public java.util.List<Recensione> ottieniRecensioniPersonali(String username) {
        return recensioneDAO.recuperaRecensioniUtente(username);
    }

    public boolean modificaRecensionePersonale(Recensione r) {
        return recensioneDAO.aggiornaRecensione(r);
    }

    public boolean eliminaRecensionePersonale(String username, int idGioco) {
        return recensioneDAO.eliminaRecensione(username, idGioco);
    }
}