package org.fgg2333.zhijianfabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WelcomeMessageHandler {
    private static final String SERVER_VERSION = "1.21.0"; // 替换为实际的服务器版本
    private static final String PLUGIN_VERSION = "1.0.0"; // 替换为实际的插件版本

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;

            if (player.hasPermissionLevel(4)) { // 检查玩家是否具有管理员权限
                sendAdminWelcomeMessage(player);
            } else {
                sendPlayerWelcomeMessage(player);
            }
        });
    }

    private static void sendAdminWelcomeMessage(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("亲爱的管理员您好,当前服务器版本为：")
                .formatted(Formatting.GREEN)
                .append(Text.literal(SERVER_VERSION).formatted(Formatting.WHITE))
                .append("\n插件版本为：")
                .append(Text.literal(PLUGIN_VERSION).formatted(Formatting.WHITE))
                .append("\n如遇服务器与插件问题请联系Huyast1114188进行处理")
                .append("\n感谢您对服务器做出的贡献，为服务器内玩家进行问题处理"), false);
    }

    private static void sendPlayerWelcomeMessage(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("欢迎您加入指尖的小服，当前服务器版本为")
                .append(Text.literal(SERVER_VERSION).formatted(Formatting.WHITE))
                .append("\n如有BUG与其他问题，请联系服务器开发者"), false);
    }
}
