/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode.inventory;

import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.ffapvp.freeforall.FreeForAllPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

public class ForceMapInventory implements Listener {

    public void openInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 3 * 9, "§8» §aMapauswahl");
        FreeForAllPlugin.getPlugin().getMapHandler().getMaps().forEach(map -> {
            ItemStackBuilder builder = new ItemStackBuilder(Material.PAPER).displayName("§8» §a" + map.getName());
            if (FreeForAllPlugin.getPlugin().getMap().getName().equals(map.getName())) {
                builder.enchantment(Enchantment.DAMAGE_ALL, 1);
            }
            inventory.addItem(builder.build());
        });
        player.openInventory(inventory);
    }

}
