import entrada.XMLaccessing;
import estructuras.Candidato;
import estructuras.Circuito;
import estructuras.GrafoMatriz;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.category.DefaultCategoryDataset;
import salida.ArchivoSalida;

import java.io.File;
import java.util.*;

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
        System.out.println("busqueda local");
        int[] camino3 = busquedaLocal(otroGrafo);
        imprimirArreglo(camino3);

        //------------------------------------------------------------
        //4. Variar parámetros y la estrategia del algoritmo de búsqueda local que optimicen el funcionamiento del mismo.
        System.out.println("busqueda Local 2");
        int[] camino4 = busquedaLocal2(otroGrafo);
        imprimirArreglo(camino4);

        //--------------------------------------------------------------
        //"jugando" un poco con el xml para agarrarle la mano
        System.out.println("accediendo a los datos xml");
        XMLaccessing archivoDeEntrada = new XMLaccessing();
        String [] resultado1 = archivoDeEntrada.adyacentesDe("1");
        String[] resultado2 = archivoDeEntrada.pesosDe("1");
        System.out.println("adyacentes del nodo '1'");
        imprimirArreglo(resultado1);
        System.out.println(("pesos de los adyacentes del nodo '1'"));
        imprimirArreglo(resultado2);
        System.out.println("creando un grafo a partir del archivo xml");
        GrafoMatriz grafoxml = new GrafoMatriz(archivoDeEntrada);
        System.out.println("obteniendo un recorrido del grafico creado");
        int[] camino5 = recorridoViajanteDeComercio(grafoxml);
        imprimirArreglo(camino5);

        //-----------------------------------------------------
        /*5. Construir un algoritmo GRASP para el problema del viajante de comercio. La entrada de su algoritmo será un
        archivo con una instancia del problema del viajante de comercio (ej: matriz de distancias), y la salida deberá ser
        un archivo de texto plano con un circuito hamiltoniano y su valor.
         */
//        System.out.println("algoritmo GRASP");
//        System.out.println("Archivo 'burma14'");
//        algoritmoGRASPconArchivoDeEntradaYDeSalida("burma14.xml","outputBurma.txt");
//        System.out.println("Archivo 'brazil158'");
//        algoritmoGRASPconArchivoDeEntradaYDeSalida("brazil58.xml","outputBrazil.txt");
//        System.out.println("Archivo 'gr137'");
//        algoritmoGRASPconArchivoDeEntradaYDeSalida("gr137.xml","outputGr137.txt");

        //---------------------------------------------------------
        /*6. Presentar un grafico de scoring contra la cantidad de iteraciones para baterías de distintas instancias, que permita
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
      int nodoActual = 0;//O (1)
      //int posicionDelMinimo=0;//O(1)
      int tamGrafo = grafo.numeroDeVertices();//O(1)
      int porcentajeDelGrafo = (tamGrafo *10)/100; //O(1)
      if (porcentajeDelGrafo<=0) porcentajeDelGrafo=1;//O(1) // para arreglos muy pequeños que no pueda sacar cierto porcentaje

      int [] caminoRecorrido = new int[tamGrafo];//O(1)
      boolean [] yaLoRecorri = new boolean[tamGrafo];//O(1)
      inicializarEnFalso(yaLoRecorri);//O(n)
      inicializarEnCero(caminoRecorrido);//O(n)
      while (!yaLoRecorri[nodoActual]) { //peor de los casos: O(n)
          float[] misAdyacentes = grafo.adyacentesDe(nodoActual);//O(1) me guardo el peso de las aristas adyacentes al nodo actual
          Candidato[] misCandidatos = new Candidato[tamGrafo];
          yaLoRecorri[nodoActual] = true;//O(1)
          insertarEnSiguientePosicionLibre(caminoRecorrido, nodoActual);//peor de los casos O(n)
          for (int indice = 0; indice < tamGrafo; indice++) {
              if (!yaLoRecorri[indice]) {
                  Candidato talCual = new Candidato(misAdyacentes[indice],indice);
                  misCandidatos[indice] = talCual;
                  }
              else {
                  Candidato valorAlto = new Candidato(999999999,indice);
                  misCandidatos[indice] = valorAlto;
              }
          }
          Arrays.sort(misCandidatos);
          //double x = (Math.random()*((max-min)+1))+min;
          double sig = Math.random() * ((porcentajeDelGrafo) + 1) + porcentajeDelGrafo;
          int siguiente = (int)sig-1;
          nodoActual = misCandidatos[siguiente].getNumero();//O(1)
      }
      return caminoRecorrido;
  }



public static int[] busquedaLocal ( GrafoMatriz grafo) {
    int[] solucionHastaAhora = recorridoViajanteDeComercio(grafo);
    int[] mejorSolucion = solucionHastaAhora.clone();
    float costoOptimo = recorrerElGrafo(grafo,mejorSolucion); // hasta ahora el unico costo que tengo

    List<int[]> vecinos = new ArrayList<>();
    for(int iterador=0; iterador < mejorSolucion.length; iterador++ ){
        int[] vecino = generarVecino(iterador, mejorSolucion.length, solucionHastaAhora);
        vecinos.add(vecino);//agrego al vecino
    }
    /* comento una posible solucion alternativa */
