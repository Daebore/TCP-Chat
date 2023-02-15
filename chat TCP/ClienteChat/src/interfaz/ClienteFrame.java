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
	
	// Declaración de variables
	
	// Nombre del cliente
	String nombre;
	
	// La parte del chat en la que escribimos
	public JTextField mensaje = new JTextField();
	
	// JScrollPane nos permitirá hacer scroll en el chat por si se envian muchos mensajes
	public JScrollPane scrollpanel;
	
	// La parte del chat que mostrará los mensajes
	public JTextArea textarea;
	
	// El botón para enviar los mensajes
	public JButton botonEnviar= new JButton("enviar");
	
	// El botón para desconectar el cliente
	public JButton botonSalir= new JButton("salir");

	public ClienteFrame(String nombre) {
		
		// Todo este código se ha modificado ligeramente, pero gran parte de él
		// ha sido ya escrito por el profesor para diseñar la interfaz gráfica del chat.
		// Se ha modificado para mostrar los mensajes escritos y enviados y se ha añadido una barra 
		// de scroll horizontal
		super("Conexión con chat:" + nombre);
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

	// Este método nos permitirá mostrar los mensajes en modo gráfico
	public void mostrarMensaje(String[] mensajes) {
		
		// Por cada cliente conectado, vamos a mostrar un mensaje de bienvenida
		String bienvenida = "Recuerda nuestras normas:\n1. Saluda al llegar y despídete al irte, todo Jedi"
				+ " debe ser educado.\n2. Respeta a todos, el respeto es fundamental en el código"
				+ " Jedi.\n3. No se permitirá lenguaje soez, es el camino al lado oscuro.\n4. El SPAM está"
				+ " totalmente prohibido.\n5. ¡Pásatelo bien!\nQue la fuerza te acompañe.\n" + "\n";
				
		textarea.setText(bienvenida + "\n");
		
		// Con este método vamos a lerr el contenido del fichero historial para mostrarlo por pantalla
		for (int i = 0; i < mensajes.length; i++) {
			textarea.setText(textarea.getText() +  mensajes[i] + "\n");
		}
			
	}

	// Este método servirá para dar utilidad a los botones
	@Override
	public void actionPerformed(ActionEvent e) {
		
		// Si se pulsa el botón enviar...
		if (e.getSource() == botonEnviar) {
			String texto = nombre + " :" + mensaje.getText();

			// Instanciamos un objeto de la clase MainCliente para poder acceder al método programado para escribir
			// y enviar los mensajes al servidor
			MainCliente.escribir(nombre, this);
			mensaje .setText("");
		}
		
		// Si se pulsa el boton salir, instanciamos un objeto MainCliente para acceder al método desconectarCliente
		// quer envía un * al servidor para desconectar los sockets del cliente y el servidor y matar al hilo lanzamos por el
		// servidor para gestionar la conexión con el cliente en concreto.
		// Después, cerramos la interfaz gráfica con un exit
		if (e.getSource() == botonSalir) {
			MainCliente.desconectarCliente(nombre);
			System.exit(0);
		}
	}

}
