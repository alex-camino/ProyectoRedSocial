package Clases;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class Principal {


	private static Mongo mongoClient;
	public static Scanner lector= new Scanner(System.in);
	private static DB db;
	
	public static void main(String[] args) throws UnknownHostException, MongoException {
		
		
		
		/* Establece la conexion al servidor en localhost:27017, a la base de datos "test" */
		mongoClient = new Mongo( "localhost" , 27017 );
		db = mongoClient.getDB( "redsocial" );
		
		menuPrincipal(db);
		
	}
	
	
	////////////////////// OPCIONES MENU PRINCIPAL //////////////////////////////
	
	public static void menuPrincipal(DB db){
		
		int opMenu=-1;

		do{
			System.out.println("--RED SOCIAL 2.0--");
			System.out.println("------------------");
			System.out.println("MENU PRINCIPAL\n"
					          +"==============\n"
							  +"1-. Crear usuario.\n"
							  +"2-. Conectarse como usuario.\n"
					          +"0-. Salir de la Red Social\n");
			System.out.print("Introduzca la opcion que desea: ");
			opMenu=Excepciones.enteros();
			
			switch(opMenu){
			
			case 0:
					System.out.println("Ha salido de la Red Social");
					
				break;
			
			case 1:
					System.out.println("Alta de un nuevo usuario.");
					
					crearUsuario(db);
				break;
			
			case 2:
					System.out.println("Conectando....");
					logearse(db);
				break;
					
			
			default:
				
				System.err.println("La opción introducida no es valida.");
				
			}
		}while(opMenu!=0);
	}
	
	public static void crearUsuario(DB db){
		
		String nombre,apellidos,correo, password;
		String[] direccion = new String[4];
		
		System.out.println("Introduzca su nombre:");
		nombre=lector.nextLine();
		System.out.println("Introduzca sus apellidos:");
		apellidos=lector.nextLine();
		System.out.println("Introduzca su correo electronico:");
		correo=lector.nextLine();
		System.out.println("Dirección,");
		System.out.print("Calle: ");
		direccion[0] = lector.nextLine();
		System.out.print("Nº: ");
		direccion[1] = lector.nextLine();
		System.out.print("Localidad: ");
		direccion[2] = lector.nextLine();
		System.out.print("C.P: ");
		direccion[3] = lector.nextLine();
		System.out.println("Introduzca su password:");
		password=lector.nextLine();
	
		
		Usuario newUsuario = new Usuario();
		newUsuario.crearUsuario(nombre,apellidos,correo,direccion,password, db);
		
	}
	
	public static void logearse(DB db){
		
		String correo, password;
		
		System.out.println("Introduzca su correo:");
		correo=lector.nextLine();
		System.out.println("Introduzca su password:");
		password=lector.nextLine();
		
		Usuario user= new Usuario();
		
		if(user.logearse(correo, password, db)){
			
			menuRedSocial(user);
		}else{
			
			System.err.println("ERROR AL LOGEARSE.");
		}
		
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//////////////////////////////////// OPCIONES MENU USUARIO ////////////////////////////////////////////////

	public static void menuRedSocial(Usuario user){
		
		
		int opMenu=-1;
		
		System.out.println("Bienvenido "+ user.getNombre());
		
		do{
			System.out.println("--RED SOCIAL 2.0--");
			System.out.println("------------------");
			System.out.println("1-. Gestionar grupos.\n"
							  +"2-. Darse de baja..\n"
					          +"0-. Desconectarse.\n");
			System.out.print("Introduzca la opcion que desea: ");
			opMenu=Excepciones.enteros();
			
			switch(opMenu){
			
			case 0:
					System.out.println("Ha salido de su sesión");
					
				break;
			
			case 1:
					System.out.println("Redirigiendolo al menu de grupos.");
					
					gestionarGrupos(user);
				break;
			
			case 2:
					System.out.println("Dar de baja usuario.");
					bajaUsuario(user);
				break;
					
			
			default:
				
				System.err.println("La opción introducida no es valida.");
				
			}
		}while(opMenu!=0);
	}
	
	
	private static void bajaUsuario(Usuario user) {
		
		
		String respuesta = "No";
		
		while (true) {

			ArrayList<Grupo> grupos = Grupo.mostrarGruposAdmin(user, db);
			
			
			// Averiguar si el usuario es administrador de algun grupo.
			
			if(grupos.size()>0){
			
				System.out.println(user.getNombre() + " eres administrador de los siguientes grupos: ");
				
				for(int i=0; i<grupos.size(); i++){
					
					System.out.println(grupos.get(i).getNombre());
				
				}
				
				System.out.println("Si te das de baja, se te eliminará como Adminstrador de los grupos pasando a ser Administrador el siguiente usuario.");
				
			}
			
			System.out.println("Seguro que desea darse de baja de la RED-SOCIAL??? si / no");
			respuesta = lector.nextLine();

			if (Character.toString(respuesta.charAt(0)).equalsIgnoreCase("S")||Character.toString(respuesta.charAt(0)).equalsIgnoreCase("s")) {
				
				//llamamos al metodo de darse de baja de la clase usuario.
				user.bajaUsuario(db);
				
				//Recorremos todos los grupos para poder quitar como administrador del grupo al Usuario
				if(grupos.size()>0){
					
					for(int i=0; i<grupos.size(); i++){
						
						grupos.get(i).abandonarGrupo(user, db);
					
					}
						
				}
				
				System.out.println("Se le ha eliminado de la BBDD de la RED-SOCIAL.");
				
				
			} else if (Character.toString(respuesta.charAt(0)).equalsIgnoreCase("N")||Character.toString(respuesta.charAt(0)).equalsIgnoreCase("n")) {
				
				System.out.println("Gracias por seguir con nosotros :), se le devolvera al menu");
				
			}

			System.out.println("La respuesta no es correcta.");
		}

	}

	
	///////////////////////////////// GRUPOS ////////////////////////////////////////////
	
	public static void gestionarGrupos(Usuario user){
		
		int opMenu=-1;

		do{
			System.out.println("--RED SOCIAL 2.0--");
			System.out.println("------------------");
			System.out.println("GESTIÓN DE GRUPOS\n"
							  +"1-. Crear a un grupo.\n"
							  +"2-. Unirse a un grupo.\n"
							  +"3-. Eliminar un grupo.\n"
							  +"4-. Comentar en un grupo.\n"
							  +"5-. Visualizar comentarios de los grupos\n"
							  +"6-. Listar los usuarios de mi localidad de un grupo.\n"
					          +"7-. Salirse de un grupo.\n"
							  +"0-. Volver.\n");
			System.out.print("Introduzca la opcion que desea: ");
			opMenu=Excepciones.enteros();
			
			switch(opMenu){
			
			case 0:
					System.out.println("Volviendo al menu de usuario...");
					
				break;
			
			case 1:
					
					crearGrupo(user);
				break;
			
			case 2:
				
					user.unirseGrupo(user, db);
				break;
			case 3:
				
					//eliminarGrupo();
				break;
					
			case 4:
					
					comentarGrupo(user);
				break;
		
			case 5:
					
					//visualizarGrupos();
				break;
				
			
			case 6:
					
					//listarUsuarios();
					
				break;
	
			case 7:
					
					//salirDelGrupo();
					
				break;
			
			default:
				
				System.err.println("La opción introducida no es valida.");
				
			}
		}while(opMenu!=0);
	}

	
	///////////////// OPCIONES DE GRUPO //////////////////////////////////////
	
	private static void crearGrupo(Usuario user) {
		

		String nombreGrupo = "";
		Grupo g = new Grupo();

		System.out.print("Introduzca el nombre del grupo que desea crear: ");
		nombreGrupo = lector.nextLine();

		g.crearGrupo(nombreGrupo, db, user);
		
		
	}
	
	
	private static void comentarGrupo(Usuario user) {
		
		System.out.println("Puedes comentar en los siguientes grupos: ");

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		int opcion=0;
		boolean repetir=true;

		do {

			
			for (int i = 0; i < grupos.size(); i++) {

				System.out.print((i + 1)+ "-. "); 
				System.out.println(grupos.get(i).getNombre());

			}

			System.out.print("Introduzca el grupo en el que desea comentar : ");
			opcion = Excepciones.enteros();

			if (opcion<1 || opcion>grupos.size()) {

				System.err.println("Tiene que escoger uno de los grupos que aparecen.");
				repetir=true;

			} else {

				repetir=false;
				
				System.out.print("Comentario: ");

				String comentario = lector.nextLine();

				grupos.get(opcion-1).addComentarioGrupo(user, db, comentario);

			}
			

		} while (opcion == -1);
	}
		
	

	private static void salirDelGrupo() {
		// TODO Auto-generated method stub.
		
	}

	private static void listarUsuarios() {
		// TODO Auto-generated method stub
		
	}

	private static void visualizarGrupos() {
		// TODO Auto-generated method stub
		
	}

	private static void eliminarGrupo() {
		// TODO Auto-generated method stub
		
	}
	

	
	
}
