package entrada;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLaccessing {
    Map<String,String[]> nodoAristas= new HashMap<>();
    Map<String,String[]> nodoPesos= new HashMap<>();

    public XMLaccessing(){
        try {
            // Creo una instancia de DocumentBuilderFactory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Creo un documentBuilder
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Obtengo el documento, a partir del XML
            Document documento = builder.parse(new File("burma14.xml"));

            // Tomo todas las etiquetas coche del documento
            NodeList listaVertices = documento.getElementsByTagName("vertex");

            // Recorro las etiquetas
            for (int i = 0; i < listaVertices.getLength(); i++) {
                // Tomo el nodo actual
                Node nodo = listaVertices.item(i);

                // Compruebo si el nodo es un elemento
                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    // Lo transformo a Element
                    Element e = (Element) nodo;
                    // Obtengo sus hijos
                    NodeList hijos = e.getChildNodes();
                    //creo un arreglo para quedarme con los hijos y otro para las distancias
                    String[] adyacentes = new String[hijos.getLength()];
                    String[] costos = new String[hijos.getLength()];

                    // Recorro sus hijos
                    for (int j = 0; j < hijos.getLength(); j++) {
                        // Obtengo al hijo actual
                        Node hijo = hijos.item(j);
                        // Compruebo si es un nodo
                        if (hijo.getNodeType() == Node.ELEMENT_NODE) {
                            //lo guardo al adyacente
                            String contenidoAdyacentes =hijo.getTextContent();
                            String contenidoPesos=(""+hijo.getAttributes().removeNamedItem("cost")).substring(6,9);
                            if (!contenidoAdyacentes.contains("null")){
                                adyacentes[j]=contenidoAdyacentes;costos[j]= contenidoPesos;}
                            else {
                                adyacentes[j]="vacio";costos[j]="vacio";
                            }
                        }



                    }//guardo las aristas a mi diccionario
                    this.nodoAristas.put(String.valueOf(i),filtrarNulos(adyacentes));
                    this.nodoPesos.put(String.valueOf(i),filtrarNulos(costos));
                    System.out.println(" ");
                }

            }

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public String[] adyacentesDe(String nodo){
        return this.nodoAristas.get(nodo);
    }

    public String[] pesosDe(String nodo){
        return this.nodoPesos.get(nodo);
    }

    public String[] filtrarNulos (String[] arreglo) {
        int cantidadDeNulos = 0;
        int tamTotal = arreglo.length;
        String[] arregloTemporal = new String[tamTotal];
        String[] arregloFinal;
        int indice = 0;
        for (int j = 0; j < tamTotal; j++) {
            if (arreglo[j]==null) {
                cantidadDeNulos++;
            } else {
                arregloTemporal[indice] = arreglo[j];
                indice++;
            }
        }
        arregloFinal = new String[tamTotal - cantidadDeNulos];
        for (int k = 0; k < arregloFinal.length; k++) {
            arregloFinal[k] = arregloTemporal[k];
        }
        return arregloFinal;
    }

    public Map<String, String[]> getNodoAristas() {
        return nodoAristas;
    }

    public Map<String, String[]> getNodoPesos() {
        return nodoPesos;
    }
}
