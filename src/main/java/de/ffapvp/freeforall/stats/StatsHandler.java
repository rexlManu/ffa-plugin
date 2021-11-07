/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.stats;

import com.mongodb.client.MongoCollection;
import de.ffapvp.freeforall.FreeForAllPlugin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.var;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StatsHandler {

    private Set<PlayerStats> playerStatsSet;
    @Getter
    private Set<GlobalPlayerStats> globalPlayerStats;

    public StatsHandler() {
        this.playerStatsSet = new HashSet<>();
        this.globalPlayerStats = new HashSet<>();
    }

    public PlayerStats getStatsByPlayer(Player player) {
        PlayerStats playerStats = this.playerStatsSet.stream().filter(playerStats1 -> playerStats1.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
        if (playerStats == null) {
            playerStats = PlayerStats.of(player.getUniqueId());
            this.playerStatsSet.add(playerStats);
        }
        return playerStats;
    }

    public PlayerStats getStatsByPlayer(UUID uuid) {
        PlayerStats playerStats = this.playerStatsSet.stream().filter(playerStats1 -> playerStats1.getUuid().equals(uuid)).findFirst().orElse(null);
        if (playerStats == null) {
            playerStats = PlayerStats.of(uuid);
            this.playerStatsSet.add(playerStats);
        }
        return playerStats;
    }

    public GlobalPlayerStats getGlobalStatsByPlayer(Player player) {
        return this.globalPlayerStats.stream().filter(playerStats1 -> playerStats1.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    public GlobalPlayerStats getGlobalStatsByPlayer(UUID uuid) {
        return this.globalPlayerStats.stream().filter(playerStats1 -> playerStats1.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public GlobalPlayerStats loadGlobalStats(UUID uuid, boolean cache) {
        List<Document> documents = FreeForAllPlugin.getPlugin().getDatabaseConnector().findMany("uuid", uuid.toString());
        GlobalPlayerStats globalPlayerStats = new GlobalPlayerStats(uuid);
        documents.forEach(document -> globalPlayerStats.getPlayerStats().add(new PlayerStats(
                uuid,
                document.getLong("joinTime"),
                document.getLong("leaveTime"),
                document.getInteger("kills"),
                document.getInteger("deaths")
        )));
        if (cache)
            this.globalPlayerStats.add(globalPlayerStats);
        return globalPlayerStats;
    }

    public void savePlayerStats(Player player) {
        PlayerStats playerStats = this.getStatsByPlayer(player);
        if (playerStats.isEmpty()) return;
        playerStats.setLeaveTime(System.currentTimeMillis());
        FreeForAllPlugin.getPlugin().getDatabaseConnector().insert(
                new Document()
                        .append("kills", playerStats.getKills())
                        .append("deaths", playerStats.getDeaths())
                        .append("joinTime", playerStats.getJoinTime())
                        .append("leaveTime", playerStats.getLeaveTime())
                        .append("uuid", playerStats.getUuid().toString())
        );
        this.playerStatsSet.remove(playerStats);
    }

    public List<Document> calculateRanking() {
        MongoCollection<Document> statsCollection = FreeForAllPlugin.getPlugin().getDatabaseConnector().getStatsCollection();
        ArrayList<Document> documents = statsCollection.find().into(new ArrayList<>());
        Map<UUID, Document> globalStats = new HashMap<>();
        documents.forEach(document -> {
            UUID uuid = UUID.fromString(document.getString("uuid"));
            if (!globalStats.containsKey(uuid))
                globalStats.put(uuid, new Document().append("uuid", uuid.toString()).append("kills", 0).append("deaths", 0).append("playTime", 0));
            Document statsDocument = globalStats.get(uuid);
            statsDocument.replace("kills", document.getInteger("kills") + statsDocument.getInteger("kills"));
            statsDocument.replace("deaths", document.getInteger("deaths") + statsDocument.getInteger("deaths"));
            statsDocument.replace("playTime", (document.getLong("leaveTime") - document.getLong("joinTime")) + statsDocument.getLong("playTime"));
        });
        TreeMap<UUID, Document> map = new TreeMap<>(new StatsComparator(globalStats));
        map.putAll(globalStats);
        return new ArrayList<>(map.values());
    }


    @AllArgsConstructor
    public static class StatsComparator implements Comparator<UUID> {

        private Map<UUID, Document> globalStats;

        @Override
        public int compare(UUID o1, UUID o2) {
            Document object1 = globalStats.get(o1);
            Document object2 = globalStats.get(o2);
            return diff(object1.getInteger("kills"), object1.getInteger("deaths"))
                    >= diff(object2.getInteger("kills"), object2.getInteger("deaths")) ?
                    -1 : 1;
        }

        private int diff(int kills, int deaths) {
            return kills - deaths;
        }
    }
}
