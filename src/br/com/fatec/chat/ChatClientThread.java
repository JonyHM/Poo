package br.com.fatec.chat;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread {
	
	private Socket socket = null;
	private Client client = null;
	private DataInputStream streamIn = null;

	public ChatClientThread(Client _client, Socket _socket) {
		client = _client;
		socket = _socket;
		open();
		start();
	}

	public void open() {
		try {
			streamIn = new DataInputStream(socket.getInputStream());
		} catch (IOException ioe) {
			System.out.println("Erro ao receber dados do servidor: " + ioe);
			client.stop();
		}
	}

	public void close() {
		try {
			if (streamIn != null)
				streamIn.close();
		} catch (IOException ioe) {
			System.out.println("Erro ao encerrar a conexão com o serviço de mensagens: " + ioe);
		}
	}

	public void run() {
		while (true) {
			try {
				client.handle(streamIn.readUTF());
			} catch (IOException ioe) {
				System.out.println("Erro de escuta: " + ioe.getMessage());
				client.stop();
			}
		}
	}
}
