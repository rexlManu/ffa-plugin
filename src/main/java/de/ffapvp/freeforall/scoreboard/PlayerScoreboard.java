/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.scoreboard;

import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.mode.map.Map;
import de.ffapvp.freeforall.stats.GlobalPlayerStats;
import de.ffapvp.freeforall.stats.PlayerStats;
import de.ffapvp.freeforall.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PlayerScoreboard extends Task {

    private int animationTick;

    public PlayerScoreboard() {
        this.animationTick = 0;
        this.runTaskTimer(FreeForAllPlugin.getPlugin(), 1, 7);
    }

    @Override
    public void run() {
        if (animationTick >= animation.length) animationTick = 0;

        Bukkit.getOnlinePlayers().forEach(player -> {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null) return;
            Objective objective = scoreboard.getObjective("ffa");
            if (objective == null) return;
            objective.setDisplayName(animation[animationTick]);
            player.setScoreboard(scoreboard);
        });

        animationTick++;
    }

    private String[] animation = new String[]
            {
                    "§8▎",
                    "§8▎ §aF",
                    "§8▎ §aFF",
                    "§8▎ §aFFA",
                    "§8▎ §aFFA§2§o4",
                    "§8▎ §aFFA§2§o4§8.§aF",
                    "§8▎ §aFFA§2§o4§8.§aFU",
                    "§8▎ §aFFA§2§o4§8.§aFUN",
                    "§8▎ §aFFA§2§o4§8.§aFUN §8▎",
                    "§8▎ §aFFA§2§o4§8.§aFUN §8▎",
                    "§8▎ §aFFA§2§o4§8.§aFUN §8▎",
                    "§8▎ §aFFA§2§o4§8.§aFUN §8▎",
                    "§8▎ §aFFA§2§o4§8.§aFUN",
                    "§8▎ §aFFA§2§o4§8.§aFU",
                    "§8▎ §aFFA§2§o4§8.§aF",
                    "§8▎ §aFFA§2§o4",
                    "§8▎ §aFFA",
                    "§8▎ §aFF",
                    "§8▎ §aF",
                    "§8▎",
                    "§r"
            };

    public void sendScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("ffa", "dummy");
        objective.setDisplayName("§r");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Map map = FreeForAllPlugin.getPlugin().getMap();

        GlobalPlayerStats stats = FreeForAllPlugin.getPlugin().getStatsHandler().getGlobalStatsByPlayer(player);
        PlayerStats currentStats = FreeForAllPlugin.getPlugin().getStatsHandler().getStatsByPlayer(player);
        PlayerStats combinedStats = stats.getCombinedStats();

        objective.getScore("§a").setScore(12);
        objective.getScore("§7Map").setScore(11);
        this.createElement(scoreboard, objective, "0x1", "§1§8» §a", "", map.getName(), 10);
        objective.getScore("§b").setScore(9);
        objective.getScore("§7Killstreak").setScore(8);
        this.createElement(scoreboard, objective, "0x2", "§2§8» §a", "", player.getLevel() + "", 7);
        objective.getScore("§c").setScore(6);
        objective.getScore("§7Kills").setScore(5);
        this.createElement(scoreboard, objective, "0x3", "§3§8» §a", "", combinedStats.getKills() + currentStats.getKills() + "", 4);
        objective.getScore("§d").setScore(3);
        objective.getScore("§7Deaths").setScore(2);
        this.createElement(scoreboard, objective, "0x4", "§4§8» §a", "", combinedStats.getDeaths() + currentStats.getDeaths() + "", 1);
        objective.getScore("§e").setScore(0);

        player.setScoreboard(scoreboard);
    }

    private void createElement(Scoreboard scoreboard, Objective objective, String fieldName, String entry, String prefix, String suffix, int score) {
        Team team = scoreboard.registerNewTeam(fieldName);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.addEntry(entry);
        objective.getScore(entry).setScore(score);
    }

    public void updateScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == null) return;
        GlobalPlayerStats stats = FreeForAllPlugin.getPlugin().getStatsHandler().getGlobalStatsByPlayer(player);
        PlayerStats currentStats = FreeForAllPlugin.getPlugin().getStatsHandler().getStatsByPlayer(player);

        PlayerStats playerStats = stats.getCombinedStats();
        Map map = FreeForAllPlugin.getPlugin().getMap();
        if (scoreboard.getTeam("0x1") == null) return;
        scoreboard.getTeam("0x1").setSuffix(map.getName());
        scoreboard.getTeam("0x2").setSuffix(player.getLevel() + "");
        scoreboard.getTeam("0x3").setSuffix(playerStats.getKills() + currentStats.getKills() + "");
        scoreboard.getTeam("0x4").setSuffix(playerStats.getDeaths() + currentStats.getDeaths() + "");
    }
}
