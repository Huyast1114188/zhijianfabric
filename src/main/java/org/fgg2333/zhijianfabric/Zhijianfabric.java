package org.fgg2333.zhijianfabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Zhijianfabric implements ModInitializer {
    private static final Logger LOGGER = LogManager.getLogger("Zhijianfabric");

    @Override
    public void onInitialize() {
        LOGGER.info("[zhijian] 插件启动");

        SleepAccelerator.registerEvents();
        HomeManager.loadHomes();
        ResidenceManager.loadResidences();
        WelcomeMessageHandler.register(); // 注册欢迎消息处理程序

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            CommandHandler.registerCommands(dispatcher);
        });
    }
}
