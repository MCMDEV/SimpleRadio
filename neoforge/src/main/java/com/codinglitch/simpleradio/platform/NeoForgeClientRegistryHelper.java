package com.codinglitch.simpleradio.platform;

import com.codinglitch.simpleradio.platform.services.ClientRegistryHelper;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NeoForgeClientRegistryHelper implements ClientRegistryHelper {

    public static final Set<Entry> ENTRIES = new HashSet<>();

    @Override
    public <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void registerScreen(MenuType<? extends M> menuType, ScreenConstructor<M, U> screenConstructor) {
        ENTRIES.add(new Entry<>(menuType, screenConstructor::create));
    }

    public record Entry<M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>>(MenuType<? extends M> menuType, MenuScreens.ScreenConstructor<M, U> screenConstructor) {

    }
}
