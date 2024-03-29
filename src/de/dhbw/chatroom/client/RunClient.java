package de.dhbw.chatroom.client;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;

import de.dhbw.chatroom.util.*;

/**
 * Dies ist die Hauptklasse der Clientanwendung. Hier wird zunächst
 * eine entfernte Referenz auf den ChatServer der Serveranwendung
 * besorgt und anschließend das Hauptfenster der Anwendung erzeugt.
 */
public class RunClient {
    public static void main(String[] args) {
        // Java Sicherheitsrichtlinien etablieren
        System.setProperty("java.security.policy", "java.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Hauptfenster anzeigen
        // HINWEIS: Im Konstruktor des MainWindowController wird die Verbindung
        // zum entfernten Chatserver hergestellt.
        MainWindowController mwc = null;

        try {
            mwc = new MainWindowController();
            mwc.show();
        } catch (RemoteException ex) {
        	SmartExceptionsHandling.EnableStackTrace(ex, "ACHTUNG: Haben Sie die rmiregistry gestartet?");        
        } catch (NotBoundException ex) {
        	SmartExceptionsHandling.EnableStackTrace(ex, "ACHTUNG: L�uft auch die Serveranwendung?"); 
        } catch (MalformedURLException ex) {
        	SmartExceptionsHandling.EnableStackTrace(ex, "ACHTUNG: Bitte keinen Unfug als Adresse der rmiregistry eintragen!"); 
        }
    }
}
