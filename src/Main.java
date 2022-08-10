import entrada.XMLaccessing;
import estructuras.Circuito;
import estructuras.GrafoMatriz;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import salida.ArchivoSalida;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        //creo un grafo completo de 4 nodos de ejemplo, solo para probar la funcionalidad de los recorridos implementados
        GrafoMatriz otroGrafo = crearGrafoDeEjemplo();
        //1. Proponer un algoritmo goloso para el problema del viajante de comercio.
        int [] camino = recorridoViajanteDeComercio(otroGrafo);
        imprimirArreglo (camino);

        //-------------------------------------------------------
        //2. Aleatorizar el algoritmo anterior.
        System.out.println("ahora aleatorio");
        int []camino2 = recorridoViajanteDeComercioAleatorio(otroGrafo);
        imprimirArreglo(camino2);

        //----------------------------------------------------------
        //3. Proponer un algoritmo de búsqueda local para el problema del viajante de comercio.
        Circuito inicial = new Circuito(camino);
        Circuito mejorCircuito = busquedaLocal(inicial,otroGrafo);
        imprimirArreglo(mejorCircuito.camino);

        //------------------------------------------------------------
        //4. Variar parámetros y la estrategia del algoritmo de búsqueda local que optimicen el funcionamiento del mismo.
        Circuito otroCircuito = busquedaLocalOptimizada(inicial,otroGrafo,3);
        imprimirArreglo(otroCircuito.camino);

        //--------------------------------------------------------------
        //"jugando" un poco con el xml para agarrarle la mano
        System.out.println("accediendo a los datos xml");
