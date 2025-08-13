package com.codinglitch.simpleradio.client;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.platform.NeoForgeClientRegistryHelper;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CommonSimpleRadio.ID, bus = EventBusSubscriber.Bus.MOD)
public class SimpleRadioClient {
    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        CommonSimpleRadioClient.loadLayerDefinitions(event::registerLayerDefinition);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        CommonSimpleRadioClient.loadBlockEntityRenderers(event::registerBlockEntityRenderer);
        CommonSimpleRadioClient.loadEntityRenderers(event::registerEntityRenderer);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        CommonSimpleRadioClient.loadParticles(event::registerSpriteSet);
    }

    @SubscribeEvent
    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        NeoForgeClientRegistryHelper.ENTRIES.forEach(entry -> {
            event.register(entry.menuType(), entry.screenConstructor());
        });
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CommonSimpleRadioClient.initialize();
        CommonSimpleRadioClient.loadProperties(ItemProperties::register);

        event.enqueueWork(CommonSimpleRadioClient::loadScreens);
    }
}
