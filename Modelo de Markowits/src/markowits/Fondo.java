package markowits;

public class Fondo {
    
    /* Atributos */
    String    nombre;
    double    rendimiento;
    double    varianza;
    double[] covarianza;

    /* Constructor */
    public Fondo (String nombre_, double rendimiento_, double varianza_, double[] covarianza_) {
        this.nombre      = nombre_;
        this.rendimiento = rendimiento_;
        this.varianza    = varianza_;
        this.covarianza  = new double[covarianza_.length];
        
        for (int i = 0; i < covarianza.length; i++) {
                this.covarianza[i] = covarianza_[i];
        }    
    }
    
    /* Metodo para imprimir el fondo estudiado */
    public void imprimir(){
        System.out.println("\n********** " + this.nombre + " **********");
        System.out.println("   - Rendimiento promedio: " + this.rendimiento);
        System.out.println("   - Varianza: " + this.varianza);
        
        System.out.println("   - Covarianzas: ");
        for (int i = 0; i < covarianza.length; i++) {
            if (covarianza[i] != 0) {
                System.out.println("     --> " + this.covarianza[i]);
            }            
        }

    }
    
}
