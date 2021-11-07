/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode.map;

import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.utility.Region;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
public class Map {

    private String name;
    private YamlConfiguration configuration;
    private Region spawnRegion;

    public Map(String name, YamlConfiguration configuration) {
        this.name = name;
        this.configuration = configuration;

        if (configuration.get("pos1") != null && configuration.get("pos2") != null) {
            this.spawnRegion = new Region((Location) configuration.get("pos1"), (Location) configuration.get("pos2"));
        }
    }

    public void saveConfiguration() {
        try {
            this.configuration.save(new File(FreeForAllPlugin.getPlugin().getDataFolder(), name + "/configuration.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Location getLocation(String key) {
        Object object = this.configuration.get(key);
        if (object == null) return null;
        return (Location) object;
    }

}
