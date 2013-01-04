package de.dhbw.chatroom.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * Diese Klasse implementiert das ChatRoom Interface. Obwohl es sich
 * hier um entfernte Objekte handelt, werden diese nicht in der RMI
 * Registry abgelegt. Stattdessen erzeugt der ChatServer die ChatRooms
 * erst zur Laufzeit.
 *
 * TODO: Klasse für entfernte Aufrufe anpassen
 */
public class ChatRoomService extends UnicastRemoteObject implements ChatRoom{
    private int id;
    private String name;

    private Map<Integer, String> messages = new HashMap<Integer, String>();
    private volatile int previousId = -1;

    private List<String> users = new ArrayList<String>();

    /**
     * Diese Konstruktor müssen wir für RMI immer anlegen.
     * TODO: Hier fehlt etwas
     */
    public ChatRoomService() throws RemoteException {
        // Hier gibt es nichts zu tun
    }

    /**
     * Diesen Konstuktor verwenden wir wirklich.
     */
    public ChatRoomService(int id, String name) throws RemoteException {
        this.id = id;
        this.name = name;
    }

    public int getId() throws RemoteException { return this.id; }
    public String getName() throws RemoteException { return this.name; }

    // TODO: Ausprogrammieren der übrigen Methoden, die vom
    // Interface vorgeschrieben werden

    /**
     * Erzeugt eine neue Nummer für eine Nachricht. Da die Methode
     * synchronized ist, kann sie nur von einem Thread gleichzeitig
     * aufgerufen werden. Wenn der Aufrufer der Methode dann immer mit
     * der zurückgegebenen ID arbeitet und nicht mit der Instanzvariable
     * previousID, werden somit Race Conditions verhindert.
     */
    private synchronized int getNewId() throws RemoteException {
        return ++this.previousId;
    }

    /**
     * Fügt eine gepostete Nachricht dem Raum hinzu
     */
    private synchronized int addMessage(String message) throws RemoteException {
        int newId = this.getNewId();
        this.messages.put(newId, message);
        return newId;
    }
    
    public int enterRoom(String username) throws RemoteException{
    	if(this.users.contains(username) == false){
    		this.users.add(username);
    	}
    	
    	int mId = this.addMessage(username + " betritt den Saal " + this.name);
    	return (mId-1);
    }
    
    public void leaveRoom(String username) throws RemoteException{
    	if(this.users.contains(username)){
    		this.users.remove(username);
    	}
    	this.addMessage(username + " hat den Chat verlassen");   	
    }
    
    public void postMessage(String username, String message) throws RemoteException{
    	this.addMessage("[" + username + "] " + message); 
    }
    
    public Map<Integer, String> getMessages(int lastKnownMessage) throws RemoteException{
    	Map<Integer, String> messages = new HashMap<Integer, String>();
    	int id =  this.previousId;
    	if(lastKnownMessage >= -1){
    		for (int i = lastKnownMessage + 1; i <= id; i++){
    			messages.put(i, this.messages.get(i));
    		}
    	}
    	return messages;
    }
    
    public List<String> getUsers() throws RemoteException{
    	return this.users;
    }
}
