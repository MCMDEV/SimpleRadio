package com.codinglitch.simpleradio.core;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.core.registry.SimpleRadioItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public record ItemsEnabledCondition(String item) implements ResourceCondition {

    public static final MapCodec<ItemsEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            Codec.STRING.fieldOf("item").forGetter(ItemsEnabledCondition::item)
    ).apply(builder, ItemsEnabledCondition::new));

    public static final ResourceConditionType<ItemsEnabledCondition> TYPE = ResourceConditionType.create(CommonSimpleRadio.id("items_enabled"), CODEC);

    @Override
    public String toString() {
        return "item_enabled(\"" + item + "\")";
    }

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(HolderLookup.@Nullable Provider registryLookup) {
        return SimpleRadioItems.getByName(item) != null;
    }
}