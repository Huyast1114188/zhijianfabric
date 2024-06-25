package org.fgg2333.zhijianfabric;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HomeManager {
    private static final File CONFIG_FILE = new File("config/zhijianfabric_homes.json");
    private static final Gson GSON = new Gson();
    private static final Map<UUID, Home> homes = new HashMap<>();
    private static final Map<UUID, Location> lastLocations = new HashMap<>();

    public static void loadHomes() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<UUID, Home>>(){}.getType();
                Map<UUID, Home> loadedHomes = GSON.fromJson(reader, type);
                if (loadedHomes != null) {
                    homes.putAll(loadedHomes);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveHomes() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(homes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setHome(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        homes.put(player.getUuid(), new Home(pos, dimension));
        saveHomes();
    }

    public static Home getHome(ServerPlayerEntity player) {
        return homes.get(player.getUuid());
    }

    public static void setLastLocation(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        lastLocations.put(player.getUuid(), new Location(pos, dimension));
    }

    public static Location getLastLocation(ServerPlayerEntity player) {
        return lastLocations.get(player.getUuid());
    }

    public static class Home {
        private final BlockPos position;
        private final String dimension;

        public Home(BlockPos position, String dimension) {
            this.position = position;
            this.dimension = dimension;
        }

        public BlockPos getPosition() {
            return position;
        }

        public String getDimension() {
            return dimension;
        }
    }

    public static class Location {
        private final BlockPos position;
        private final String dimension;

        public Location(BlockPos position, String dimension) {
            this.position = position;
            this.dimension = dimension;
        }

        public BlockPos getPosition() {
            return position;
        }

        public String getDimension() {
            return dimension;
        }
    }
}
