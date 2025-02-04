package net.kings_world.fabric_mod;

import net.dv8tion.jda.api.entities.Message;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Events {
    public static final Event<PlayerJoin> PLAYER_JOIN = EventFactory.createArrayBacked(PlayerJoin.class, callbacks -> player -> {
        for (PlayerJoin callback : callbacks) {
            callback.onPlayerJoin(player);
        }
    });

    public static final Event<PlayerLeave> PLAYER_LEAVE = EventFactory.createArrayBacked(PlayerLeave.class, callbacks -> (player, reason) -> {
        for (PlayerLeave callback : callbacks) {
            callback.onPlayerLeave(player, reason);
        }
    });

    public static final Event<PlayerDeath> PLAYER_DEATH = EventFactory.createArrayBacked(PlayerDeath.class, callbacks -> (player, reason) -> {
        for (PlayerDeath callback : callbacks) {
            callback.onPlayerDeath(player, reason);
        }
    });

    public static final Event<PlayerAdvancement> PLAYER_ADVANCEMENT = EventFactory.createArrayBacked(PlayerAdvancement.class, callbacks -> (player, advancement) -> {
        for (PlayerAdvancement callback : callbacks) {
            callback.onPlayerAdvancement(player, advancement);
        }
    });

    public static final Event<DiscordMessage> DISCORD_MESSAGE = EventFactory.createArrayBacked(DiscordMessage.class, callbacks -> message -> {
        for (DiscordMessage callback : callbacks) {
            callback.onDiscordMessage(message);
        }
    });

    public interface PlayerJoin {
        void onPlayerJoin(ServerPlayerEntity player);
    }

    public interface PlayerLeave {
        void onPlayerLeave(ServerPlayerEntity player, Text reason);
    }

    public interface PlayerDeath {
        void onPlayerDeath(ServerPlayerEntity player, Text reason);
    }

    public interface PlayerAdvancement {
        void onPlayerAdvancement(ServerPlayerEntity player, AdvancementDisplay advancement);
    }

    public interface DiscordMessage {
        void onDiscordMessage(Message message);
    }
}
