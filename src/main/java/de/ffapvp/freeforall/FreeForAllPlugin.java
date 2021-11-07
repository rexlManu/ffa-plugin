/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall;

import com.mongodb.ConnectionString;
import de.dytanic.cloudnet.api.CloudAPI;
import de.ffapvp.freeforall.command.ForceMapCommand;
import de.ffapvp.freeforall.command.FreeForAllCommand;
import de.ffapvp.freeforall.command.StatsCommand;
import de.ffapvp.freeforall.database.ConfigurationLoader;
import de.ffapvp.freeforall.database.DatabaseConfiguration;
import de.ffapvp.freeforall.database.DatabaseConnector;
import de.ffapvp.freeforall.database.inventory.SortInventoryHandler;
import de.ffapvp.freeforall.listener.FreeForAllListener;
import de.ffapvp.freeforall.mode.GameType;
import de.ffapvp.freeforall.mode.map.Map;
import de.ffapvp.freeforall.mode.map.MapHandler;
import de.ffapvp.freeforall.scoreboard.PlayerScoreboard;
import de.ffapvp.freeforall.stats.StatsHandler;
import de.ffapvp.freeforall.task.ActionbarTask;
import de.ffapvp.freeforall.task.MapchangeTask;
import de.ffapvp.freeforall.task.WorldTask;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class FreeForAllPlugin extends JavaPlugin {

    @Getter
    private static FreeForAllPlugin plugin;

    public static final String PREFIX = "§8▎ §2FFA §8» §7";

    private File databaseConfigurationFile;
    private DatabaseConfiguration databaseConfiguration;
    private DatabaseConnector databaseConnector;

    private GameType gameType;
    private File file;
    private YamlConfiguration locationConfiguration;
    private MapHandler mapHandler;
    private StatsHandler statsHandler;
    private Map map;
    private PlayerScoreboard playerScoreboard;
    private MapchangeTask mapchangeTask;
    private SortInventoryHandler sortInventoryHandler;

    @Override
    public void onEnable() {
        plugin = this;
        this.getDataFolder().mkdirs();
        this.databaseConfigurationFile = new File(this.getDataFolder(), "database.json");

        try {
            if (!this.databaseConfigurationFile.exists()) {
                ConfigurationLoader.saveConfig(this.databaseConfigurationFile.toPath(), DatabaseConfiguration.class);
            } else {
                this.databaseConfiguration = ConfigurationLoader.loadConfig(this.databaseConfigurationFile.toPath(), DatabaseConfiguration.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.gameType = GameType.getByGroupName(CloudAPI.getInstance().getGroup());
        if (this.gameType == null) return;

        this.file = new File(this.getDataFolder(), "locations.yml");
        this.locationConfiguration = YamlConfiguration.loadConfiguration(this.file);
        this.mapHandler = new MapHandler();
        this.statsHandler = new StatsHandler();
        this.getCommand("ffa").setExecutor(new FreeForAllCommand());
        this.getCommand("forcemap").setExecutor(new ForceMapCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        if (this.databaseConfiguration != null)
            this.databaseConnector = new DatabaseConnector(this.databaseConfiguration, "stats");

        if (this.mapHandler.getMaps().isEmpty()) return;
        this.map = mapHandler.getRandomMap();
        this.sortInventoryHandler = new SortInventoryHandler();
        Bukkit.getPluginManager().registerEvents(new FreeForAllListener(), this);

        this.playerScoreboard = new PlayerScoreboard();
        this.mapchangeTask = new MapchangeTask();
        new ActionbarTask();
        new WorldTask();
    }

    public Location getLocation(String key) {
        Object object = this.locationConfiguration.get(key);
        if (object == null) return null;
        return (Location) object;
    }

    public void saveLocationConfiguration() {
        try {
            this.locationConfiguration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
