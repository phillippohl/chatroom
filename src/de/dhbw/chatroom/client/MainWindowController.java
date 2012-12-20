package de.dhbw.chatroom.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.Timer;

import de.dhbw.chatroom.server.ChatRoom;
import de.dhbw.chatroom.server.ChatServer;

/**
 * Erinnern Sie sich noch an das MVC-Pattern? Das hier ist der
 * Controller, der hinter dem Hauptfenster steckt. Er verbindet das
 * Fenster (Anzeige) mit unserer Geschäftslogik (Model). Diese Klasse
 * ist also die cremige Füllung im MVC-Doppelkeks.
 */
public class MainWindowController {
    private ChatServer chatServer;
    private MainWindow wnd;
    private ChatRoom room = null;
    private Timer roomTimer;
    private String username = "";

    private String TITLE = "Guy's darkroom";

    /**
     * Konstruktor
     */
    public MainWindowController() throws RemoteException, NotBoundException, MalformedURLException {
        // Hauptfenster initialisieren
        this.wnd = new MainWindow(this);
        this.wnd.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.wnd.setLocationByPlatform(true);
        this.wnd.pack();

        // Verbindung zum Chatserver herstellen
        String namingHost = this.username = JOptionPane.showInputDialog(this.wnd, "Adresse der rmiregistry:", "localhost:1099");
        String objectName = "ChatServer";

        if (namingHost != null && ! namingHost.equals("")) {
            objectName = "rmi://" + namingHost + "/" + objectName;
        }

        // TODO: ChatServer-Objekt aus dem Namensdiens auslesen. Den hierfür
        // nötigen Namen finden Sie in der Variable objectName.
        this.chatServer = (ChatServer) Naming.lookup(objectName);
        
        // Zu verwendenden Benutzernamen erfragen
        this.username = JOptionPane.showInputDialog(this.wnd, "Ihr Benutzername:", "Big Dick");

        if (this.username == null || this.username.equals("")) {
            this.username = "Big Dick";
        }

        this.TITLE = this.TITLE + " / " + this.username;
        this.wnd.setTitle(this.TITLE);

        // Raumliste anzeigen
        this.changeUiMode();
        this.refreshRoomList();
    }

    /**
     * Fenster anzeigen
     */
    public void show() {
        this.wnd.setVisible(true);
    }

    /**
     * Diese Methode aktiviert und deaktiviert UI Elemente, je nach
     * Zustand des Controllers
     */
    public void changeUiMode() {
        boolean refreshRoomListActive = false;
        boolean enterRoomActive = false;
        boolean leaveRoomActive = false;
        boolean createRoomActive = false;
        boolean deleteRoomActive = false;
        boolean writePmActive = false;
        boolean sendLineActive = false;
        int visibleCard = 0;

        if (this.room == null) {
            refreshRoomListActive = true;
            enterRoomActive = true;
            leaveRoomActive = false;
            createRoomActive = true;
            deleteRoomActive = true;
            writePmActive = true;
            sendLineActive = false;
            visibleCard = 0;
        } else {
            refreshRoomListActive = false;
            enterRoomActive = false;
            leaveRoomActive = true;
            createRoomActive = false;
            deleteRoomActive = false;
            writePmActive = true;
            sendLineActive = true;
            visibleCard = 1;
        }

        this.wnd.setActionEnabled(MainWindow.REFRESH_ROOM_LIST, refreshRoomListActive);
        this.wnd.setActionEnabled(MainWindow.ENTER_ROOM, enterRoomActive);
        this.wnd.setActionEnabled(MainWindow.LEAVE_ROOM, leaveRoomActive);
        this.wnd.setActionEnabled(MainWindow.CREATE_ROOM, createRoomActive);
        this.wnd.setActionEnabled(MainWindow.DELETE_ROOM, deleteRoomActive);
        this.wnd.setActionEnabled(MainWindow.WRITE_PM, writePmActive);
        this.wnd.setActionEnabled(MainWindow.SEND_LINE, sendLineActive);

        if (visibleCard < 1) {
            this.wnd.getMainCards().first(this.wnd.getMainPanel());
        } else {
            this.wnd.getMainCards().last(this.wnd.getMainPanel());
        }
    }

    /* ==================
     * UI Rückrufmethoden
     * ==================
     */

    /**
     * Diese Methode wird aufgerufen, wenn das Hauptfenster geschlossen
     * wurde. Hier können wir den User noch aus dem aktuellen ChatRoom
     * ausloggen.
     */
    public void onMainWindowClosed() throws Exception {
        if (this.room != null) {
            this.room.leaveRoom(this.username);
        }

        if (this.roomTimer != null) {
            this.roomTimer.stop();
        }
    }

