package de.dhbw.chatroom.chatbot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.util.Map;

import de.dhbw.chatroom.server.ChatRoom;
import de.dhbw.chatroom.server.ChatServer;

/**
 * Dieses Programm implementiert einen einfachen Chatbot, der ähnlich wie das
 * DOCTOR Skript von Joseph Weizenbaums berühmter Eliza funktioniert. Aufgrund
 * der Einfachheit des Algorithmus müssen Sie sich mit Eliza auf englisch
 * unterhalten.
 */
public class RunChatbot {
    public static void main(String[] args) throws Exception {
        // Programmstart
        System.out.println("Eliza Chatbot");
        System.out.println("=============");
        System.out.println();

        System.setProperty("java.security.policy", "java.policy");

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        BufferedReader fromKeyboard = new BufferedReader(new InputStreamReader(System.in));

        // Verbindung zum Chatserver herstellen
        System.out.print("Adresse der rmiregistry: ");
        String namingHost = fromKeyboard.readLine();
        String objectName = "ChatServer";

        if (namingHost != null && ! namingHost.equals("")) {
            objectName = "rmi://" + namingHost + "/" + objectName;
        }

        // TODO: ChatServer-Objekt aus dem Namensdienst auslesen und in der
        // Variable server speichern. Den hierfür nötigen Namen finden Sie
        // in der Variable objectName.
        ChatServer server = (ChatServer) Naming.lookup(objectName);

        System.out.print("Benutzername des Bots: ");
        String username = fromKeyboard.readLine();

        if (username == null || username.equals("")) {
            username = "Eliza Weizenbaum";
        }

        System.out.println();

        // Einem Chatroom beitreten
        System.out.println("Verfügbare Chatrooms");
        System.out.println("--------------------");
        System.out.println("");

        // TODO: Liste der Chat Rooms ermitteln und in einer Liste anzeigen
        // Dabei muss zu jedem Raum auch die Nummer sichtbar sein.
        for (int i = 1; i <= server.getListOfChatRooms().size(); i++){
        	System.out.print("["+i+"] ");
        	System.out.println(server.getChatRoom(i-1).getName());
        }

        System.out.println();
        int roomnr = -1;

        while (roomnr < 0) {
            try {
                System.out.print("Raumnummer: ");
                roomnr = new Integer(fromKeyboard.readLine());
                roomnr -= 1;
            } catch (Exception ex) {
                roomnr = -1;
            }
        }

        System.out.println();

        ChatRoom room = server.getChatRoom(roomnr);

        if (room == null ) {
            System.out.println("Unbekannter Raum!");
            return;
        }

        System.out.println("Betrete " + room.getName());
        System.out.println();

        int lastMessage = room.enterRoom(username);

        // TODO: Chatbot starten
        ElizaThread thread = new ElizaThread(new Eliza(), username, room, lastMessage);
        thread.start();
        System.out.println("Drücken Sie [ENTER], um das Programm zu beenden!");
        fromKeyboard.readLine();

        thread.finish();

        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException ex) {
                // Kann passieren
            }
        }

        room.leaveRoom(username);
    }
}
