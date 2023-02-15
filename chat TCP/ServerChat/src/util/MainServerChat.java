package util;

import javax.swing.JOptionPane;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import data.SalaChat;
import hilos.HiloServidorChat;

public class MainServerChat {

	// Máximo de usuarios conectados
	static final int MAXIMO = 10;

	public static void main(String[] args) {
		
		// Cada hilo va a tener un fichero llamado historial en el que se van a guardar todos los mensajes
		// recibidos al servidor que va a leer cada clientes recién conectado
		File historial = new File("historial.txt");
	
		// Puerto para conectarse al servidor (el cliente usará el mismo)
		int PUERTO = 6000;
		
		// Creamos el servidor y lo inializamos como nulo
		ServerSocket servidor = null;
		
		// Mostramos un mensaje de bienvenida, indicando que el chat se ha inicializado
		try {
			servidor = new ServerSocket(PUERTO);
			JOptionPane.showMessageDialog(null, "Server on", "INFO", JOptionPane.INFORMATION_MESSAGE, null);

		// Controlamos la excepción
		} catch (IOException e) {
			System.out.println("Error al crear el servidor");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Creamos un array de Sockets que tendrá lo longitud máxima el númerp de 
		// conexiones permitidas anteriormente declarado
		Socket tabla[] = new Socket[MAXIMO];
		
		// Creamos un objeto de la clase SalaChat 
		SalaChat sala = new SalaChat(MAXIMO, 0, 0, tabla, historial);
		
		// Creamos un bucle que se repetirá mientras no se llegue al máximo de conexiones permitidas (10)
		while(sala.getConexiones() < MAXIMO) {
			
			// Por cada vuelta del bucle, creamos un nuevo objeto de tipo socket (una nueva conexión)
			Socket nuevo = null;
			
			try {
				
				// Con accept el hilo en ejecución se queda en espera hasta recibir una conexión entrante
				nuevo = servidor.accept();
				
			} catch (IOException e) {
				System.out.println("ERR_CONNECTION_REFUSED: El servidor ha rechazado la conexión");
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(-2);
			}
			
			// IMPORTANTE: para que el servidor pueda enviar los mensajes recibidos a TODOS los clientes
			// conectados al chat, es necesario que cada hilo tenga un array que contenga todos los sockets.
			// Si no se hace esto, el servidor únicamente mandará los mensajes recibidos al cliente en específico
			// que se lo ha enviado
			sala.addTabla(nuevo, sala.getConexiones());
			
			// Le pasamos al futuro hilo el socket en concreto que va a tener para poder comunicarse con el servidor
			sala.setActuales(sala.getActuales() + 1);
			
			// Le sumamos 1 al valor de las conexiones actuales (nos servirá para saber cuántos sockets tenemos,
			// o lo que es lo mismo, a cuántos clientes tenemos que mandarles los mensajes recibidos)
			sala.setConexiones(sala.getConexiones() + 1);
			
		// Lanzamos un hilo por cada usuario conectado al que le pasamos un socket y un objeto de tipo SalaChat
		HiloServidorChat hilo = new HiloServidorChat(nuevo, sala, historial);
		
		// Lanzamos el hilo
		hilo.start();
		}
		
		// Este mensaje está fuera del while y se mostrará sólamente cuando el número de clientes
		// conectados al servidor sea mayor de 10. 
		// Después cerramos el servidor y se acaba el programa
		JOptionPane.showMessageDialog(null, "ERROR 503: SERVER IS OVERLOADED. Se han superado el máximo de conexiones permitidas", "ERROR", JOptionPane.ERROR_MESSAGE, null);
		System.exit(0);

		// Cerramos el servidor y controlamos la excepción
		try {
			servidor.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar el servidor");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-3);
		}

	}

}