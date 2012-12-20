package de.dhbw.chatroom.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;

/**
 * Dies ist unser Hauptfenster. Gemäß MVC machen wir hier nichts
 * anderes, als das Fenster auf den Bildschirm zu bringen und auf
 * ein Paar Aktionen zu Buttonklicks zu reagieren, die wir dann an
 * den Controller delegieren.
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private MainWindowController mwc;

    private CardLayout mainCards;
    private JTextField messageEntry;
    private JPanel mainPanel;
    private JList roomList;
    private JTextArea chatTextArea;
    private JList userList;
    private AbstractAction[] actions;

    public static int REFRESH_ROOM_LIST = 0;
    public static int ENTER_ROOM = 1;
    public static int LEAVE_ROOM = 2;
    public static int CREATE_ROOM = 3;
    public static int DELETE_ROOM = 4;
    public static int WRITE_PM = 5;
    public static int SEND_LINE = 6;

    public MainWindow(MainWindowController mwc) {
        this.mwc = mwc;

        this.setMinimumSize(new Dimension(300, 300));

        // Toolbar
        this.actions = new AbstractAction[] {
            new RefreshRoomListAction(this.mwc, this),
            new EnterRoomAction(this.mwc, this),
            new LeaveRoomAction(this.mwc, this),
            new CreateRoomAction(this.mwc, this),
            new DeleteRoomAction(this.mwc, this),
            new WritePrivateMessageAction(this.mwc, this),
            new SendLineAction(this.mwc, this)
        };

        JToolBar toolbar = new JToolBar();
        toolbar.add(this.actions[REFRESH_ROOM_LIST]);
        toolbar.add(this.actions[ENTER_ROOM]);
        toolbar.add(this.actions[LEAVE_ROOM]);
        toolbar.add(this.actions[CREATE_ROOM]);
        toolbar.add(this.actions[DELETE_ROOM]);
        toolbar.add(this.actions[WRITE_PM]);
        this.add(toolbar, BorderLayout.NORTH);

        // Hauptbereich
        this.mainCards = new CardLayout();
        this.mainPanel = new JPanel(this.mainCards);
        this.add(this.mainPanel, BorderLayout.CENTER);

        this.roomList = new JList(new DefaultListModel());
        this.mainPanel.add(new JScrollPane(roomList));

        JPanel chatPanel = new JPanel(new BorderLayout());
        this.chatTextArea = new JTextArea();
        chatPanel.add(new JScrollPane(chatTextArea), BorderLayout.CENTER);
        this.userList = new JList(new DefaultListModel());
        chatPanel.add(new JScrollPane(this.userList), BorderLayout.EAST);
        this.mainPanel.add(chatPanel);

        // Eingabezeile
        JPanel entryPanel = new JPanel(new BorderLayout());
        this.messageEntry = new JTextField();
        this.messageEntry.addActionListener(this.actions[SEND_LINE]);
        JButton sendButton = new JButton(this.actions[SEND_LINE]);
        entryPanel.add(this.messageEntry, BorderLayout.CENTER);
        entryPanel.add(sendButton, BorderLayout.EAST);

        this.add(entryPanel, BorderLayout.SOUTH);
    }

    public JTextField getMessageEntry() { return this.messageEntry; }
    public JPanel getMainPanel() { return this.mainPanel; }
    public CardLayout getMainCards() { return this.mainCards; }
    public JList getRoomList() { return this.roomList; }
    public JTextArea getChatTextArea() { return this.chatTextArea; }
    public JList getUserList() { return this.userList; }

    public void setActionEnabled(int id, boolean enabled) {
        this.actions[id].setEnabled(enabled);
    }

    /**
     * Reaktion auf das Schließen/Zerstören des Fensters.
     */
    public void dispose() {
        try {
            this.mwc.onMainWindowClosed();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
        }

        super.dispose();
    }

    /**
     * Aktion, um die Raumliste zu aktualisieren
     */
    class RefreshRoomListAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public RefreshRoomListAction(MainWindowController mwc, MainWindow mw) {
            super("Aktualisieren");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.refreshRoomList();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um einen Raum zu betreten
     */
    class EnterRoomAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public EnterRoomAction(MainWindowController mwc, MainWindow mw) {
            super("Eintreten");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.enterRoom();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um einen Raum wieder zu verlassen
     */
    class LeaveRoomAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public LeaveRoomAction(MainWindowController mwc, MainWindow mw) {
            super("Raum verlassen");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.leaveRoom();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um einen neuen Raum anzulegen
     */
    class CreateRoomAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public CreateRoomAction(MainWindowController mwc, MainWindow mw) {
            super("Neuer Raum");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.createRoom();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um einen Raum zu löschen
     */
    class DeleteRoomAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public DeleteRoomAction(MainWindowController mwc, MainWindow mw) {
            super("Raum auflösen");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.deleteRoom();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um eine private Nachricht zu schreiben
     */
    class WritePrivateMessageAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public WritePrivateMessageAction(MainWindowController mwc, MainWindow mw) {
            super("Private Nachricht");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                this.mwc.writePrivateMessage();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktion, um die aktuelle Textzeile abzuschicken
     */
    class SendLineAction extends AbstractAction {
    	private static final long serialVersionUID = 1L;
    	
        private MainWindowController mwc;
        private MainWindow mw;

        public SendLineAction(MainWindowController mwc, MainWindow mw) {
            super("Abschicken");
            this.mwc = mwc;
            this.mw = mw;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                String message = mw.getMessageEntry().getText();
                this.mwc.postMessage(message);
                mw.getMessageEntry().setText("");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this.mw, ex.getMessage(), "Unerwarteter Fehler", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
