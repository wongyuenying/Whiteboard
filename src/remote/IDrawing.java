/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
*/

package remote;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Remote interface for drawing
 */

public interface IDrawing extends Remote {
    void registerClient(IClient client) throws RemoteException;
	void updateCanvas(byte[] newCanvas) throws RemoteException;
	byte[] getCanvas() throws RemoteException;

}
