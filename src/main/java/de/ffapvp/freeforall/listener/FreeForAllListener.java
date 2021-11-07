/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.listener;

import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.bridge.CloudServer;
import de.dytanic.cloudnet.bridge.event.bukkit.BukkitPlayerUpdateEvent;
import de.dytanic.cloudnet.bridge.internal.util.ItemStackBuilder;
import de.dytanic.cloudnet.lib.player.permission.PermissionGroup;
import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.stats.GlobalPlayerStats;
import de.ffapvp.freeforall.utility.Region;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

public class FreeForAllListener implements Listener {

    private static Set<Player> spawnZonePlayers, editInventoryPlayers, protectFallDamage;

    public FreeForAllListener() {
        this.spawnZonePlayers = new HashSet<>();
        this.editInventoryPlayers = new HashSet<>();
        this.protectFallDamage = new HashSet<>();
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();

        player.setHealthScale(20);
        player.setHealth(player.getMaxHealth());
        player.setLevel(0);
        player.setExp(0);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);

        player.teleport(FreeForAllPlugin.getPlugin().getMap().getLocation("spawn"));
        player.setVelocity(new Vector(0, 0.1, 0));
        GlobalPlayerStats globalPlayerStats = FreeForAllPlugin.getPlugin().getStatsHandler().loadGlobalStats(player.getUniqueId(), true);
        FreeForAllPlugin.getPlugin().getPlayerScoreboard().sendScoreboard(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(FreeForAllPlugin.getPlugin(), () -> CloudServer.getInstance().updateNameTags(player), 3L);
        this.spawnZonePlayers.add(player);
        spawnZoneEnter(player);
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
        FreeForAllPlugin.getPlugin().getSortInventoryHandler().loadInventory(player);
        player.sendMessage(FreeForAllPlugin.PREFIX + String.format("§aMap§8: §7%s", FreeForAllPlugin.getPlugin().getMap().getName()));
        player.sendMessage(FreeForAllPlugin.PREFIX + "§aBuilder§8: §7FFAPVP.de Bauteam");
        player.sendMessage(FreeForAllPlugin.PREFIX + "§aInfo§8: §7Mit der §aKiste§7 kannst du deine §aInventarsortierung §7anpassen!");
    }

    @EventHandler
    public void handle(PluginEnableEvent event) {
        Bukkit.getOnlinePlayers().forEach(o -> {
            FreeForAllPlugin.getPlugin().getSortInventoryHandler().loadInventory(o);
        });
        FreeForAllPlugin.getPlugin().getMapchangeTask().trigger();
    }

    @EventHandler
    public void handle(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        Player player = event.getEntity();

        Player killer = player.getKiller();
        player.spigot().respawn();
        player.teleport(FreeForAllPlugin.getPlugin().getMap().getLocation("spawn"));

        PlayerInventory inventory = player.getInventory();
        inventory.clear();
        inventory.setArmorContents(null);
        player.setLevel(0);
        player.setExp(0);

        player.setVelocity(new Vector(0, 0.3, 0));

        if (killer != null) {
            player.sendMessage(FreeForAllPlugin.PREFIX + String.format("Du wurdest von §a%s §7mit §4%.1f❤ §7getötet!", killer.getName(), killer.getHealth()));
            killer.setHealth(killer.getMaxHealth());
            killer.setLevel(killer.getLevel() + 1);
            FreeForAllPlugin.getPlugin().getStatsHandler().getStatsByPlayer(killer).addKill();
            FreeForAllPlugin.getPlugin().getPlayerScoreboard().updateScoreboard(killer);
            killer.sendMessage(FreeForAllPlugin.PREFIX + String.format("Du hast §a%s §7getötet!", player.getName()));

            int killstreak = killer.getLevel();
            if ((killstreak % 5 == 0 || killstreak == 3)) {
                Bukkit.getOnlinePlayers().forEach(o -> {
                    o.sendMessage(FreeForAllPlugin.PREFIX + String.format("§a%s §7hat eine §a%ser §7Killstreak!", killer.getName(), killstreak));
                });
            }

        } else {
            player.sendMessage(FreeForAllPlugin.PREFIX + "§7Du bist gestorben!");
        }
        FreeForAllPlugin.getPlugin().getStatsHandler().getStatsByPlayer(player).addDeath();
        FreeForAllPlugin.getPlugin().getPlayerScoreboard().updateScoreboard(player);
        this.spawnZonePlayers.add(player);
        spawnZoneEnter(player);
    }

