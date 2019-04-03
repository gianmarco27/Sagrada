package ingsw.Server.Grid;

import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class handle the loading of grid Json from a resource folder
 */
public class GridLoader {

    public static Grid loadFromFile(String path) throws IOException, InvalidJsonException {
        String jsonContent = readFile(path, StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(jsonContent);
        return new Grid(json);

    }
    private static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    public static CopyOnWriteArrayList<String> listFiles(String path) {
        CopyOnWriteArrayList<String> results = new CopyOnWriteArrayList<>();

        File[] files = new File(path).listFiles();
        //If this pathname does not denote a directory, then listFiles() returns null.
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.isFile()) {
                results.add(path + File.separatorChar + file.getName());
            }
        }
        return results;
    }
}
