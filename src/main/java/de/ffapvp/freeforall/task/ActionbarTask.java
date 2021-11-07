/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.task;

import de.dytanic.cloudnet.lib.player.PlayerConnection;
import de.ffapvp.freeforall.FreeForAllPlugin;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ActionbarTask extends Task {

    public ActionbarTask() {
        this.runTaskTimerAsynchronously(FreeForAllPlugin.getPlugin(), 0, 1);
    }

    @Override
    public void run() {
        int seconds = FreeForAllPlugin.getPlugin().getMapchangeTask().getSeconds();
        Bukkit.getOnlinePlayers().forEach(o -> {
            this.sendActionBar(o, String.format("§aMapchange §8» §7%02d:%02d §8▎ §aKillstreak §8» §7%s", seconds / 60, seconds % 60, o.getLevel()));
        });
    }

    private void sendActionBar(Player player, String msg) {
        IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + msg + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chat, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }
}
