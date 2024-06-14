/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package server;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.MainApplication;

/**
 * This class is for starting the whiteboard application server by creating
 * instances of the Drawing and UserManagement class and publishes them in the
 * rmi registry and start the program as a manager
 */

public class CreateWhiteBoard {
	
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("ERROR: Invalid input.");
			System.out.println("Usage: java CreateWhiteBoard <serverIPAddress> <serverPort> <username>");
			System.exit(1);
		}

		String serverIP = args[0];
		int serverPort;
		String username = args[2];
		if (isValidServerAddress(serverIP)) {
			try {
				serverPort = Integer.parseInt(args[1]);

			} catch (NumberFormatException nfe) {
				System.out.println("ERROR: Invalid port number.");
				System.out.println("Usage: java CreateWhiteBoard <serverIPAddress> <serverPort> <username>");
				return;
			}

			try {

				LocateRegistry.createRegistry(serverPort);

				/**
				 * Creates instances of the drawing and userManagemnnt class and publishes them
				 * in the rmiregistry
				 */
				Drawing drawing = new Drawing();
				UserManagement userManagement = new UserManagement(username);

				// Publish the remote object's stub in the registry
				Registry registry = LocateRegistry.getRegistry();
				registry.bind("Drawing", drawing);
				registry.bind("UserManagement", userManagement);

				// start the application
				MainApplication mainApplication = new MainApplication(username, true, drawing, userManagement);
				userManagement.registerClient(username, mainApplication.getClient());

			}catch (ExportException e) {
				System.out.println("ERROR: Unable to connect to the server.");
				e.printStackTrace();
				
			}catch (IOException e) {
				System.out.println("ERROR: Unable to connect to the server.");
				e.printStackTrace();
			}
			catch (Exception e) {
				System.out.println("ERROR: Unable to connect to the server.");

				e.printStackTrace();
			}
		}else {
			System.out.println("ERROR: Invalid Server Address.");
			System.exit(1);
		}
	}
	
	// check whether the server address is valid
	private static boolean isValidServerAddress(String address) {
		String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(address);
		return matcher.matches();
	}


}
