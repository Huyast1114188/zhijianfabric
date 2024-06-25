package org.fgg2333.zhijianfabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

import java.text.SimpleDateFormat;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;

public class CommandHandler {
    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        registerTpaCommand(dispatcher);
        registerTpacceptCommand(dispatcher);
        registerTpanoCommand(dispatcher);
        registerSethomeCommand(dispatcher);
        registerHomeCommand(dispatcher);
        registerBackCommand(dispatcher);
        registerResCommands(dispatcher);
    }

    private static void registerTpaCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpa")
                .then(CommandManager.argument("target", StringArgumentType.string())
                        .executes(context -> {
                            String targetName = StringArgumentType.getString(context, "target");
                            ServerPlayerEntity sender = context.getSource().getPlayer();
                            ServerPlayerEntity target = context.getSource().getServer().getPlayerManager().getPlayer(targetName);

                            if (target != null) {
                                TpaManager.sendTpaRequest(sender, target);
                            } else {
                                sender.sendMessage(Text.literal("[zhijian] 玩家 " + targetName + " 不在线。").formatted(Formatting.RED), false);
                            }
                            return 1;
                        })));
    }

    private static void registerTpacceptCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpaccept")
                .executes(context -> {
                    ServerPlayerEntity target = context.getSource().getPlayer();
                    TpaManager.acceptTpaRequest(target);
                    return 1;
                }));
    }

    private static void registerTpanoCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("tpano")
                .executes(context -> {
                    ServerPlayerEntity target = context.getSource().getPlayer();
                    TpaManager.denyTpaRequest(target);
                    return 1;
                }));
    }

    private static void registerSethomeCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("sethome")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    HomeManager.setHome(player);
                    player.sendMessage(Text.literal("[zhijian] 家已设置。").formatted(Formatting.GREEN), false);
                    return 1;
                }));
    }

    private static void registerHomeCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("home")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    HomeManager.setLastLocation(player);

                    HomeManager.Home home = HomeManager.getHome(player);
                    if (home != null) {
                        ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());
                        if (world != null) {
                            BlockPos pos = home.getPosition();
                            player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
                            player.sendMessage(Text.literal("[zhijian] 已传送到家。").formatted(Formatting.GREEN), false);
                        } else {
                            player.sendMessage(Text.literal("[zhijian] 无法找到目标维度。").formatted(Formatting.RED), false);
                        }
                    } else {
                        player.sendMessage(Text.literal("[zhijian] 你还没有设置家。").formatted(Formatting.RED), false);
                    }
                    return 1;
                }));
    }

    private static void registerBackCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("back")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    HomeManager.Location lastLocation = HomeManager.getLastLocation(player);

                    if (lastLocation != null) {
                        ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());
                        if (world != null) {
                            BlockPos pos = lastLocation.getPosition();
                            player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
                            player.sendMessage(Text.literal("[zhijian] 已传送到上一个位置。").formatted(Formatting.GREEN), false);
                        } else {
                            player.sendMessage(Text.literal("[zhijian] 无法找到目标维度。").formatted(Formatting.RED), false);
                        }
                    } else {
                        player.sendMessage(Text.literal("[zhijian] 没有找到上一个位置。").formatted(Formatting.RED), false);
                    }
                    return 1;
                }));
    }

    private static void registerResCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("res")
                .then(literal("cj")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    String name = StringArgumentType.getString(context, "name");
                                    ResidenceManager.createResidence(name, player);
                                    player.sendMessage(Text.literal("[zhijian] 位置已创建：").formatted(Formatting.GREEN)
                                            .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                    return 1;
                                })))
                .then(literal("about")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    String name = StringArgumentType.getString(context, "name");
                                    ResidenceManager.Residence residence = ResidenceManager.getResidence(name);
                                    if (residence != null) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        player.sendMessage(Text.literal("[zhijian] 位置信息：").formatted(Formatting.GREEN)
                                                .append(Text.literal("\n名称：").formatted(Formatting.GREEN))
                                                .append(Text.literal(residence.getName()).formatted(Formatting.WHITE))
                                                .append(Text.literal("\n创建人：").formatted(Formatting.GREEN))
                                                .append(Text.literal(residence.getOwner()).formatted(Formatting.WHITE))
                                                .append(Text.literal("\n创建时间：").formatted(Formatting.GREEN))
                                                .append(Text.literal(sdf.format(residence.getCreatedDate())).formatted(Formatting.WHITE)), false);
                                    } else {
                                        player.sendMessage(Text.literal("[zhijian] 未找到位置：").formatted(Formatting.RED)
                                                .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                    }
                                    return 1;
                                })))
                .then(literal("del")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    String name = StringArgumentType.getString(context, "name");
                                    ResidenceManager.Residence residence = ResidenceManager.getResidence(name);
                                    if (residence != null) {
                                        if (residence.getOwner().equals(player.getName().getString()) || player.hasPermissionLevel(4)) {
                                            ResidenceManager.deleteResidence(name, player);
                                            player.sendMessage(Text.literal("[zhijian] 位置已删除：").formatted(Formatting.GREEN)
                                                    .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                        } else {
                                            player.sendMessage(Text.literal("[zhijian] 你没有权限删除该位置。").formatted(Formatting.RED), false);
                                        }
                                    } else {
                                        player.sendMessage(Text.literal("[zhijian] 未找到位置：").formatted(Formatting.RED)
                                                .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                    }
                                    return 1;
                                })))
                .then(literal("list")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            listResidences(player, 0);
                            return 1;
                        })
                        .then(CommandManager.argument("page", IntegerArgumentType.integer(1))
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    int page = IntegerArgumentType.getInteger(context, "page") - 1;
                                    listResidences(player, page);
                                    return 1;
                                })))
                .then(literal("tp")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    ServerPlayerEntity player = context.getSource().getPlayer();
                                    String name = StringArgumentType.getString(context, "name");
                                    ResidenceManager.Residence residence = ResidenceManager.getResidence(name);
                                    if (residence != null) {
                                        ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());
                                        if (world != null) {
                                            BlockPos pos = residence.getPosition();
                                            player.teleport(world, pos.getX(), pos.getY(), pos.getZ(), player.getYaw(), player.getPitch());
                                            player.sendMessage(Text.literal("[zhijian] 已传送到位置：").formatted(Formatting.GREEN)
                                                    .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                        } else {
                                            player.sendMessage(Text.literal("[zhijian] 无法找到目标维度。").formatted(Formatting.RED), false);
                                        }
                                    } else {
                                        player.sendMessage(Text.literal("[zhijian] 未找到位置：").formatted(Formatting.RED)
                                                .append(Text.literal(name).formatted(Formatting.WHITE)), false);
                                    }
                                    return 1;
                                }))));
    }

    private static void listResidences(ServerPlayerEntity player, int page) {
        int pageSize = 10;
        List<ResidenceManager.Residence> residences = ResidenceManager.listResidences(page, pageSize);
        int totalPages = ResidenceManager.getTotalPages(pageSize);
        MutableText message = Text.literal("--------------位置列表--------------").formatted(Formatting.WHITE);

        for (int i = 0; i < residences.size(); i++) {
            ResidenceManager.Residence residence = residences.get(i);
            message.append(Text.literal("\n" + (page * pageSize + i + 1) + ". ")
                    .append(Text.literal(residence.getName()).formatted(Formatting.WHITE))
                    .append(" 创建人：")
                    .append(Text.literal(residence.getOwner()).formatted(Formatting.WHITE)));
        }

        message.append(Text.literal("\n---上一页-----第 " + (page + 1) + " 页-----下一页---").formatted(Formatting.WHITE));

        player.sendMessage(message, false);
    }
}