    /**
     * Dem ausgewählten Raum beitreten.
     */
    public void enterRoom() throws Exception {
        ChatRoom selectedRoom = this.getSelectedChatRoom();
        if (selectedRoom == null) return;

        int msgNr = selectedRoom.enterRoom(this.username);
        this.room = selectedRoom;
        this.wnd.setTitle(TITLE + " (" + selectedRoom.getName() + ")");
        this.changeUiMode();

        this.roomTimer = new Timer(1000, new ChatRoomTimer(this.chatServer, this.username, this.room, msgNr, this.wnd));
        this.roomTimer.start();
    }

    /**
     * Den aktuellen Raum wieder verlassen.
     */
    public void leaveRoom() throws Exception {
        ChatRoom chatRoom = this.checkActiveRoom();
        if (chatRoom == null) return;

        this.roomTimer.stop();
        this.roomTimer = null;

        chatRoom.leaveRoom(this.username);
        this.room = null;
        this.changeUiMode();

        this.wnd.setTitle(TITLE);
        this.refreshRoomList();
    }

    /**
     * Einen neuen Raum erzeugen.
     */
    public void createRoom() throws Exception {
        String name = JOptionPane.showInputDialog(this.wnd, "Name des neuen Raums:");
        if (name == null || name.isEmpty()) return;

        // TODO: Raum auf dem Server anlegen lassen
        this.chatServer.createChatRoom(name);
        this.refreshRoomList();
    }

    /**
     * Den ausgewählten Raum löschen.
     */
    public void deleteRoom() throws Exception {
        ChatRoom selectedRoom = this.getSelectedChatRoom();
        if (selectedRoom == null) return;

        this.chatServer.deleteChatRoom(selectedRoom.getId());
        this.refreshRoomList();
    }

    /**
     * Eine Nachricht an einen Benutzer schreiben.
     */
    public void writePrivateMessage() throws Exception {
        String toUser = JOptionPane.showInputDialog(this.wnd, "Empfänger der Nachricht:");
        if (toUser == null || toUser.isEmpty()) return;

        String text = JOptionPane.showInputDialog(this.wnd, "Nachricht:");
        if (text == null || text.isEmpty()) return;

        // TODO: Private Nachricht tatsächlich verschicken
        this.chatServer.sendPrivateMessage(this.username, toUser, text);
        text = this.username + " an " + toUser + ": " + text + "\n";
        this.wnd.getChatTextArea().append(text);
    }

    /**
     * Nachricht an den aktuellen Raum schicken.
     */
    public void postMessage(String message) throws Exception {
        ChatRoom chatRoom = this.checkActiveRoom();
        if (chatRoom == null) return;

        // TODO: Eingegebenen Text posten
        chatRoom.postMessage(this.username, message);
    }

    public void refreshRoomList() throws RemoteException {
        Map<Integer, String> rooms = this.chatServer.getListOfChatRooms();
        DefaultListModel listModel = (DefaultListModel) this.wnd.getRoomList().getModel();
        listModel.clear();

        if (rooms != null) {
            for (int id : rooms.keySet()) {
                listModel.addElement(new RoomListElement(id, rooms.get(id)));
            }
        }
    }

    /* =====================
     * Weitere Hilfsmethoden
     * =====================
     */

    /**
     * Sicherstellen, dass sich der Benutzer in einem Raum befindet.
     * In diesem Fall wird eine Referenz auf den Raum zurückgegeben.
     * Andererseits wird eine Fehlermeldung ausgegeben und der
     * Rückgabewert ist null.
     */
    private ChatRoom checkActiveRoom() {
        if (this.room == null) {
            JOptionPane.showMessageDialog(this.wnd, "Diese Aktion kann nur in einem Chatraum ausgeführt werden.", "Achtung", JOptionPane.WARNING_MESSAGE);
        }

        return this.room;
    }

    /**
     * Ermitteln des in der Übersicht ausgewählten ChatRoom und liefert
     * eine entfernte Referenz darauf zurück. Falls kein Eintrag
     * ausgewählt wurde, erfolgt eine Fehlermeldung und es wird der
     * Wert null zurückgeliefert.
     */
    private ChatRoom getSelectedChatRoom() throws RemoteException {
        DefaultListModel listModel = (DefaultListModel) this.wnd.getRoomList().getModel();
        int index = this.wnd.getRoomList().getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this.wnd, "Wählen Sie erst einen Chatraum aus.", "Achtung", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        RoomListElement element = (RoomListElement) listModel.elementAt(index);
        return this.chatServer.getChatRoom(element.id);
    }

    /**
     * Listenelement der Raumliste. Wird benötigt, damit die Raum ID
     * in der List mit abgelegt wird und wieder abgerufen werden kann.
     */
    private class RoomListElement {
        public int id = -1;
        public String name = "";

        public RoomListElement (int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}
