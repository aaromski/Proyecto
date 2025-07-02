import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

public class PromedioNotas {
    public static void main(String[] args) {
        try {
          RegistroNotas<Double> reg1 = new RegistroNotas<>(new Double[] {5d,8d,3d,2d,1d});
          double prom = RegistroNotas.promedio(new Double[] {5d,3d,8d,4d}) ;
          reg1.minimo(prom);
         
        } catch (Exception e) {
          System.out.println(e.getMessage());
        }

    }
    
}