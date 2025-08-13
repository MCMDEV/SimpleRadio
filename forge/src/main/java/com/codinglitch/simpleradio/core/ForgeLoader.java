package com.codinglitch.simpleradio.core;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.core.networking.CustomPacket;
import com.codinglitch.simpleradio.core.networking.SimpleRadioNetworking;
import com.codinglitch.simpleradio.core.registry.*;
import com.codinglitch.simpleradio.datagen.SimpleRadioBlockLootTableProvider;
import com.codinglitch.simpleradio.datagen.SimpleRadioRecipeProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkProtocol;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = CommonSimpleRadio.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeLoader {
    public static final SimpleChannel CHANNEL = ChannelBuilder.named(CommonSimpleRadio.id("channel"))
            .optional()
            .networkProtocolVersion(0)
            .simpleChannel();

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(
                event.includeServer(),
                new SimpleRadioRecipeProvider(generator.getPackOutput(), event.getLookupProvider())
        );

        generator.addProvider(
                event.includeServer(),
                new LootTableProvider(generator.getPackOutput(), Set.of(), List.of(
                        new LootTableProvider.SubProviderEntry(SimpleRadioBlockLootTableProvider::new, LootContextParamSets.BLOCK)
                ), event.getLookupProvider())
        );
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ITEMS, helper -> SimpleRadioItems.ITEMS.forEach((location, itemHolder) -> helper.register(location, itemHolder.get())));
        event.register(ForgeRegistries.Keys.BLOCKS, helper -> SimpleRadioBlocks.BLOCKS.forEach((helper::register)));

        event.register(ForgeRegistries.Keys.ENTITY_TYPES, helper -> SimpleRadioEntities.ENTITIES.forEach((helper::register)));
        event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, helper -> SimpleRadioBlockEntities.BLOCK_ENTITIES.forEach((helper::register)));

        event.register(ForgeRegistries.Keys.MENU_TYPES, helper -> SimpleRadioMenus.MENUS.forEach(helper::register));
        event.register(Registries.CREATIVE_MODE_TAB, helper -> SimpleRadioMenus.CREATIVE_TABS.forEach(helper::register));

        event.register(ForgeRegistries.Keys.PARTICLE_TYPES, helper -> SimpleRadioParticles.PARTICLES.forEach(helper::register));

        event.register(ForgeRegistries.Keys.CONDITION_SERIALIZERS, helper -> {
            helper.register(CommonSimpleRadio.id("items_enabled"), ItemsEnabledCondition.CODEC);
        });

        CommonSimpleRadio.load();
    }

    public static void loadPackets() {
        AtomicInteger index = new AtomicInteger();

        SimpleRadioNetworking.loadServerbound(new SimpleRadioNetworking.ServerboundRegistry() {
            @Override
            public <T extends CustomPacket> void register(CustomPacketPayload.Type<T> type, Class<T> packetClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, TriConsumer<T, MinecraftServer, ServerPlayer> handler) {
                CHANNEL.<T, RegistryFriendlyByteBuf>messageBuilder(packetClass, index.getAndIncrement(), (NetworkProtocol<RegistryFriendlyByteBuf>) null)
                        .codec(codec)
                        .consumerMainThread((packet, context) -> {
                            handler.accept(packet, context.getSender().getServer(), context.getSender());
                            context.setPacketHandled(true);
                        }).add();
            }
        });

        SimpleRadioNetworking.loadClientbound(new SimpleRadioNetworking.ClientboundRegistry() {
            @Override
            public <T extends CustomPacket> void register(CustomPacketPayload.Type<T> type, Class<T> packetClass, StreamCodec<RegistryFriendlyByteBuf, T> codec, Consumer<T> handler) {
                CHANNEL.<T, RegistryFriendlyByteBuf>messageBuilder(packetClass, index.getAndIncrement(), (NetworkProtocol<RegistryFriendlyByteBuf>) null)
                        .codec(codec)
                        .consumerMainThread((packet, context) -> {
                            handler.accept(packet);
                            context.setPacketHandled(true);
                        }).add();
            }
        });
    }

    public static void loadItems() {

    }

    public static void load() {
        loadItems();
        loadPackets();
    }

    public static void loadClient() {
    }
}
