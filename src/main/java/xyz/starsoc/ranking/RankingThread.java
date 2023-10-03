package xyz.starsoc.ranking;

import xyz.starsoc.file.Config;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingThread {
    private static final Config config = Config.INSTANCE;
    public static void run() {
        Runnable runnable = () -> {

        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 20, config.getTime(), TimeUnit.SECONDS);
    }
}
