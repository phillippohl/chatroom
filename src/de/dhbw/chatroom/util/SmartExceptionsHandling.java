package de.dhbw.chatroom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SmartExceptionsHandling {
	private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	
	public static void EnableStackTrace(Exception e){
		System.out.println("Do you wish to print the stack trace (y/n)?");
		try {
			if (br.readLine().equalsIgnoreCase("y")){
				e.printStackTrace();
			}
			else{
				System.exit(-1);
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}
}
