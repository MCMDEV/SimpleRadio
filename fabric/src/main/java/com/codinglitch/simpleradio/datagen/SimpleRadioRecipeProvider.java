package com.codinglitch.simpleradio.datagen;

import com.codinglitch.simpleradio.core.ItemsEnabledCondition;
import com.codinglitch.simpleradio.core.central.ItemHolder;
import com.codinglitch.simpleradio.core.registry.SimpleRadioItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SimpleRadioRecipeProvider extends FabricRecipeProvider {

    public SimpleRadioRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    protected RecipeOutput withItemConditions(RecipeOutput exporter, Item item) {
        Optional<Map.Entry<ResourceLocation, ItemHolder<Item>>> optional = SimpleRadioItems.ITEMS.entrySet().stream().filter(entry -> entry.getValue().get() == item).findFirst();
        if (optional.isEmpty())
            return exporter;

        ResourceLocation location = optional.get().getKey();
        return withConditions(exporter, new ItemsEnabledCondition(location.getPath()));
    }

    @Override
    public void buildRecipes(RecipeOutput output) {
        CommonRecipeProvider.defineRecipes(item -> withItemConditions(output, item));
    }
}
