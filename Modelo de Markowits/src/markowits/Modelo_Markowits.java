/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package markowits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.xssf.usermodel.XSSFCell;
/**
 *
 * @author luism
 */
public class Modelo_Markowits {
    
/***********************************************************************************************************************/
/********************* Lee el archivo excel, para almacenar cada unos de los cierres de cada fondo *********************/
/***********************************************************************************************************************/
    public static void Leer_excel (String url_file, double[][] cierres_fondos, String[] nombres_fondos) throws IOException {
        
        /* Apertura de archivo */
        Workbook workbook = WorkbookFactory.create(new File(url_file));
        
        /* Pasos:
            1.- Lectura de Hoja1
            2.- Lectura de fila x columnas 
            3.- Establecer los valores de cierre de cada fondo en el vector correspondiente según sea [1,2,3,4,5,6,7,8]
        */
        Sheet sheet = workbook.getSheetAt(0);
        
        Iterator<Row> iterator_row = sheet.rowIterator(); /* iterador de filas de la hoja de trabajo del excel*/
        while (iterator_row.hasNext())
        {   
            Row row = (Row) iterator_row.next();
            Iterator<Cell> iterator_col = row.cellIterator(); /* iterador de las columnas de cada fila */
          
            while (iterator_col.hasNext()) 
            {
                Cell col = iterator_col.next();

                if (row.getRowNum() == 0) 
                {   
                    if (col.getColumnIndex() >=1 && col.getColumnIndex() <= nombres_fondos.length) 
                    { 
                      nombres_fondos[col.getColumnIndex()-1] = col.getStringCellValue();
                    }
                }
                
                if (row.getRowNum() >= 1) 
                {
                    double cierre = (double) col.getNumericCellValue();

                    if (col.getColumnIndex() == 1) { cierres_fondos[0][row.getRowNum()-1] = cierre; }
                    if (col.getColumnIndex() == 2) { cierres_fondos[1][row.getRowNum()-1] = cierre; }
                    if (col.getColumnIndex() == 3) { cierres_fondos[2][row.getRowNum()-1] = cierre; }                        
                    if (col.getColumnIndex() == 4) { cierres_fondos[3][row.getRowNum()-1] = cierre; }                        
                    if (col.getColumnIndex() == 5) { cierres_fondos[4][row.getRowNum()-1] = cierre; }                       
                    if (col.getColumnIndex() == 6) { cierres_fondos[5][row.getRowNum()-1] = cierre; }                       
                    if (col.getColumnIndex() == 7) { cierres_fondos[6][row.getRowNum()-1] = cierre; } 
                    if (col.getColumnIndex() == 8) { cierres_fondos[7][row.getRowNum()-1] = cierre; }    
                }
            } /* Cierre while columnas */
        } /*Cierre while filas */   
        
        
    }
    
/***********************************************************************************************************************/
/************************** Calcula el rendimiento de cada cierre, por cada uno de los fondos **************************/
/***********************************************************************************************************************/
    public static void Rendimientos_fondos(double[][] rendimientos_fondos, double[][] cierres_fondos){
        
        for (int i = 0; i < cierres_fondos.length; i++) {
            for (int j = 0; j < cierres_fondos[i].length; j++) {
                if ( j == 0) { rendimientos_fondos[i][j] = 0; }
                if ( j >  0) { rendimientos_fondos[i][j] = ((cierres_fondos[i][j]/cierres_fondos[i][j-1])-1); }
            }
        }     
    }
    
/***********************************************************************************************************************/
/************************* Calcula el rendimiento promedio, del fondo que recibe por parametro *************************/ 
/***********************************************************************************************************************/

    public static double Rendimiento_promedio(double[] rendimientos_fondo) {
        double rendimiento_promedio = 0;

        double suma = 0 ;
        
        /* Calculo del rendimiento promedio */    
        for (int j = 0; j < rendimientos_fondo.length; j++) {
            suma = suma + rendimientos_fondo[j];
        }
            
        rendimiento_promedio = (suma/rendimientos_fondo.length);
        return rendimiento_promedio;
    }
     
    
/***********************************************************************************************************************/
/********** Calcula la varianza y la desviación estandar de los rendimientos del fondo que recibe por parametro ********/
/***********************************************************************************************************************/
    public static double[] Var_and_desv_est (double[] rendimientos_fondo, double rendimiento_promedio) {
        double suma = 0 ;
        double varianza = 0;
        double desviacion_estandar  = 0;
        double[] var_desv_est = new double[2];
        
        
        /* Calculo de la varianza y la desviación estandar - riesgo */
        suma = 0;
        for (int j = 0; j < rendimientos_fondo.length; j++) {
            double iteracion = Math.pow(Math.abs(rendimientos_fondo[j] - rendimiento_promedio), 2); 
            suma = suma + iteracion;
        }
        
        varianza = suma/rendimientos_fondo.length;
        desviacion_estandar = Math.sqrt(varianza);
        
        var_desv_est[0] = varianza;
        var_desv_est[1] = desviacion_estandar;
        
        return var_desv_est;    
    }
    
/***********************************************************************************************************************/
/**************************************** Determina las covarianzas entre los fondos************************************/
/***********************************************************************************************************************/
    public static double[][] Covarianzas (double[][] rendimientos_fondos, double[] promedios) {
        double acum;
        double[][] convarianzas  = new double[rendimientos_fondos.length][rendimientos_fondos.length-1];
        int cont = 0;
        
        for (int h = 0; h < rendimientos_fondos.length; h++) {
            
            cont = 0;
            for (int i = h+1; i < rendimientos_fondos.length; i++) {
                
                acum = 0;
                for (int j = 0; j < rendimientos_fondos[i].length; j++) {
                    if( i < (rendimientos_fondos.length)) {
                        acum  = acum + ((rendimientos_fondos[h][j]- promedios[h]) * (rendimientos_fondos[i][j]- promedios[i])) ;
                    } 

                }
                
                convarianzas[h][cont] = acum/rendimientos_fondos[i].length;
                cont ++;
            }
        }
        
        return convarianzas;
    }    
}
