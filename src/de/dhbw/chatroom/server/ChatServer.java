package de.dhbw.chatroom.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Dies ist die entfernte Schnittstelle des ChatServers. Sie beschreibt,
 * wie Clients auf das Server-Objekt zugreifen können.
 *
 * TODO: Interface für entfernte Aufrufe anpassen
 * TODO: Weitere Methoden hinzufügen
 */
public interface ChatServer extends Remote {
    public Map<Integer, String> getListOfChatRooms() throws RemoteException;
    public void sendPrivateMessage(String fromUser, String toUser, String text) throws RemoteException;
    public List<PrivateMessage> getPrivateMessages(String forUser) throws RemoteException;
    public ChatRoom getChatRoom(int nr) throws RemoteException;
    public ChatRoom createChatRoom(String name) throws RemoteException;
    public void deleteChatRoom(int nr) throws RemoteException;
}
