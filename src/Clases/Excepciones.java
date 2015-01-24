package Clases;

import java.util.InputMismatchException;

public class Excepciones {
	
	public static int enteros(){
		
		int numero=0;
		boolean error=false;
		
		
		do{
			
			try{
				error=false;
				numero=Principal.lector.nextInt();
				Principal.lector.nextLine();
				
			}catch(InputMismatchException e){
				
				error=true;
				System.err.println("Tiene que introducir numeros enteros");
				Principal.lector.nextLine();
			}
		}while(error);
		
		return numero;
		
	}
	
	public static double decimales(){
		
		double numero=0;
		boolean error=false;
		
		
		do{
			
			try{
				error=false;
				numero=Principal.lector.nextDouble();
				Principal.lector.nextLine();
				
			}catch(InputMismatchException e){
				
				error=true;
				System.err.println("Tiene que introducir numeros decimales");
				Principal.lector.nextLine();
			}
		}while(error);
		
		return numero;
		
	}
	

}
