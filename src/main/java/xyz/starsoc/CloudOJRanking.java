package xyz.starsoc;

import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import xyz.starsoc.event.Command;
import xyz.starsoc.event.GroupMsg;
import xyz.starsoc.file.Config;
import xyz.starsoc.file.Contests;
import xyz.starsoc.file.Data;
import xyz.starsoc.file.Message;
import xyz.starsoc.ranking.RankingThread;

public final class CloudOJRanking extends JavaPlugin{
public static final CloudOJRanking INSTANCE=new CloudOJRanking();

    private CloudOJRanking(){
        super(new JvmPluginDescriptionBuilder("xyz.starsoc.cloudojranking","0.3.0")
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
        RankingThread.run();

    }

    public void reload(){
        reloadPluginConfig(Config.INSTANCE);
        reloadPluginConfig(Message.INSTANCE);
        reloadPluginData(Data.INSTANCE);
        reloadPluginData(Contests.INSTANCE);
    }
}