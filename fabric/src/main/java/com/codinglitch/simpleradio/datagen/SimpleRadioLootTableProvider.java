package com.codinglitch.simpleradio.datagen;

import com.codinglitch.simpleradio.core.registry.SimpleRadioBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class SimpleRadioLootTableProvider extends FabricBlockLootTableProvider {

    public SimpleRadioLootTableProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generate() {
        //TODO: create way to differentiate self-dropping blocks
        SimpleRadioBlocks.BLOCKS.forEach(((resourceLocation, block) -> dropSelf(block)));
    }
}
