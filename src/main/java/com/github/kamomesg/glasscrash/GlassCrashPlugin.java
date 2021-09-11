package com.github.kamomesg.glasscrash;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class GlassCrashPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof AbstractArrow && e.getHitBlock() != null) {
            AbstractArrow arrow = (AbstractArrow) e.getEntity();
            Block block = e.getHitBlock();

            if (!block.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING) && arrow.getShooter() instanceof Entity && !(arrow.getShooter() instanceof Player))
                return;

            if (block.getType().name().contains("GLASS_PANE")) {
                arrow.remove();
                breakGlassPanes(block);
            }
        }
    }

    void breakGlassPanes(Block block) {
        //block.getLocation()で得られた整数のLocationをブロックの中心に修正するためのVector
        final Vector fix = new Vector(0.5, 0.5, 0.5);

        Location location = block.getLocation();
        World world = block.getWorld();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location neighbor = location.clone().add(x, y, z);
                    if (location.distance(neighbor) <= 1) {
                        if (neighbor.getBlock().getType().name().contains("GLASS_PANE")) {
                            world.spawnParticle(Particle.BLOCK_DUST, neighbor.clone().add(fix), 100, neighbor.getBlock().getBlockData());
                            world.playSound(neighbor.clone().add(fix), Sound.BLOCK_GLASS_BREAK, 1, 1);
                            neighbor.getBlock().breakNaturally();
                            world.getNearbyEntities(neighbor.clone().add(fix), 1, 1, 1).forEach(entity -> {
                                if (entity instanceof LivingEntity) {
                                    LivingEntity living = (LivingEntity) entity;
                                    living.damage(3);
                                    living.setNoDamageTicks(0);
                                }
                            });

                            breakGlassPanes(neighbor.getBlock());
                        }
                    }
                }
            }
        }
    }
}
