/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.database.inventory;

import de.ffapvp.freeforall.mode.GameType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class SortInventory {

    private GameType gameType;
    private UUID uuid;
    private List<SortItem> sortItems;

    public SortInventory(GameType gameType, UUID uuid) {
        this.gameType = gameType;
        this.uuid = uuid;
        this.sortItems = new ArrayList<>();
        this.gameType.getKit().getKitItems().forEach(kitItem -> this.sortItems.add(kitItem.getSortItem()));
    }
}
