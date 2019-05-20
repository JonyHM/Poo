import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {

	private Socket socket = null;
	private Thread thread = null;
	private DataOutputStream streamOut = null;
	private ChatClientThread client = null;
	private BufferedReader bReader = null;
	private String nick;
	private String[] mensagem;

	public Client(String serverName, int serverPort, String _nick) {
		nick = _nick;
		
		System.out.println("Estabelecendo conex�o. Aguarde, por favor...");
		try {
			socket = new Socket(serverName, serverPort);
			System.out.println("Conectado: " + socket);
			System.out.println("Digite .sair para sair");
			start();
		} catch (UnknownHostException uhe) {
			System.out.println("Host desconhecido: " + uhe.getMessage());
		} catch (IOException ioe) {
			System.out.println("Erro inesperado: " + ioe.getMessage());
		}
	}

	public void run() {
		while (thread != null) {
			try {
				streamOut.writeUTF(nick + ";" + bReader.readLine());
				streamOut.flush();
			} catch (IOException ioe) {
				System.out.println("Erro de envio: " + ioe.getMessage());
				stop();
			}
		}
	}

	public void handle(String msg) {
		if (msg.equals(".sair")) {
			System.out.println("Saindo. Pressione qualquer tecla para sair...");
			stop();
		} else
			mensagem  = msg.split(";");
			System.out.println(mensagem[0] + ": " + mensagem[1]);
	}

	public void start() throws IOException {
		bReader = new BufferedReader(new InputStreamReader(System.in));
		streamOut = new DataOutputStream(socket.getOutputStream());
		if (thread == null) {
			client = new ChatClientThread(this, socket);
			thread = new Thread(this);
			thread.start();
		}
	}

	public void stop() {
		if (thread != null) {
			thread = null;
		}
		try {
			if (bReader != null)
				bReader.close();
			if (streamOut != null)
				streamOut.close();
			if (socket != null)
				socket.close();
		} catch (IOException ioe) {
			System.out.println("Erro ao encerrar...");
		}
		client.close();
		client = null;
		
		System.exit(0);
	}

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String args[]) {
		Scanner scan = new Scanner(System.in);
		Client client = null;
		
		System.out.print("Insira um apelido para entrar: ");
		String nick = scan.nextLine();
		
		if (args.length != 2)
			System.out.println("Modo de uso: java Client.java apelido porta");
		else
			client = new Client(args[0], Integer.parseInt(args[1]), nick);
		
	}
}



/**
 * To Do:
 * - Definir nick no contrutor das threads
 * - escolher nick nos mains e chamar as threads informando o nick. 
 * - ID das threads será o nick
 * - Tradução de tudo
 */
