package entrada;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class XMLtesting {
    public static void main(String[] args) throws ParserConfigurationException, SAXException
    {
        try {
            File file = new File("burma14.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
            System.out.println("Root Element :" + document.getDocumentElement().getNodeName());
            //NodeList nList = document.getElementsByTagName("employee");
            NodeList nList = document.getElementsByTagName("vertex");
            System.out.println("----------------------------");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    //System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                    System.out.println("Adyacentes : " + eElement.getElementsByTagName("edge").item(0).getTextContent() );
                    for (int aristas=0; aristas < ((Element) nNode).getElementsByTagName("edge").getLength() ;aristas++){
                        //System.out.println("pesa la arista "+eElement.getAttribute("cost").startsWith("cost="));
                        System.out.println("pesa la arista "+eElement.getPrefix());
                    }
                    //System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                    //System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());
                }
            }
        }
        catch(IOException e) {
            System.out.println(e);
        }
    }

}
