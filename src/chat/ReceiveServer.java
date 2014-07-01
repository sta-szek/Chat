package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReceiveServer implements Runnable {

	private Server server;
	private Socket client;
	private BufferedReader br;

	ReceiveServer(Server server, Socket socket) {
		this.server = server;
		client = socket;
		try {
			br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			new Thread(this).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		String message = null;
		try {
			while ((message = br.readLine()) != null) {
				server.rozeslij(message);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}