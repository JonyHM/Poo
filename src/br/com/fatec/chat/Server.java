package br.com.fatec.chat;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

	private List<ChatServerThread> clients = new ArrayList<>();
	private ServerSocket server = null;
	private Thread thread = null;
	private int clientCount = 0;
	private String[] mensagem;
	private InetAddress ipToBind;
	private int backlog;

	public Server(int port) throws UnknownHostException {
		ipToBind = InetAddress.getLocalHost(); 
		backlog = 50;
        
		try {
			System.out.println("Conectando-se � porta " + port + ". Aguarde, por favor...");
			server = new ServerSocket(port, backlog, ipToBind);
			System.out.println("Servidor iniciado: " + server.toString());
			start();
		} catch (IOException ioe) {
			System.out.println("Imposs�vel se conectar � porta " + port + ": " + ioe.getMessage());
		}
	}

	public void run() {
		while (thread != null) {
			try {
				System.out.println("Aguardando um cliente...");
				addThread(server.accept());
			} catch (IOException ioe) {
				System.out.println("Erro ao aceitar cliente: " + ioe);
				stop();
			}
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread = null;
		}
	}

	private int findClient(int ID) {
		for (int i = 0; i < clientCount; i++)
			if (clients.get(i).getID() == ID)
				return i;
		return -1;
	}

	public synchronized void handle(int ID, String input) {
		mensagem = input.split(";");
		mensagem[1] = Cripto.decriptar(mensagem[1]);
		
		if (mensagem[1].equals(".sair")) {
			clients.get(findClient(ID)).send(".sair");
			remove(ID);
		} else {
			for (int i = 0; i < clientCount; i++)
				clients.get(i).send(input);
		}
	}

	public synchronized void remove(int ID) {
		int pos = findClient(ID);
		if (pos >= 0) {
			ChatServerThread toTerminate = clients.get(pos);
			System.out.println("Removendo thread do cliente " + ID 
					+ " na posi��o " + pos);
			
			// Se a posi��o a ser removida for menor que a �ltima da lista,
			// diminui 1 da posi��o dos itens que vierem depois do que ser�
			// removido e diminui o contador
			if (pos < clientCount - 1)
				for (int i = pos + 1; i < clientCount; i++)
					clients.set(i-1, clients.get(i));
			clientCount--;
			try {
				toTerminate.close();
			} catch (IOException ioe) {
				System.out.println("Erro ao encerrar thread: " + ioe);
			}
			toTerminate = null;
		} else {
			System.out.println("Imposs�vel encontrar ID do cliente fornecido!");
		}
	}

	private void addThread(Socket socket) {
		System.out.println("Cliente aceito: " + socket);
		clients.add(new ChatServerThread(this, socket));
		try {
			clients.get(clientCount).open();
			clients.get(clientCount).start();
			clientCount++;
		} catch (IOException ioe) {
			System.out.println("Erro ao abrir thread: " + ioe);
		}
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) throws NumberFormatException, UnknownHostException {
		Server server = null;
		if (args.length != 1)
			System.out.println("Modo de uso: java -jar Server.jar porta");
		else
			server = new Server(Integer.parseInt(args[0]));
	}
}
