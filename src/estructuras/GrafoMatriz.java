package estructuras;

import entrada.XMLaccessing;

public class GrafoMatriz {
    float[][] adyacentes;
    private final int infinito = 9999999;//un valor muy alto
    public GrafoMatriz(int dimension) {
        this.adyacentes=new float[dimension][dimension];
        this.ponerValorAltoAristaCiclica();
    }

    public GrafoMatriz(XMLaccessing archivoDeEntrada) {
        int dimension = archivoDeEntrada.getNodoAristas().size();
        this.adyacentes=new float[dimension][dimension];
        for (int indice = 0; indice < dimension; indice++){
            String clave = String.valueOf(indice);
            llenarFila (indice,archivoDeEntrada.getNodoAristas().get(clave),archivoDeEntrada.getNodoPesos().get(clave));
        }
        ponerValorAltoAristaCiclica();
    }

    private void llenarFila(int fila, String[] vertices, String[] peso) {
        for(int i = 0;i < vertices.length;i++){
            int posicion = Integer.parseInt(vertices[i]);
            float costo = Float.parseFloat(peso[i]);
            this.adyacentes[fila][posicion]=costo;
        }
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

    public float[] adyacentesDe(int nodoActual) {
        float[] fila = new float[adyacentes.length];
        System.arraycopy(this.adyacentes[nodoActual], 0, fila, 0, adyacentes.length);
        return fila;
    }

    public float pesoMasAlto(int nodoActual){
        //siempre el peso mas alto sera la inteseccion de un nodo con si mismo
        return this.adyacentes[nodoActual][nodoActual];
    }

    public float pesoDeArista(int vertice, int otroVertice) {
        float resultado = 0;
        if ((vertice < this.numeroDeVertices())&&(otroVertice<this.numeroDeVertices())){
            resultado = this.adyacentes[vertice][otroVertice];
        }

        return resultado;
    }
}
