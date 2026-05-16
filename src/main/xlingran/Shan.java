package main.xlingran;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public final class Shan extends JavaPlugin implements Listener {

    private final Map<String, PotionEffectType> effectTypeMap = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("§a欢迎使用寄寄の家，大厅药水效果插件!");
        
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(this, this);
        
        // 保存默认配置
        saveDefaultConfig();
        
        // 初始化药水效果类型映射
        initEffectTypeMap();
        
        // 重新加载配置
        reloadConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("§c大厅效果插件已成功卸载");
    }

    private void initEffectTypeMap() {
        effectTypeMap.put("Speed", PotionEffectType.SPEED);
        effectTypeMap.put("Strength", PotionEffectType.STRENGTH);
        effectTypeMap.put("Regeneration", PotionEffectType.REGENERATION);
        effectTypeMap.put("Resistance", PotionEffectType.RESISTANCE);
        effectTypeMap.put("Fire_Resistance", PotionEffectType.FIRE_RESISTANCE);
        effectTypeMap.put("Jump_Boost", PotionEffectType.JUMP_BOOST);
        effectTypeMap.put("Water_Breathing", PotionEffectType.WATER_BREATHING);
        effectTypeMap.put("Invisibility", PotionEffectType.INVISIBILITY);
        effectTypeMap.put("Night_Vision", PotionEffectType.NIGHT_VISION);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        applyBuffs(player);
    }

    private void applyBuffs(Player player) {
        FileConfiguration config = getConfig();
        
        // 获取持续时间（分钟）
        int timeMinutes = config.getInt("Time", 0);
        int durationTicks;
        
        // 0 表示无限，否则转换为 tick（1分钟 = 1200 tick）
        if (timeMinutes == 0) {
            durationTicks = Integer.MAX_VALUE;
        } else {
            durationTicks = timeMinutes * 1200;
        }
        
        // 获取 Buff 配置
        if (config.isConfigurationSection("Buff")) {
            for (String effectName : config.getConfigurationSection("Buff").getKeys(false)) {
                PotionEffectType effectType = effectTypeMap.get(effectName);
                
                if (effectType != null) {
                    int amplifier = config.getInt("Buff." + effectName, 0);
                    
                    // 添加药水效果
                    // amplifier 是等级-1（0=I, 1=II, 2=III...）
                    player.addPotionEffect(new PotionEffect(
                        effectType, 
                        durationTicks, 
                        amplifier - 1, 
                        false, // 不显示粒子效果
                        false  // 不是图标效果
                    ));
                }
            }
        }
    }
}
