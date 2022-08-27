package estructuras;

public class Circuito implements Comparable <Circuito> {
    public int [] camino;
    public float costo;

    public Circuito(int[] vecinoNuevo, float costoNuevo)
    {
        this.camino = vecinoNuevo;
        this.costo = costoNuevo;
    }

    @Override
    public int compareTo(Circuito o) {
        return Float.compare(this.costo, o.costo);
    }
}
