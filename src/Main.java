import entrada.XMLaccessing;
import estructuras.Circuito;
import estructuras.GrafoMatriz;

import java.util.concurrent.ThreadLocalRandom;

public class Main {
    public static void main(String[] args) {
        GrafoMatriz otroGrafo = new GrafoMatriz(4);
        otroGrafo.nuevoArco("1","2",7);
        otroGrafo.nuevoArco("1","3",9);
        otroGrafo.nuevoArco("1","4",8);
        otroGrafo.nuevoArco("2","1",7);
        otroGrafo.nuevoArco("2","3",10);
        otroGrafo.nuevoArco("2","4",4);
        otroGrafo.nuevoArco("3","1",9);
        otroGrafo.nuevoArco("3","2",10);
        otroGrafo.nuevoArco("3","4",15);
        otroGrafo.nuevoArco("4","1",8);
        otroGrafo.nuevoArco("4","2",4);
        otroGrafo.nuevoArco("4","3",15);
        int [] camino = recorridoViajanteDeComercio(otroGrafo);
        imprimirArreglo (camino);
        System.out.println("ahora aleatorio");
        int []camino2 = recorridoViajanteDeComercioAleatorio(otroGrafo);
        imprimirArreglo(camino2);
        //Circuito inicial = new Circuito(camino);
        //int menorCosto = busquedaLocal(inicial,otroGrafo);
        //System.out.println("el costo es de " + menorCosto);
        System.out.println("accediendo a los datos xml");
        XMLaccessing archivoDeEntrada = new XMLaccessing();
        String [] resultado1 = archivoDeEntrada.adyacentesDe("1");
        String[] resultado2 = archivoDeEntrada.pesosDe("1");
        imprimirArreglo(resultado1);
        imprimirArreglo(resultado2);
        GrafoMatriz grafoxml = new GrafoMatriz(archivoDeEntrada);
        int[] camino3 = recorridoViajanteDeComercio(grafoxml);
        imprimirArreglo(camino3);
    }

    private static void imprimirArreglo(int[] arreglo) {
        System.out.println("se tiene el siguiente arreglo");
        for (int i =0;i <arreglo.length;i++){
            System.out.println(" "+ arreglo[i]+ ";");
        }
    }
    private static void imprimirArreglo(String [] arreglo) {
        System.out.println("se tiene el siguiente arreglo");
        for (int i =0;i <arreglo.length;i++){
            System.out.println( arreglo[i] + ";");
        }
    }

    public static int[] recorridoViajanteDeComercio(GrafoMatriz grafo) {
        int nodoActual = 0;
        int posicionDelMinimo=0;
        int tamGrafo = grafo.numeroDeVertices();
        int [] caminoRecorrido = new int[tamGrafo];
        boolean [] yaLoRecorri = new boolean[tamGrafo];
        inicializarEnFalso(yaLoRecorri);
        inicializarEnCero(caminoRecorrido);
        while (!yaLoRecorri[nodoActual]) {
            float[] misAdyacentes = grafo.adyacentesDe(nodoActual);
            yaLoRecorri[nodoActual] = true;
            insertarEnSiguientePosicionLibre(caminoRecorrido, nodoActual);
            float pesoMinimo = grafo.pesoMasAlto(nodoActual);
            for (int indice = 0; indice < tamGrafo; indice++) {
                if (!yaLoRecorri[indice]) {
                    if (misAdyacentes[indice] < pesoMinimo) {
                        pesoMinimo = misAdyacentes[indice];
                        posicionDelMinimo = indice;
                    }
                }
            }
            nodoActual = posicionDelMinimo;
        }
        return caminoRecorrido;
    }

