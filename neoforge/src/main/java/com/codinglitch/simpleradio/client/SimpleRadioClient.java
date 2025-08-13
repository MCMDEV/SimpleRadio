package com.codinglitch.simpleradio.client;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.platform.NeoForgeClientRegistryHelper;
import com.codinglitch.simpleradio.platform.services.ClientRegistryHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = CommonSimpleRadio.ID)
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
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        CommonSimpleRadioClient.loadScreensWithHelper(new ClientRegistryHelper() {
            @Override
            public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> menuType, ScreenConstructor<M, U> screenConstructor) {
                event.register(menuType, screenConstructor::create);
            }
        });
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        CommonSimpleRadioClient.initialize();
        CommonSimpleRadioClient.loadProperties(ItemProperties::register);
    }
}
