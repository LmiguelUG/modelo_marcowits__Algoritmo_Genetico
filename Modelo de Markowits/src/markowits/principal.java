/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package markowits;

import java.io.IOException;
import java.util.ArrayList;
import static markowits.Modelo_Markowits.Covarianzas;
import static markowits.Modelo_Markowits.Leer_excel;
import static markowits.Modelo_Markowits.Rendimiento_promedio;
import static markowits.Modelo_Markowits.Rendimientos_fondos;
import static markowits.Modelo_Markowits.Var_and_desv_est;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

/**
 *
 * @author luism
 */
public class principal {

    public static void main(String[] args) throws EncryptedDocumentException, InvalidFormatException, IOException, Exception  {
        
        /* Ruta del archivo */
        String url_file = "C:\\Users\\luism\\Documents\\NetBeansProjects\\Modelo de Markowits\\src\\archivos_excel\\Fondos.xlsx";
        
        int cantidad_fondos = 8;
        int cantidad_datos = 60;
        
        String[]   nombres_fondos          = new String[cantidad_fondos];
        double[][] cierres_fondos          = new double[cantidad_fondos][cantidad_datos];
        
        double[][] rendimientos_fondos     = new double[cantidad_fondos][cantidad_datos];
        double[]   rendimientos_promedios  = new double[cantidad_fondos];
        
        double[]   varianza                = new double[cantidad_fondos];
        double[]   desviaciones_estandars  = new double[cantidad_fondos];
        
        double[][] covarianzas             = new double[cantidad_fondos][cantidad_fondos-1];
         
        Modelo_Markowits obj_MM = new Modelo_Markowits();
        
        /* Llamado del Metodo para lectura de excel */
        obj_MM.Leer_excel          ( url_file, cierres_fondos, nombres_fondos );
        /* Llamado del metodo que calcula los rendimientos de cada todos los fondos */
        obj_MM.Rendimientos_fondos ( rendimientos_fondos, cierres_fondos );
        
        for (int i = 0; i < rendimientos_fondos.length; i++) {
            /* Llamado del metodo para calcular el rendimiento promedio del fondo i */
            rendimientos_promedios[i] = obj_MM.Rendimiento_promedio(rendimientos_fondos[i]);
            /* Llamado del metodo para calcular la varianza del fondo i */
            varianza[i]               = obj_MM.Var_and_desv_est(rendimientos_fondos[i], rendimientos_promedios[i])[0]; // En la posición cero retorna la varianza
            /* Llamado del metodo para calcular la desviación estandar del fondo i  */
            desviaciones_estandars[i] = obj_MM.Var_and_desv_est(rendimientos_fondos[i], rendimientos_promedios[i])[1]; // En la posición uno retorna la desviación estandar
        }
        
        /* Llamado del metodo para calcular para determinar las covarianzas entre los distintos fondos */
        covarianzas = obj_MM.Covarianzas(rendimientos_fondos, rendimientos_promedios);
        
        
        /* Creando objetos de Fondos monetarios */
        Fondo[] fondos_monetarios = new Fondo[cantidad_fondos];
        for (int i = 0; i < cantidad_fondos; i++) {
            fondos_monetarios[i] = new Fondo (nombres_fondos[i], rendimientos_promedios[i], varianza[i], covarianzas[i]);
            fondos_monetarios[i].imprimir();
        }
        
        
        
        Algoritmo_Genetico obj_AG = new Algoritmo_Genetico(fondos_monetarios, nombres_fondos);
        obj_AG.Poblacion();
        obj_AG.imprimirPoblacion("Población Inicial");
        
        int var=0;
        do {            
            obj_AG.Ruleta();
            obj_AG.Cruce();
            obj_AG.imprimirPoblacion("Poblacion despues del cruce ");
            
            System.out.println(" ");
            obj_AG.imprimirPoblacion("Población Ordenada por Fitness");
            obj_AG.Mutacion();
            obj_AG.Insercion_Directa();
            var++;
        } while (obj_AG.parada<700);
       
}
    
}
