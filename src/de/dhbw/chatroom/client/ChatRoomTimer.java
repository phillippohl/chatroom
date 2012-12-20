package de.dhbw.chatroom.client;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import de.dhbw.chatroom.server.ChatRoom;
import de.dhbw.chatroom.server.ChatServer;
import de.dhbw.chatroom.server.PrivateMessage;

/**
 * Swing Timer, der alle Sekunde den akuellen ChatRoom nach neuen
 * Nachrichten abfragt und diese anzeigt.
 */
public class ChatRoomTimer implements ActionListener {
    ChatServer server;
    String username;
    ChatRoom room;
    MainWindow wnd;
    volatile int msgNr;

    public ChatRoomTimer(ChatServer server, String username, ChatRoom room, int msgNr, MainWindow wnd) {
        this.server = server;
        this.username = username;
        this.room = room;
        this.msgNr = msgNr;
        this.wnd = wnd;

        this.wnd.getChatTextArea().setText("");
    }

    public void actionPerformed(ActionEvent evt) {
        // Nachrichten des Chatrooms anzeigen
        Map<Integer, String> messages;

        try {
            messages = this.room.getMessages(this.msgNr);

            if (messages == null) return;

            for (int nr : messages.keySet()) {
                this.msgNr = nr;
                this.wnd.getChatTextArea().append(messages.get(nr) + "\n");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.wnd, ex.getMessage(), "Fehler beim Nachrichtenabruf", JOptionPane.ERROR_MESSAGE);
        }

        // Private Nachrichten anzeigen
        try {
            List<PrivateMessage> pms = this.server.getPrivateMessages(this.username);

            if (pms != null) {
                for (PrivateMessage pm : pms) {
                    String text = pm.getSender() + " an " + this.username + ": " + pm.getMessage() + "\n";
                    this.wnd.getChatTextArea().append(text);
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.wnd, ex.getMessage(), "Fehler beim Abruf privater Nachrichten", JOptionPane.ERROR_MESSAGE);
        }

        // Liste der Raumbenutzer aktualisieren
        try {
            List<String> users = this.room.getUsers();
            DefaultListModel listModel = (DefaultListModel) this.wnd.getUserList().getModel();
            listModel.clear();
            int index = 0;

            for (String user : users) {
                listModel.add(index++, user);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this.wnd, ex.getMessage(), "Fehler beim Abruf Raumbenutzer", JOptionPane.ERROR_MESSAGE);
        }
    }
}
