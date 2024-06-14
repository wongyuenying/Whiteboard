/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import remote.IClient;
import remote.IUserManagement;

/**
 * Server side implementation of the remote interface IUserManagement This class
 * is for managing all the users actions
 */

public class UserManagement extends UnicastRemoteObject implements IUserManagement {

	private Map<String, IClient> clients;
	private String manager;

	protected UserManagement(String managerName) throws RemoteException {
		this.manager = managerName;
		clients = new HashMap<>();
		clients.put(managerName, null);
	}

	// Add a new user to the list of users online.
	public synchronized void addUser(String username) throws IOException {
		if (!clients.containsKey(username)) {
			clients.put(username, null);
			notifyJoinRequest(username);
		}
	}

	// Remove a user from the list of users
	public synchronized void removeUser(String username) throws RemoteException {
		clients.remove(username);
		if (username.equals(manager)) {
			notifyManagerQuit();
		}
		broadcastUserList();
	}

	// Return the user list
	@Override
	public String[] getUserList() throws RemoteException {
		return clients.keySet().toArray(new String[0]);
	}

	// Approve the client and put the client into the clients' list
	@Override
	public synchronized void approveUser(String username) throws IOException {
		IClient client = clients.get(username);
		try {
			if (client != null) {
				broadcastUserList();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.approveUser();
	}

	// Reject the user and remove them from the clients' list
	@Override
	public synchronized void rejectUser(String username) throws RemoteException {
		IClient client = clients.get(username);
		if (client != null) {
			client.rejectUser();
			clients.remove(username);
		}
		broadcastUserList();
	}

	// Register the user to the client server
	@Override
	public synchronized void registerClient(String username, IClient client) throws RemoteException {

		if (username.equals(manager)) {
			clients.put(manager, client);
			broadcastUserList();
		} else if (clients.containsKey(username)) {
			clients.put(username, client);
		}
	}

	// Receive the message and broadcast to all the other clients
	public synchronized void sendMessage(String username, String message) throws RemoteException {
		for (IClient client : clients.values()) {
			if (client != null) {
				client.updateChatMessage(username, message);
			}
		}
	}

	// Notify the manager that a new user has joined in
	private synchronized void notifyJoinRequest(String username) throws IOException {
		IClient managerClient = clients.get(manager);
		if (managerClient != null) {
			managerClient.showApprovalDialog(username);
		}
	}

	// Udate all the users that the manager has quitted
	private synchronized void notifyManagerQuit() throws RemoteException {
		for (IClient client : clients.values()) {
			if (client != null) {
				client.managerQuit();
			}
		}
	}

	// Update each client's user list
	private synchronized void broadcastUserList() throws RemoteException {
		String[] userList = clients.keySet().toArray(new String[0]);
		for (IClient client : clients.values()) {
			if (client != null) {
				client.updateUserList(userList);
			}
		}
	}

}
