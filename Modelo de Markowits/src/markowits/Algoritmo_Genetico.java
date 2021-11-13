/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package markowits;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 *
 * @author luism
 */
public class Algoritmo_Genetico {
        
    ArrayList<Fondo> fondos = new ArrayList();
    Double[][] poblacion;
    int parada = 0;
    static int cant_cruzar = 0;
    double     fitness_total = 0;
    double[] proporcion_ruleta;
    int[][] parejas;
    String[] nombre_fondos;





    Algoritmo_Genetico(Fondo[] fondos_, String[] nombre_fondos_){
        
        for (int i = 0; i < fondos_.length; i++) {
            fondos.add(fondos_[i]);
        }
        
        this.poblacion = new Double[100][fondos.size()+1];
        this.cant_cruzar = poblacion.length/2;
        proporcion_ruleta= new double[cant_cruzar];
        parejas= new int[cant_cruzar/2][2];
        
        this.nombre_fondos = new String[nombre_fondos_.length];
        for (int i = 0; i < nombre_fondos_.length; i++) {
            nombre_fondos[i] = nombre_fondos_[i];    
        }
       
    }

    public double Formatodecimal (double numero, int decimal){
        BigDecimal bd = new BigDecimal(numero);
        bd = bd.setScale(decimal, RoundingMode.HALF_UP);     
        return bd.doubleValue();
    }

    public void Poblacion(){

        double rango; 
        for (int i = 0; i < 100; i++) 
        {
            rango = 1;
            for (int j = 0; j < fondos.size(); j++) 
            {
                if (rango>0 && j != (fondos.size()-1)) {
                    double numero =  Formatodecimal((Math.random() * rango), 4);
                    poblacion[i][j] = numero;
                    rango = rango - numero; 
                
                }else{
                    if (j==fondos.size()-1) {
                        poblacion[i][j]= Formatodecimal(rango,4);
                    
                    }else{
                        poblacion[i][j]=(double) 0;
                    }
                }   
            }

            poblacion[i][fondos.size()] = Formatodecimal(fitness(i),4);  
        }   
    }
        
    
    
    public double fitness(int fila){
        
        double Er    = RendimientoEsperadoPortafolio(fila);
        double Varrp = MatrizVarianzaRendimiento(fila);
        
        double fitness=Er/Varrp;
        parada++;
        return fitness;
    }
    
    
    
    public double RendimientoEsperadoPortafolio(int fila){
        
        double Er  = 0;
        for (int i = 0; i < fondos.size(); i++) {
            Er = Er + poblacion[fila][i]*fondos.get(i).rendimiento;
        }
        return Er;
    }
    
    
    public double MatrizVarianzaRendimiento(int fila){
        
        Double[][] matriz=new Double[fondos.size()][fondos.size()];
        double Varrp = 0;
        
        for (int i = 0; i < fondos.size(); i++) {
            int cov=0;
            for (int j = 0; j < fondos.size(); j++) {
                if (i==j) {
                    matriz[i][j]=(poblacion[fila][j]*poblacion[fila][j])*fondos.get(i).varianza;
                    Varrp=Varrp+matriz[i][j];
                }else{
                    matriz[i][j]=(poblacion[fila][i]*poblacion[fila][j])*fondos.get(i).covarianza[cov];
                    Varrp=Varrp+matriz[i][j];
                    cov++;
                }
            }
        }
        return Varrp;
    }
    
    public void Porcentaje_Ruleta(int filas){
        fitness_total = 0;
        for (int i = 0; i < filas; i++) {
            fitness_total = fitness_total + poblacion[i][fondos.size()];
        }
        
        for (int i = 0; i < filas; i++) {
            proporcion_ruleta[i] = Formatodecimal((poblacion[i][fondos.size()])/Formatodecimal(fitness_total, 2), 3);
        }
              
    }   
    
    public void Ruleta(){
        double contador=0;
        int individuo=0;
        int vuelta=0;
        int numPareja=0;
        Porcentaje_Ruleta(cant_cruzar);
        
        do {           
            contador=0;
            double numero =  Formatodecimal((Math.random() * 1),3);
           // System.out.println(numero);
            for (int i = 0; i < proporcion_ruleta.length; i++) {
                contador=contador + proporcion_ruleta[i];
                if (contador>=numero) {
                    individuo=i;
                    break;
                }
            }
            if (numPareja==0) {
               parejas[vuelta][numPareja] = individuo;
               numPareja=1;
            }else{
                if (parejas[vuelta][numPareja-1]!=individuo) {
                    parejas[vuelta][numPareja]=individuo;
                    numPareja=0;
                    vuelta++;
                }
            }
            
        } while (vuelta<(cant_cruzar/2));
    }
    
