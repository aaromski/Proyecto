public class RegistroNotas<T extends Number> {
    final double promedio = 6;
     T[] numeros;

    public RegistroNotas(T notas[]) throws Exception {
        if (notas.length == 0) {
            throw new Exception("Arreglo vacio");
        }
        this.numeros = notas;
    }

    public static <T extends Number> double promedio(T[] numeros) {
        double suma = 0;
        for (T n : numeros) {
            suma += n.doubleValue();
        }
        return suma / numeros.length;
    }

    public void minimo(double prom) {
        System.out.println("Promedio: " + prom);
        if (prom >= promedio) {
            System.out.println("Promedio minimo alcanzado");
        } else {
            System.out.println("Promedio minimo no alcanzado");
        }
    }


}