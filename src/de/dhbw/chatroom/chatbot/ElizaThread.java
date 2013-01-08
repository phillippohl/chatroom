package de.dhbw.chatroom.chatbot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.rmi.RemoteException;

import de.dhbw.chatroom.server.ChatRoom;
import de.dhbw.chatroom.util.SmartExceptionsHandling;
/**
 * Nachdem das Hauptprogramm die Verbindung zum Chatserver hergestellt und
 * einen Raum betreten hat, übergibt es die Kontrolle an diesen Thread. Er
 * überwacht den Chatroom und postet regelmäßig neue Einträge darin.
 */
public class ElizaThread extends Thread {
    private Eliza eliza;
    private String username;
    private String ownPrefix;
    private ChatRoom room;
    private int lastKnownMessage;
    private boolean quit = false;
    
    private List<String> users = new ArrayList<String>();
    Map<Integer, String> messages = new HashMap<Integer, String>();

    /**
     * Der Konstruktor des Threads. Ãœber seine Parameter wird der Thread
     * konfiguriert. Wenn Sie etwas Spaß am Programmieren haben, programmieren
     * Sie doch einen Multibot, der mehreren Räumen gleichzeitig beitreten
     * kann. Sie müssten nur das Hauptprogramm ändern, so dass es mehrere
     * Threads erzeugt.
     */
    public ElizaThread(Eliza eliza, String username, ChatRoom room, int lastMessage) {
        this.eliza = eliza;
        this.username = username;
        this.room = room;
        this.lastKnownMessage = lastMessage;

        this.ownPrefix = "[" + this.username + "]";
    }

    /**
     * Diese Methode setzt ein Kennzeichen, dass der Thread zuende laufen
     * soll.
     */
    synchronized public void finish() {
        this.quit = true;
    }

    /**
     * Die eigentliche Logik des Threads. Hier wird der Chatroom überwacht
     * und mit Hilfe des Eliza-Objekts Einträge darin gepostet.
     */
    public void run() {  	
    	boolean newMessage = false;
    	boolean sentGreeting = false;
    	int counter = 0;
    	String message= "";
    	String response = "";

        // TODO: Chatbot zuende programmieren
    	while (!quit){   		
    		try {
    			Thread.sleep(1000);
    			users = room.getUsers();
    			
    			this.messages = room.getMessages(this.lastKnownMessage);
    			
    			if (this.lastKnownMessage < this.lastKnownMessage + this.messages.size()){
    				this.lastKnownMessage += this.messages.size();
    				newMessage = true;
    			}
    			
    			if (users.isEmpty()){
    				continue;
    			}
    			else{
    				if (counter == 0 && !sentGreeting){
    					room.postMessage(username, eliza.getFirstMessage(username));
    					sentGreeting = true;
    				}
    				 				
    				if(newMessage == false){
    					counter++;
    				}
    				else{
        	   			message = this.messages.get(this.lastKnownMessage);
        	   			System.out.println(this.messages.toString());
            			if (!message.startsWith(this.ownPrefix) && message.startsWith("[")){
            				int split = message.indexOf("]") + 2;
            				String text = message.substring(split);
            				response = eliza.talk(text);
            				room.postMessage(username, response);
            			}
            			newMessage = false;
    				}
    				
    				if(counter > 60){
    					room.postMessage(username, eliza.getIdleMessage());
    					counter = 0;
    				}
    			}   			   			       		
			} catch (InterruptedException ie) {
			} catch (RemoteException re) {
				SmartExceptionsHandling.EnableStackTrace(re, "ACHTUNG: Haben Sie die rmiregistry gestartet?");
			}
    	}
    }
}
