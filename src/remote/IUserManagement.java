/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
*/

package remote;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Remote interface for user management
 */

public interface IUserManagement extends Remote {
    void addUser(String username) throws RemoteException, IOException;
    void removeUser(String username) throws RemoteException;
    String[] getUserList() throws RemoteException;
    void approveUser(String username) throws RemoteException, IOException;
    void rejectUser(String username) throws RemoteException;
    void registerClient(String username, IClient client) throws RemoteException;
    void sendMessage(String username, String message) throws RemoteException;
   // void broadcastUserList() throws RemoteException;
   // void notifyManagerQuit() throws RemoteException;
}