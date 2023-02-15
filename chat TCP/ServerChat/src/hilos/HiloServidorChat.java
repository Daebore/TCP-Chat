package hilos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

import data.SalaChat;

public class HiloServidorChat extends Thread{
	
	// Declaraci�n de variables
	
	// Cada hilo tendr� un socket para poder conectarse al servidor
	Socket socket = null;
	
	// Cada hilo tendr� un objeto de tipo SalaChat, le servir� para poder acceder
	// a un array de sockets
	SalaChat sala;
	
	// Cada hilo tendr� un DataInputStream para leer los mensajes
	DataInputStream entrada;

	// M�todo constructor
	public HiloServidorChat(Socket socket, SalaChat sala, File historial) {
	
		this.socket = socket;
		this.sala = sala;
		try {
			entrada = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	// M�todo run
	public void run() {
		
		// En este array de tipo String vamos a guardar los mensajes recibidos del cliente
		String[] mensajes = null;
		
		try {
			
			// Lo primero que haremos ser� enviar al cliente un la informaci�n guardada en un fichero,
			// esta informaci�n servir� a modo de historial para que el cliente al conectarse pueda
			// ver los mensajes que se han enviado antes de que se conectara
			mandarFichero();
			
		} catch (IOException e2) {
			System.out.println("Error a la hora de leer el historial.");
			e2.printStackTrace();
		}
		
		// Entramos en un bucle infinito
		while (true) {
			
			// Cremos un String vac�o en el que guardaremos los mensajes que va escribiendo el cliente en el chat
			String cadena = "";
			try {
				
				// Guardamos los mensajes le�dos en una variable
				cadena = entrada.readUTF();
				
				// Separamos los mensajes recibidos por dos puntos (la estructura de los mensajes es
				// nombre:mensaje), por lo que gracias a split podemos separar el nombre del mensaje
				mensajes = cadena.split(":");
				
				// La segunda posici�n del array es el mensaje, si recibimos un *, nos vamos del bucle
				if(mensajes[1].trim().equalsIgnoreCase("*")) {
					break;
				}else {
					// Escribimos en el fichero el mensaje recibido (que servir� como historial de chat)
					escribirEnFichero(cadena);
				}
				
				// Invocamos el m�todo enviarATodos, que como su nombre indica, envia
				// los mensajes recibidos a todos los clientes conedctados
				enviarATodos(sala, cadena);
				
			} catch (EOFException e) {
				System.out.println("Error a la hora de enviar los mensajes");
				System.exit(0);
			} catch (IOException e1) {
				// Cerramos el servidor y controlamos la excepci�n
				System.out.println("Error a la hora de enviar los mensajes");
				System.exit(0);
			}

		}
			// Fin del bucle, cerramos el socket
		try {
			socket.close();
		} catch (IOException e) {
			System.out.println("Error al cerrar el servidor");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-3);
		}
	}
	
	// M�todos
	
	// Este m�todo servir� para escribir todo lo que se escribe en el chat en un fichero
	// que se enviar� al cliente y lo leer� cada vez que un usuario nuevo se conecte al chat
	public synchronized void escribirEnFichero(String cadena) {
		try {
			
			// Creamos el BufferedWritter que nos permitir� escribir y le pasamos el fichero y un append
			// como true para que el contenido se vaya a�adiendo todo el rato
            BufferedWriter bw = new BufferedWriter(new FileWriter("historial.txt", true));
            
            	 // Ponemos al final de cada mensaje recibido un s�mbolo (en este caso -) para poder
                // programar la lectura en el cliente de modo que los mensajes se vayan leyendo como si fuera un chat
                // y no como un String (en el cliente guardaremos los mensajes que recibamos en un array de String
                // y con el m�todo split() los cortaremos cada vez que encontremos el s�mbolo -)
                bw.write(cadena + "-");
                
                // Importante a�adir un salto de l�nea despu�s de cada mensajes recibido
                bw.write("\n");
                
                bw.close();
            
    		}catch (IOException e1) {
    			System.out.println("Error a la hora de escribir en el fichero");
    			e1.printStackTrace();
    		}
           
	}
	
	// Este m�todo servir� para enviar el mensaje al cliente
	public void mandarFichero() throws IOException {
		
	    // Flujo salida (su funcionamiento es similar al de los fichero en java)
		OutputStream os = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
		
        // Leemos el contenido del fichero con BufferedReader
		BufferedReader br = new BufferedReader(new FileReader("historial.txt"));
		
		// Como vamos a enviar el contenido del fichero l�nea a l�nea, creamos una variable String
		// en la que guardaremos cada l�nea le�da
		String linea =" ";
		
		// Creamos un StringBuilder en el que vamos a enviar lo le�do (es m�s �ptimo
		// para env�os que un String)
		StringBuilder sb = new StringBuilder();
		
		// Leemos la primera l�nea...
		linea = br.readLine();

		// Mientras lo le�do no sea nulo, continuamos leyendo
		while(linea != null) {
			
			// A�adimos el valor de linea al StringBuilder
			sb.append(linea);
			
			// Volvemos a leer, reemplazando el valor de linea por cada vuelta
			linea = br.readLine();
		}
		
		// Una vez guardado todo en el StringBuilder, se lo enviamos al cliente
        dos.writeUTF(sb.toString());
	}
	
	// Este m�todo se va a encargar de enviar todos los mensajes recibidos a todos los 
	// clientes conectados al chat en el mismo momento en que son mandados
	public void enviarATodos(SalaChat sala, String cadena) throws IOException {
		
		// Recorremos el array de socket (al cual accedemos pas�ndole por par�metro al hilo un objeto de tipo
		// de la clase SalaChat, la cual contiene este array)
		// Notas -> el array de rellena en la clase MainServerChat
		for (int i = 0; i < sala.getActuales(); i++) {
			
			// Por cada vuelta del bucle, leemos una posici�n del array y la enviamos
			OutputStream os = sala.getTabla()[i].getOutputStream();
	        DataOutputStream dos = new DataOutputStream(os);
	        
	        dos.writeUTF(cadena);      
		}
	}

}