package chat;



import java.io.*;

import javax.swing.JTextArea;

public class ReceiveClient implements Runnable {

	private BufferedReader in = null;
	private JTextArea trg = null;


	ReceiveClient(InputStream input, JTextArea target) {
		this.in = new BufferedReader(new InputStreamReader(input));
		trg = target;
		new Thread(this).start();

	}

	@Override
	public void run() {
		String read = null;
		try {
			while ((read = in.readLine()) != null) {
				if(read.toLowerCase().endsWith("exit")) System.exit(0);
				trg.setText(trg.getText()+"\n"+read);
			}

		} catch (IOException e) {
			System.err.println("U¿ytkownik siê roz³¹czy³.");
			System.exit(0);
		}
	}
}
