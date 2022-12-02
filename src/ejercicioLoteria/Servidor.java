package ejercicioLoteria;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @author Daniel Vázquez
 *
 */
class ServidorTCP {
	private Socket socketCliente;
	private ServerSocket socketServidor;
	private BufferedReader entrada;
	private PrintWriter salida;

	public ServidorTCP(int puerto) {
		this.socketCliente = null;
		this.socketServidor = null;
		this.entrada = null;
		this.salida = null;
		try {
			socketServidor = new ServerSocket(puerto);
			System.out.println("Esperando conexi�n...");
			socketCliente = socketServidor.accept();
			System.out.println("Conexi�n acceptada: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("No puede escuchar en el puerto: " + puerto);
			System.exit(-1);
		}
	}

	public void closeServidorTCP() {
		try {
			salida.close();
			entrada.close();
			socketCliente.close();
			socketServidor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Servidor Terminado");
	}

	public String recibirMsg() {
		String linea = "";
		try {
			linea = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linea;
	}

	public void enviarMsg(String linea) {
		salida.println(linea);
	}

}

public class Servidor {
	public static void main(String[] args) throws IOException {
		ServidorTCP canal = new ServidorTCP(5555);
		String boletoGanadorString = "";
		int digitosEnBoleto = 5;
		for (int i = 0; i < digitosEnBoleto; i++) {
			boletoGanadorString += ThreadLocalRandom.current().nextInt(0, 9 + 1);
		}
		int boletoGanador = Integer.parseInt(boletoGanadorString);
		int numeroCliente;
		System.out.println(boletoGanadorString);
		String numeroClienteString;
		do {
			numeroClienteString = canal.recibirMsg();
			if (!numeroClienteString.equals("0")) {
				numeroCliente = Integer.parseInt(numeroClienteString);
				System.out.println("Cliente: " + numeroClienteString);
				if (numeroCliente == boletoGanador) {
					canal.enviarMsg("Premio gordo");
				} else if (boletoGanador + 1 == numeroCliente) {
					canal.enviarMsg("Número posterior");
				} else if (boletoGanador - 1 == numeroCliente) {
					canal.enviarMsg("Número anterior");
				} else if (boletoGanadorString.substring(2, digitosEnBoleto - 1)
						.equals(numeroClienteString.substring(2, digitosEnBoleto - 1))) {
					// Tres últimas
					canal.enviarMsg("Centenas");
				} else if (boletoGanadorString.substring(3, digitosEnBoleto - 1)
						.equals(numeroClienteString.substring(3, digitosEnBoleto - 1))) {
					// Dos últimas
					canal.enviarMsg("Dos últimas cifras");
				} else if (boletoGanadorString.charAt(digitosEnBoleto - 1) == numeroClienteString
						.charAt(digitosEnBoleto - 1)) {
					// Ultima (Reintegro)
					canal.enviarMsg("Reintegro");
				} else {
					// No premio
					canal.enviarMsg("Sin premio");
				}
			}
		} while (!numeroClienteString.equals("0"));
		canal.closeServidorTCP();
	}
}
