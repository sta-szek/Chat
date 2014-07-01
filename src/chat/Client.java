package chat;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Client extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextArea czat;
	private JTextArea wiadomosc;
	private JButton wyslij;
	private JScrollPane pkonwersacja;
	
	PrintWriter out;
	private String tmp = "";
	private String name = "";
	private int port = 4444;
	private Socket client = null;
	private String host = "localhost";

	Client( String host) {
		setTitle("Komunikator");
		setSize(500, 400);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		stworzGUI();
		Logowanie l = new Logowanie(this, true);		
		l.loguj();
		l.dispose();
		try {
			new ReceiveClient(client.getInputStream(), czat);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void stworzGUI(){
		setLayout(new BorderLayout(1,10));
		setResizable(false);
		czat = new JTextArea(15, 1);
		czat.setEditable(false);
		pkonwersacja = new JScrollPane(czat);
		pkonwersacja.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		wiadomosc = new JTextArea();
		wyslij = new JButton("Wyslij");

		
		wyslij.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent source) {
				wiadomosc.requestFocus();
				tmp = wiadomosc.getText();
				out.println(name+">> "+tmp);
				out.flush();
				wiadomosc.setText("");				
			}
		});
		
		add(pkonwersacja, BorderLayout.NORTH);
		add(wiadomosc, BorderLayout.CENTER);
		add(wyslij, BorderLayout.SOUTH);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Client("localhost");

	}
	
	class Logowanie extends Dialog implements ActionListener, Runnable{
		private static final long serialVersionUID = 1L;
		Button login;
		Label msg;
		TextField nick;
		DataInputStream in = null;
		
		public Logowanie(Frame parent, boolean modal) {
			
			super(parent, modal);
			setLayout(new BorderLayout());
			setSize(200, 100);
			setResizable(false);

			login = new Button("OK");
			nick = new TextField();
			msg = new Label("Podaj nick.");
			add(msg, BorderLayout.NORTH);
			add(nick, BorderLayout.CENTER);
			add(login, BorderLayout.SOUTH);

			login.addActionListener(this);
			
			try {
				client = new Socket(host, port);
				in = new DataInputStream(client.getInputStream());
				out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new Thread(this).start();
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			out.println(nick.getText());
			out.flush();
			
		}
		private void loguj(){
			try {
				while(!in.readBoolean()){
					msg.setText("Nick zajety, podaj inny.");
				}
				name = nick.getText();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void run() {
			setVisible(true);
		}
	}

}
