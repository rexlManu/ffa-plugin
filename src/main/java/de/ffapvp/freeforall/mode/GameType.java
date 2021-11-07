/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.mode;

import com.google.common.base.Enums;
import de.ffapvp.freeforall.mode.kit.Kit;
import de.ffapvp.freeforall.mode.kit.KitItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum GameType {

    NORMAL("NFFA", Kit.Builder.create()
            .adds(
                    KitItem.Builder.create().name("sword").position(0).itemStack(new ItemStack(Material.IRON_SWORD)).build(),
                    KitItem.Builder.create().name("rod").position(1).itemStack(new ItemStack(Material.FISHING_ROD)).build(),
                    KitItem.Builder.create().name("apple").position(2).itemStack(new ItemStack(Material.GOLDEN_APPLE)).build(),
                    KitItem.Builder.create().name("boots").position(100).itemStack(new ItemStack(Material.IRON_BOOTS)).build(),
                    KitItem.Builder.create().name("leggings").position(101).itemStack(new ItemStack(Material.IRON_LEGGINGS)).build(),
                    KitItem.Builder.create().name("chestplate").position(102).itemStack(new ItemStack(Material.IRON_CHESTPLATE)).build(),
                    KitItem.Builder.create().name("helmet").position(103).itemStack(new ItemStack(Material.IRON_HELMET)).build()
            ).build()),
    OP("OPFFA", Kit.Builder.create().build()),
    SOUP("SFFA", Kit.Builder.create().build()),
    NOHITDELAY("NHDFFA", Kit.Builder.create().build()),
    BUILD("BFFA", Kit.Builder.create().build()),
    KNOCKBACK("KBFFA", Kit.Builder.create().build()),
    UHCFFA("UHCFFA", Kit.Builder.create().build());

    private String groupName;
    private Kit kit;

    public static GameType getByGroupName(String groupName) {
        return Arrays.stream(GameType.values()).filter(gameType -> gameType.getGroupName().equals(groupName)).findFirst().orElse(null);
    }
}
