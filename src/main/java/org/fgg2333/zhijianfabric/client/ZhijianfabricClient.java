package org.fgg2333.zhijianfabric.client;

import net.fabricmc.api.ClientModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZhijianfabricClient implements ClientModInitializer {
    // 创建一个Logger实例
    private static final Logger LOGGER = LogManager.getLogger("Zhijianfabric");

    @Override
    public void onInitializeClient() {
        // 输出客户端加载信息到日志
        LOGGER.info("[zhijian]client plugin start");
    }
}
