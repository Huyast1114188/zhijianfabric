package org.fgg2333.zhijianfabric;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpaManager {
    private static final Map<ServerPlayerEntity, ServerPlayerEntity> tpaRequests = new HashMap<>();
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    private static final long COOLDOWN_TIME = 60 * 1000; // 60 seconds

    public static void sendTpaRequest(ServerPlayerEntity sender, ServerPlayerEntity target) {
        long currentTime = System.currentTimeMillis();
        if (cooldowns.containsKey(sender.getUuid()) && currentTime - cooldowns.get(sender.getUuid()) < COOLDOWN_TIME) {
            long remainingTime = (COOLDOWN_TIME - (currentTime - cooldowns.get(sender.getUuid()))) / 1000;
            sender.sendMessage(Text.literal("[zhijian] 传送请求冷却中，剩余时间: " + remainingTime + "秒").formatted(Formatting.RED), false);
            return;
        }

        tpaRequests.put(target, sender);
        cooldowns.put(sender.getUuid(), currentTime);

        target.sendMessage(Text.literal("[zhijian] " + sender.getName().getString() + " 向你发送了传送请求。使用 /tpaccept 接受或 /tpano 拒绝。").formatted(Formatting.GREEN), false);
        sender.sendMessage(Text.literal("[zhijian] 传送请求已发送给 " + target.getName().getString() + "。").formatted(Formatting.GREEN), false);
    }

    public static void acceptTpaRequest(ServerPlayerEntity target) {
        ServerPlayerEntity sender = tpaRequests.remove(target);
        if (sender != null) {
            sender.sendMessage(Text.literal("[zhijian] " + target.getName().getString() + " 接受了你的传送请求。").formatted(Formatting.GREEN), false);
            sender.teleport(target.getServerWorld(), target.getX(), target.getY(), target.getZ(), target.getYaw(), target.getPitch());
            target.sendMessage(Text.literal("[zhijian] 你接受了 " + sender.getName().getString() + " 的传送请求。").formatted(Formatting.GREEN), false);
        } else {
            target.sendMessage(Text.literal("[zhijian] 没有待处理的传送请求。").formatted(Formatting.RED), false);
        }
    }

    public static void denyTpaRequest(ServerPlayerEntity target) {
        ServerPlayerEntity sender = tpaRequests.remove(target);
        if (sender != null) {
            sender.sendMessage(Text.literal("[zhijian] " + target.getName().getString() + " 拒绝了你的传送请求。").formatted(Formatting.RED), false);
            target.sendMessage(Text.literal("[zhijian] 你已拒绝 " + sender.getName().getString() + " 的传送请求。").formatted(Formatting.GREEN), false);
        } else {
            target.sendMessage(Text.literal("[zhijian] 没有待处理的传送请求。").formatted(Formatting.RED), false);
        }
    }
}
