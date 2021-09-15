package com.github.kamomesg.glasscrash;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class GlassCrashPlugin extends JavaPlugin implements Listener {
    public GlassCrashConfig config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        config = new GlassCrashConfig(this);

        getCommand("config").setExecutor(new GlassCrashCommand(this));
        getCommand("config").setTabCompleter(new GlassCrashCommand(this));
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
        Location location = block.getLocation();
        World world = block.getWorld();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Location neighbor = location.clone().add(x, y, z);
                    if (location.distance(neighbor) <= 1 && neighbor.getBlock().getType().name().contains("GLASS_PANE")) {
                        world.spawnParticle(Particle.BLOCK_DUST, fix(neighbor), 100, neighbor.getBlock().getBlockData());
                        world.playSound(fix(neighbor), Sound.BLOCK_GLASS_BREAK, 1, 1);
                        neighbor.getBlock().breakNaturally();
                        //damageかdamage-radiusのいずれかが0ならダメージは発生しない
                        if (config.getDamage() > 0 && config.getDamageRadius() > 0) {
                            double damageRadius = config.getDamageRadius();
                            world.getNearbyEntities(fix(neighbor), damageRadius, damageRadius, damageRadius).stream()
                                    .filter(entity -> entity instanceof LivingEntity)
                                    .forEach(entity ->  {
                                        ((LivingEntity) entity).damage(config.getDamage());
                                        ((LivingEntity) entity).setNoDamageTicks(0);
                                    });
                        }

                        breakGlassPanes(neighbor.getBlock());
                    }
                }
            }
        }
    }

    //block.getLocation()で得られた整数のLocationをブロックの中心に修正するためのメソッド
    Location fix(Location location) {
        return location.clone().add(0.5, 0.5, 0.5);
    }
}
