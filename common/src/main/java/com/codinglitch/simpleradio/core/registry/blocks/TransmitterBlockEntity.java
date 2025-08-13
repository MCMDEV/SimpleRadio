package com.codinglitch.simpleradio.core.registry.blocks;

import com.codinglitch.simpleradio.CommonSimpleRadio;
import com.codinglitch.simpleradio.api.central.Transmitting;
import com.codinglitch.simpleradio.api.central.WorldlyPosition;
import com.codinglitch.simpleradio.client.ClientRadioManager;
import com.codinglitch.simpleradio.core.registry.SimpleRadioBlockEntities;
import com.codinglitch.simpleradio.core.registry.SimpleRadioBlocks;
import com.codinglitch.simpleradio.core.registry.SimpleRadioComponents;
import com.codinglitch.simpleradio.core.registry.SimpleRadioSounds;
import com.codinglitch.simpleradio.platform.Services;
import com.codinglitch.simpleradio.radio.RadioManager;
import com.codinglitch.simpleradio.radio.RadioRouter;
import com.codinglitch.simpleradio.radio.RadioTransmitter;
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

public class TransmitterBlockEntity extends CatalyzingBlockEntity implements Transmitting {
    public boolean isActive = false;
    public boolean isDirty = true;
    public int antennaPower = 0;

    public TransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(SimpleRadioBlockEntities.TRANSMITTER, pos, state);
    }

    @Override
    public BlockPos getAdaptorLocation() {
        return getBlockPos().relative(getBlockState().getValue(TransmitterBlock.FACING).getOpposite());
    }

    @Override
    public void setRemoved() {
        if (level != null && !level.isClientSide && this.transmitter != null) {
            level.playSound(
                    null, transmitter.location.x, transmitter.location.y, transmitter.location.z,
                    SimpleRadioSounds.RADIO_CLOSE,
                    SoundSource.PLAYERS,
                    1f, 1f
            );
        }

        inactivate();

        super.setRemoved();
    }

    @Override
    public void loadTag(CompoundTag tag) {
        //inactivate();
        super.loadTag(tag);
    }
    @Override
    public void saveTag(CompoundTag tag) {
        super.saveTag(tag);

        tag.putInt("antennaPower", antennaPower);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider $$1) {
        super.loadAdditional(tag, $$1);
        loadTag(tag);

        if (tag.contains("antennaPower")) {
            this.antennaPower = tag.getInt("antennaPower");
        }
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

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, TransmitterBlockEntity blockEntity) {
        if (blockEntity.frequency != null && blockEntity.id != null && !blockEntity.isActive) {
            blockEntity.activate();
        }
        CatalyzingBlockEntity.tick(level, pos, blockState, blockEntity);

        if (blockEntity.transmitter != null) blockEntity.transmitter.active = blockEntity.catalyst != null;

        if (!blockEntity.catalyzed) return;

        if (blockEntity.isDirty && level.getGameTime() % 200 == 0 && !level.isClientSide) {
            blockEntity.antennaPower = blockEntity.calculateAntennaPower(blockEntity.getAdaptorLocation(), level);
            RadioRouter router = blockEntity.getRouter();
            if (router instanceof RadioTransmitter transmitter) transmitter.antennaPower = blockEntity.antennaPower;

            level.sendBlockUpdated(pos, blockState, blockState, Block.UPDATE_CLIENTS);
            blockEntity.setChanged();
            blockEntity.isDirty = false;
        }
    }

    public void inactivate() {
        if (this.frequency != null) {
            RadioManager.removeRouterSided(this.id, this.level.isClientSide);
            if (!this.level.isClientSide) stopTransmitting(frequency.frequency, frequency.modulation, this.id);
        }

        this.isActive = false;
    }

    public void activate() {
        WorldlyPosition location = Services.COMPAT.modifyPosition(WorldlyPosition.of(worldPosition, level, worldPosition));

        if (!level.isClientSide) {
            this.transmitter = SimpleRadioBlocks.TRANSMITTER.getOrCreateTransmitter(location, frequency, id, this.getBlockState());

            level.playSound(
                    null, location.x, location.y, location.z,
                    SimpleRadioSounds.RADIO_OPEN,
                    SoundSource.PLAYERS,
                    1f, 1f
            );
        } else {
            this.transmitter = new RadioTransmitter(frequency, location, id);
            ClientRadioManager.registerRouter(transmitter);
        }

        this.isActive = true;
    }

    public int getAntennaPower() {
        return antennaPower;
    }
}
