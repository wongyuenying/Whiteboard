/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.IOException;
import gui.DrawingPanel;
import gui.MainApplication;
import remote.IClient;

/**
 * This class is representing a client for using the application, functions called by the
 * server to send update to the client
 */

public class Client extends UnicastRemoteObject implements IClient {
	private DrawingPanel drawingPanel;
	private MainApplication mainApplication;

	public Client(DrawingPanel drawingPanel, MainApplication mainApplication) throws RemoteException {
		super();
		this.drawingPanel = drawingPanel;
		this.mainApplication = mainApplication;
	}

	public void updateCanvas(byte[] canvas) throws RemoteException {
		drawingPanel.updateCanvas(canvas);
	}

	public void updateUserList(String[] userList) throws RemoteException {
		mainApplication.updateUserList(userList);
	}

	public void updateChatMessage(String username, String message) throws RemoteException {
		mainApplication.getChatMessage(username, message);
	}

	public void approveUser() throws IOException {
		mainApplication.approveUser();
	}

	public void rejectUser() throws RemoteException {
		mainApplication.rejectUser();
	}

	public void showApprovalDialog(String user) throws IOException {
		mainApplication.showApprovalDialog(user);
	}

	public void managerQuit() throws RemoteException {
		mainApplication.showTerminateDialog();
	}

}