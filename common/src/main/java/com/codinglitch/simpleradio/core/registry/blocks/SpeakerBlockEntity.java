package com.codinglitch.simpleradio.core.registry.blocks;

import com.codinglitch.simpleradio.SimpleRadioLibrary;
import com.codinglitch.simpleradio.api.central.Speaking;
import com.codinglitch.simpleradio.api.central.WorldlyPosition;
import com.codinglitch.simpleradio.client.ClientRadioManager;
import com.codinglitch.simpleradio.core.registry.SimpleRadioBlockEntities;
import com.codinglitch.simpleradio.core.registry.SimpleRadioBlocks;
import com.codinglitch.simpleradio.core.registry.SimpleRadioComponents;
import com.codinglitch.simpleradio.core.registry.SimpleRadioSounds;
import com.codinglitch.simpleradio.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class SpeakerBlockEntity extends AuditoryBlockEntity implements Speaking {
    public boolean isActive = false;

    public SpeakerBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleRadioBlockEntities.SPEAKER, pos, state);
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide && this.speaker != null) {
            level.playSound(
                    null, speaker.location.x, speaker.location.y, speaker.location.z,
                    SimpleRadioSounds.RADIO_CLOSE,
                    SoundSource.PLAYERS,
                    1f, 1f
            );
        }

        inactivate();

        super.setRemoved();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider $$1) {
        super.loadAdditional(tag, $$1);
        loadTag(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider $$1) {
        saveTag(tag);
        super.saveAdditional(tag, $$1);
    }

    @Override
    public void saveToItem(ItemStack $$0, HolderLookup.Provider $$1) {
        CompoundTag tag = new CompoundTag();
        saveTag(tag);
        DataComponentPatch dataComponentPatch = DataComponentPatch.builder()
                .set(SimpleRadioComponents.BLOCK_ITEM_DATA, CustomData.of(tag))
                .build();
        $$0.applyComponents(dataComponentPatch);
        super.saveToItem($$0, $$1);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SpeakerBlockEntity blockEntity) {
        if (!blockEntity.isActive && blockEntity.id != null) {
            blockEntity.activate();
        }

        if (blockEntity.level == null) return;
        if (blockEntity.speaker != null && blockEntity.speaker.activityTime >= 0) {
            if (blockEntity.speaker.activityTime % SimpleRadioLibrary.SERVER_CONFIG.speaker.redstonePolling == 0) {
                level.updateNeighborsAt(pos, SimpleRadioBlocks.SPEAKER);
            }
            if (SimpleRadioLibrary.CLIENT_CONFIG.speaker.particleInterval != 0) {
                if (blockEntity.level.isClientSide && blockEntity.speaker.activityTime % SimpleRadioLibrary.CLIENT_CONFIG.speaker.particleInterval == 0) {
                    ClientRadioManager.handleSpeakParticle(state, blockEntity);
                }
            }
        }
    }

    public void inactivate() {
        if (this.isActive) {
            stopSpeaking();
            //stopReceiving(frequency.frequency, frequency.modulation, id);
        }

        this.isActive = false;
    }

    public void activate() {
        WorldlyPosition location = Services.COMPAT.modifyPosition(WorldlyPosition.of(worldPosition, level, worldPosition));

        this.speaker = SimpleRadioBlocks.SPEAKER.getOrCreateSpeaker(location, id, this.getBlockState());
        if (!level.isClientSide) {
            level.playSound(
                    null, location.x, location.y, location.z,
                    SimpleRadioSounds.RADIO_OPEN,
                    SoundSource.PLAYERS,
                    1f, 1f
            );
        }

        this.isActive = true;
    }

    @Override
    public void loadTag(CompoundTag tag) {
        inactivate();
        super.loadTag(tag);
    }
}
