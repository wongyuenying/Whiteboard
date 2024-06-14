/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
*/

package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;


public interface IClient extends Remote {
    void updateCanvas(byte[] canvas) throws RemoteException;
    void updateUserList(String[] userList) throws RemoteException;
    void updateChatMessage(String username, String message) throws RemoteException;
    void showApprovalDialog(String user) throws RemoteException, IOException;
    void approveUser() throws RemoteException, IOException;
    void rejectUser() throws RemoteException;
    void managerQuit() throws RemoteException;
}