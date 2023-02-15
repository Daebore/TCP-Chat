package interfaz;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Scanner;
import util.MainCliente;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ClienteFrame extends JFrame implements ActionListener{
	
	// Declaraci�n de variables
	
	// Nombre del cliente
	String nombre;
	
	// La parte del chat en la que escribimos
	public JTextField mensaje = new JTextField();
	
	// JScrollPane nos permitir� hacer scroll en el chat por si se envian muchos mensajes
	public JScrollPane scrollpanel;
	
	// La parte del chat que mostrar� los mensajes
	public JTextArea textarea;
	
	// El bot�n para enviar los mensajes
	public JButton botonEnviar= new JButton("enviar");
	
	// El bot�n para desconectar el cliente
	public JButton botonSalir= new JButton("salir");

	public ClienteFrame(String nombre) {
		
		// Todo este c�digo se ha modificado ligeramente, pero gran parte de �l
		// ha sido ya escrito por el profesor para dise�ar la interfaz gr�fica del chat.
		// Se ha modificado para mostrar los mensajes escritos y enviados y se ha a�adido una barra 
		// de scroll horizontal
		super("Conexi�n con chat:" + nombre);
		this.nombre = nombre;
		setSize(540,420);
		setLayout(null);
		mensaje.setBounds(10,10,400,30);
		this.add(mensaje);
		textarea = new JTextArea();
		scrollpanel = new JScrollPane(textarea);
		scrollpanel.setBounds(10,50,400,300);
		this.add(scrollpanel);
		botonEnviar.setBounds(410,10,100,30);
		botonEnviar.addActionListener(this);
		this.add(botonEnviar);
		botonSalir.setBounds(410,50,100,30);
		botonSalir.addActionListener(this);
		this.add(botonSalir);
		textarea.setBounds(0, 0, 550, 400);
		textarea.setEditable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	// Este m�todo nos permitir� mostrar los mensajes en modo gr�fico
	public void mostrarMensaje(String[] mensajes) {
		
		// Por cada cliente conectado, vamos a mostrar un mensaje de bienvenida
		String bienvenida = "Recuerda nuestras normas:\n1. Saluda al llegar y desp�dete al irte, todo Jedi"
				+ " debe ser educado.\n2. Respeta a todos, el respeto es fundamental en el c�digo"
				+ " Jedi.\n3. No se permitir� lenguaje soez, es el camino al lado oscuro.\n4. El SPAM est�"
				+ " totalmente prohibido.\n5. �P�satelo bien!\nQue la fuerza te acompa�e.\n" + "\n";
				
		textarea.setText(bienvenida + "\n");
		
		// Con este m�todo vamos a lerr el contenido del fichero historial para mostrarlo por pantalla
		for (int i = 0; i < mensajes.length; i++) {
			textarea.setText(textarea.getText() +  mensajes[i] + "\n");
		}
			
	}

	// Este m�todo servir� para dar utilidad a los botones
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Si se pulsa el bot�n enviar...
		if (e.getSource() == botonEnviar) {
			String texto = nombre + " :" + mensaje.getText();

			// Instanciamos un objeto de la clase MainCliente para poder acceder al m�todo programado para escribir
			// y enviar los mensajes al servidor
			MainCliente.escribir(nombre, this);
			mensaje .setText("");
		}
		
		// Si se pulsa el boton salir, instanciamos un objeto MainCliente para acceder al m�todo desconectarCliente
		// quer env�a un * al servidor para desconectar los sockets del cliente y el servidor y matar al hilo lanzamos por el
		// servidor para gestionar la conexi�n con el cliente en concreto.
		// Despu�s, cerramos la interfaz gr�fica con un exit
		if (e.getSource() == botonSalir) {
			MainCliente.desconectarCliente(nombre);
			System.exit(0);
		}
	}

}
