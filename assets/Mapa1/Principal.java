public class Principal {
    public static void main(String[] args) {
            try {
                ConjuntoDeObjetos<Object> Probar = new ConjuntoDeObjetos<>(0);
            } catch (MiExcepciones e) {
                System.out.println("⚠️ Error: " + e.getMessage());
            }
        ConjuntoDeObjetos<Object> ProductoA = new ConjuntoDeObjetos<>(new Object[]{
            "0001", "Manzana", 105.2f});
            ConjuntoDeObjetos<Object> ProductoB = new ConjuntoDeObjetos<>(new Object[]{
                "0051", "Fresa", 155.2f});
        
        ConjuntoDeObjetos<Object> union = ProductoA.unir(ProductoB.getConjunto());
        System.out.print("Union: ");
       union.imprimir();

       ConjuntoDeObjetos<Object> diferencia = ProductoA.Diferencia(ProductoB.getConjunto());
       System.out.print("\nDiferencia: ");
       diferencia.imprimir();

       System.out.print("\nProducto: \n");
       ProductoA.ProductoCartesiano(ProductoB.getConjunto());
    }
}