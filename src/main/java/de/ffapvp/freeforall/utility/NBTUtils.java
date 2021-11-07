/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.utility;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTUtils {

    public static ItemStack setKitName(ItemStack itemStack, String kitName) {
        net.minecraft.server.v1_8_R3.ItemStack stack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound nbttagcompound = stack.getTag() == null ? new NBTTagCompound() : stack.getTag();
        nbttagcompound.setString("kit", kitName);
        stack.setTag(nbttagcompound);
        return CraftItemStack.asBukkitCopy(stack);
    }

    public static String readKitName(ItemStack itemStack) {
        net.minecraft.server.v1_8_R3.ItemStack copy = CraftItemStack.asNMSCopy(itemStack);
        if (!copy.getTag().hasKey("kit")) return null;
        return copy.getTag().getString("kit");
    }
}
