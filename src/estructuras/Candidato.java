package estructuras;

import java.util.Arrays;

public class Candidato implements Comparable<Candidato> {
    private float peso;
    private int numero;

    public Candidato(float unPeso, int unNum){
        this.peso = unPeso;
        this.numero = unNum;
    }

    public Candidato[] ordenadoPorPeso(Candidato[] arreglo){
        Candidato[] ordenados= arreglo.clone();
         Arrays.sort(ordenados);
         return ordenados;
    }

    @Override
    public int compareTo(Candidato o) {

        return Float.compare( this.getPeso() , o.getPeso());
    }

    private float getPeso() {
        return this.peso;
    }

    public int getNumero() {
        return this.numero;
    }
}

