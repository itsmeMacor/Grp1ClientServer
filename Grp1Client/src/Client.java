import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Client extends  JFrame {
	
	private JTextField enter;
	private JTextArea display;
	ObjectOutputStream output;
	ObjectInputStream input;
	String message = "";
	
	public Client(){
		super ("Client");
		
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
	
	public void runClient(){
		
		Socket client;
		
		try{
			display.setText("Attempting Connection...\n");
			
			client = new Socket( InetAddress.getByName( "127.0.0.1"), 5000);
				display.append("Connected to:" + client.getInetAddress().getHostName());
				
			output = new ObjectOutputStream(client.getOutputStream());
			output.flush();
			input = new ObjectInputStream(client.getInputStream());
			display.append("\nGot IO stream\n");
			
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
				
			} while(!message.equals("SERVER>>> TERMINATE"));
			
			display.append("\nclosing connection");
			input.close();
			output.close();
			client.close();
			
		}
		catch(EOFException eof){
			System.out.println("server terminated connection");
		}
		catch(IOException io){
			io.printStackTrace();
		}
	}
	
	private void sendData(String s){
		try{
			message = s;
			output.writeObject("CLIENT>>>" + s);
			output.flush();
			display.append("\nCLIENT>>>" + s);
		}
		catch(IOException cnfex){
			display.append("\nError writing object");
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Client app = new Client();
		
		app.addWindowListener(
				new WindowAdapter(){
					public void windowClosing(WindowEvent e){
						System.exit(0);
					}
				}
		);
		
		app.runClient();
		
		

	}

}