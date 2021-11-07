/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode.map;

import de.ffapvp.freeforall.FreeForAllPlugin;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapHandler {

    private static final Random RANDOM = new Random();

    @Getter
    private List<Map> maps;
    private File file;
    private YamlConfiguration configuration;

    public MapHandler() {
        this.maps = new ArrayList<>();
        this.file = new File(FreeForAllPlugin.getPlugin().getDataFolder(), "maps.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        this.configuration.getStringList("maps").forEach(mapName -> {
            File mapConfigurationFile = new File(FreeForAllPlugin.getPlugin().getDataFolder(), mapName + "/configuration.yml");
            this.maps.add(new Map(mapName, YamlConfiguration.loadConfiguration(mapConfigurationFile)));
        });
    }

    public void addMap(String name) {
        if (this.configuration.get("maps") == null) this.configuration.set("maps", new ArrayList<>());
        this.configuration.getStringList("maps").add(name);
        try {
            this.configuration.save(this.file);
        } catch (IOException ignored) {
        }

        this.maps.add(new Map(name, YamlConfiguration.loadConfiguration(new File(FreeForAllPlugin.getPlugin().getDataFolder(), name + "/configuration.yml"))));
    }

    public Map getMapByName(String name) {
        return this.maps.stream().filter(map -> map.getName().equals(name)).findFirst().orElse(null);
    }

    public Map getRandomMap() {
        return this.maps.get(RANDOM.nextInt(maps.size()));
    }
}