//    Iterator i = vecinos.iterator();
//    while(i.hasNext())
//    {
//        float nuevoCosto = recorrerElGrafo(grafo, (int[]) i.next());
//        if (nuevoCosto <costoOptimo){
//            costoOptimo = nuevoCosto;
//            mejorSolucion = (int[]) i.next();
//        }
//    }
    for (int[] vecino : vecinos) {
        float otroCosto = recorrerElGrafo(grafo, vecino);
        System.out.println("Costo del vecino "+ otroCosto);
        if (otroCosto <= costoOptimo) {
            costoOptimo = otroCosto;
            mejorSolucion = vecino;
        }
    }

    return mejorSolucion;
}


public static int[] busquedaLocal2 ( GrafoMatriz grafo) {
    int[] solucionHastaAhora = recorridoViajanteDeComercio(grafo);
    Circuito elPrimero = new Circuito(solucionHastaAhora.clone(), recorrerElGrafo(grafo,solucionHastaAhora));

    List<Circuito> vecinos = new ArrayList<>();
    vecinos.add(elPrimero);
    for(int iterador=0; iterador < solucionHastaAhora.length; iterador++ ){
        int[] vecino = generarVecino(iterador, solucionHastaAhora.length, solucionHastaAhora);
        Circuito nuevoVecino = new Circuito(vecino,calcularCostoDePermutarParNro(iterador,grafo,elPrimero,vecino));
        vecinos.add(nuevoVecino);//agrego al vecino
        System.out.println("costo del vecino " + nuevoVecino.costo);

    }
    Collections.sort(vecinos);

    return vecinos.get(1).camino;
}

    private static float calcularCostoDePermutarParNro(int iterador, GrafoMatriz grafo, Circuito solucionHastaAhora, int[] vecino) {
        int[] caminoOriginal = solucionHastaAhora.camino.clone();
        int maximo = grafo.numeroDeVertices();
        float costoDeVercicesQueNoEstan ;
        float costoDeVerticesQueAhoraEstan ;
        if (iterador ==0) { //se cambio el primer par entoces la arista nueva va del segundo al tercero
            costoDeVercicesQueNoEstan = grafo.pesoDeArista(caminoOriginal[2], caminoOriginal[3]);
            costoDeVerticesQueAhoraEstan = grafo.pesoDeArista(vecino[2], vecino[3]);
        } else if (iterador ==maximo){ //se cambio el inicio y el fin del camino, las aristas nuevas van del penulimo al ultimo y del primero al segundo
                costoDeVercicesQueNoEstan = grafo.pesoDeArista(caminoOriginal[0],caminoOriginal[1])+ grafo.pesoDeArista(caminoOriginal[maximo-1],caminoOriginal[maximo]);
                costoDeVerticesQueAhoraEstan= grafo.pesoDeArista(vecino[0],vecino[1])+grafo.pesoDeArista(vecino[maximo-1],vecino[maximo]);
        }
        else {// intercambie un par del medio las aristas anterior y posterior al par cambian
            if (iterador< maximo-2){
                costoDeVercicesQueNoEstan = grafo.pesoDeArista(caminoOriginal[iterador-1],caminoOriginal[iterador])+ grafo.pesoDeArista(caminoOriginal[iterador+1],caminoOriginal[iterador+2]);
                costoDeVerticesQueAhoraEstan = grafo.pesoDeArista(vecino[iterador-1],vecino[iterador])+grafo.pesoDeArista(vecino[iterador+1],vecino[iterador+2]);
            }
            else {
                costoDeVercicesQueNoEstan = grafo.pesoDeArista(caminoOriginal[iterador-1],caminoOriginal[iterador]);
                costoDeVerticesQueAhoraEstan = grafo.pesoDeArista(vecino[iterador-1],vecino[iterador]);
            }

        }
        return solucionHastaAhora.costo- costoDeVercicesQueNoEstan + costoDeVerticesQueAhoraEstan;
    }



    public static void algoritmoGRASPconArchivoDeEntradaYDeSalida(String nombreDelArchivoEntrada, String nombreDelArchivoDeSalida) {
        XMLaccessing miEntrada = new XMLaccessing(nombreDelArchivoEntrada);
        GrafoMatriz miGrafo = new GrafoMatriz(miEntrada);
        ArchivoSalida miSalida = new ArchivoSalida(nombreDelArchivoDeSalida);
        miSalida.escribirEnElArchivo("Algoritmo GRASP aplicado al archivo "+ nombreDelArchivoEntrada);
        int [] camino = recorridoViajanteDeComercio(miGrafo);
        miSalida.escribirEnElArchivo("obtuvimos la siguiente solucion usando camino Hamiltoneano: "+Arrays.toString(camino));
        Circuito elMejorHastaAhora = new Circuito(camino,recorrerElGrafo(miGrafo,camino));
        miSalida.escribirEnElArchivo("cuyo costo de recorrer es: "+ elMejorHastaAhora.costo);
        int [] otroCamino = busquedaLocal2(miGrafo);
        Circuito hayOtroMejor = new Circuito(otroCamino,recorrerElGrafo(miGrafo,otroCamino));
        while (hayOtroMejor != elMejorHastaAhora){
            elMejorHastaAhora = hayOtroMejor;
            miSalida.escribirEnElArchivo("Con busqueda local se presento esta nueva solucion: "+ Arrays.toString(elMejorHastaAhora.camino));
            miSalida.escribirEnElArchivo("y el costo de recorrer es: " + elMejorHastaAhora.costo);
            otroCamino = busquedaLocal2(miGrafo);
            //hayOtroMejor = new Circuito(otroCamino,recorrerElGrafo(miGrafo,otroCamino));
        }
        miSalida.cerrarArchivo();
    }




    private static float recorrerElGrafo(GrafoMatriz grafo, int[] camino) {
        float pesoAcumulado=0; //O(1)
        for (int vertice =0; vertice< camino.length-1; vertice++){ //O(n)
            pesoAcumulado += grafo.pesoDeArista(camino[vertice],camino[vertice+1]); //O(1)
        }
        return pesoAcumulado;
    } // el metodo es de O(n)

    private static int[] generarVecino(int indice, int tamanioArreglo, int[] laMejorSolucion) {
        int[] vecinoNuevo = laMejorSolucion.clone(); //O(1) //le copio todos los valores
        if (indice < tamanioArreglo-1){ //peor de los caso se ejecuta dos intrucciones de orden 1
            //swappeo el del indice actual y su siguiente
            vecinoNuevo[indice]= laMejorSolucion[indice + 1]; //O(1)
            vecinoNuevo[indice +1] = laMejorSolucion[indice]; //O(1)
        }
        else {
            // swappeo el primero con el ultimo
            vecinoNuevo[indice]= laMejorSolucion[0]; //O(1)
            vecinoNuevo[0] = laMejorSolucion[indice]; //O(1)
        }
        return vecinoNuevo;
    }// ejecuta 3 asignaciones de orden constante -> O(1)






    private static void insertarEnSiguientePosicionLibre(int[] unArreglo, int aGuardar) {
        int puntero = 0; //O(1)
        while ((puntero < unArreglo.length)&& (unArreglo[puntero]!=0)){//O(n) //recorro hasta encontrar una posicion en 0
            puntero++;//O(1)
        }
        if (puntero<unArreglo.length)
            unArreglo[puntero]=aGuardar+1;//O(1)
    } //el while le da orden a este metodo: O(n)


    private static void inicializarEnCero(int[] caminoRecorrido) {
        Arrays.fill(caminoRecorrido, 0);
    }

    private static void inicializarEnFalso(boolean[] yaLoRecorri) {
        Arrays.fill(yaLoRecorri, false);
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