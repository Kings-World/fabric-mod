package net.kings_world.fabric_mod;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Predicate;

import static net.kings_world.fabric_mod.Main.modId;

public class Utils {
    public static String stringReplace(String string, Map<String, String> replacements) {
        String result = string;
        for (Map.Entry<String, ?> entry : replacements.entrySet()) {
            result = result.replaceAll("\\{" + entry.getKey() + "}", entry.getValue().toString());
        }
        return result;
    }

    public static @NotNull Predicate<ServerCommandSource> requirePermission(String permission, int defaultLevel) {
        return isFabricPermissionsAPILoaded()
            ? Permissions.require(modId + "." + permission, defaultLevel)
            : source -> source.hasPermissionLevel(defaultLevel);
    }

    public static boolean isFabricPermissionsAPILoaded() {
        return FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");
    }
}
