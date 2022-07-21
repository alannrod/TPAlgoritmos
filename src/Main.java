import entrada.XMLaccessing;
import estructuras.Circuito;
import estructuras.GrafoMatriz;
import org.jfree.chart.ChartPanel;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import salida.ArchivoSalida;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        GrafoMatriz otroGrafo = crearGrafoDeEjemplo();
        int [] camino = recorridoViajanteDeComercio(otroGrafo);
        imprimirArreglo (camino);
        System.out.println("ahora aleatorio");
        int []camino2 = recorridoViajanteDeComercioAleatorio(otroGrafo);
        imprimirArreglo(camino2);
        Circuito inicial = new Circuito(camino);
        Circuito mejorCircuito = busquedaLocal(inicial,otroGrafo);
        imprimirArreglo(mejorCircuito.camino);
        Circuito otroCircuito = busquedaLocalOptimizada(inicial,otroGrafo,3);
        imprimirArreglo(otroCircuito.camino);
        System.out.println("accediendo a los datos xml");
        XMLaccessing archivoDeEntrada = new XMLaccessing();
        //String [] resultado1 = archivoDeEntrada.adyacentesDe("1");
        //String[] resultado2 = archivoDeEntrada.pesosDe("1");
        //imprimirArreglo(resultado1);
        //imprimirArreglo(resultado2);
        //GrafoMatriz grafoxml = new GrafoMatriz(archivoDeEntrada);
        //int[] camino3 = recorridoViajanteDeComercio(grafoxml);
        //imprimirArreglo(camino3);
        algoritmoGRASPconArchivoDeEntradaYDeSalida("burma14.xml","outputBurma.txt");
        algoritmoGRASPconArchivoDeEntradaYDeSalida("brazil58.xml","outputBrazil.txt");
        algoritmoGRASPconArchivoDeEntradaYDeSalida("gr137.xml","outputGr137.txt");

        XMLaccessing archivoDeEntrada1 = new XMLaccessing("burma14.xml");
        XMLaccessing archivoDeEntrada2 = new XMLaccessing("brazil58.xml");
        XMLaccessing archivoDeEntrada3 = new XMLaccessing("gr137.xml");
        GrafoMatriz grafo1 = new GrafoMatriz(archivoDeEntrada1);
        GrafoMatriz grafo2 = new GrafoMatriz(archivoDeEntrada2);
        GrafoMatriz grafo3 = new GrafoMatriz(archivoDeEntrada3);

        JFreeChart grafico = null;
        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        float dato1 = recorrerElGrafo(grafo1,recorridoViajanteDeComercio(grafo1));
        float dato2 = recorrerElGrafo(grafo2,recorridoViajanteDeComercio(grafo2));
        float dato3 = recorrerElGrafo(grafo3,recorridoViajanteDeComercio(grafo3));
        datos.addValue(dato1,"Grafica GRASP", "burma14");
        datos.addValue(dato2,"Grafica GRASP", "brazil58");
        datos.addValue(dato3,"Grafica GRASP", "gr137");
        grafico = ChartFactory.createLineChart("Grafica GRASP","iteraciones","datos",datos);
        // Creación del panel con el gráfico
        ChartPanel panel = new ChartPanel(grafico);

        JFrame ventana = new JFrame("El grafico");
        ventana.getContentPane().add(panel);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static void imprimirArreglo(int[] arreglo) {
        System.out.println("se tiene el siguiente arreglo");
        for (int posicion :arreglo){
            System.out.print(" "+ posicion + ";");
        }
        System.out.println(" ");
    }
    private static void imprimirArreglo(String [] arreglo) {
        System.out.println("se tiene el siguiente arreglo");
        for (String palabra : arreglo){
            System.out.print( palabra + ";");
        }
        System.out.println(" ");
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
            int siguiente = getNumeroRandom(ocupados(candidatos));
            nodoActual = candidatos[siguiente];
        }
        return caminoRecorrido;
    }

    public static Circuito busquedaLocal(Circuito caminoSugerido, GrafoMatriz grafo){
        int tamanioArreglo = caminoSugerido.camino.length;
        caminoSugerido.costo = recorrerElGrafo(grafo,caminoSugerido.camino);
        Circuito mejorSolucion = caminoSugerido;
        Circuito[] vecindad = new Circuito[caminoSugerido.camino.length];
        for (int indice = 0; indice < tamanioArreglo; indice++) {
            vecindad[indice]= generarVecino(indice,tamanioArreglo, caminoSugerido);
        }
        for (Circuito vecino: vecindad) {
            vecino.costo = recorrerElGrafo(grafo,vecino.camino);
            if (caminoSugerido.costo> vecino.costo){
                mejorSolucion = vecino;
            }
        }
        if (mejorSolucion != caminoSugerido){
            return busquedaLocal(mejorSolucion,grafo);
        }
        else {
            return mejorSolucion;
        }
    }

    public static Circuito busquedaLocalOptimizada(Circuito caminoSugerido, GrafoMatriz grafo,int intentosMax) {
        Circuito mejorSolucion = caminoSugerido;
        if (intentosMax > 0) {
            int tamanioArreglo = caminoSugerido.camino.length;
            //caminoSugerido.costo = recorrerElGrafo(grafo, caminoSugerido.camino);
            Circuito[] vecindad = new Circuito[caminoSugerido.camino.length];
            for (int indice = 0; indice < tamanioArreglo; indice++) {
                vecindad[indice] = generarVecino(indice, tamanioArreglo, caminoSugerido);
            }
            for (Circuito vecino : vecindad) {
                //vecino.costo = recorrerElGrafo(grafo, vecino.camino);
                if (encontreMejorSolucion(caminoSugerido, vecino, grafo)) {
                    mejorSolucion = vecino;
                }
            }
            if (mejorSolucion != caminoSugerido) {
                return busquedaLocalOptimizada(mejorSolucion, grafo, intentosMax--);
            }
        }
        return mejorSolucion;
    }


    public static void algoritmoGRASPconArchivoDeEntradaYDeSalida(String nombreDelArchivoEntrada, String nombreDelArchivoDeSalida) {
        XMLaccessing miEntrada = new XMLaccessing(nombreDelArchivoEntrada);
        GrafoMatriz miGrafo = new GrafoMatriz(miEntrada);
        ArchivoSalida miSalida = new ArchivoSalida(nombreDelArchivoDeSalida);
        miSalida.escribirEnElArchivo("Algoritmo GRASP aplicado al archivo "+ nombreDelArchivoEntrada);
        int [] camino = recorridoViajanteDeComercio(miGrafo);
        miSalida.escribirEnElArchivo("obtuvimos la siguiente solucion usando camino Hamiltoneano: "+Arrays.toString(camino));
        Circuito elMejorHastaAhora = new Circuito(camino);
        elMejorHastaAhora.costo = recorrerElGrafo(miGrafo,camino);
        miSalida.escribirEnElArchivo("cuyo costo de recorrer es: "+ elMejorHastaAhora.costo);
        Circuito hayOtroMejor = busquedaLocal(elMejorHastaAhora,miGrafo);
        while (hayOtroMejor != elMejorHastaAhora){
            elMejorHastaAhora = hayOtroMejor;
            miSalida.escribirEnElArchivo("Con busqueda local se presento esta nueva solucion: "+ Arrays.toString(elMejorHastaAhora.camino));
            miSalida.escribirEnElArchivo("y el costo de recorrer es: " + elMejorHastaAhora.costo);
            hayOtroMejor = busquedaLocal(elMejorHastaAhora,miGrafo);
        }
        miSalida.cerrarArchivo();
    }



    private static boolean encontreMejorSolucion(Circuito caminoSugerido, Circuito vecino, GrafoMatriz grafo) {
        //compararemos ambos caminos
        int tamanio = caminoSugerido.camino.length;
        String [] estanSoloEnSugerido= new String[tamanio];
        int costoMenos = 0;
        String [] estanSoloEnElVecino = new String[tamanio];
        int costoMas = 0;
        for (int i = 0; i < tamanio; i++){
            if (vecino.camino[i]!=caminoSugerido.camino[i]){
                if (i < tamanio-1){
                    //si lo encontre en antes del ultimo puesto comparo con el siguiente, por ahi se invirtio y eso es la misma arista con el mismo peso
                    if ((vecino.camino[i]!=caminoSugerido.camino[i+1])&&(caminoSugerido.camino[i]!=vecino.camino[i+1])){
                        estanSoloEnSugerido[i] = String.valueOf(caminoSugerido.camino[i]);
                        estanSoloEnElVecino[i] = String.valueOf(vecino.camino[i]);
                        estanSoloEnSugerido[i+1]= String.valueOf(caminoSugerido.camino[i+1]);
                        estanSoloEnElVecino[i+1] = String.valueOf(vecino.camino[i+1]);
                    }
                }
                else {
                    estanSoloEnSugerido[i] = String.valueOf(caminoSugerido.camino[i]);
                    estanSoloEnElVecino[i] = String.valueOf(vecino.camino[i]);
                    estanSoloEnSugerido[i-1]= String.valueOf(caminoSugerido.camino[i-1]);
                    estanSoloEnElVecino[i-1] = String.valueOf(vecino.camino[i-1]);
                }
            }
        }
        String [] soloEnSugerido = filtrarNulos(estanSoloEnSugerido);
        String [] soloEnVecinos = filtrarNulos(estanSoloEnElVecino);
        for (int j =0; j < soloEnVecinos.length-1; j++){
            if (j%2 == 0) {//solo de a pares saco el costo (origen destino de arista)
                costoMas += grafo.pesoDeArista(Integer.parseInt(soloEnVecinos[j]), Integer.parseInt(soloEnVecinos[j + 1]));
                costoMenos += grafo.pesoDeArista(Integer.parseInt(soloEnSugerido[j]), Integer.parseInt(soloEnSugerido[j + 1]));
            }
        }
        vecino.costo = caminoSugerido.costo - costoMenos + costoMas;
        return caminoSugerido.costo> vecino.costo;
    }

    private static float recorrerElGrafo(GrafoMatriz grafo, int[] camino) {
        float pesoAcumulado=0;
        for (int vertice =0; vertice< camino.length; vertice++){
            pesoAcumulado += grafo.pesoDeArista(vertice,vertice+1);
        }
        return pesoAcumulado;
    }

    private static Circuito generarVecino(int indice, int tamanioArreglo, Circuito mejorSolucion) {
        int[] vecinoNuevo = mejorSolucion.camino; //le copio todos los valores
        if (indice < tamanioArreglo-1){
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
        for (int posibleCandidato : noCandidatos){
            insertarEnSiguientePosicionLibre(candidatos,posibleCandidato);
        }
    }

    private static int getNumeroRandom(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);
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
        Arrays.fill(caminoRecorrido, 0);
    }

    private static void inicializarEnFalso(boolean[] yaLoRecorri) {
        Arrays.fill(yaLoRecorri, false);
    }
    private static String[] filtrarNulos(String[] arreglo) {
        int cantidadDeNulos = 0;
        int tamTotal = arreglo.length;
        String[] arregloTemporal = new String[tamTotal];
        String[] arregloFinal;
        int indice = 0;
        for (String s : arreglo) {
            if (s == null) {
                cantidadDeNulos++;
            } else {
                arregloTemporal[indice] = s;
                indice++;
            }
        }
        arregloFinal = new String[tamTotal - cantidadDeNulos];
        System.arraycopy(arregloTemporal, 0, arregloFinal, 0, arregloFinal.length);
        return arregloFinal;
    }

    private static GrafoMatriz crearGrafoDeEjemplo(){
        GrafoMatriz resultante =new GrafoMatriz(4);
        resultante.nuevoArco("1","2",7);
        resultante.nuevoArco("1","3",9);
        resultante.nuevoArco("1","4",8);
        resultante.nuevoArco("2","1",7);
        resultante.nuevoArco("2","3",10);
        resultante.nuevoArco("2","4",4);
        resultante.nuevoArco("3","1",9);
        resultante.nuevoArco("3","2",10);
        resultante.nuevoArco("3","4",15);
        resultante.nuevoArco("4","1",8);
        resultante.nuevoArco("4","2",4);
        resultante.nuevoArco("4","3",15);
        return resultante;
    }


}