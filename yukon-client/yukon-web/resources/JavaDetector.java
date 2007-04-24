import java.applet.Applet;

public class JavaDetector extends Applet {
    public String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
