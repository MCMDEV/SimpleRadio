package com.codinglitch.simpleradio.core;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.core.networking.CustomPacket;
import com.codinglitch.simpleradio.core.networking.SimpleRadioNetworking;
import com.codinglitch.simpleradio.core.registry.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.Consumer;

public class FabricLoader {
    public static void loadItems() {
        SimpleRadioItems.ITEMS.forEach(((location, item) -> {
            Registry.register(BuiltInRegistries.ITEM, location, item.get());
        }));
    }

    public static void loadBlocks() {
        SimpleRadioBlocks.BLOCKS.forEach(((location, block) -> Registry.register(BuiltInRegistries.BLOCK, location, block)));
    }

    public static void loadParticles() {
        SimpleRadioParticles.PARTICLES.forEach(((location, particleType) -> Registry.register(BuiltInRegistries.PARTICLE_TYPE, location, particleType)));
    }

    public static void loadComponents() {
        SimpleRadioComponents.COMPONENT_TYPES.forEach(((location, componentType) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, location, componentType)));
    }

    public static void loadPackets() {
        SimpleRadioNetworking.loadServerbound(new SimpleRadioNetworking.ServerboundRegistry() {
            @Override
            public <T extends CustomPacket> void register(CustomPacketPayload.Type<T> type, Class<T> packetClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
                PayloadTypeRegistry.playC2S().register(type, codec);
                ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload, context.server(), context.player()));
            }
        });
    }

    public static void loadClientPackets() {
        SimpleRadioNetworking.loadClientbound(new SimpleRadioNetworking.ClientboundRegistry() {
            @Override
            public <T extends CustomPacket> void register(CustomPacketPayload.Type<T> type, Class<T> packetClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<T> handler) {
                PayloadTypeRegistry.playS2C().register(type, codec);
                ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.accept(payload));
            }
        });
    }

//    public static <P> ServerPlayNetworking.PlayChannelHandler serverbound(Function<FriendlyByteBuf, P> decoder, TriConsumer<P, MinecraftServer, ServerPlayer> consumer) {
//        return (server, player, handler, buf, response) -> consumer.accept(decoder.apply(buf), server, player);
//    }
//    public static <P> ClientPlayNetworking.PlayChannelHandler clientbound(Function<FriendlyByteBuf, P> decoder, Consumer<P> consumer) {
//        return (client, listener, buffer, sender) -> consumer.accept(decoder.apply(buffer));
//    }

    public static void loadResourceConditions() {
        ResourceConditions.register(ItemsEnabledCondition.TYPE);
    }

    public static void load() {
        loadItems();
        loadBlocks();
        loadPackets();
        loadParticles();
        loadComponents();
        loadResourceConditions();

        CommonSimpleRadio.load();
    }
}
