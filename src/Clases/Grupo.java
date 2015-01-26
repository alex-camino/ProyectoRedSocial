package Clases;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Grupo {

	//private ObjectId id;
		private String nombre;
		private int cantidad_usuarios;
		private int cantidad_comentarios;
		private ArrayList<String> comentarios;

		private DBCollection collection;

		
		public Grupo() {

		}

		public Grupo( String nombre, int total_usuarios,int total_comentarios) {

			//this.id = id;
			this.nombre = nombre;
			this.cantidad_usuarios = total_usuarios;
			this.cantidad_comentarios = total_comentarios;

		}

		/////////////// SETTERS Y GETTERS /////////////////////////////
		
		/*public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}*/

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public int getTotal_usuarios() {
			return cantidad_usuarios;
		}

		public void setTotal_usuarios(int total_usuarios) {
			this.cantidad_usuarios = total_usuarios;
		}

		public int getTotal_comentarios() {
			return cantidad_comentarios;
		}

		public void setTotal_comentarios(int total_comentarios) {
			this.cantidad_comentarios = total_comentarios;
		}

		
		///////////////////////////// METODOS //////////////////////////////////
		
		/**
		 * Metodo que crea un grupo
		 *
		 */
		public void crearGrupo(String nombre, DB db, Usuario user) {

			Date fechaCreacion = new Date();
			
			BasicDBObject doc = new BasicDBObject();
			doc.put("_id", nombre);
			doc.put("usuarios",
					Arrays.asList(new BasicDBObject("usuario", user.getId()).append(
							"fecha_ingreso", fechaCreacion).append("admin", true)));
			doc.put("cantidad_usuarios", 1);
			doc.put("cantidad_comentarios", 0);


			this.collection = db.getCollection("grupo");
			collection.save(doc);
			
			//Asigno el nombre para luego poder añadirlo al array de grupos en el usuario.
			this.nombre=nombre;
		}

		
		/*
		 * Método que añade un usuario en el grupo en el que estamos.
		 * */
		public void addUserGrupo(Usuario user, DB db){
			
			this.collection = db.getCollection("grupo");
			
			Date fechaCreacion = new Date();
			
			//Busco el grupo en el que estamos para luego poder realizar el update.
			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);
			
			DBObject updateQuery = new BasicDBObject("$push", new BasicDBObject("usuarios", 
					new BasicDBObject("usuario", user.getId()).append("fecha_ingreso", fechaCreacion).append("admin", false)));
			
			this.collection.update(buscarGrupo, updateQuery);
			
			this.incrementarUsuarios(db);
		}
		
		/*
		 * Agrega un comentario a partir de un usuario en un grupo determinado.
		 * */
		public void addComentarioGrupo(Usuario user, DB db, String textoComentario){
			
			this.collection = db.getCollection("grupo");
			
			Date fechaCreacion = new Date();

			//Busco el grupo en el que estamos para luego poder realizar el update.
			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);
			
			DBObject updateQuery = new BasicDBObject("$push", new BasicDBObject("comentario", new BasicDBObject("texto", textoComentario).append(
							"usuario", user.getId()).append("fecha", fechaCreacion)));
			
			this.collection.update(buscarGrupo, updateQuery);
			
			this.incrementarComentario(db);
			
			
		}
		
		/*
		 * Mostrara los comentarios de un usuario a partir de un grupo.
		 * */
		public void mostrarComentariosUsuario(Usuario user, DB db){

			String texto;
			ObjectId idUsuario;
			DateFormat fechaFormateada = new SimpleDateFormat("EEEE MMMM d HH:mm:ss z yyyy");
			Date fecha = null;
			
			
			this.collection = db.getCollection("grupo");
			
			//Busco el grupo en el que estamos para luego poder visualizar todos los comentarios.
			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);
			
			DBCursor cursor = collection.find(buscarGrupo);
			
			for (DBObject grupo : cursor) {

				ArrayList<DBObject> comentarios = (ArrayList<DBObject>) grupo.get("comentario");
				
				if(comentarios!=null){
					
					for(int i=0;i<comentarios.size();i++){
						
						texto=(String)comentarios.get(i).get("texto");
						idUsuario=(ObjectId) comentarios.get(i).get("usuario");
						fecha=(Date) comentarios.get(i).get("fecha");
						
						//Creo un nuevo usuario para poder averiguar los datos del usuario.
						Usuario nuevoUser = new Usuario();
						nuevoUser.buscarInfoUsuario(idUsuario, db);
						
						System.out.println("Usuario: "+nuevoUser.getNombre()+" "+nuevoUser.getApellidos());
						System.out.println("Comentario: "+texto);
						System.out.println("Fecha: "+fechaFormateada.format(fecha));
					}
					
				}else{
					
					System.out.println("El grupo no tiene comentarios.");
				}

			}
		}
		

		/*
		 * Mostrara los usuarios que hay en un determinado grupo que sean de mi localidad.
		 * */
		public void mostrarUsuariosLocalidadGrupo(Usuario user, DB db){

			String texto;
			ObjectId idUsuario;		
			
			this.collection = db.getCollection("grupo");
			
			//Busco el grupo en el que estamos para luego poder visualizar todos los comentarios.
			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);
			
			DBCursor cursor = collection.find(buscarGrupo);
			
			for (DBObject grupo : cursor) {

				ArrayList<DBObject> usuarios = (ArrayList<DBObject>) grupo.get("usuarios");
				
				
					for(int i=0;i<usuarios.size();i++){
						
						idUsuario=(ObjectId) usuarios.get(i).get("usuario");
						
						//Creo un nuevo usuario para poder averiguar los datos del usuario.
						Usuario nuevoUser = new Usuario();
						nuevoUser.buscarInfoUsuario(idUsuario, db);
						
						if(user.getDireccion()[2].equals(nuevoUser.getDireccion()[2])){
							
							System.out.println(" ");
							System.out.println("Usuario: "+nuevoUser.getNombre()+" "+nuevoUser.getApellidos());
							System.out.println("Dirección: \n"
											  +"\nCalle: "+nuevoUser.getDireccion()[0]
											  +"\nNúmero: "+nuevoUser.getDireccion()[1]
											  +"\nLocalidad: "+nuevoUser.getDireccion()[2]
											  +"\nCódigo Postal: "+nuevoUser.getDireccion()[3]);
							System.out.println("\n"
											  +"================================================");
							
						}
						
					}
					

			}
		}
		
		
		/*
		 * Mostrara la cantidad de usuarios que hay en un determinado grupo.
		 * */
		public void cantidadUsuariosGrupo(Usuario user, DB db){

			String texto;
			ObjectId idUsuario;		
			
			this.collection = db.getCollection("grupo");
			
			//Busco el grupo en el que estamos para luego poder visualizar todos los comentarios.
			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);
			
			DBCursor cursor = collection.find(buscarGrupo);
			
			for (DBObject grupo : cursor) {

				System.out.println("Nombre del grupo: "+grupo.get("_id"));
				System.out.println("Cantidad de usuarios: "+grupo.get("cantidad_usuarios"));
				System.out.println("Cantidad de comentarios: "+grupo.get("cantidad_comentarios"));

			}
		}
		
		
		//Incremento en uno el campo cantidad_usuarios, cada vez que se añade un usuario al grupo.
		public void incrementarUsuarios(DB db) {
			
			this.collection = db.getCollection("grupo");

			this.cantidad_usuarios++;

			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);

			DBObject updateQuery = new BasicDBObject("$set", new BasicDBObject(
					"cantidad_usuarios", this.cantidad_usuarios));
			
			this.collection.update(buscarGrupo, updateQuery);

		}
		
		/**
		 * Incremento en uno los comentarios escritos
		 * 
		 */
		public void incrementarComentario(DB db) {

			this.collection = db.getCollection("grupo");
			
			this.cantidad_comentarios++;

			BasicDBObject buscarGrupo = new BasicDBObject("_id", this.nombre);

			DBObject updateQuery = new BasicDBObject("$set", new BasicDBObject(
					"cantidad_comentarios", this.cantidad_comentarios));
			
			this.collection.update(buscarGrupo, updateQuery);

		}
		
		
		/**
		 * Borrar Grupo
		 * 
		 */
		public void borrarGrupo(DB db) {

			BasicDBObject query = new BasicDBObject("_id", this.nombre);

			this.collection = db.getCollection("grupo");

			this.collection.remove(query);

		}

		
		
		/**
		 * Quitar usuario del grupo
		 */
		public void abandonarGrupo(Usuario user, DB db){

			BasicDBObject busqueda = new BasicDBObject("_id", this.nombre);

			DBObject updateQuery = new BasicDBObject("$pull", new BasicDBObject("usuarios", new BasicDBObject("usuario", user.getId())));
			System.out.println(updateQuery);
			this.collection = db.getCollection("grupo");
			this.collection.update(busqueda, updateQuery);
			
		}

		
		///////////////////////////////////////////////METODOS STATIC///////////////////////////////////////////////////

		/**
		 * Mostrar grupos en los que el usuario es administrador
		 * 
		 */
		public static ArrayList<Grupo> mostrarGruposAdmin(Usuario user, DB db) {

			ArrayList<Grupo> grupos = new ArrayList<>();

			BasicDBObject query = new BasicDBObject();
			query.put("usuarios.usuario", user.getId());
			query.put("usuarios.admin", true);

			DBCollection coleccion = db.getCollection("grupo");

			DBCursor cursor = coleccion.find(query);
			
			for (DBObject grupo : cursor) {

				//ObjectId id = (ObjectId) grupo.get("_id");
				String nombre = (String) grupo.get("nombre");
				int cantidad_usuarios = (int) grupo.get("cantidad_usuarios");
				int cantidad_comentarios = (int) grupo.get("cantidad_comentarios");

				grupos.add(new Grupo(nombre, cantidad_usuarios, cantidad_comentarios));

			}

			return grupos;

		}

		/**
		 * Metodo que busca todos los grupos donde esta un usuario, para que pueda
		 * escoger en cual comenta.
		 * ,
		 */
		public static ArrayList<Grupo> mostrarGrupos(Usuario user, DB db) {

			ArrayList<Grupo> grupos = new ArrayList<>();

			BasicDBObject query = new BasicDBObject();
			query.put("usuarios.usuario", user.getId());

			DBCollection col = db.getCollection("grupo");

			DBCursor cursor = col.find(query);
			
			for (DBObject grupo : cursor) {

				String nombre = (String) grupo.get("_id");
				int cantidad_usuarios = (int) grupo.get("cantidad_usuarios");
				int cantidad_comentarios = (int) grupo.get("cantidad_comentarios");

				grupos.add(new Grupo(nombre, cantidad_usuarios, cantidad_comentarios));

			}

			return grupos;

		}
		

		/*
		 * Método que busca los grupos en los que el usuario no esta inscrito.
		 * */
		public static ArrayList<Grupo> gruposDisponibles(Usuario user, DB db) {


			ArrayList<Grupo> grupos = new ArrayList<>();

			BasicDBObject query = new BasicDBObject(new BasicDBObject("usuarios",
					new BasicDBObject("$not", new BasicDBObject("$elemMatch",
							new BasicDBObject("usuario", user.getId())))));
			
			DBCollection col = db.getCollection("grupo");
			DBCursor cursor = col.find(query);
			
			for (DBObject grupo : cursor) {

				String nombre = (String) grupo.get("_id");
				int total_usuarios = (int) grupo.get("cantidad_usuarios");
				int total_comentarios = (int) grupo.get("cantidad_comentarios");

				
				grupos.add(new Grupo(nombre, total_usuarios, total_comentarios));

			}
			
			return grupos;

		}
}
