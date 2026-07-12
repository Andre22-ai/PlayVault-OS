package com.progetto.graphiccontrollercli;

import java.util.Scanner;

import com.progetto.controllo.AutenticazioneControl;
import com.progetto.controllo.RegistrazioneControl;
import com.progetto.exceptions.CredenzialiErrateException;
import com.progetto.exceptions.UtenteGiaEsistenteException;
import com.progetto.utils.GestoreLingua;

public class LoginGraphicControllerCLI {

    private final AutenticazioneControl authControl;
    private final RegistrazioneControl regControl;

    private static final String MSG_ERRORE = "[ERRORE] ";
    private static final String MSG_OK = "[OK] ";

    public LoginGraphicControllerCLI(AutenticazioneControl authControl, RegistrazioneControl regControl) {
        this.authControl = authControl;
        this.regControl = regControl;
    }

    public void avviaMenuAccesso(Scanner scanner) {
        System.out.println("\n--- " + GestoreLingua.getIstanza().get("cli.login.menu.header") + " ---");
        System.out.println(GestoreLingua.getIstanza().get("cli.login.menu.login"));
        System.out.println(GestoreLingua.getIstanza().get("cli.login.menu.register"));
        System.out.println(GestoreLingua.getIstanza().get("cli.login.menu.exit"));
        System.out.print(GestoreLingua.getIstanza().get("cli.login.prompt") + " ");

        String scelta = scanner.nextLine().trim();

        switch (scelta) {
            case "1":
                eseguiLogin(scanner);
                break;
            case "2":
                eseguiRegistrazione(scanner);
                break;
            case "3":
                System.out.println(GestoreLingua.getIstanza().get("cli.login.exit_msg"));
                System.exit(0);
                break;
            default:
                System.out.println(GestoreLingua.getIstanza().get("cli.login.error.invalid_choice"));
        }
    }

    private void eseguiLogin(Scanner scanner) {
        System.out.print(GestoreLingua.getIstanza().get("cli.login.username") + " ");
        String username = scanner.nextLine().trim();
        System.out.print(GestoreLingua.getIstanza().get("cli.login.password") + " ");
        String password = scanner.nextLine().trim();

        System.out.println(GestoreLingua.getIstanza().get("cli.login.verifying"));
        try {
            authControl.eseguiLogin(username, password);
            System.out.println(MSG_OK + GestoreLingua.getIstanza().get("cli.login.success") + " " + username.toUpperCase());
        } catch (CredenzialiErrateException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }

    private void eseguiRegistrazione(Scanner scanner) {
        System.out.println("\n--- " + GestoreLingua.getIstanza().get("cli.register.header") + " ---");
        System.out.print(GestoreLingua.getIstanza().get("cli.login.username") + " ");
        String user = scanner.nextLine().trim();
        System.out.print(GestoreLingua.getIstanza().get("cli.login.password") + " ");
        String pass = scanner.nextLine().trim();
        System.out.print(GestoreLingua.getIstanza().get("cli.register.confirm_password") + " ");
        String confPass = scanner.nextLine().trim();

        try {
            regControl.registraNuovoUtente(user, pass, confPass);
            System.out.println(MSG_OK + GestoreLingua.getIstanza().get("cli.register.success"));
        } catch (UtenteGiaEsistenteException e) {
            System.out.println(MSG_ERRORE + e.getMessage());
        }
    }
}