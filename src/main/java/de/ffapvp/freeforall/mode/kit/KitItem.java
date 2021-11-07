/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode.kit;

import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.ffapvp.freeforall.database.inventory.SortItem;
import de.ffapvp.freeforall.utility.NBTUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class KitItem {

    private SortItem sortItem;
    private ItemStack itemStack;

    public static class Builder {

        public static Builder create() {
            return new Builder();
        }

        private SortItem sortItem;
        private ItemStack itemStack;

        Builder() {
            this.sortItem = new SortItem();
            this.itemStack = new ItemStack(Material.STONE);
        }

        public Builder name(String itemName) {
            this.sortItem.setKitItemName(itemName);
            return this;
        }

        public Builder position(int position) {
            this.sortItem.setPosition(position);
            return this;
        }

        public Builder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public KitItem build() {
            return new KitItem(this.sortItem, NBTUtils.setKitName(this.itemStack, this.sortItem.getKitItemName()));
        }
    }
}
