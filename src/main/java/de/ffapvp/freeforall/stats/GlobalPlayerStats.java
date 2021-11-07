/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Stream;

@Data
@AllArgsConstructor
public class GlobalPlayerStats {

    private UUID uuid;
    private List<PlayerStats> playerStats;

    public GlobalPlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.playerStats = new ArrayList<>();
    }

    public PlayerStats getCombinedStats() {
        PlayerStats playerStats = PlayerStats.of(uuid);
        playerStats.setKills(this.playerStats.stream().mapToInt(PlayerStats::getKills).sum());
        playerStats.setDeaths(this.playerStats.stream().mapToInt(PlayerStats::getDeaths).sum());
        return playerStats;
    }

    public long getPlayTimeInMillis() {
        return this.playerStats.stream().mapToLong(stats -> stats.getLeaveTime() - stats.getJoinTime()).sum();
    }
}
