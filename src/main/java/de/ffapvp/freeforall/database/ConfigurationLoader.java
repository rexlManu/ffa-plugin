/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class ConfigurationLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T loadConfig(Path path, Class<T> type) throws Exception {
        return GSON.fromJson(new String(Files.readAllBytes(path)), type);
    }

    public static void saveConfig(Path path, Class<?> type) throws Exception {
        Files.write(path, GSON.toJson(type.newInstance()).getBytes(StandardCharsets.UTF_8));
    }

}
