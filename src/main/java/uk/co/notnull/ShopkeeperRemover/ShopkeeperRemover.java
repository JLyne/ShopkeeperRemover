package uk.co.notnull.ShopkeeperRemover;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.ShopkeeperRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.wiefferink.areashop.events.notify.UnrentedRegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ShopkeeperRemover extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void unrentedRegionEvent(UnrentedRegionEvent event) {
        ProtectedRegion region = event.getRegion().getRegion();

        getLogger().info("Region " + event.getRegion().getName() + " unrented");

        if(region == null) {
            getLogger().info("Region is null for some reason");
            return;
        }

         // Create the task anonymously and schedule to run it once, after 20 ticks
        new BukkitRunnable() {
            @Override
            public void run() {
                ShopkeeperRegistry registry = ShopkeepersAPI.getShopkeeperRegistry();

                World world = event.getRegion().getWorld();
                List<? extends Shopkeeper> shopkeepers = registry.getShopkeepersInWorld(world, false);

                getLogger().info("Checking shopkeepers");

                shopkeepers.forEach(shopkeeper -> {
                    getLogger().info("Checking shopkeeper " + shopkeeper.getName());

                    if(region.contains(shopkeeper.getX(), shopkeeper.getY(), shopkeeper.getZ())) {
                        getLogger().info("Sshopkeeper " + shopkeeper.getName() + " is within region, deleting");
                        shopkeeper.delete();
                    }
                });
            }
        }.runTaskLater(this, 10);
    }
}
