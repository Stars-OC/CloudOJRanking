package xyz.starsoc;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;

public final class CloudOJRanking extends JavaPlugin{
public static final CloudOJRanking INSTANCE=new CloudOJRanking();

    private CloudOJRanking(){
        super(new JvmPluginDescriptionBuilder("xyz.starsoc.cloudojranking","0.1.0")
                .name("CloudOJRanking")
                .author("Clusters_stars")
                .build());
    }

    @Override
    public void onEnable(){
        getLogger().info("Plugin loaded!");
    }
}