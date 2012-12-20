package de.dhbw.chatroom.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse implementiert das ChatServer Interface. Darum wird ein
 * Objekt dieser Klasse in der RMI Registry zum allgemeinen Zugriff
 * hinterlegt.
 *
 * TODO: Klasse für entfernte Aufrufe anpassen
 */
public class ChatServerService extends UnicastRemoteObject implements ChatServer{
    private Map<Integer, ChatRoom> chatRooms = new HashMap<Integer, ChatRoom>();
    private int previousId = -1;

    private Map<String, List<PrivateMessage>> privateMessages = new HashMap<String, List<PrivateMessage>>();

    /**
     * Diesen Konstruktor brauchen wir für RMI.
     * TODO: Hier fehlt etwas
     */
    public ChatServerService() throws RemoteException{
        // Hier gibt es nichts zu tun
    }

    /**
     * Gibt eine Liste der vorhandenen ChatRooms zurück. Dabei hat jeder
     * ChatRoom eine eindeutige Raumnummer und einen Namen. Die Nummer
     * wird benötigt, um einem Raum beizutreten (Methode getChatRoom)
     * usw.
     */
    public Map<Integer, String> getListOfChatRooms() throws RemoteException{
        Map<Integer, String> rooms = new HashMap<Integer, String>();

        for (ChatRoom room : this.chatRooms.values()) {
            rooms.put(room.getId(), room.getName());
        }

        return rooms;
    }

    // TODO: Ausprogrammieren der übrigen Methoden, die vom
    // Interface vorgeschrieben werden

    /**
     * Sendet eine private Nachricht an einen anderen Benutzer.
     */
    public synchronized void sendPrivateMessage(String fromUser, String toUser, String text) throws RemoteException {
        List<PrivateMessage> queue = this.getMessageQueueForUser(toUser);
        PrivateMessage message = new PrivateMessageImpl(fromUser, toUser, text);
        queue.add(message);
    }

    /**
     * Ruft alle noch nicht abgerufenen, privaten Nachrichten eines
     * Benutzers vom Server ab. Falls keine Nachrichten vorliegen,
     * wird null zurückgeliefert.
     */
    public synchronized List<PrivateMessage> getPrivateMessages(String forUser) throws RemoteException {
        List<PrivateMessage> queue = this.privateMessages.get(forUser);
        this.privateMessages.remove(forUser);
        return queue;
    }

    /**
     * Erzeugt eine neue Nummer für einen ChatRoom. Da die Methode
     * synchronized ist, kann sie nur von einem Thread gleichzeitig
     * aufgerufen werden. Wenn der Aufrufer der Methode dann immer mit
     * der zurückgegebenen ID arbeitet und nicht mit der Instanzvariable
     * previousID, werden somit Race Conditions verhindert.
     */
    private synchronized int getNewId() throws RemoteException {
        return ++this.previousId;
    }

    /**
     * Diese Methode ist ebenfalls synchronized, um Race Conditions
     * zu verhindern. Sie schaut nach, ob es für den gegebenen Benutzer
     * eine Queue für private Nachrichten gibt. Falls nein, wird eine
     * erzeugt.
     */
    private synchronized List<PrivateMessage> getMessageQueueForUser(String username) throws RemoteException {
        List<PrivateMessage> queue = this.privateMessages.get(username);

        if (queue == null) {
            queue = new ArrayList<PrivateMessage>();
            this.privateMessages.put(username, queue);
        }

        return queue;
    }
    
    public ChatRoom getChatRoom(int nr) throws RemoteException {
    	return this.chatRooms.get(nr);
    }
    
    public ChatRoom createChatRoom(String name) throws RemoteException {
    	int roomNr;
    	ChatRoomService result;
    	for (ChatRoom i:this.chatRooms.values()){
    		if (i.getName() == name){
    			return null;
    		}
    	}
    	
    	roomNr = getNewId();
    	result = new ChatRoomService(roomNr, name);
    	this.chatRooms.put(roomNr, result);
    	return result;
    }
    
    public void deleteChatRoom(int nr) throws RemoteException {
    	if(chatRooms.containsKey(nr)){
    		if(chatRooms.get(nr).getUsers().isEmpty()){
    			chatRooms.remove(nr);
    		}
    	}   			
    }
}
