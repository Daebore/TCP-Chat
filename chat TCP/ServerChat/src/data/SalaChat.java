package data;

import java.io.File;
import java.net.Socket;

public class SalaChat {
	
	// Declaración de variables
	
	// Esta variable va a controlar el número de conexiones totales
	private int conexiones;
	
	// El socket actual que pasaremos al hilo
	private int actuales;
	
	// Esta variable servirá para controlar que no haya + de 10 clientes conectados
	private int MAXIMO;
	
	// Los objetos instanciados de este clase tendrán un array de sockets
	public Socket tabla[] = new Socket[MAXIMO];
	
	// También tendrán un fichero llamado historial que contendrá un historial de
	// todos los mensajes enviados y recibidos en los chats
	File historial;
	
	// Método constructor
	public SalaChat(int mAXIMO,int conexiones, int actuales, Socket[] tabla, File historial) {
		
		// Estas dos variables servirán para selecionar el
		this.setConexiones(conexiones);
		this.setActuales(actuales);
		int MAXIMO = mAXIMO;
		this.tabla = tabla;
		this.historial = historial;
		
	}

	// Getters/Setters
	public synchronized int getConexiones() {
		return conexiones;
	}

	public synchronized void setConexiones(int conexiones) {
		this.conexiones = conexiones;
	}

	public synchronized int getActuales() {
		return actuales;
	}

	public synchronized void setActuales(int actuales) {
		this.actuales = actuales;
	}
	public int getMAXIMO() {
		return MAXIMO;
	}

	public void setMAXIMO(int mAXIMO) {
		MAXIMO = mAXIMO;
	}
	
	public File getHistorial() {
		return historial;
	}

	public void setHistorial(File historial) {
		this.historial = historial;
	}
	
	public Socket[] getTabla() {
		return tabla;
	}

	public void setTabla(Socket[] tabla) {
		this.tabla = tabla;
	}

	// Le damos un socket a cada nuevo usuario conectado al chat para que pueda
	// establecer una conexión con el servidor
	public void addTabla (Socket s,int i) {
		tabla[i] = s;
	}
	
	// Con este método accedemos al array y nos devulve una posición en cocreta pasada
	// por parámetro
	public Socket getElementTabla(int i) {
		return tabla[i];
	}

	
}