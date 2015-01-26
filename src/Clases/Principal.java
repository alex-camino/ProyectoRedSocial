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
		

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		String respuesta;
		boolean repetir;
		do{
			
			System.out.println("Seguro que desea darse de baja de la RED-SOCIAL??? si / no");
			respuesta = lector.nextLine();

			
			if (respuesta.equalsIgnoreCase("si")){
				
				repetir=false;
				
				for (int i = 0; i < grupos.size(); i++) {
					
					grupos.get(i).abandonarGrupo(user, db);
				}
				System.out.println("La información de los grupos ha sido eliminada.");
				
				user.bajaUsuario(db);
				System.out.println("EL USUARIO HA SIDO ELIMINADO DE LA RED SOCIAL CORRECTAMENTE.");
			}else if(respuesta.equalsIgnoreCase("no")){
				
				System.out.println(user.getNombre()+" "+user.getApellidos()+", se ha cancelado la operación. Gracias por confiar en nosotros.");
				repetir=false;
				
			}else{
				
				System.out.println("La respuesta introducida es incorrecta.");
				repetir=true;
			}
			
		}while(repetir);
		
				
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
							  +"3-. Comentar en un grupo.\n"
							  +"4-. Eliminar un grupo.\n"
							  +"5-. Visualizar comentarios de los grupos\n"
							  +"6-. Listar los usuarios de mi localidad de un grupo.\n"
							  +"7-. Mostrar numero de usuarios por grupo.\n"
					          +"8-. Salirse de un grupo.\n"
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
				
					comentarGrupo(user);
					
				break;
					
			case 4:
					
					eliminarGrupo(user);
				break;
		
			case 5:
					
					visualizarComentariosGrupos(user);
				break;
				
			
			case 6:
					
					visualizarUsuariosLocalidadGrupo(user);
					
				break;
	
			case 7:
					
					cantidadUsuariosGrupo(user);
					
				break;
				
			case 8:
				
					salirDelGrupo(user);
				
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
		boolean repetir;
		do{
			
			System.out.print("Introduzca el nombre del grupo que desea crear: ");
			nombreGrupo = lector.nextLine();

			if(g.comprobarGrupo(nombreGrupo, db)){
				
				System.err.println("Ese nombre de grupo ya esta en uso, introduzca otro.");
				repetir=true;
			}else{
				
				g.crearGrupo(nombreGrupo, db, user);
				repetir=false;
				
			}
			
		}while(repetir);
		
		
		
	}
	
	
	private static void comentarGrupo(Usuario user) {
		
		System.out.println("Puedes comentar en los siguientes grupos: ");

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		int opcion=0;
		boolean repetir;

		do {

			
			for (int i = 0; i < grupos.size(); i++) {

				System.out.print((i + 1)+ "-. "); 
				System.out.println(grupos.get(i).getNombre());

			}

				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
				
			System.out.print("Introduzca el grupo en el que desea comentar : ");
			opcion = Excepciones.enteros();

			
			if(opcion==0){
				
				repetir=false;
				
				System.out.println("Volviendo al menú usuario.");
				
			}else if (opcion<1 || opcion>grupos.size()) {

				System.err.println("Tiene que escoger uno de los grupos que aparecen.");
				repetir=true;

			} else {

				repetir=false;
				
				System.out.print("Comentario: ");

				String comentario = lector.nextLine();

				grupos.get(opcion-1).addComentarioGrupo(user, db, comentario);

			}
			

		} while (repetir);
	}
		
	private static void visualizarComentariosGrupos(Usuario user) {
		
		System.out.println("Puedes visualizar los comentarios de uno de los siguientes grupos: ");

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		int opcion=0;
		boolean repetir;

		do {

			
			for (int i = 0; i < grupos.size(); i++) {

				System.out.print((i + 1)+ "-. "); 
				System.out.println(grupos.get(i).getNombre());

			}

			
				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
				
			System.out.print("Introduzca el grupo en el que desea buscar los comentarios : ");
			opcion = Excepciones.enteros();

			
			if(opcion==0){
				
				repetir=false;
				
				System.out.println("Volviendo al menú usuario.");
				
			}else if (opcion<1 || opcion>grupos.size()) {

				System.err.println("Tiene que escoger uno de los grupos que aparecen.");
				repetir=true;

			} else {

				repetir=false;
				
				

				grupos.get(opcion-1).mostrarComentariosUsuario(user, db);

			}
			

		} while (repetir);
		
	}

	/*
	 * Mostrará los usuarios que hay en un determinado grupo que sean de mi misma localidad.
	 * */
	public static void visualizarUsuariosLocalidadGrupo(Usuario user){
		
		System.out.println("Puedes visualizar los usuarios de uno de los siguientes grupos: ");

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		int opcion=0;
		boolean repetir;

		do {

			
			for (int i = 0; i < grupos.size(); i++) {

				System.out.print((i + 1)+ "-. "); 
				System.out.println(grupos.get(i).getNombre());

			}

				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
			
			System.out.print("Introduzca el grupo en el que desea buscar los usuarios : ");
			opcion = Excepciones.enteros();

			
			if(opcion==0){
				
				repetir=false;
				
				System.out.println("Volviendo al menú usuario.");
				
			}else if (opcion<1 || opcion>grupos.size()) {

				System.err.println("Tiene que escoger uno de los grupos que aparecen.");
				repetir=true;

			} else {

				repetir=false;
				
				

				grupos.get(opcion-1).mostrarUsuariosLocalidadGrupo(user, db);

			}
			

		} while (repetir);
	}
	
	/*
	 * Método que devuelve la cantidad de usuarios que tiene el grupo.
	 * */
	
	private static void cantidadUsuariosGrupo(Usuario user) {
		
		
		System.out.println("Puedes visualizar la cantidad de usuarios de uno de los siguientes grupos: ");

		//Llamamos al metodo mostrar grupos a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);

		int opcion=0;
		boolean repetir;

		do {

			
			for (int i = 0; i < grupos.size(); i++) {

				System.out.print((i + 1)+ "-. "); 
				System.out.println(grupos.get(i).getNombre());

			}

				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
				
			System.out.print("Introduzca el grupo del que desees saber la cantidad de usuarios que tiene : ");
			opcion = Excepciones.enteros();

			if(opcion==0){
				
				repetir=false;
				
				System.out.println("Volviendo al menú usuario.");
				
			}else if (opcion<1 || opcion>grupos.size()) {

				System.err.println("Tiene que escoger uno de los grupos que aparecen.");
				repetir=true;

			} else {

				repetir=false;

				grupos.get(opcion-1).cantidadUsuariosGrupo(user, db);

			}
			

		} while (repetir);
		
	}
	private static void salirDelGrupo(Usuario user) {
		
		
		//Llamamos al metodo mostrar grupos en los que esta el usuario a partir de un usuario
		ArrayList<Grupo> grupos = Grupo.mostrarGrupos(user, db);
		int opcion=0;
		boolean repetir;
		
		
		//Mostramos los grupos en los que el usuario esta unido
		if(grupos.size()!=0){
		
			System.out.println("Usted esta unido en los siguientes grupos: ");
			
			for(int i=0;i<grupos.size();i++){
				
				System.out.println((i+1)+"-. "+grupos.get(i).getNombre());
			}
			
				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
			do {

				System.out.print("Introduzca el grupo del que desees darte de baja : ");
				opcion = Excepciones.enteros();

				
				if(opcion==0){
					
					repetir=false;
					
					System.out.println("Volviendo al menú usuario.");
					
				}else if (opcion<1 || opcion>grupos.size()) {

					System.err.println("Tiene que escoger uno de los grupos que aparecen.");
					repetir=true;

				} else {

					repetir=false;

					grupos.get(opcion-1).abandonarGrupo(user, db);

				}
				

			} while (repetir);
			
			
			
		}else{
			
			System.out.println("Usted no esta unido en ningún grupo.");
		}
		
	}


	private static void eliminarGrupo(Usuario user) {
		
		//Llamamos al metodo mostrar grupos Administrador en los que el usuario es Admin
		ArrayList<Grupo> grupos = Grupo.mostrarGruposAdmin(user, db);
		int opcion=0;
		boolean repetir;
		
		
		//Mostramos los grupos en los que el usuario esta unido
		if(grupos.size()!= 0){
		
			System.out.println("Usted es Administrador de los siguientes grupos: ");
			
			for(int i=0;i<grupos.size();i++){
				
				System.out.println((i+1)+"-. "+grupos.get(i).getNombre());
			}
			
				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
			do {

				System.out.print("Introduzca el grupo del que desees darte de baja : ");
				opcion = Excepciones.enteros();

				if(opcion==0){
					
					repetir=false;
					
					System.out.println("Volviendo al menú usuario.");
					
				}else if (opcion<1 || opcion>grupos.size()) {

					System.err.println("Tiene que escoger uno de los grupos que aparecen.");
					repetir=true;

				} else {

					repetir=false;

					grupos.get(opcion-1).borrarGrupo(db);

					System.out.println("El grupo se ha borrado correctamente.");
				}
				

			} while (repetir);
			
			
			
		}else{
			
			System.out.println("Usted no es Administrador de ningun grupo.");
		}
		
	}
	

	
	
}
