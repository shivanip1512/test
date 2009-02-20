import java.applet.Applet;

public class JavaDetector extends Applet {
	public String javaVersion = "foo.bar";
    public String getJavaVersion = "bar.foo";
	public String getJavaVersion() {
        return System.getProperty("java.version");
    }
}
