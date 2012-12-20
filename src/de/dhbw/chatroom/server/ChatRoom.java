package de.dhbw.chatroom.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Dieses Interface beschreibt einen auf dem Server vorhandenen
 * ChatRoom. Beachten Sie, dass die ChatRooms nicht in der RMI Registry
 * eingetragen sind. Stattdessen werden sie von einem ChatServer-Objekt
 * erst zur Laufzeit erzeugt. Trotzdem handelt es sich um entfernte
 * Objekte, denn nur wenn die ChatRooms auf dem Server liegen (und nicht
 * serialisiert zum Client übertragen werden), können mehrere Benutzer
 * miteinander chatten.
 *
 * TODO: Interface für entfernte Aufrufe anpassen
 * TODO: Weitere Methoden hinzufügen
 */
public interface ChatRoom extends Remote {
    public int getId() throws RemoteException;
    public String getName() throws RemoteException;
    public int enterRoom(String username) throws RemoteException;
    public void leaveRoom(String username) throws RemoteException;
    public void postMessage(String username, String message) throws RemoteException;
    public Map<Integer, String> getMessages(int lastKnownMessage) throws RemoteException;
    public List<String> getUsers() throws RemoteException;
}
