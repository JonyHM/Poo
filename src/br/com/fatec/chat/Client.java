package br.com.fatec.chat;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
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
	private String enviar;

	public Client(InetAddress IP, int serverPort, String _nick) throws UnknownHostException {
		nick = _nick;
		
		System.out.println("Estabelecendo conex�o. Aguarde, por favor...");
		try {
			socket = new Socket(IP, serverPort);
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
				enviar = Cripto.encriptar(bReader.readLine());
				streamOut.writeUTF(nick + ";" + enviar);
				streamOut.flush();
			} catch (IOException ioe) {
				System.out.println("Erro de envio: " + ioe.getMessage());
				this.stop();
			}
		}
	}

	public void handle(String msg) {
		mensagem  = msg.split(";");		
		mensagem[1] = Cripto.decriptar(mensagem[1]);
				
		if (mensagem[1].equals(".sair")) {
			if(mensagem[0] == nick) {
				System.out.println("Saindo. Pressione qualquer tecla para sair...");
				this.stop();
			}
		} else
			mensagem  = msg.split(";");
			mensagem[1] = Cripto.decriptar(mensagem[1]);
			
			int index = mensagem[1].indexOf("@");
			
			if(index == 0) {
				if(mensagem[1].indexOf(nick) >= 0) {
					System.out.println(mensagem[0] + ": " + mensagem[1]);
				}
			} else {
				System.out.println(mensagem[0] + ": " + mensagem[1]);
			}
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
		
			System.out.println("\nChat encerrado\n");
		} catch (IOException ioe) {
			System.out.println("Erro ao encerrar...");
		}
		client.close();
		client = null;
		
		System.exit(0);
	}
	
	@SuppressWarnings({ "unused", "resource" })
	public static void main(String args[]) throws NumberFormatException, UnknownHostException {
		Scanner scan = new Scanner(System.in);
		Client client = null;
		
		
		if (args.length != 2)
			System.out.println("Modo de uso: java -jar Client.jar IP porta");
		
		else {
			System.out.print("Insira um apelido para entrar: ");
			String nick = scan.nextLine();
			client = new Client(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), nick);
		}
		
	}
}
