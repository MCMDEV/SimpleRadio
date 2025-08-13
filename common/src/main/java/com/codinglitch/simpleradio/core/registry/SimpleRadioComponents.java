package com.codinglitch.simpleradio.core.registry;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.codinglitch.simpleradio.CommonSimpleRadio.id;

public class SimpleRadioComponents {
    public static final Map<ResourceLocation, DataComponentType<?>> COMPONENT_TYPES = new LinkedHashMap<>();

    public static final DataComponentType<CustomData> BLOCK_ITEM_DATA = register(id("block_item_data"), DataComponentType.<CustomData>builder()
            .persistent(CustomData.CODEC)
            .build());

    private static <T> DataComponentType<T> register(ResourceLocation location, DataComponentType<T> componentType) {
        COMPONENT_TYPES.put(location, componentType);
        return componentType;
    }

    public static CompoundTag getOrCreateTagOnItemStack(ItemStack stack) {
        CustomData customData = stack.get(SimpleRadioComponents.BLOCK_ITEM_DATA);
        if(customData == null) {
            return new CompoundTag();
        }
        return customData.copyTag();
    }

    public static <R> R modifyTagOnItemStackReturning(ItemStack stack, TagModifierWithReturnValue<R> tagModifier) {
        CustomData customData = stack.get(SimpleRadioComponents.BLOCK_ITEM_DATA);
        if(customData == null) {
            customData = CustomData.of(new CompoundTag());
        }

        CompoundTag tagCopy = customData.copyTag();
        DirtyMarker dirtyMarker = new DirtyMarker();
        R returnValue = tagModifier.modify(tagCopy, dirtyMarker);
        customData = CustomData.of(tagCopy);

        if(dirtyMarker.isDirty()) {
            stack.applyComponents(DataComponentPatch.builder().set(SimpleRadioComponents.BLOCK_ITEM_DATA, customData).build());
        }
        return returnValue;
    }

    public static void modifyTagOnItemStack(ItemStack tag, TagModifier tagModifier) {
        modifyTagOnItemStackReturning(tag, (TagModifierWithReturnValue<Void>) (copiedTag, dirty) -> {
            tagModifier.modify(copiedTag, dirty);
            return null;
        });
    }

    @FunctionalInterface
    public interface TagModifier {
        void modify(CompoundTag tag, DirtyMarker dirty);
    }

    @FunctionalInterface
    public interface TagModifierWithReturnValue<R> {
        R modify(CompoundTag tag, DirtyMarker dirty);
    }

    public static class DirtyMarker {
        private boolean dirty;

        public void set() {
            this.dirty = true;
        }

        private boolean isDirty() {
            return dirty;
        }
    }
}