//        XMLaccessing archivoDeEntrada = new XMLaccessing();
//        String [] resultado1 = archivoDeEntrada.adyacentesDe("1");
//        String[] resultado2 = archivoDeEntrada.pesosDe("1");
//        imprimirArreglo(resultado1);
//        imprimirArreglo(resultado2);
//        GrafoMatriz grafoxml = new GrafoMatriz(archivoDeEntrada);
//        int[] camino3 = recorridoViajanteDeComercio(grafoxml);
//        imprimirArreglo(camino3);

        //-----------------------------------------------------
        /*5. Construir un algoritmo GRASP para el problema del viajante de comercio. La entrada de su algoritmo será un
        archivo con una instancia del problema del viajante de comercio (ej: matriz de distancias), y la salida deberá ser
        un archivo de texto plano con un circuito hamiltoniano y su valor.
         */
        algoritmoGRASPconArchivoDeEntradaYDeSalida("burma14.xml","outputBurma.txt");
        algoritmoGRASPconArchivoDeEntradaYDeSalida("brazil58.xml","outputBrazil.txt");
        algoritmoGRASPconArchivoDeEntradaYDeSalida("gr137.xml","outputGr137.txt");

        //---------------------------------------------------------
        /*6. Presentar un gráco de scoring contra la cantidad de iteraciones para baterías de distintas instancias, que permita
        decidir una cantidad de iteraciones que ayude a encontrar un valor cercano al óptimo sin desperdiciar tiempo de
        cómputo.
        * */
       // presentarElGrafico();

    }

    private static void presentarElGrafico() {
        XMLaccessing archivoDeEntrada1 = new XMLaccessing("burma14.xml");
        XMLaccessing archivoDeEntrada2 = new XMLaccessing("brazil58.xml");
        XMLaccessing archivoDeEntrada3 = new XMLaccessing("gr137.xml");
        GrafoMatriz grafo1 = new GrafoMatriz(archivoDeEntrada1);
        GrafoMatriz grafo2 = new GrafoMatriz(archivoDeEntrada2);
        GrafoMatriz grafo3 = new GrafoMatriz(archivoDeEntrada3);

        DefaultCategoryDataset datos = new DefaultCategoryDataset();
        float dato1 = recorrerElGrafo(grafo1,recorridoViajanteDeComercio(grafo1));
        float dato2 = recorrerElGrafo(grafo2,recorridoViajanteDeComercio(grafo2));
        float dato3 = recorrerElGrafo(grafo3,recorridoViajanteDeComercio(grafo3));
        datos.addValue(dato1,"Grafica GRASP", "burma14");
        datos.addValue(dato2,"Grafica GRASP", "brazil58");
        datos.addValue(dato3,"Grafica GRASP", "gr137");
        JFreeChart grafico = ChartFactory.createLineChart("Grafica GRASP","iteraciones","datos",datos);
        // Creación del panel con el gráfico
        ChartPanel panel = new ChartPanel(grafico);

        JFrame ventana = new JFrame("El grafico");
        try {
            ChartUtilities.saveChartAsJPEG(new File("grafico.jpg"), grafico, 500, 300);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        int nodoActual = 0;//O (1)
        int posicionDelMinimo=0;//O(1)
        int tamGrafo = grafo.numeroDeVertices();//O(1)
        int [] caminoRecorrido = new int[tamGrafo];//O(1)
        boolean [] yaLoRecorri = new boolean[tamGrafo];//O(1)
        inicializarEnFalso(yaLoRecorri);//O(n)
        inicializarEnCero(caminoRecorrido);//O(n)
        while (!yaLoRecorri[nodoActual]) { //peor de los casos: O(n)
            float[] misAdyacentes = grafo.adyacentesDe(nodoActual);//O(1)
            yaLoRecorri[nodoActual] = true;//O(1)
            insertarEnSiguientePosicionLibre(caminoRecorrido, nodoActual);//peor de los casos O(n)
            float pesoMinimo = grafo.pesoMasAlto(nodoActual);// O(1)
            for (int indice = 0; indice < tamGrafo; indice++) { //todo el if se ejecuta tantas veces como nodos haya O(m)
                if (!yaLoRecorri[indice]) {
                    if (misAdyacentes[indice] < pesoMinimo) {
                        pesoMinimo = misAdyacentes[indice];
                        posicionDelMinimo = indice;
                    }
                }
            }
            nodoActual = posicionDelMinimo;//O(1)
        }
        return caminoRecorrido;
    } // el while que ejecuta un for define el orden del metodo : O(n.m)

    public static int[] recorridoViajanteDeComercioAleatorio(GrafoMatriz grafo) {
        int nodoActual = 0; //O(1)
        int tamGrafo = grafo.numeroDeVertices(); //O(1)
        int [] caminoRecorrido = new int[tamGrafo]; //O(1)
        boolean [] yaLoRecorri = new boolean[tamGrafo]; //O(1)
        //elementos adicionales una cierta cantidad de candidatos
        int porcentajeDelGrafo = (tamGrafo *5)/100; //O(1)
        if (porcentajeDelGrafo<=0) porcentajeDelGrafo=1;//O(1) // para arreglos muy pequeños que no pueda sacar cierto porcentaje
        //un diccionario donde por cada peso dejo un nodo
        Map<Float, Integer> diccionario = new HashMap<>();
        inicializarEnFalso(yaLoRecorri); //O(n)
        inicializarEnCero(caminoRecorrido); //O(n)
        float[] candidatos = new float[tamGrafo]; //O(1)
        while (yaRecorridos(yaLoRecorri)< tamGrafo) { //peor de los casos se ejecuta n veces O(n)
            if(! yaLoRecorri[nodoActual]) {
                float[] misAdyacentes = grafo.adyacentesDe(nodoActual); //O(1)
                inicializarEnCero(candidatos); //O(n)
                yaLoRecorri[nodoActual] = true; //O(1)
                insertarEnSiguientePosicionLibre(caminoRecorrido, nodoActual); //peor de los casos O(n)
                for (int indice = 0; indice < tamGrafo; indice++) { //se ejecuta tantas veces como nodos tenga: O(m)
                    if (!yaLoRecorri[indice]) {
                        insertarEnSiguientePosicionLibre(candidatos, misAdyacentes[indice]);
                        //System.out.println("agrego al arreglo");
                        //inserto todos los adyacentes
                        diccionario.put(misAdyacentes[indice], indice);
                    }
                }
                //ordeno de mayor a menor los adyacentes
                Arrays.sort(candidatos);
            }
            //double x = (Math.random()*((max-min)+1))+min;
            double sig = Math.random() * ((porcentajeDelGrafo) + 1) + porcentajeDelGrafo;
            int siguiente = (int)sig;
            //System.out.println(siguiente);
            if((!diccionario.isEmpty())&& (diccionario.get(candidatos[siguiente])!=null))
                nodoActual = diccionario.get(candidatos[siguiente]); //O(1)
            else nodoActual= siguiente;
        }
        return caminoRecorrido;
    } // el while dentro del for le da el orden a este metodo: O(n.m)

    private static int yaRecorridos(boolean[] yaLoRecorri) {
        int contador =0;
        for (boolean posicion: yaLoRecorri){
            {
                if (posicion) contador++;
            }
        }
        return contador;
    }


    private static void inicializarEnCero(float[] candidatos) {
        for (int i = 0; i< candidatos.length; i++) candidatos[i]=0;
    }

    public static Circuito busquedaLocal(Circuito caminoSugerido, GrafoMatriz grafo){
        int tamanioArreglo = caminoSugerido.camino.length; // O(1)
        caminoSugerido.costo = recorrerElGrafo(grafo,caminoSugerido.camino); // recorrer es de O(m)
        Circuito mejorSolucion = caminoSugerido; //O(1)
        Circuito[] vecindad = new Circuito[caminoSugerido.camino.length]; //O(1)
        for (int indice = 0; indice < tamanioArreglo; indice++) { // tantos nodos tenga O(m)
            vecindad[indice]= generarVecino(indice,tamanioArreglo, caminoSugerido);
        }
        for (Circuito vecino: vecindad) { //tantos elementos de la vecindad: O(n)
            vecino.costo = recorrerElGrafo(grafo,vecino.camino); // recorrer es de O(m)
            if (caminoSugerido.costo> vecino.costo){
                mejorSolucion = vecino;
            }
        }
        if (mejorSolucion != caminoSugerido){ //mejor o peor caso O(1)
            return busquedaLocal(mejorSolucion,grafo);
        }
        else {
            return mejorSolucion;
        }
    } //tenemos "m" de recorrer + "m" de generar vecinos + "n" veces buscar el optimo recorriendo cada vez "m"
    //nos da 2m + n.m = (2+n)m = Orden(n.m)

    public static Circuito busquedaLocalOptimizada(Circuito caminoSugerido, GrafoMatriz grafo,int intentosMax) {
        Circuito mejorSolucion = caminoSugerido; //O(1)
        if (intentosMax > 0) { //peor de los casos ejecutara los 2 for de orden m -> O (m . m)
            int tamanioArreglo = caminoSugerido.camino.length;//O(1)
            //caminoSugerido.costo = recorrerElGrafo(grafo, caminoSugerido.camino);
            Circuito[] vecindad = new Circuito[caminoSugerido.camino.length]; //O(1)
            for (int indice = 0; indice < tamanioArreglo; indice++) { //se ejecuta cuantos vertices tenga -> O(m)
                vecindad[indice] = generarVecino(indice, tamanioArreglo, caminoSugerido);
            }
            for (Circuito vecino : vecindad) {//se ejecuta cuantos vecinos tenga -> O(n)
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
    } // el orden se los da los dos for O(m+n)


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
        float pesoAcumulado=0; //O(1)
        for (int vertice =0; vertice< camino.length; vertice++){ //O(n)
            pesoAcumulado += grafo.pesoDeArista(vertice,vertice+1); //O(1)
        }
        return pesoAcumulado;
    } // el metodo es de O(n)

    private static Circuito generarVecino(int indice, int tamanioArreglo, Circuito mejorSolucion) {
        int[] vecinoNuevo = mejorSolucion.camino; //O(1) //le copio todos los valores
        if (indice < tamanioArreglo-1){ //peor de los caso se ejecuta dos intrucciones de orden 1
            //swappeo el del indice actual y su siguiente
            vecinoNuevo[indice]= mejorSolucion.camino[indice + 1]; //O(1)
            vecinoNuevo[indice +1] = mejorSolucion.camino[indice]; //O(1)
        }
        else {
            // swappeo el primero con el ultimo
            vecinoNuevo[indice]= mejorSolucion.camino[0]; //O(1)
            vecinoNuevo[0] = mejorSolucion.camino[indice]; //O(1)
        }
        return new Circuito(vecinoNuevo);
    }// ejecuta 3 asignaciones de orden constante -> O(1)


    private static int ocupados(int[] candidatos) {
        int contador = 0;
        while ((contador < candidatos.length)&&(candidatos[contador]!=0)){// peor de los casos se ejecuta n veces
            contador++; //O(1)
        }
        return contador;
    }// el metodo es de O(n)

    private static void agregarLosNoCandidatosALosCandidatos(int[] candidatos, int[] noCandidatos) {
        for (int posibleCandidato : noCandidatos){ //se ejecuta tantos candidatos tenga: O(n)
            insertarEnSiguientePosicionLibre(candidatos,posibleCandidato); //O(n)
        }
    } //O(n cuadrado)

    private static int getNumeroRandom(int max) {
        return ThreadLocalRandom.current().nextInt(0, max);//O(1)
    } //O(1)


    private static void insertarEnSiguientePosicionLibre(int[] unArreglo, int aGuardar) {
        int puntero = 0; //O(1)
        while ((puntero < unArreglo.length)&& (unArreglo[puntero]!=0)){//O(n) //recorro hasta encontrar una posicion en 0
            puntero++;//O(1)
        }
        if (puntero<unArreglo.length)
            unArreglo[puntero]=aGuardar+1;//O(1)
    } //el while le da orden a este metodo: O(n)
    private static void insertarEnSiguientePosicionLibre(float[] unArreglo, float aGuardar) {
        int puntero = 0; //O(1)
        while ((puntero < unArreglo.length)&& (unArreglo[puntero]!=0)){//O(n) //recorro hasta encontrar una posicion en 0
            puntero++;//O(1)
        }
        if (puntero<unArreglo.length)
            unArreglo[puntero]=aGuardar+1;//O(1)
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