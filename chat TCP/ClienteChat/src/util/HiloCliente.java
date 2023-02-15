package util;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;

import interfaz.ClienteFrame;

public class HiloCliente extends Thread{
	
	// Declaración de variables
	
	// Le pasaremos al hilo un DataInputStream para que pueda leer lo que le llega
	DataInputStream dis;
	
	// Tendrá también un ClienteFrame para poder mostrar un chat en modo gráfico
	ClienteFrame clf;
	
	// Constructor
	public HiloCliente(DataInputStream diss, ClienteFrame clf) {
		this.dis = diss;
		this.clf = clf;
	}
	
	// Método run
	public void run() {
		
		// Este hilo contendrá un hilo infinito que se encargará de mostrar pon pantalla todos los mensajes
		// que recibe
		while(true) {
			try {
				
				// Guardamos los mensajes recibidos en una variable String
				String mensaje = dis.readUTF();
				
				// Concatenamos el nuevo mensaje recibido al contenido del ClienteFrame
				clf.textarea.setText(clf.textarea.getText() + mensaje + "\n");
				
			} catch (IOException e) {
				System.exit(0);
			}
		}
	}

}
