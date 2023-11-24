package xyz.starsoc;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import xyz.starsoc.event.Command;
import xyz.starsoc.event.GroupMsg;
import xyz.starsoc.file.*;
import xyz.starsoc.ranking.RankingThread;

public final class CloudOJRanking extends JavaPlugin{
public static final CloudOJRanking INSTANCE=new CloudOJRanking();

    private CloudOJRanking(){
        super(new JvmPluginDescriptionBuilder("xyz.starsoc.cloudojranking","0.6.0")
                .name("CloudOJRanking")
                .author("Clusters_stars")
                .build());
    }

    @Override
    public void onEnable(){
        getLogger().info("CloudOJRanking插件加载成功");
        CommandManager.INSTANCE.registerCommand(Command.INSTANCE,true);
        GlobalEventChannel.INSTANCE.registerListenerHost(new GroupMsg());
        reload();
        //执行线程
        new RankingThread().run();
    }

    public void reload(){
        reloadPluginConfig(Config.INSTANCE);
        reloadPluginConfig(Message.INSTANCE);
        reloadPluginConfig(Redis.INSTANCE);
        reloadPluginData(Data.INSTANCE);
        reloadPluginData(ContestsDataFile.INSTANCE);
    }
}