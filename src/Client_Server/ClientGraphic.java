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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author saopayne
 */
/*
 * The Client with its GUI
 */
public class ClientGraphic extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	// will first hold "Username:", later on "Enter message"
	private JLabel label;
	// to hold the Username and later on the messages
	private JTextField tf;
	// to hold the server address an the port number
	private JTextField tfServer, tfPort;
	// to Logout and get the list of the users
	private JButton login, logout, whoIsIn;
	// for the chat room
	private JTextArea ta;
        private JTextArea detailsTa;
        private JLabel singleLbl;
	// if it is for connection
	private boolean connected;
	// the Client object
	private Client client;
	// the default port number
	private int defaultPort;
	private String defaultHost;

	// Constructor connection receiving a socket number
	ClientGraphic(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with:
		JPanel northPanel = new JPanel(new GridLayout(5,1,15,10));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 3, 10));
		// the two JTextField with default value for server address and port number
		tfServer = new JTextField(host);
		tfPort = new JTextField("" + port);
		tfPort.setHorizontalAlignment(SwingConstants.RIGHT);

		serverAndPort.add(new JLabel("Server Address/IP Address of host:  "));
		serverAndPort.add(tfServer);
		serverAndPort.add(new JLabel("Port Number:  "));
		serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
                label.setBackground(Color.GRAY);
		northPanel.add(label);
		tf = new JTextField("User");
		tf.setBackground(Color.WHITE);
		northPanel.add(tf);
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		ta = new JTextArea("Welcome to the Chat room\n", 80, 40);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
                
		centerPanel.add(new JScrollPane(ta));
		ta.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);
                
                //The group members Details Panel
                String groupNames = "Oyewale Ademola S. CSC/2011/088 \n"
                        + "Adetola Akinwale S.  CSC/2011/016 \n"+
                        "Oyetunde O. Gabriel CSC/2011/087\n"+
                        "Makanju O. Perfect  CSC/2011/056\n"+
                        "Otuoniyo Harny O. CSC/2011/083\n"+
                        "Raymond Bolade CSC/2012/114\n"+
                        "Muibi-Hammed Aliu CSC/2011/059\n"+
                        "Odeyemi Suliat O. CSC/2011/063\n"+
                        "Jebutu Morifeoluwa CSC/2011/054\n"+
                        "Ekundayo Blessing F. CSC/2011/040\n"+
                        "Oluseesin Olatomide CSC/2011/078\n"+
                        "Yusuf Mustapha L. CSC/2009/152";
//                detailsTa = new JTextArea(groupNames,80,40);
//                JPanel centerDetailsPanel = new JPanel(new GridLayout(1,1));
//                centerDetailsPanel.add(new JScrollPane(detailsTa));
//                detailsTa.setEditable(false);
//                add(centerPanel,BorderLayout.EAST);

		// the 3 buttons
		login = new JButton("Sign In");
                login.setBackground(Color.GREEN);
		login.addActionListener(this);
		logout = new JButton("Sign Out");
                logout.setBackground(Color.RED);
                logout.setForeground(Color.white);
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		whoIsIn = new JButton("Logged in Users");
                whoIsIn.setBackground(Color.blue);
		whoIsIn.addActionListener(this);
		whoIsIn.setEnabled(false);		// you have to login before being able to see the already logged in users.

		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(whoIsIn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500, 600);
		setVisible(true);
		tf.requestFocus();

	}

	// called by the Client to append text in the TextArea 
	void append(String str) {
               
		ta.append(str);
		ta.setCaretPosition(ta.getText().length() - 1);
	}
	// called by the GUI is the connection failed
	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		whoIsIn.setEnabled(false);
		label.setText("Enter your custom user");
		tf.setText("user");
		// reset port number and host name as a construction time
		tfPort.setText("" + defaultPort);
		tfServer.setText(defaultHost);
		// let the user change them
		tfServer.setEditable(false);
		tfPort.setEditable(false);
		tf.removeActionListener(this);
		connected = false;
	}
		
	/*
	* Button or JTextField clicked
	*/
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		// if it is the Logout button
		if(o == logout) {
			client.sendMessage(new Message(Message.LOGOUT, ""));
			return;
		}
		// if it the who is in button
		if(o == whoIsIn) {
			client.sendMessage(new Message(Message.WHOISIN, ""));				
			return;
		}

		// ok it is coming from the JTextField
		if(connected) {
			// just have to send the message
			client.sendMessage(new Message(Message.MESSAGE, tf.getText()));				
			tf.setText("");
			return;
		}
		

		if(o == login) {
			// ok it is a connection request
			String username = tf.getText().trim();
			// empty username ignore it
			if(username.length() == 0)
				return;
			// empty serverAddress ignore it
			String server = tfServer.getText().trim();
			if(server.length() == 0)
				return;
			// empty or invalid port numer, ignore it
			String portNumber = tfPort.getText().trim();
			if(portNumber.length() == 0)
				return;
			int port = 0;
			try {
				port = Integer.parseInt(portNumber);
			}
			catch(Exception en) {
				return;   // nothing I can do if port number is not valid
			}

			// try creating a new Client with GUI
			client = new Client(server, port, username, this);
			// test if we can start the Client
			if(!client.start()) 
				return;
			tf.setText("");
			label.setText("Type in message below");
			connected = true;
			
			// disable login button
			login.setEnabled(false);
			// enable the 2 buttons
			logout.setEnabled(true);
			whoIsIn.setEnabled(true);
			// disable the Server and Port JTextField
			tfServer.setEditable(false);
			tfPort.setEditable(false);
			// Action listener for when the user enter a message
			tf.addActionListener(this);
		}

	}

	// to start the whole thing the server
	public static void main(String[] args) {
		new ClientGraphic("localhost", 1500);
	}

}
