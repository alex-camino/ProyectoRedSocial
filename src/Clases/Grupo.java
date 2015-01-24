package Clases;

import java.util.ArrayList;
import java.util.Date;

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

		
		/**
		 * Metodo que crea un grupo
		 *
		 */
		public void crearGrupo(String nombre, DB db, Usuario user) {

			Date fechaCreacion = new Date();
			
			BasicDBObject doc = new BasicDBObject();
			doc.put("_id", nombre);
			doc.put("usuarios",
					new BasicDBObject(new BasicDBObject("usuario", user.getId())
							.append("fecha_ingreso", fechaCreacion).append("administrador", true)));
			doc.put("cantidad_usuarios", 1);
			doc.put("cantidad_comentarios", 0);

			this.collection = db.getCollection("grupo");
			collection.save(doc);
			
			//Asigno el nombre para luego poder añadirlo al array de grupos en el usuario.
			this.nombre=nombre;
		}

		
		/**
		 * Borrado de Grupo
		 * @param db
		 */
		public void borrarGrupo(DB db) {

			BasicDBObject query = new BasicDBObject("_id", this.nombre);

			this.collection = db.getCollection("grupo");

			this.collection.remove(query);

		}

		/**
		 * Incremento de los comentarios escritos
		 * @param db
		 */
		public void incrementarComentario(DB db) {

			this.cantidad_comentarios++;

			BasicDBObject busqueda = new BasicDBObject("_id", this.nombre);

			DBObject updateQuery = new BasicDBObject("$set", new BasicDBObject(
					"total_comentarios", this.cantidad_comentarios));
			this.collection = db.getCollection("grupo");
			this.collection.update(busqueda, updateQuery);

		}
		
		/**
		 * Quitar usuario del grupo
		 */
		public void abandonarGrupo(Usuario u, DB db){

			BasicDBObject busqueda = new BasicDBObject("_id", this.nombre);

			DBObject updateQuery = new BasicDBObject("$pull", new BasicDBObject("usuarios", new BasicDBObject("usuario", u.getId())));
			System.out.println(updateQuery);
			this.collection = db.getCollection("grupo");
			this.collection.update(busqueda, updateQuery);
			
		}

		///////////////////////////////////////////////METODOS STATIC///////////////////////////////////////////////////

		/**
		 * Mostrar grupos en los que el usuario es administrador
		 * @param user
		 * @param db
		 * @return
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
		 * Metodo que busca todos los grupos donde esta un usuario
		 * 
		 */
		public static ArrayList<Grupo> mostrarGrupos(Usuario u, DB db) {

			ArrayList<Grupo> grupos = new ArrayList<>();

			BasicDBObject query = new BasicDBObject();
			query.put("usuarios.usuario", u.getId());

			DBCollection col = db.getCollection("grupo");

			DBCursor cursor = col.find(query);
			
			for (DBObject grupo : cursor) {

				//ObjectId id = (ObjectId) grupo.get("_id");
				String nombre = (String) grupo.get("nombre");
				int cantidad_usuarios = (int) grupo.get("cantidad_usuarios");
				int cantidad_comentarios = (int) grupo.get("cantidad_comentarios");

				grupos.add(new Grupo(nombre, cantidad_usuarios, cantidad_comentarios));

			}

			return grupos;

		}
}
