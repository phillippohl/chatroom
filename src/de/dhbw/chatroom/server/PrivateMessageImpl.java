package de.dhbw.chatroom.server;

import java.io.Serializable;

/**
 * Dies ist eine einfache Umsetzung des PrivateMessage Interfaces.
 * Objekte diesen Typs erzeugt der Server und schickt sie an den
 * Client. Da die Clients aber nur an das PrivateMessage Interface
 * gebunden sind, muss diese Klasse von den Clients zur Laufzeit
 * dynamisch nachgeladen werden.
 */
public class PrivateMessageImpl implements PrivateMessage, Serializable {
    private String sender = "";
    private String receiver = "";
    private String message = "";

    public PrivateMessageImpl(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() { return this.sender; }
    public String getReceiver() { return this.receiver; }
    public String getMessage() { return this.message; }
}