    public static int[] recorridoViajanteDeComercioAleatorio(GrafoMatriz grafo) {
        int nodoActual = 0;
        int tamGrafo = grafo.numeroDeVertices();
        int [] caminoRecorrido = new int[tamGrafo];
        boolean [] yaLoRecorri = new boolean[tamGrafo];
        //elementos adicionales una cierta cantidad de candidatos
        int porcentajeDelGrafo = (tamGrafo *5)/100;
        if (porcentajeDelGrafo<=0) porcentajeDelGrafo=1;//para arreglos muy pequeños que no pueda sacar cierto porcentaje
        //otro elemento adicional un arreglo de los nodos candidatos
        int[] candidatos = new int[tamGrafo];
        // y un arreglo para los que no son los minimos pero por descarte se deben recorrer
        int[] noCandidatos = new int[tamGrafo];
        inicializarEnFalso(yaLoRecorri);
        inicializarEnCero(caminoRecorrido);
        inicializarEnCero(candidatos);
        inicializarEnCero(noCandidatos);
        while ((nodoActual < tamGrafo)&&(!yaLoRecorri[nodoActual])) {
            float[] misAdyacentes = grafo.adyacentesDe(nodoActual);
            yaLoRecorri[nodoActual] = true;
            insertarEnSiguientePosicionLibre(caminoRecorrido, nodoActual);
            float pesoMinimo = grafo.pesoMasAlto(nodoActual);
            for (int indice = 0; indice < tamGrafo; indice++) {
                if (!yaLoRecorri[indice]) {
                    if (misAdyacentes[indice] < pesoMinimo) {
                        pesoMinimo = misAdyacentes[indice];
                        insertarEnSiguientePosicionLibre(candidatos,indice);
                        //System.out.println("agrego candidato");
                    }
                    else {
                        insertarEnSiguientePosicionLibre(noCandidatos,indice);
                        //System.out.println("agrego al otro arreglo");
                    }
                }
            }
            agregarLosNoCandidatosALosCandidatos (candidatos,noCandidatos);
            int siguiente = getNumeroRandom(0, ocupados(candidatos));
            nodoActual = candidatos[siguiente];
        }
        return caminoRecorrido;
    }

    public static int busquedaLocal(Circuito caminoSugerido, GrafoMatriz grafo){
        int tamanioArreglo = caminoSugerido.camino.length;
        caminoSugerido.costo = recorrerElGrafo(grafo,caminoSugerido.camino);
        Circuito mejorSolucion = caminoSugerido;
        Circuito[] vecindad = new Circuito[caminoSugerido.camino.length];
        for (int indice = 0; indice < tamanioArreglo; indice++) {
            vecindad[indice]= generarVecino(indice,tamanioArreglo, caminoSugerido);
        }
        for (Circuito vecino: vecindad) {
            vecino.costo = recorrerElGrafo(grafo,vecino.camino);
            if (encontreMejorSolucion(caminoSugerido,vecino)){
                mejorSolucion = vecino;
            }
        }
        if (mejorSolucion != caminoSugerido){
            return busquedaLocal(mejorSolucion,grafo);
        }
        else {
            return caminoSugerido.costo;
        }
    }

    private static boolean encontreMejorSolucion(Circuito caminoSugerido, Circuito vecino) {
        return caminoSugerido.costo> vecino.costo;
    }

    private static int recorrerElGrafo(GrafoMatriz grafo, int[] camino) {
        int pesoAcumulado=0;
        for (int vertice =0; vertice< camino.length; vertice++){
            String actual = String.valueOf(vertice);
            String siguiente = String.valueOf(vertice+1);
            pesoAcumulado += grafo.pesoDeArista(vertice,vertice+1);
        }
        return pesoAcumulado;
    }

    private static Circuito generarVecino(int indice, int tamanioArreglo, Circuito mejorSolucion) {
        int[] vecinoNuevo = mejorSolucion.camino; //le copio todos los valores
        if (indice < tamanioArreglo+1){
            //swappeo el del indice actual y su siguiente
            vecinoNuevo[indice]= mejorSolucion.camino[indice + 1];
            vecinoNuevo[indice +1] = mejorSolucion.camino[indice];
        }
        else {
            // swappeo el primero con el ultimo
            vecinoNuevo[indice]= mejorSolucion.camino[0];
            vecinoNuevo[0] = mejorSolucion.camino[indice];
        }
        return new Circuito(vecinoNuevo);
    }


    private static int ocupados(int[] candidatos) {
        int contador = 0;
        while ((contador < candidatos.length)&&(candidatos[contador]!=0)){
            contador++;
        }
        return contador;
    }

    private static void agregarLosNoCandidatosALosCandidatos(int[] candidatos, int[] noCandidatos) {
        for (int i =0; i < noCandidatos.length;i++){
            insertarEnSiguientePosicionLibre(candidatos,noCandidatos[i]);
        }
    }

    private static int getNumeroRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max);
    }


    private static void insertarEnSiguientePosicionLibre(int[] unArreglo, int aGuardar) {
        int puntero = 0;
        while ((puntero < unArreglo.length)&& (unArreglo[puntero]!=0)){//recorro hasta encontrar una posicion en 0
            puntero++;
        }
        if (puntero<unArreglo.length)
            unArreglo[puntero]=aGuardar+1;
    }

    private static void inicializarEnCero(int[] caminoRecorrido) {
        for (int pos = 0; pos < caminoRecorrido.length;pos++){
            caminoRecorrido[pos]=0;
        }
    }

    private static void inicializarEnFalso(boolean[] yaLoRecorri) {
        for(int posicion=0;posicion < yaLoRecorri.length;posicion++){
            yaLoRecorri[posicion]=false;
        }
    }

}