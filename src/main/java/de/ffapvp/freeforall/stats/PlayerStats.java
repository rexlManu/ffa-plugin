/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerStats {

    public static PlayerStats of(UUID uuid) {
        return new PlayerStats(uuid, System.currentTimeMillis(), 0, 0, 0);
    }

    private UUID uuid;
    private long joinTime;
    private long leaveTime;
    private int kills, deaths;

    public PlayerStats addKill() {
        this.kills++;
        return this;
    }

    public PlayerStats addKills(int kills) {
        this.kills += kills;
        return this;
    }

    public PlayerStats addDeath() {
        this.deaths++;
        return this;
    }

    public PlayerStats addDeaths(int deaths) {
        this.deaths += deaths;
        return this;
    }

    public boolean isEmpty() {
        return this.kills == 0 && this.deaths == 0;
    }
}
