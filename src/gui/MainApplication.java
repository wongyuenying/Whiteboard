/**
 * Name: Yuen Ying Wong
 * Studnet ID: 1348552
 */

package gui;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.Client;
import remote.IDrawing;
import remote.IUserManagement;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.lang.Object;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * This class is for launching and initializing the main application window
 */

public class MainApplication {

	private JFrame frame;
	private DrawingPanel drawingPanel;
	private ToolboxPanel toolboxPanel;
	private Client client;
	private JPanel userPanel;
	private JTextArea userTextArea;
	private boolean isManager = false;
	private JFrame waitingDialogFrame;
	private JTextArea waitingText;
	private JTextArea chatArea;
	private JTextField chatInput;
	private JPanel chatPanel;
	public String username;
	public IDrawing drawing;
	public IUserManagement userManagement;

	public MainApplication(String username, boolean isManager, IDrawing drawing, IUserManagement userManagement)
			throws IOException {
		this.username = username;
		this.isManager = isManager;
		this.drawing = drawing;
		this.userManagement = userManagement;
		drawingPanel = new DrawingPanel(drawing);
		client = new Client(drawingPanel, this);

		// update the server whenever a user has left the application
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				userManagement.removeUser(username);
			} catch (RemoteException e) {
				System.out.println("ERROR: Unable to connect to the server.");
			}
		}));

		/**
		 * initialize the whiteboard if it is a manager for other users, show the
		 * waiting dialog first and wait for approval from manager
		 */
		if (isManager) {
			initialize();
			frame.setVisible(true);
		} else {
			SwingUtilities.invokeLater(() -> {
				showWaitingDialog();
			});
		}

	}

	// initialize the frame of the application
	public void initialize() throws IOException {
		frame = new JFrame();
		frame.setBounds(100, 100, 1000, 850);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		drawing.registerClient(client);

		// initialize the drawing panel area
		drawingPanel.setBounds(99, 0, 750, 600);
		frame.getContentPane().add(drawingPanel);
		drawingPanel.setBackground(Color.WHITE);

		// initialize the tool box area
		toolboxPanel = new ToolboxPanel(drawingPanel);
		toolboxPanel.setSize(100, 600);
		toolboxPanel.setLocation(0, 0);
		frame.getContentPane().add(toolboxPanel, BorderLayout.WEST);
		frame.getContentPane().add(drawingPanel, BorderLayout.CENTER);

		// initialize the user list area
		userPanel = new JPanel();
		userPanel.setBounds(850, 0, 144, 600);
		frame.getContentPane().add(userPanel);

		userTextArea = new JTextArea();
		userTextArea.setEditable(false);
		userTextArea.setWrapStyleWord(true);
		userTextArea.setLineWrap(true);
		userTextArea.setBackground(Color.LIGHT_GRAY);

		JScrollPane userScrollPane = new JScrollPane(userTextArea);
		userScrollPane.setPreferredSize(new Dimension(140, 600));
		userPanel.add(userScrollPane, BorderLayout.CENTER);
		frame.getContentPane().add(userPanel);

		// initialize the chat box area
		chatPanel = new JPanel(new BorderLayout());
		chatPanel.setBounds(99, 625, 750, 150);
		chatArea = new JTextArea(5, 20);
		chatArea.setEditable(false);

		chatInput = new JTextField();
		chatInput.addActionListener(e -> {
			try {
				sendChatMessage(username, chatInput.getText());
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			chatInput.setText("");
		});

		JScrollPane chatScrollPane = new JScrollPane(chatArea);
		chatPanel.add(chatScrollPane, BorderLayout.CENTER);
		chatPanel.add(chatInput, BorderLayout.SOUTH);

		frame.getContentPane().add(chatPanel, BorderLayout.SOUTH);

		// initialize the kick button and file operation for manager only
		if (isManager) {
			addKickButton();
			addFileMenu();
		}
	}

	// return the client server
	public Client getClient() {
		return client;
	}

	// show the waiting dialog to users when they try to launch the application
	public void showWaitingDialog() {
		waitingDialogFrame = new JFrame();
		waitingDialogFrame.setBounds(100, 100, 300, 150);
		waitingDialogFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		waitingDialogFrame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 300, 150);
		waitingDialogFrame.getContentPane().add(panel);

		waitingText = new JTextArea();
		waitingText.setBounds(0, 0, 250, 150);
		waitingText.setEditable(false);
		waitingText.setWrapStyleWord(true);
		waitingText.setLineWrap(true);
		waitingText.setText("Waiting for Manager Approval");

		panel.add(waitingText);
		waitingDialogFrame.setVisible(true);

		waitingDialogFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// update the user list by appending the user names and show it in the
	// userTextArea
	public void updateUserList(String[] userListArray) {
		if (userTextArea != null) {
			StringBuilder userListBuilder = new StringBuilder();
			for (String username : userListArray) {
				userListBuilder.append(username).append("\n");
			}
			userTextArea.setText(userListBuilder.toString());
		}
	}

	// show the approval dialog to manager when a user tries to join in
	public void showApprovalDialog(String username) throws IOException {
		if (isManager) {
			SwingUtilities.invokeLater(() -> {
				int result = JOptionPane.showConfirmDialog(frame,
						"\"" + username + "\" wants to share your whiteboard.", "Join Request",
						JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_NO_OPTION) {
					try {
						userManagement.approveUser(username);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					try {
						userManagement.rejectUser(username);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				}
			});
			// }).start();
		}
	}

	// initialize the application for users after the manager approved them
	public void approveUser() throws IOException {
		if (waitingDialogFrame != null) {
			waitingDialogFrame.dispose();
		}
		initialize();
		updateUserList(userManagement.getUserList());
		frame.setVisible(true);
	}

	/**
	 * show a rejection dialog when a user is either rejected by the manager when
	 * they tried to join in or get kicked out by the manager
	 */

	public void rejectUser() {
		if (waitingDialogFrame != null) {
			waitingText.setText("Sorry, you got rejected by the manager.");
		}
		if (frame != null) {
			JFrame quitFrame = new JFrame();
			quitFrame.setBounds(100, 100, 300, 150);
			quitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			quitFrame.getContentPane().setLayout(null);

			JPanel panel = new JPanel();
			panel.setBounds(0, 0, 300, 150);
			quitFrame.getContentPane().add(panel);

			JTextArea quitText = new JTextArea();
			quitText.setBounds(0, 0, 250, 150);
			quitText.setEditable(false);
			quitText.setWrapStyleWord(true);
			quitText.setLineWrap(true);
			quitText.setText("Unfortunately, the Manager has kicked you out, please exit.");

			panel.add(quitText);
			quitFrame.setVisible(true);

			quitFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			frame.setVisible(false);
		}
	}

	// show the dialog when the manager has left the application and all users have
	// to leave
	public void showTerminateDialog() {
		frame.setVisible(false);
		JFrame quitFrame = new JFrame();
		quitFrame.setBounds(100, 100, 300, 150);
		quitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		quitFrame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 300, 150);
		quitFrame.getContentPane().add(panel);

		JTextArea quitText = new JTextArea();
		quitText.setBounds(0, 0, 250, 150);
		quitText.setEditable(false);
		quitText.setWrapStyleWord(true);
		quitText.setLineWrap(true);
		quitText.setText("The Manager has quit, please exit.");

		panel.add(quitText);
		quitFrame.setVisible(true);

		quitFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	// get the message from the server and append it to the chat box area
	public void getChatMessage(String username, String message) {
		chatArea.append(username + ": " + message + "\n");
	}

	// sending the message to the server
	private void sendChatMessage(String username, String message) throws RemoteException {
		userManagement.sendMessage(username, message);
	}

	// initialize a kick user button for manager only
	private void addKickButton() {
		JButton kickButton = new JButton("Kick User");
		kickButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String user = JOptionPane.showInputDialog(frame, "Enter username to kick:");
				if (user != null && !user.isEmpty() && user != username) {
					try {
						userManagement.rejectUser(user);
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
		kickButton.setBounds(850, 610, 144, 30);
		frame.getContentPane().add(kickButton);

	}

	// initialize a file menu for manager only
	private void addFileMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem newItem = new JMenuItem("New");
		newItem.addActionListener(e -> newFile());
		fileMenu.add(newItem);

		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(e -> openFile());
		fileMenu.add(openItem);

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(e -> saveFile());
		fileMenu.add(saveItem);

		JMenuItem saveAsItem = new JMenuItem("Save As...");
		saveAsItem.addActionListener(e -> saveAsFile());
		fileMenu.add(saveAsItem);

		JMenuItem closeItem = new JMenuItem("Close");
		closeItem.addActionListener(e -> closeApplication());
		fileMenu.add(closeItem);

		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);
	}

	// clear the board when New File button is pressed
	private void newFile() {
		drawingPanel.clearBoard();
	}

	// open a png file when Open File button is pressed
	private void openFile() {
		drawingPanel.openFile();
	}

	// save the current drawing to the current file
	private void saveFile() {
		drawingPanel.saveFile();
	}

	// save the current drawing to a new file
	private void saveAsFile() {
		drawingPanel.saveAsFile();
	}

	// exit the application and close the window
	private void closeApplication() {
		System.exit(0);
	}

}
