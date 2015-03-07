/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Client_Server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author saopayne
 */
public class ServerGraphic extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	// the stop and start buttons
	private JButton stopStart;
	// JTextArea for the chat room and the events
	private JTextArea chat, event;
	// The port number
	private JTextField tPortNumber;
	// my server
	private Server server;
	
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGraphic(int port) {
		super("Server");
		server = null;
		// in the NorthPanel the PortNumber the Start and Stop buttons
		JPanel north = new JPanel();
		north.add(new JLabel("Enter Default Port Number: "));
		tPortNumber = new JTextField("  " + port);
		north.add(tPortNumber);
		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Start Server");
                stopStart.setBackground(Color.GREEN);
		stopStart.addActionListener(this);
		north.add(stopStart);
		add(north, BorderLayout.NORTH);
		
		// the event and chat room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(120,100);
		chat.setEditable(false);
		appendRoom("users chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Connection details.\n");
		center.add(new JScrollPane(event));	
		add(center);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(400, 400);
		setVisible(true);
	}		

	// append message to the two JTextArea
	// position at the end
	public void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	public void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
		
	}
	
	// start or stop where clicked
	public void actionPerformed(ActionEvent e) {
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
                        stopStart.setBackground(Color.green);
			tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
      	// OK start the server	
		int port;
		try {
			port = Integer.parseInt(tPortNumber.getText().trim());
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		// ceate a new Server
		server = new Server(port, this);
		// and start it as a thread
		new ServerRunning().start();
		stopStart.setText("Stop");
                stopStart.setBackground(Color.red);
		tPortNumber.setEditable(false);
	}
	
	// entry point to start the Server
	public static void main(String[] arg) {
		new ServerGraphic(1500);
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {
		public void run() {
			server.start();         // should execute until if fails
			// the server failed
			stopStart.setText("Start Server");
			tPortNumber.setEditable(true);
			appendEvent("Server has stopped Responding\n");
			server = null;
		}
	}


}
