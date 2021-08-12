import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class BpgpUtils {
    public static String writeToTempFile(String generatedCode) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("BPGP-", ".tmp.js", getResourcesPath());
            //        tempFile.deleteOnExit();      //TODO uncomment
            System.out.println(tempFile.getAbsolutePath()); //TODO del

            FileWriter fileWriter = new FileWriter(tempFile, false);
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(generatedCode.toString());
            bw.close();
            return tempFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static File getResourcesPath() {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        URL url = classLoader.getResource(".");
        File path = null;
        try {
            path = new File(url.toURI());
        } catch (URISyntaxException e) {
            path = new File(url.getPath());
        } finally {
            return path;
        }
    }

    private String resourceToString(String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        String result = null;
        try {
            result = IOUtils.toString(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
