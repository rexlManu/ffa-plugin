/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.database.inventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.mode.GameType;
import de.ffapvp.freeforall.mode.kit.Kit;
import de.ffapvp.freeforall.utility.NBTUtils;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
@Getter
public class SortInventoryHandler {

    private static final Gson GSON = new GsonBuilder().serializeNulls().create();

    private List<SortInventory> sortInventories;

    public SortInventoryHandler() {
        this.sortInventories = new ArrayList<>();
    }

    public void loadInventory(Player player) {
        Document document = FreeForAllPlugin.getPlugin().getDatabaseConnector().getInventoriesCollection().find(Filters.and(
                Filters.eq("uuid", player.getUniqueId().toString()),
                Filters.eq("gameType", FreeForAllPlugin.getPlugin().getGameType().name()))).first();

        if (document == null) {
            this.sortInventories.add(new SortInventory(FreeForAllPlugin.getPlugin().getGameType(), player.getUniqueId()));
            return;
        }

        SortInventory sortInventory = GSON.fromJson(document.toJson(), SortInventory.class);
        this.sortInventories.add(sortInventory);
    }

    public void setInventory(Player player) {
        SortInventory sortInventory = sortInventories.stream().filter(s -> s.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
        if (sortInventory == null) {
            player.sendMessage("SortInventory == null");
            return;
        }

        Kit kit = sortInventory.getGameType().getKit();
        kit.getKitItems().forEach(kitItem -> {
            SortItem sortItem = sortInventory.getSortItems().stream().filter(s -> s.getKitItemName().equals(kitItem.getSortItem().getKitItemName())).findFirst().orElse(null);
            if (sortItem == null) {
                // OLD DATA || RESET DATA ASAP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                player.getInventory().addItem(kitItem.getItemStack());
                player.sendMessage(kitItem.getSortItem().getKitItemName() + " NOT FOUND ASAP");
            } else {
                switch (sortItem.getPosition()) {
                    case 100:
                        player.getInventory().setBoots(kitItem.getItemStack());
                        break;
                    case 101:
                        player.getInventory().setLeggings(kitItem.getItemStack());
                        break;
                    case 102:
                        player.getInventory().setChestplate(kitItem.getItemStack());
                        break;
                    case 103:
                        player.getInventory().setHelmet(kitItem.getItemStack());
                        break;
                    default:
                        player.getInventory().setItem(sortItem.getPosition(), kitItem.getItemStack());
                        break;
                }
            }
        });
    }

    public void updateInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        SortInventory sortInventory = sortInventories.stream().filter(s -> s.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
        if (sortInventory == null) {
            player.sendMessage("SortInventory == null");
            return;
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack content = inventory.getItem(i);
            if (content == null) continue;
            String kitName = NBTUtils.readKitName(content);
            if (kitName == null) continue;
            SortItem sortItem = sortInventory.getSortItems().stream().filter(s -> s.getKitItemName().equals(kitName)).findFirst().orElse(null);
            if (sortItem == null) continue;
            sortItem.setPosition(i);
        }
    }

    public void saveInventory(Player player) {
        SortInventory sortInventory = sortInventories.stream().filter(s -> s.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
        if (sortInventory == null) {
            player.sendMessage("SortInventory == null");
            return;
        }

        MongoCollection<Document> collection = FreeForAllPlugin.getPlugin().getDatabaseConnector().getInventoriesCollection();
        Bson and = Filters.and(
                Filters.eq("uuid", player.getUniqueId().toString()),
                Filters.eq("gameType", FreeForAllPlugin.getPlugin().getGameType().name()));
        Document document = collection.find(and).first();

        if (document == null) {
            collection.insertOne(Document.parse(GSON.toJson(sortInventory)));
            this.sortInventories.remove(sortInventory);
            return;
        }
        collection.updateOne(and, new Document("$set", Document.parse(GSON.toJson(sortInventory))));
        this.sortInventories.remove(sortInventory);
    }

    public void resetInventory(Player player) {

    }


}
