public class ConjuntoDeObjetos <T> {
    private T conjunto[];

    @SuppressWarnings("unchecked")
    public ConjuntoDeObjetos (int Tamano) throws MiExcepciones{
        if (Tamano <= 0) {
            throw new MiExcepciones("Tamano invalido");
        }
        conjunto = (T[]) new Integer [Tamano];
    }

    public ConjuntoDeObjetos(T[] elementos) {
        conjunto = elementos;
    }

    public T[] getConjunto () {
        return conjunto;
    }



    public void ingresarElemento(T elemento) {
        int cont = 0;

        if (conjunto[0] == null) {
            conjunto[0] = elemento;
            return;
        }
        while (cont < conjunto.length) {
            if (conjunto[cont] == null) {
                conjunto[cont] = elemento;
                break;
            }

            if (conjunto[cont].equals(elemento)) {
                System.out.println("Error Ingreso elemento duplicado");
                break;
            }
            cont++;
            } 
        }

        @SuppressWarnings("unchecked")
        public ConjuntoDeObjetos<T> unir(T segundoConjunto[]) {
            int tamano = conjunto.length + segundoConjunto.length;
            T[] union = (T[]) new Integer[tamano];

            int cont = 0;

            for (T elemento : conjunto) {
                union[cont++] = elemento;
            }
            conjunto = union;
            for (int i = 0; i < segundoConjunto.length; i++) {
                ingresarElemento(segundoConjunto[i]);
            }
            return this;
        }

        public ConjuntoDeObjetos<T> Diferencia(T segundoConjunto[]) {
            int cont = 0;
            for (T elemento : conjunto) {
                boolean v = true;
                for (T seg : segundoConjunto) {
                    if (elemento.equals(seg)) {
                        v = false;
                        break;
                    }
                }
                if (v) {cont++;}
            }
            @SuppressWarnings("unchecked")
            T[] diferencia = (T[]) new Object[cont];
            cont = 0;
            for (T elemento : conjunto) {
                boolean v = true;
                for (T seg : segundoConjunto) {
                    if (elemento.equals(seg)) {
                        v = false;
                        break;
                    }
                }
                if (v) {diferencia[cont] = elemento; cont++;}
            }
            conjunto = diferencia;
            return this;
        }

        public void ProductoCartesiano(T B[]) {
            imprimirProducto("A", conjunto);
            imprimirProducto("B", B);
            System.out.print("A x B = {");
            for (int i = 0; i < conjunto.length; i++) {
                for (int j = 0; j < B.length; j++) {
                    System.out.print("(" + conjunto[i] + "," + B[j] + ")");
                    if (i != conjunto.length - 1 || j != B.length - 1) {
                        System.out.print (", ");
                    } else {
                        System.out.print("}");
                    }
                }
                
            }
        }

        public void imprimirProducto(String Letra, T c[]) {
            System.out.print(Letra + " = {");
            for (int i = 0; i < c.length; i++) {
                System.out.print(c[i]);
                if (i < c.length - 1) {
                    System.out.print(", ");
                } else {
                    System.out.print("}");
                }
            }
            System.out.print("\n");
        }

        public void imprimir() {
            for (T elemento : conjunto) {
                System.out.print(elemento + " - ");
            }
        }
 }

