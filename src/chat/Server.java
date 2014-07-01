package chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


// The 
public class Server implements Runnable {

	private int port;
	private ServerSocket socket = null;
	private Map<String, Socket> map = new ConcurrentHashMap<String, Socket>(100);

	Server(int port) {
		this.port = port;
		try {
			socket = new ServerSocket(this.port);
		} catch (IOException e) {
			System.err.println("Cannot listen on: " + port);
			System.exit(-1);
		}
		for (int i = 0; i < 10; i++) {
			new Thread(this).start();
			;
		}

	}

	private Socket nasluchuj() {
		try {
			return socket.accept();
		} catch (IOException e) {
			System.err.println("Nie mo¿na zaakceptowaæ klienta");
			return null;
		}
	}

	private synchronized boolean sprawdzImie(String name) {
		Set<String> names = map.keySet();

		for (Object s : names.toArray()) {
			if (s.equals(name))
				return false;
		}
		return true;
	}

	private synchronized void dodajKlienta(String name, Socket who) {
		map.put(name, who);
	}

	private void zapytajImie(Socket who) {
		String name;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(who.getInputStream()));
			DataOutputStream out = new DataOutputStream(who.getOutputStream());
			while(true){
				name = in.readLine();
				if(sprawdzImie(name))
					break;
				out.writeBoolean(false);
			}
			out.writeBoolean(true);
			dodajKlienta(name, who);

		} catch (IOException e) {
			System.err.println("Klient sie nie podlaczyl");
		}
	}

	protected synchronized void rozeslij(String msg) {
		Set<String> names = map.keySet();
		PrintWriter bw = null;
		Socket target = null;
		for (Object s : names.toArray()) {
			target = map.get(s);
			try {
				bw = new PrintWriter(target.getOutputStream());
				bw.println(msg);
				bw.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public static void main(String args[]) {
		new Server(4444);
		while(true){}
	}

	@Override
	public void run() {
		Socket client = null;
		if ((client = nasluchuj()) != null) {
			zapytajImie(client);
			new ReceiveServer(this, client);
		}
	}
}