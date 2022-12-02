package ejercicioLoteria;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 * @author Daniel Vázquez
 *
 */
class ClienteTCP {
	private Socket socketCliente = null;
	private BufferedReader entrada = null;
	private PrintWriter salida = null;

	public ClienteTCP(String ip, int puerto) {
		try {
			socketCliente = new Socket(ip, puerto);
			System.out.println("Conexi�n establecida: " + socketCliente);
			entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
			salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
		} catch (IOException e) {
			System.err.printf("Imposible conectar con ip:%s / puerto:%d", ip, puerto);
			System.exit(-1);
		}
	}

	public void closeClienteTCP() {
		try {
			salida.close();
			entrada.close();
			socketCliente.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("-> Cliente Terminado");
	}

	public void enviarMsg(String linea) {
		salida.println(linea);
	}

	public String recibirMsg() {
		String msg = "";
		try {
			msg = entrada.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return msg;
	}
}

public class Cliente {
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		int digitosBoleto = 5;
		ClienteTCP canal = new ClienteTCP("localhost", 5555);
		System.out.println("Comience a escribir ('0') para terminar");
		String boleto = "default";
		do {
			System.out.println("Introduzca su número (" + digitosBoleto + " cifras): ");
			boleto = sc.nextLine();
			if (comprobarBoleto(boleto)) {
				canal.enviarMsg(boleto);
				if (!boleto.equals("0"))
					System.out.println(canal.recibirMsg());
			} else {
				System.out.println("Tiene que ser un número de 5 dígitos");
			}
		} while (!boleto.equals("0"));
		canal.closeClienteTCP();
		sc.close();
	}

	private static boolean comprobarBoleto(String boleto) {
		boolean valido = true;
		if (!boleto.equals("0")) {
			if (boleto.length() == 5) {
				for (int i = 0; i < boleto.length(); i++)
					if (!Character.isDigit(boleto.charAt(i)))
						valido = false;
			} else
				valido = false;
		}
		return valido;
	}
}
