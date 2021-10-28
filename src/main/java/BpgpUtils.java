import ec.EvolutionState;
import org.apache.commons.io.IOUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BpgpUtils {

    // return an int ECJ param
    public static int getEcjIntParam(EvolutionState state, String name) {
        ec.util.Parameter param = new ec.util.Parameter(name);
        return state.parameters.getInt(param, null);
    }

    // return how many times does word appear in s
    public static int countInString(String s, String word) {
        int i = 0;
        Pattern p = Pattern.compile(word);
        Matcher m = p.matcher(s);
        while (m.find()) {
            i++;
        }
        return i;
    }

    public static String writeToTempFile(String generatedCode) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("BPGP-", ".tmp.js", getResourcesPath());

            tempFile.deleteOnExit();
//            System.out.println(tempFile.getAbsolutePath());

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
        ClassLoader classLoader = BpgpUtils.class.getClassLoader();
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

    static boolean isSlurm() {
        return System.getenv("SLURM_JOB_ID") != null;
    }

    static String getResourceFileName(String resource) {
        ClassLoader classLoader = BpgpUtils.class.getClassLoader();
        return classLoader.getResource(resource).getPath();
    }

//    static double sigmoid(double x) {
//        return 1.0 / (1 + Math.exp(-x));
//    }

//    static double pressure(double x) {
//        //TODO remember denom?
//        return (1.0 - Math.exp(-x)) / (1 - Math.exp(-1));
//    }

//    private String resourceToString(String resourceName) {
//        URL url = this.getClass().getResource(resourceName);
//        String result = null;
//        try {
//            result = IOUtils.toString(url);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

}
