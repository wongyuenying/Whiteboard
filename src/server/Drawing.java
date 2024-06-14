/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package server;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import javax.imageio.ImageIO;
import remote.IClient;
import remote.IDrawing;

/**
 * Server side implementation of the remote interface IDrawing This class is for
 * managing all the updates of the drawing panel
 */

public class Drawing extends UnicastRemoteObject implements IDrawing {

	private List<IClient> clients;
	private BufferedImage canvas = null;
	private static final int CANVAS_WIDTH = 750;
	private static final int CANVAS_HEIGHT = 600;

	protected Drawing() throws RemoteException {
		clients = new ArrayList<>();
		canvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		;
	}

	// Register the client and add it to the client list so that they can receive
	// drawing updates broadcasted by the server
	@Override
	public synchronized void registerClient(IClient client) throws RemoteException {
		if (!clients.contains(client)) {
			clients.add(client);
			client.updateCanvas(getCanvas());
		}
	}

	// Get the updated canvas from the client and broadcast to all the other clients
	@Override
	public synchronized void updateCanvas(byte[] newCanvas) throws RemoteException {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(newCanvas);
			canvas = ImageIO.read(bais);
			broadcastCanvas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Return the canvas
	@Override
	public synchronized byte[] getCanvas() throws RemoteException {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(canvas, "png", baos);
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	// Broadcast the updated canvas to all the other registered clients
	private synchronized void broadcastCanvas() throws RemoteException {
		byte[] canvasCopy = getCanvas();
		for (IClient client : clients) {
			try {
				client.updateCanvas(canvasCopy);
			} catch (RemoteException e) {
				clients.remove(client);
				e.printStackTrace();
			}
		}
	}

}
