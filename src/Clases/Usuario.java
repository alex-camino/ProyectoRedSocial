package Clases;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Usuario {

	
	private DBCollection collection;	
	
	private ObjectId id;
	private String nombre;
	private String apellidos;
	private String correo;
	private String[] direccion=new String[4];
	private String password;
	
	
	
	public Usuario(){
		
	}
	public Usuario(ObjectId id, String nombre, String apellidos, String correo,String password, String[] direccion) {

		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.correo = correo;
		this.password = password;
		this.direccion[0] = direccion[0];
		this.direccion[1] = direccion[1];
		this.direccion[2] = direccion[2];
		this.direccion[3] = direccion[3];

	}
	public void crearUsuario(String nombre, String apellidos, String correo, String[] direccion, String password, DB db){
		
		/* Con la clase BasicDBObject creamos objetos Mongo y lo insertamos en la coleccion "user" */
		BasicDBObject doc = new BasicDBObject();
			//doc.put("_id", new Long(1));
	        doc.put("nombre", nombre);
	        doc.put("apellidos",apellidos);
	        doc.put("correo", correo);
	        doc.put("direccion",
					new BasicDBObject("calle", direccion[0])
							.append("numero", direccion[1])
							.append("localidad", direccion[2])
							.append("codigo postal", direccion[3]));
	        doc.put("password", password);
	        
	    this.collection = db.getCollection("usuario");				
		collection.save(doc);
		
		System.out.println("El usuario ha sido creado correctamente, ya puede logearse.");
		
	}
	
	/////////////////////////// SETTER Y GETTER ///////////////////////////////////////

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String[] getDireccion() {
		return direccion;
	}

	public void setDireccion(String[] direccion) {
		this.direccion = direccion;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	///////////////////////////////// OPCIONES USUARIO ////////////////////////////////////////////
	
	public void crearUsuario(DB db){
		
		String nombre,apellidos,correo, password;
		String[] direccion = new String[4];
		
		System.out.println("Introduzca su nombre:");
		nombre=Principal.lector.nextLine();
		System.out.println("Introduzca sus apellidos:");
		apellidos=Principal.lector.nextLine();
		correo=comprobarCorreo(db);
		System.out.println("Dirección,");
		System.out.print("Calle: ");
		direccion[0] = Principal.lector.nextLine();
		System.out.print("Nº: ");
		direccion[1] = Principal.lector.nextLine();
		System.out.print("Localidad: ");
		direccion[2] = Principal.lector.nextLine();
		System.out.print("C.P: ");
		direccion[3] = Principal.lector.nextLine();
		System.out.println("Introduzca su password:");
		password=Principal.lector.nextLine();
		
		this.crearUsuario(nombre, apellidos, correo, direccion, password, db);
	}
	
	public String comprobarCorreo(DB db){
		
		boolean emailCorrecto, repetir;
		String email=""; 
        Pattern plantilla = null;
        Matcher resultado = null;
        
        plantilla = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
        
        do{
        	
	        do{
	        	
	        	System.out.println("Introduzca su email:");
	            email=Principal.lector.nextLine();
	            
	            resultado = plantilla.matcher(email);
	            
	            if(resultado.find()==true){
	            	
	            	repetir=false;
	                     
	            }else{
	            	
	            	repetir=true;
	                System.out.println("El email que ha introducido es incorrecto. \n"
	                				  +"Ejemplo de email:  juanito@gmail.com");
	            }
	            
	        }while(repetir);
       
        	emailCorrecto=comprobarCorreoUsuario(db, email);
        	
        }while(!emailCorrecto);
        
        return email;
	}
	
	/*
	 * Método que comprobará que un correo no se introduce dos veces. 
	 * */
	
	public boolean comprobarCorreoUsuario(DB db, String correo){
		
		this.collection = db.getCollection("usuario");
		
		DBCursor cursor = collection.find();
		
		for (DBObject usuario : cursor) {
			
			if(correo.equals(usuario.get("correo"))){
				
				System.out.println("El correo introducido ya esta en uso, introduzca otro.");
				return false;
			}
			
		}
		
		return true;
	}
	
	public boolean logearse(String correo, String password, DB db){
		
		this.collection = db.getCollection("usuario");	
		
		
		/* Con la clase BasicDBObject tambien creamos objetos con los que hacer consultas */
		
		//BasicDBObject query = new BasicDBObject("correo","alcam").append("password","321");
		BasicDBObject query = new BasicDBObject();
		query.put("correo",correo);
		query.put("password", password);
		DBCursor cursor = collection.find(query);
		
		for (DBObject usuario: cursor) {
			
			
			// Asigno los valores que me devuelve mongo al usuario.
			this.id=(ObjectId)usuario.get("_id");
			this.nombre=usuario.get("nombre").toString();
			this.apellidos=usuario.get("apellidos").toString();
			this.correo=usuario.get("correo").toString();
			DBObject direccion = (DBObject) usuario.get("direccion");

			this.direccion[0] = (String) direccion.get("calle");
			this.direccion[1] = (String) direccion.get("numero");
			this.direccion[2] = (String) direccion.get("localidad");
			this.direccion[3] = (String) direccion.get("codigo postal");
			
			this.password=usuario.get("password").toString();
			
			return true;
		}
		
		return false;
	}
	
	/*
	 * Método que añade el grupo al array de grupos en el usuario.
	 * */
	public void addGrupoUsuario(Usuario user, Grupo grupo_1, DB db){
		
		Date fechaCreacion = new Date();
		
		DBCollection collection = db.getCollection("usuario");	
		
		
		BasicDBObject buscarUsuario = new BasicDBObject("_id", user.getId());

		DBObject updateQuery = new BasicDBObject("$push", new BasicDBObject("grupos", new BasicDBObject("nombre", grupo_1.getNombre()).append("fecha", fechaCreacion)));

		collection.update(buscarUsuario, updateQuery);

		grupo_1.addUserGrupo(user, db);
	}
	
	
	
	
	/*
	 * Método que añade un grupo al array de usuarios.
	 * */
	public void unirseGrupo(Usuario user, DB db) {
		
		ArrayList<Grupo> grupos= Grupo.gruposDisponibles(user, db);
		
		int opcion;
		boolean repetir=false;
		
		System.out.println("A que grupo te quieres unir??");
		
		do{
			
			for(int i=0;i<grupos.size();i++){
				
				System.out.println((i+1)+"-. "+grupos.get(i).getNombre());
			}
				System.out.println("0-. Volver al menú.");
				System.out.println(" ");
				
			opcion=Excepciones.enteros();
			
			if(opcion==0){
				
				repetir=false;
				
				System.out.println("Volviendo al menú usuario.");
			}else if(opcion<0 || opcion>grupos.size()){
				
				repetir=true;
				System.out.println("Solo puedes unirte a uno de estos grupos.");
			}else{
				
				repetir=false;
				addGrupoUsuario(user,grupos.get(opcion-1),db);
			}	
			
			
		}while(repetir);
		
		
	}
	
	/*
	 * Buscar información de un usuario a partir de un ID.
	 * */
	
	public void buscarInfoUsuario(ObjectId idUsuario, DB db){
		
		this.collection = db.getCollection("usuario");
		
		//Busco el usuario para luego poder visualizar toda su informacion
		BasicDBObject buscarUsuario = new BasicDBObject("_id", idUsuario);
		
		DBCursor cursor = collection.find(buscarUsuario);
		
		for (DBObject usuario : cursor) {

			this.id=(ObjectId) usuario.get("_id");
			this.nombre=(String) usuario.get("nombre");
			this.apellidos=(String) usuario.get("apellidos");
			this.correo=(String) usuario.get("correo");
			DBObject direccion = (DBObject) usuario.get("direccion");

			this.direccion[0] = (String) direccion.get("calle");
			this.direccion[1] = (String) direccion.get("numero");
			this.direccion[2] = (String) direccion.get("localidad");
			this.direccion[3] = (String) direccion.get("codigo postal");
			
			this.password=(String) usuario.get("password");
		}
	}
	
	
	public void bajaUsuario(DB db) {

		BasicDBObject query = new BasicDBObject("_id", this.id);

		this.collection = db.getCollection("usuario");
		this.collection.remove(query);

	}
	
	
	
}
