package util;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import interfaz.ClienteFrame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class MainCliente {
	
    // Flujo salida (su funcionamiento es similar al de los fichero en java)
	public static DataOutputStream salida = null;	
	
	public static void main(String[] args) throws IOException {
		
		// Cremos un fichero que tendr� escritos todos lso nobmres de los usuarios conectados
		// para que no pueda iniciar sesi�n un usuario con un nombre ya cogido
		File nombres = new File("nombres.txt");
		
		// Establecemos el puerto, que ser� el mismno que el del servidor
		int puerto = 6000;
		
	    // Creamos un objeto de tipo socket que nos va a permitir conectarnos
        // con el servidor (modelo Cliente-Servidor)
		Socket s = null;
		
		try {
			// Le pasamos nuestra direcci�n y la del servidor
			s = new Socket("localhost", puerto);
		} catch (IOException e) {
			System.out.println("Error 404: No se ha podido conectarse al servidor");
        	System.out.println(e.getMessage());
            System.exit(-1);
		}
		
        // Flujo salida (su funcionamiento es similar al de los fichero en java)
		salida = null;
		try {
			salida = new DataOutputStream(s.getOutputStream());
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		// Flujo entrada (su funcionamiento es similar al de los fichero en java)
		InputStream is = s.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		
		// Le pido al cliente su nombre de usuario (este m�todo comprobar� que no existe otro usuario con el
		// mismo nombre)
		String nombre = comprobarNombre(nombres);
		
		// Si el nombre del usuario est� libre, lo escribimos en el fichero para evitar que lo vuelvan a coger
		escribirNombres(nombres, nombre);
		
		// Recibimos los mensajes del servidor (en este caso el fichero con el historial), los guardamos en un array de tipo String y 
		// los separamos por el -
		String[] mensajes = dis.readUTF().split("-");
		
		// Instanciamos un objeto de la clase ClienteFrame y le pasamos el nombre del cliente
		ClienteFrame clf = new ClienteFrame(nombre);
		
		// Mostramos la interfaz
		clf.setVisible(true);
		
		// Invocamos el m�todo mostrarMensaje para mostrar el historial
		clf.mostrarMensaje(mensajes);
			
			
			// Flujo entrada (su funcionamiento es similar al de los fichero en java). Este DataInputStream recibir� los mensajes
		    // enviados por los dem�s clientes
            InputStream iss = s.getInputStream();
            DataInputStream diss = new DataInputStream(iss);
            
            // Cremos un hilo y le pasamos el DataInputStream reci�n creado. Este hilo va a encargarse de estar leyendo continuamente
            // todos los mensajes que reciba del servidor (los mensajes enviados por TODOS los clientes)
            HiloCliente hc = new HiloCliente(dis, clf);
            
            // Lanzamos un hilo por cliente que se encargar� de leer continuamente
            hc.start();
		
	}
	
	// Este m�todo servir� para escribir en un fichero el nombre del cliente que acaba de
	// entrar en el chat para evitar que entre otro y se ponga el mismo nombre
	public static void escribirNombres(File nombres, String nombre) {
        BufferedWriter br;
		try {
			br = new BufferedWriter(new FileWriter("nombres.txt", true));
	        br.write(nombre);
	        br.write("\n");
	        
	        br.close();
		} catch (IOException e) {
			System.out.println("El nombre no puede estar vac�o");
			e.printStackTrace();
		}
		
	}
	
	// Este m�todo nos va a servir a saber si va a registrarse un usuario con el nombre repetido
	public static String comprobarNombre(File nombres) {
		int i = 0;
		String nombre = "";
		
		// Este do while va a repetir un segmento de c�digo en el que se va a pedir al cliente que
		// introduzca su nombre y, en caso de que ya exista otro cliente con el mismo nombre
		// registrado en el chat, se repita hasta que escriba uno que est� libre
		do{
			
			// Le pedimos al cliente su nombre de usuario
			nombre = JOptionPane.showInputDialog("Introduce tu nombre de usuario:");
			
			try {
				BufferedReader br = new BufferedReader(new FileReader("nombres.txt"));
				String linea = "";

				// Si al llegar al final del do while el valor de esta variable sigue siendo 0, salimos del bucle
				i = 0;
				linea = br.readLine();
				while(linea != null) {
					if(linea.equalsIgnoreCase(nombre)) {
						
						// Si ya existe un usuario registrado con un nombre que est� en el fichero nombres.txt,
						// mostramos un mensaje al usuario y a�adimos 1 al valor de i (que recordemos que est� inicializada a 0)
						// Al valor i > 0, el bucle se repetir�. Volveremos al do y el valor de i se reiniciar� otra vez a 0
						
						JOptionPane.showMessageDialog(null, "El nombre ya est� cogido, elige otro ", "ERROR", JOptionPane.ERROR_MESSAGE, null);
						i++;					
					}
					
					linea = br.readLine();
				}
					
				br.close();
				
				// En caso de que el cliente introduzca un espacio vac�o, tambi�n sumaremos 1 al valor de i
				// por lo que el bucle se repetir�
				if (nombre.equalsIgnoreCase("")) {
					System.out.println("El nombre no puede estar vac�o");
					JOptionPane.showMessageDialog(null, "El nombre no puede estar vac�o", "ERROR", JOptionPane.ERROR_MESSAGE, null);
					i++;
				}
			
			} catch (IOException e1) {
				System.out.println("Error a al hora de leer el fichero nombres.txt");
				e1.printStackTrace();
			}
			
			// El bucle se repetir� mientras el valor de i sea mayor que 0
		}while(i > 0);

		// Si el cliente ha introducido un nombre no repetido, devolvemos ese nombre
		return nombre;
		
	}
	
	// Este m�todo servir� para que el cliente escriba mansajes y se los mande al servidor, que se encargar�
	// de reenvi�rselos a los demas clientes, sin embargo, este m�todo ser� invocado en la clase ClienteFrame
	public static void escribir(String nombre, ClienteFrame clf) {
		
		// Creamos una variable de tipo String en la que vamos a guardar el mensaje que quiere mandar el servidor
		String cadena = "";
		cadena = clf.mensaje.getText();
		try {
			
			// Enviamos todo al servidor
			salida.writeUTF(nombre + ": " + cadena);
			
			if(cadena.equalsIgnoreCase("*")) {
				System.out.println("Te has desconectado.");
				// Cerramos la conexion de los sockets
				try {
					salida.close();
					System.exit(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			System.out.println("Error a la hora de enviar el mensaje al servidor");
			e.printStackTrace();
		}
	}

	// Este m�todo se invocar� cuando queramos desconectarnos como cliente, este m�todo tambi�n ser� invocado en la clase ClienteFrame
	public static void desconectarCliente(String nombre) {

		try {
			
			// Lo primer que har� ser� mandar un * al cliente, que matar� al hilo encargado de recibir los mensajes
			// de este cliente en concreto
			salida.writeUTF(nombre + ":" + "*");
			
			// Cerramos el socket
			salida.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}