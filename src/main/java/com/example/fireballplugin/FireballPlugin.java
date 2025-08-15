package com.example.fireballplugin;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class FireballPlugin extends JavaPlugin implements Listener {
    private FileConfiguration langConfig;
    private String language;
    private float fireballPower;
    private float fireballSpeed;
    private boolean damageBlocks;
    private int cooldownTime;
    private boolean enableRightClick;
    private boolean enableSpecialEffects;
    private boolean defaultFireballEnabled;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, Boolean> playerFireballEnabled = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();
        
        // 保存语言文件
        saveResource("lang/zh_TW.yml", false);
        saveResource("lang/zh_CN.yml", false);
        saveResource("lang/en_US.yml", false);
        
        // 加载配置
        loadConfig();
        
        // 注册重载命令
        getCommand("fireball").setExecutor(this);
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("火球插件已启动！");
    }
    @Override
    public void onDisable() {
        getLogger().info("火球插件已停止！");
    }
    
    private void loadConfig() {
        // 重新加载配置
        reloadConfig();
        
        // 获取配置值
        language = getConfig().getString("language", "zh_CN");
        fireballPower = (float) getConfig().getDouble("fireball.power", 1.0);
        fireballSpeed = (float) getConfig().getDouble("fireball.speed", 2.0);
        damageBlocks = getConfig().getBoolean("fireball.damage-blocks", true);
        cooldownTime = getConfig().getInt("cooldown", 5);
        enableRightClick = getConfig().getBoolean("features.right-click", true);
        enableSpecialEffects = getConfig().getBoolean("features.special-effects", true);
        defaultFireballEnabled = getConfig().getBoolean("features.default-enabled", true);
        
        // 加载语言文件
        loadLanguage();
    }
    
    private void loadLanguage() {
        File langFile = new File(getDataFolder(), "lang/" + language + ".yml");
        if (langFile.exists()) {
            langConfig = YamlConfiguration.loadConfiguration(langFile);
        } else {
            // 如果找不到指定的语言文件，回退到默认语言
            langConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(getResource("lang/zh_TW.yml"), StandardCharsets.UTF_8)
            );
        }
    }
    
    private String getMessage(String path) {
        String message = langConfig.getString("messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * 获取玩家的火焰弹开关状态
     * @param player 玩家
     * @return 如果启用返回true，否则返回false
     */
    private boolean isFireballEnabled(Player player) {
        return playerFireballEnabled.getOrDefault(player.getUniqueId(), defaultFireballEnabled);
    }
    
    /**
     * 设置玩家的火焰弹开关状态
     * @param player 玩家
     * @param enabled 是否启用
     */
    private void setFireballEnabled(Player player, boolean enabled) {
        playerFireballEnabled.put(player.getUniqueId(), enabled);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fireball")) {
            // 处理重载命令
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("fireballplugin.reload")) {
                    loadConfig();
                    sender.sendMessage(getMessage("prefix") + getMessage("reload-success"));
                    return true;
                } else {
                    sender.sendMessage(getMessage("prefix") + getMessage("no-permission"));
                    return true;
                }
            }
            
            // 检查是否为玩家
            if (!(sender instanceof Player)) {
                sender.sendMessage(getMessage("prefix") + getMessage("player-only"));
                return true;
            }

            Player player = (Player) sender;
            
            // 处理开关命令
            if (args.length > 0 && (args[0].equalsIgnoreCase("toggle") || args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off"))) {
                if (!player.hasPermission("fireballplugin.toggle")) {
                    player.sendMessage(getMessage("prefix") + getMessage("no-permission"));
                    return true;
                }
                
                boolean newState;
                if (args[0].equalsIgnoreCase("toggle")) {
                    newState = !isFireballEnabled(player);
                } else {
                    newState = args[0].equalsIgnoreCase("on");
                }
                
                setFireballEnabled(player, newState);
                String message = newState ? getMessage("fireball-enabled") : getMessage("fireball-disabled");
                player.sendMessage(getMessage("prefix") + message);
                return true;
            }
            
            // 处理状态查询命令
            if (args.length > 0 && args[0].equalsIgnoreCase("status")) {
                if (!player.hasPermission("fireballplugin.status")) {
                    player.sendMessage(getMessage("prefix") + getMessage("no-permission"));
                    return true;
                }
                
                boolean enabled = isFireballEnabled(player);
                String statusMessage = enabled ? getMessage("status-enabled") : getMessage("status-disabled");
                player.sendMessage(getMessage("prefix") + getMessage("status-message").replace("{status}", statusMessage));
                return true;
            }
            
            // 检查权限
            if (!player.hasPermission("fireballplugin.use")) {
                player.sendMessage(getMessage("prefix") + getMessage("no-permission"));
                return true;
            }
            
            // 检查火焰弹是否启用
            if (!isFireballEnabled(player)) {
                player.sendMessage(getMessage("prefix") + getMessage("fireball-disabled"));
                return true;
            }
            
            // 检查冷却时间
            if (!checkCooldown(player)) {
                        return true;
                    }
            // 发射火球
            launchFireball(player);
            
            return true;
        }
        return false;
    }
    
    /**
     * 检查玩家是否在冷却时间内
     * @param player 玩家
     * @return 如果不在冷却时间内返回true，否则返回false
     */
    private boolean checkCooldown(Player player) {
        if (cooldownTime > 0) {
            UUID playerUUID = player.getUniqueId();
            if (cooldowns.containsKey(playerUUID)) {
                long secondsLeft = ((cooldowns.get(playerUUID) / 1000) + cooldownTime) - (System.currentTimeMillis() / 1000);
                if (secondsLeft > 0) {
                    player.sendMessage(getMessage("prefix") + getMessage("cooldown").replace("{time}", String.valueOf(secondsLeft)));
                    return false;
}
            }
            cooldowns.put(playerUUID, System.currentTimeMillis());
        }
        return true;
    }
    
    /**
     * 发射火球
     * @param player 发射火球的玩家
     */
    private void launchFireball(Player player) {
        // 创建火球
        Fireball fireball = (Fireball) player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.FIREBALL);
        
        // 设置火球的方向为玩家视线方向
        fireball.setDirection(player.getLocation().getDirection().multiply(fireballSpeed));
        
        // 设置火球的发射者为玩家
        fireball.setShooter(player);
        
        // 设置火球的爆炸威力
        fireball.setYield(fireballPower);
        
        // 设置是否造成方块损坏
        fireball.setIsIncendiary(damageBlocks);
        
        // 添加发射特效
        if (enableSpecialEffects) {
            playLaunchEffects(player);
        }
        
        player.sendMessage(getMessage("prefix") + getMessage("fireball-launched"));
    }
    
    /**
     * 播放火球发射特效
     * @param player 发射火球的玩家
     */
    private void playLaunchEffects(Player player) {
        Location loc = player.getEyeLocation();
        player.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1.0f, 0.8f);
        player.getWorld().spawnParticle(Particle.FLAME, loc, 20, 0.2, 0.2, 0.2, 0.05);
        player.getWorld().spawnParticle(Particle.SMOKE, loc, 15, 0.1, 0.1, 0.1, 0.03);
    }
    
    /**
     * 处理玩家右键点击事件
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!enableRightClick) return;
        
        // 确保是右键点击
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // 避免双手触发两次事件
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // 检查玩家是否有权限
        if (!player.hasPermission("fireballplugin.rightclick")) {
            return;
        }
        
        // 检查火焰弹是否启用
        if (!isFireballEnabled(player)) {
            return;
        }
        
        // 检查玩家是否在潜行状态（可选，让玩家按住Shift键才能发射火球）
        if (!player.isSneaking()) {
            return;
        }
        
        // 检查冷却时间
        if (!checkCooldown(player)) {
            return;
        }
        
        // 发射火球
        launchFireball(player);
        
        // 防止与其他插件冲突
        event.setCancelled(true);
    }
    
    /**
     * 处理实体爆炸事件，添加爆炸特效
     */
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!enableSpecialEffects) return;
        
        if (event.getEntity() instanceof Fireball) {
            Location loc = event.getLocation();
            
            // 播放爆炸音效
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.8f);
            
            // 添加爆炸粒子效果
            loc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 2, 2, 2, 0.1);
            loc.getWorld().spawnParticle(Particle.LAVA, loc, 30, 1, 1, 1, 0.1);
            
            // 添加持续的火花效果
            new BukkitRunnable() {
                int ticks = 0;
                
                @Override
                public void run() {
                    if (ticks >= 20) {
                        this.cancel();
                        return;
                    }
                    
                    for (int i = 0; i < 5; i++) {
                        double offsetX = random.nextDouble() * 4 - 2;
                        double offsetY = random.nextDouble() * 2;
                        double offsetZ = random.nextDouble() * 4 - 2;
                        
                        Location sparkLoc = loc.clone().add(offsetX, offsetY, offsetZ);
                        loc.getWorld().spawnParticle(Particle.FLAME, sparkLoc, 3, 0.1, 0.1, 0.1, 0.05);
                        
                        if (random.nextInt(3) == 0) {
                            loc.getWorld().spawnParticle(Particle.SMOKE, sparkLoc, 2, 0.1, 0.1, 0.1, 0.01);
                        }
                    }
                    
                    ticks++;
                }
            }.runTaskTimer(this, 0L, 2L);
        }
    }
}