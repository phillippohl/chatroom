package de.dhbw.chatroom.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import de.dhbw.chatroom.util.SmartExceptionsHandling;

/**
 * Das ist unserer Serveranwendung. Wir erzeugen einfach ein
 * ChatServerService-Objekt und legen es in der RMI Registry ab,
 * damit entfernte Clients darauf zugreifen können.
 */
public class RunServer {
	//private static Class dummy = RunServer.class;
	private static BufferedReader br;
	private static Registry registry;
	
    public static void main(String[] args) {
        System.setProperty("java.security.policy", "java.policy");
        //System.setProperty("java.rmi.server.codebase", dummy.getProtectionDomain().getCodeSource().getLocation().toString());
        ChatServerService server;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // TODO: ChatServer-Objekt am Namensdienst anmelden
        try{
        	LocateRegistry.createRegistry(Registry.REGISTRY_PORT); //RMI-Port 1099
        	registry = LocateRegistry.getRegistry();
        	server = new ChatServerService();
        	br = new BufferedReader(new InputStreamReader(System.in));
        	
			Naming.rebind("ChatServer", server);
        	System.out.println("Server now running...");
        	System.out.println("Press [ENTER] to stop server...");
        	String s = br.readLine();
        	System.out.println("Server stopped.");
        	System.exit(0);
        } catch (MalformedURLException mfue) {
            System.out.println("ACHTUNG: Bitte keinen Unfug als Adresse der rmiregistry eintragen!");
        	System.out.println();
        	SmartExceptionsHandling.EnableStackTrace(mfue); 
            System.exit(-1);
        } catch (RemoteException re){
        	System.out.println("ACHTUNG: Haben Sie die rmiregistry gestartet?");
        	System.out.println();
        	SmartExceptionsHandling.EnableStackTrace(re);        
            System.exit(-1);
        } catch (IOException e) {
        	System.out.println("ACHTUNG: Ein- bzw. Ausgabe fehlerhaft!");
        	System.out.println();
        	SmartExceptionsHandling.EnableStackTrace(e);        
            System.exit(-1);
		}      
    }
}