    public void Cruce(){
        int vuelta=0;
        double alfa=Formatodecimal(Math.random()*1,2);
        System.out.print("************************** Cruce **************************");
        for (int i = cant_cruzar; i < 100; i++) {
            if ((i % 2)==0) {
                alfa=Formatodecimal(Math.random()*1,2);
            }
            for (int j = 0; j < fondos.size(); j++) {
                double result=(poblacion[parejas[vuelta][0]][j]*alfa)+(poblacion[parejas[vuelta][1]][j]*(1-alfa));
                poblacion[i][j]=Formatodecimal(result, 4);
            }
            Corregir_Hijo(i); 
            alfa = Formatodecimal(1-alfa, 2);
            if ((i % 2)!=0) {
                vuelta++; 
            }
        }
    }
    
    public void Corregir_Hijo(int fila){
        
        double suma=0;
        
        System.out.println("Viejo individuo");
        Imprimir_Individuo(fila);
        int ceros=0;
        for (int i = 0; i < fondos.size(); i++) {
            suma = Formatodecimal(suma+poblacion[fila][i],4);
            if (poblacion[fila][i]==0) {
                ceros++;
            }
        }
        System.out.println("Fila: "+fila+" suma: "+suma);
        if (suma>1) {
            System.out.println(">suma: "+(suma-1)/(fondos.size()-ceros));
            suma = Formatodecimal((suma-1)/(fondos.size()-ceros),8);
            for (int i = 0; i < fondos.size(); i++) {
                poblacion[fila][i]=Formatodecimal(poblacion[fila][i]-suma,4);
                if (poblacion[fila][i]<0) {
                    poblacion[fila][i]=0.0;
                }
            }
        }else{
            if (suma<1) {
                suma = Formatodecimal((1-suma)/(fondos.size()-ceros),4);
                System.out.println("<suma: "+(1-suma)/(fondos.size()-ceros));
                for (int i = 0; i < fondos.size(); i++) {
                    poblacion[fila][i] = Formatodecimal(poblacion[fila][i]+suma,4);
                }
            }
        }
        
        poblacion[fila][fondos.size()]= Formatodecimal(fitness(fila),4); //fitness
        System.out.println("Nuevo individuo");
        Imprimir_Individuo(fila);
        
    }
    
    
    public void Mutacion(){
        for (int j = 0; j < 3; j++) {
            int fila=(int) ((Math.random() * 99)+1);
            int columna= (int) ((Math.random() * 4));
            double valorTotal=0.0;
            double valorNuevo=0.0;
             
            valorNuevo=((Math.random() * (poblacion[fila][columna])));
            poblacion[fila][columna]=Formatodecimal(poblacion[fila][columna]+valorNuevo,4);
            Corregir_Hijo(fila);
            
            poblacion[fila][fondos.size()]=Formatodecimal(fitness(fila),4);

            System.out.println("\n ---- Individuo Mutado ---- ");
            Imprimir_Individuo(fila);
        }
    }
    
    public void Insercion_Directa(){
        int p, j;
        Double[] aux= new Double[fondos.size()+1];
        
        for (p = 1; p < poblacion.length; p++){ /* ciclo que recorre desde el segundo elemento*/
            for (int i = 0; i < fondos.size()+1; i++) {
                aux[i] = poblacion[p][i]; // Al final, guardamos el elemento y
            } 
            j = p - 1; // empezamos a comprobar con el anterior
            while ((j >= 0) && (aux[fondos.size()] > poblacion[j][fondos.size()])){ 
                for (int i = 0; i < fondos.size()+1; i++) {
                    poblacion[j+1][i]= poblacion[j][i];
                }     
                j--;                   
            }
            for (int i = 0; i < fondos.size()+1; i++) {
                poblacion[j+1][i]=aux[i];
            }
        }
    }
        
    public void Imprimir_Individuo(int i){
        System.out.print("   ");
        for (int h = 0; h < nombre_fondos.length; h++) {
            System.out.print(nombre_fondos[h] + "      ");
        }
        System.out.println("    Fitness");

        System.out.print(i+")  ");
        for (int j = 0; j < poblacion[i].length; j++) {
            System.out.print("   " + poblacion[i][j]);
        }
    }
    
    public void imprimirPoblacion(String titulo){
        
        System.out.println("********************* " + titulo + "*********************");
        
        System.out.print("   ");
        for (int i = 0; i < nombre_fondos.length; i++) {
            System.out.print(nombre_fondos[i] + "      ");
        }
        System.out.println("Fitness    Rend     Var");
        System.out.println("");
        for (int i = 0; i < poblacion.length; i++) {
            System.out.print(i + ") ");
            for (int j = 0; j < poblacion[i].length; j++) {
                System.out.print( poblacion[i][j] + "   " );
            }
            
            double rendimiento = RendimientoEsperadoPortafolio(i);
            double varianza    = rendimiento/poblacion[i][8];
            System.out.print  ( "    " + Formatodecimal(rendimiento, 6));
            System.out.println( "    " +Formatodecimal(varianza, 6));
        }     
    }
    
    

    
    
    
    
}
