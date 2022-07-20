package estructuras;

public class GrafoMatriz {
    int[][] adyacentes;
    private int infinito = 9999;//un valor muy alto
    public GrafoMatriz(int dimension) {
        this.adyacentes=new int[dimension][dimension];
        this.ponerValorAltoAristaCiclica();
    }

    private void ponerValorAltoAristaCiclica() {
         for (int i= 0; i<this.adyacentes.length;i++){
             this.adyacentes[i][i]= this.infinito;
         }
    }

    public void nuevoArco(String vertice1, String vertice2, int peso) {
        int x = Integer.parseInt(vertice1)-1;
        int y = Integer.parseInt(vertice2)-1;
        adyacentes[x][y]=peso;
    }

    public int numeroDeVertices() {
        return this.adyacentes.length;
    }

    public int[] adyacentesDe(int nodoActual) {
        int[] fila = new int[adyacentes.length];
        for (int i = 0; i<adyacentes.length;i++){
            fila[i]= this.adyacentes[nodoActual][i];
        }
        return fila;
    }

    public int pesoMasAlto(int nodoActual){
        //siempre el peso mas alto sera la inteseccion de un nodo con si mismo
        return this.adyacentes[nodoActual][nodoActual];
    }

    public int pesoDeArista(int vertice, int otroVertice) {
        int resultado = this.infinito;
        if ((vertice < this.numeroDeVertices())&&(otroVertice<this.numeroDeVertices())){
            resultado = this.adyacentes[vertice][otroVertice];
        }

        return resultado;
    }
}