    @EventHandler
    public void handleUpdate(final BukkitPlayerUpdateEvent e) {
        if (Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()) != null && e.getCloudPlayer().getServer() != null && e.getCloudPlayer().getServer().equalsIgnoreCase(CloudAPI.getInstance().getServerId())) {
            CloudServer.getInstance().updateNameTags(Bukkit.getPlayer(e.getCloudPlayer().getUniqueId()));
        }
    }

    public static void spawnZoneEnter(Player player) {
        editInventoryPlayers.remove(player);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItem(4, new ItemStackBuilder(Material.CHEST).displayName("§8» §aInventarsortierung").build());
        player.getInventory().setItem(8, new ItemStackBuilder(Material.IRON_DOOR).displayName("§8» §aVerlassen").build());
        spawnZonePlayers.add(player);
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!this.editInventoryPlayers.contains(player)) {
            event.setCancelled(true);
        } else {
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            event.setCancelled(true);
        }
        if (editInventoryPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR) && !event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (event.getItem() == null) return;
        Player player = event.getPlayer();
        if (!this.spawnZonePlayers.contains(player)) return;
        if (event.getItem().getItemMeta() == null) return;
        if (event.getItem().getItemMeta().getDisplayName() == null) return;
        String displayName = event.getItem().getItemMeta().getDisplayName();
        switch (displayName) {
            case "§8» §aInventarsortierung":
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                player.sendMessage(FreeForAllPlugin.PREFIX + "§7Du kannst nun dein Inventar umsortieren.");
                player.sendMessage(FreeForAllPlugin.PREFIX + "§7Zum §aspeichern §7einfach sneaken.");
                FreeForAllPlugin.getPlugin().getSortInventoryHandler().setInventory(player);
                this.editInventoryPlayers.add(player);
                break;
            case "§8» §aVerlassen":
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1f, 1f);
                player.kickPlayer("");
                break;
        }
    }

    @EventHandler
    public void handle(PlayerToggleSneakEvent event) {
        if (event.isSneaking()) {
            Player player = event.getPlayer();
            if (this.editInventoryPlayers.remove(player)) {
                FreeForAllPlugin.getPlugin().getSortInventoryHandler().updateInventory(player);
                player.sendMessage(FreeForAllPlugin.PREFIX + "Deine Inventarsortierung wurde §aerfolgreich§7 gespeichert.");
                player.playSound(player.getLocation(), Sound.VILLAGER_YES, 1f, 1f);
                spawnZoneEnter(player);
            }
        }
    }

    private void spawnZoneLeave(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        FreeForAllPlugin.getPlugin().getSortInventoryHandler().setInventory(player);
        player.sendMessage(FreeForAllPlugin.PREFIX + "Du bist nun angreifbar!");
        player.playSound(player.getLocation(), Sound.NOTE_BASS, 5f, 5f);
        this.editInventoryPlayers.remove(player);

        this.protectFallDamage.add(player);
        Bukkit.getScheduler().runTaskLaterAsynchronously(FreeForAllPlugin.getPlugin(), () -> protectFallDamage.remove(player), 3 * 20);
    }

    @EventHandler
    public void handle(PlayerMoveEvent event) {
        Region spawnRegion = FreeForAllPlugin.getPlugin().getMap().getSpawnRegion();
        Player player = event.getPlayer();
        if (spawnRegion != null && (spawnRegion.locationIsInRegion(event.getTo()))) {
        } else {
            if (this.spawnZonePlayers.remove(player)) {
                this.spawnZoneLeave(player);
            }
        }
    }

    @EventHandler
    public void handle(final AsyncPlayerChatEvent e) {
        final PermissionGroup permissionGroup = CloudServer.getInstance().getCachedPlayer(e.getPlayer().getUniqueId()).getPermissionEntity().getHighestPermissionGroup(CloudAPI.getInstance().getPermissionPool());
        if (permissionGroup == null) {
            return;
        }
        e.setFormat(ChatColor.translateAlternateColorCodes('&', "%display%%player% §8» §7%message%".replace("%display%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getDisplay())).replace("%prefix%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getPrefix())).replace("%suffix%", ChatColor.translateAlternateColorCodes('&', permissionGroup.getSuffix())).replace("%group%", permissionGroup.getName()).replace("%player%", e.getPlayer().getName()).replace("%message%", e.getPlayer().hasPermission("cloudnet.chat.color") ? ChatColor.translateAlternateColorCodes('&', e.getMessage().replace("%", "%%")) : ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', e.getMessage().replace("%", "%%"))))));
    }


    @EventHandler
    public void handle(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        FreeForAllPlugin.getPlugin().getSortInventoryHandler().saveInventory(event.getPlayer());
        GlobalPlayerStats globalStatsByPlayer = FreeForAllPlugin.getPlugin().getStatsHandler().getGlobalStatsByPlayer(event.getPlayer());
        FreeForAllPlugin.getPlugin().getStatsHandler().savePlayerStats(event.getPlayer());
        FreeForAllPlugin.getPlugin().getStatsHandler().getGlobalPlayerStats().remove(globalStatsByPlayer);
    }

    @EventHandler
    public void handle(EntityDamageEvent event) {
        Region spawnRegion = FreeForAllPlugin.getPlugin().getMap().getSpawnRegion();
        if (spawnRegion == null) return;
        if (spawnRegion.locationIsInRegion(event.getEntity().getLocation())) {
            event.setCancelled(true);
        } else {
            if (!(event.getEntity() instanceof Player)) return;
            if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && protectFallDamage.contains(event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void handle(CreatureSpawnEvent event) {
        event.setCancelled(!event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM));
    }

    @EventHandler
    public void handle(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) return;
        Region spawnRegion = FreeForAllPlugin.getPlugin().getMap().getSpawnRegion();
        if (spawnRegion != null && (spawnRegion.locationIsInRegion(event.getEntity().getLocation()) || spawnRegion.locationIsInRegion(event.getDamager().getLocation()))) {
            event.setCancelled(true);
            event.getDamager().sendMessage(FreeForAllPlugin.PREFIX + "Du kannst §akeine§7 Spieler am Spawn angreifen!");
        }

    }

    @EventHandler
    public void handle(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handle(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

}
