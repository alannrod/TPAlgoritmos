package salida;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class ArchivoSalida {

    PrintWriter writer;

    public ArchivoSalida(){
        try {
            this.writer = new PrintWriter("filename.txt", StandardCharsets.UTF_8);
            writer.println("Primera línea");
            writer.println("Segunda línea");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArchivoSalida(String nombreDelArchivo){
        try {
            this.writer = new PrintWriter(nombreDelArchivo, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escribirEnElArchivo(String renglon){
        this.writer.println(renglon);
    }
    public void cerrarArchivo(){
        this.writer.close();
    }
}
