package com.progetto.controllo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.progetto.database.LibreriaDAO;
import com.progetto.entita.ElementoLibreria;
import com.progetto.entita.Utente;

public class HallOfFameControl {
    private final LibreriaDAO libreriaDao;

    public HallOfFameControl(LibreriaDAO libreriaDao) {
        this.libreriaDao = libreriaDao;
    }

    public long calcolaGiochiCompletati(Utente u) {
        List<ElementoLibreria> libreria = libreriaDao.getLibreriaUtenteCompleta(u.getUsername());
        return libreria.stream().filter(ElementoLibreria::isCompletato).count();
    }

    public String calcolaGenerePreferito(Utente u) {
        List<ElementoLibreria> libreria = libreriaDao.getLibreriaUtenteCompleta(u.getUsername());
        if (libreria.isEmpty()) return "Nessuno";

        return libreria.stream()
                .map(el -> el.getVideogioco().getGenere())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Nessuno");
    }
}