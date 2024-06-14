/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.MainApplication;
import remote.IDrawing;
import remote.IUserManagement;

/**
 * This class is for clients joining the whiteboard application and starting the
 * application
 */

public class JoinWhiteBoard {

	private MainApplication mainApplication;
	public IDrawing drawing;
	public IUserManagement userManagement;
	public String username;
	private boolean validUsername = true;

	public static void main(String[] args) {
		JoinWhiteBoard client = new JoinWhiteBoard();
		if (client.checkInput(args) == true) {
			client.connectToRegistry(args[0], Integer.parseInt(args[1]), args[2]);
		}

		else {
			System.out.println("Usage: java JoinWhiteBoard <serverIPAddress> <serverPort> <username>");
			System.exit(1);
		}
	}

	// check user input
	private boolean checkInput(String[] args) {

		if (args.length != 3) {
			System.out.println("ERROR: Invalid Input.");
			return false;
		} else {

			username = args[2];
			String serverIP = args[0];

			if (isValidServerAddress(serverIP)) {
				try {
					int serverPort = Integer.parseInt(args[1]);

				} catch (NumberFormatException nfe) {
					System.out.println("ERROR: Invalid port number.");
					return false;
				}
				return true;
			} else {
				System.out.println("ERROR: Invalid Server Address.");
				return false;
			}

		}
	}

	// connect to the registry
	private void connectToRegistry(String serverIP, int serverPort, String username) {
		try {

			// Connect to the rmiregistry that is running on localhost
			Registry registry = LocateRegistry.getRegistry(serverIP, serverPort);

			// Retrieve the stub/proxy for the remote drawing object from the registry
			drawing = (IDrawing) registry.lookup("Drawing");
			userManagement = (IUserManagement) registry.lookup("UserManagement");

			// check whether there is duplicated usernames
			for (String user : userManagement.getUserList()) {
				if (username.equals(user)) {
					validUsername = false;
					break;
				}
			}
			// Initialize the Main application
			if (validUsername == true) {
				userManagement.addUser(username);
				mainApplication = new MainApplication(username, false, drawing, userManagement);
				userManagement.registerClient(username, mainApplication.getClient());
			} else {
				System.out.println("ERROR: Duplicated username. Please enter another username.");
			}

		} catch (Exception e) {
			System.out.println("Unable to connect to registry.");
			e.printStackTrace();
			System.exit(1);
		}

	}

	// check whether the server address is valid
	private boolean isValidServerAddress(String address) {
		String regex = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(address);
		return matcher.matches();
	}

}
