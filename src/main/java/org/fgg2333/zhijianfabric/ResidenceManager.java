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
import java.util.*;

public class ResidenceManager {
    private static final File CONFIG_FILE = new File("config/zhijianfabric_residences.json");
    private static final Gson GSON = new Gson();
    private static final Map<String, Residence> residences = new HashMap<>();

    public static void loadResidences() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                Type type = new TypeToken<Map<String, Residence>>(){}.getType();
                Map<String, Residence> loadedResidences = GSON.fromJson(reader, type);
                if (loadedResidences != null) {
                    residences.putAll(loadedResidences);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveResidences() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(residences, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createResidence(String name, ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        String dimension = player.getWorld().getRegistryKey().getValue().toString();
        Residence residence = new Residence(name, player.getName().getString(), pos, dimension, new Date());
        residences.put(name, residence);
        saveResidences();
    }

    public static Residence getResidence(String name) {
        return residences.get(name);
    }

    public static void deleteResidence(String name, ServerPlayerEntity player) {
        Residence residence = residences.get(name);
        if (residence != null && (residence.getOwner().equals(player.getName().getString()) || player.hasPermissionLevel(4))) {
            residences.remove(name);
            saveResidences();
        }
    }

    public static List<Residence> listResidences(int page, int pageSize) {
        List<Residence> list = new ArrayList<>(residences.values());
        int fromIndex = Math.min(page * pageSize, list.size());
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }

    public static int getTotalPages(int pageSize) {
        return (residences.size() + pageSize - 1) / pageSize;
    }

    public static class Residence {
        private final String name;
        private final String owner;
        private final BlockPos position;
        private final String dimension;
        private final Date createdDate;

        public Residence(String name, String owner, BlockPos position, String dimension, Date createdDate) {
            this.name = name;
            this.owner = owner;
            this.position = position;
            this.dimension = dimension;
            this.createdDate = createdDate;
        }

        public String getName() {
            return name;
        }

        public String getOwner() {
            return owner;
        }

        public BlockPos getPosition() {
            return position;
        }

        public String getDimension() {
            return dimension;
        }

        public Date getCreatedDate() {
            return createdDate;
        }
    }
}
