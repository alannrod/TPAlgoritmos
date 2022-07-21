package salida;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class CrearArchivo2Test {
    public static void main(String[] ars){
        try {
            PrintWriter writer = new PrintWriter("filename.txt", StandardCharsets.UTF_8);
            writer.println("Primera línea");
            writer.println("Segunda línea");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
