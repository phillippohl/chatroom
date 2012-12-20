package de.dhbw.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;

/**
 * Das ist unserer Serveranwendung. Wir erzeugen einfach ein
 * ChatServerService-Objekt und legen es in der RMI Registry ab,
 * damit entfernte Clients darauf zugreifen können.
 */
public class RunServer {
	private static Class dummy = RunServer.class;
	private static BufferedReader br;
	
    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");
        System.setProperty("java.rmi.server.codebase", dummy.getProtectionDomain().getCodeSource().getLocation().toString());
        ChatServerService server;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // TODO: ChatServer-Objekt am Namensdienst anmelden
        try{
        	server = new ChatServerService();
        	br = new BufferedReader(new InputStreamReader(System.in));
        	
			Naming.rebind("ChatServer", server);
        	System.out.println("Server now running...");
        	System.out.println("Press [ENTER] to stop server...");
        	br.readLine();
        	System.out.println("Server stopped.");
        	System.exit(0);
        } catch (MalformedURLException mfue) {
        	mfue.printStackTrace();
        } catch (RemoteException re){
        	re.printStackTrace();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}      
    }
}