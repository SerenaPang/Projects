
import java.io.IOException;
import java.io.InputStream;
 
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
 
public class MyImageUtils {
 
    
    // resourcePath: "/org/o7planning/swt/icon/java-32.png"
    public static Image getImage(Display display, String resourcePath) {
        InputStream input = null;
        try {
            // /org/o7planning/swt/icon/java-32.png
            input = MyImageUtils.class.getResourceAsStream(resourcePath);
            Image image = new Image(display, input);
            return image;
        } finally {
            closeQuietly(input);
        }
    }
 
    private static void closeQuietly(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
        }
    }
    
}