package de.dhbw.chatroom.server;

/**
 * Dieses Interface beschreibt eine private Nachricht zwischen zwei
 * Benutzern. Da es sich hierbei um ein normales Interface handelt,
 * das nicht java.rmi.Remote erweitert, werden die Nachrichten in
 * serialisierter Form zwischen Client und Server übertragen.
 *
 * Beide Seiten benötigen dieses Interface, damit die Anwendungen
 * kompiliert werden können. Wenn eine Anwendung Objekte dieses Typs
 * erzeugt, benötigt sie ebenso noch eine Klasse, die das Interface
 * implementiert. Das selbe gilt für den Empfänger. Beim Emfänger kann
 * allerdings erst zur Laufzeit festgestellt werden, ob zu einem
 * empfangenen Objekt eine Klasse vorliegt und nachgeladen werden muss.
 */
public interface PrivateMessage {
    public String getSender();
    public String getMessage();
}
