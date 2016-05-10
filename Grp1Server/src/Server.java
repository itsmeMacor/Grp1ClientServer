import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Server extends JFrame {
	
	private JTextField enter;
	private JTextArea display;
	ObjectOutputStream output;
	ObjectInputStream input;
	
	public Server(){
		
		super("Server");
		
		Container c = getContentPane();
		enter = new JTextField();
		enter.setEnabled(false);
		enter.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e){
						
						sendData(e.getActionCommand());
					}
					
				}
				
		);
		c.add(enter, BorderLayout.NORTH);
		
		display = new JTextArea();
		c.add(new JScrollPane (display), BorderLayout.CENTER);
		
		setSize(300, 150);
		show();
		
		
	}
	
	public void runServer(){
		
		ServerSocket server;
		Socket connection;
		int counter = 1;
		
		try{
			server = new ServerSocket(5000, 100);
			
			while(true){
				
				display.setText("waiting for connection\n");
				connection = server.accept();
				
				display.append("Connection" + counter + "received from: " + connection.getInetAddress().getHostName());
				
				output = new ObjectOutputStream( connection.getOutputStream());
				output.flush();
				
				input = new ObjectInputStream( connection.getInputStream());
				display.append("\nGot IO stream\n");
				
				String message = "SERVER>>> Connection Successful";
				output.writeObject(message);
				output.flush();
				enter.setEnabled(true);
				
				do{
					try{
						message = (String) input.readObject();
						display.append("\n" + message);
						display.setCaretPosition(display.getText().length());
						
					}
					catch(ClassNotFoundException cnfex){
						display.append("\nUnknow obj type received");
					}
					
				} while(!message.equals("CLIENT>>> TERMINATE"));
				
				display.append("\nUser terminated connection");
				enter.setEnabled(false);
				output.close();
				input.close();
				connection.close();
				
				++counter;
				
				
			}
			
		}
		catch(EOFException eof){
			System.out.println("Client terminated connection");
		}
		catch(IOException io){
			io.printStackTrace();
		}
	}
	
	private void sendData(String s){
		try{
			output.writeObject("SERVER>>>" + s);
			output.flush();
			display.append("\nSERVER>>>" + s);
		}
		catch(IOException cnfex){
			display.append("\nError writing object");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Server app = new Server();
		
		app.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);
					}
				}
		);
		
		app.runServer();

	}

}