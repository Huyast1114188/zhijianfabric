package org.fgg2333.zhijianfabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class SleepAccelerator {
    private static final Logger LOGGER = LogManager.getLogger("Zhijianfabric");
    private static final Set<ServerPlayerEntity> sleepingPlayers = new HashSet<>();
    private static MinecraftServer server;
    private static int timeAcceleration = 1;

    public static void registerEvents() {
        ServerTickEvents.END_SERVER_TICK.register(SleepAccelerator::onServerTick);
        ServerWorldEvents.LOAD.register((server, world) -> onWorldLoad(server, world));

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient()) {
                return ActionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            if (world.getBlockState(pos).getBlock().toString().contains("bed")) {
                onPlayerStartSleep((ServerPlayerEntity) player);
            }
            return ActionResult.PASS;
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            onPlayerStopSleep(handler.player);
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.isSleeping() && !sleepingPlayers.contains(player)) {
                    onPlayerStartSleep(player);
                } else if (!player.isSleeping() && sleepingPlayers.contains(player)) {
                    onPlayerStopSleep(player);
                }
            }
            updateActionBarMessage();
        });
    }

    private static void onWorldLoad(MinecraftServer server, World world) {
        if (world.getRegistryKey() == World.OVERWORLD) {
            SleepAccelerator.server = server;
            LOGGER.info("World loaded: " + world.getRegistryKey().getValue());
        }
    }

    public static void onPlayerStartSleep(ServerPlayerEntity player) {
        if (!sleepingPlayers.contains(player)) {
            sleepingPlayers.add(player);
            updateTimeAcceleration();
            LOGGER.info(player.getName().getString() + " started sleeping.");
        }
    }

    public static void onPlayerStopSleep(ServerPlayerEntity player) {
        if (sleepingPlayers.contains(player)) {
            sleepingPlayers.remove(player);
            updateTimeAcceleration();
            LOGGER.info(player.getName().getString() + " stopped sleeping.");
        }
    }

    private static void updateTimeAcceleration() {
        if (sleepingPlayers.size() == 0) {
            timeAcceleration = 1;
        } else if (sleepingPlayers.size() == 1) {
            timeAcceleration = 5;
        } else {
            timeAcceleration = 10;
        }
    }

    private static void updateActionBarMessage() {
        String message;
        if (sleepingPlayers.size() == 0) {
            message = "当前没有人睡觉";
        } else if (sleepingPlayers.size() == 1) {
            message = "当前有1人正在睡觉，游戏加速5x";
        } else {
            message = "当前有" + sleepingPlayers.size() + "人正在睡觉，游戏加速10x";
        }
        sendActionBarMessage(message, Formatting.GREEN);
    }

    private static void sendActionBarMessage(String message, Formatting color) {
        Text text = Text.literal(message).formatted(color);
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(text, true);
        }
    }

    private static void onServerTick(MinecraftServer server) {
        if (timeAcceleration > 1) {
            server.getOverworld().setTimeOfDay(server.getOverworld().getTimeOfDay() + timeAcceleration - 1);
        }
    }
}
