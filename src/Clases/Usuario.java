package Clases;

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
	private String direccion;
	private String password;
	
	
	
	public Usuario(){
		
	}
	
	public void crearUsuario(String nombre, String apellidos, String correo, String[] direccion, String password, DB db){
		
		/* Con la clase BasicDBObject creamos objetos Mongo y lo insertamos en la coleccion "user" */
		BasicDBObject doc = new BasicDBObject();
			//doc.put("_id", new Long(1));
	        doc.put("nombre", nombre);
	        doc.put("apellidos",apellidos);
	        doc.put("correo", correo);
	        doc.put("direccion", direccion);
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

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	///////////////////////////////// OPCIONES USUARIO ////////////////////////////////////////////
	
	
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
			this.direccion=usuario.get("direccion").toString();
			this.password=usuario.get("password").toString();
			
			return true;
		}
		
		return false;
	}
	
	public void addComentario(Grupo grupo, String comentario, DB db) {

		BasicDBObject buscarUsuario = new BasicDBObject("_id", this.id);

		DBObject updateQuery = new BasicDBObject("$push", new BasicDBObject("comentario", new BasicDBObject("texto", comentario).append("grupo", grupo.getNombre())));

		collection.update(buscarUsuario, updateQuery);

		grupo.incrementarComentario(db);

	}
	
	public void bajaUsuario(DB db) {

		BasicDBObject query = new BasicDBObject("_id", this.id);

		this.collection = db.getCollection("usuario");
		this.collection.remove(query);

	}
	
}
