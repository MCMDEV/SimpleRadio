package com.codinglitch.simpleradio.mixin;

import com.codinglitch.simpleradio.radio.RadioManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JukeboxSongPlayer.class)
public abstract class MixinJukeboxBlockEntity {

    @Shadow
    @Final
    private BlockPos blockPos;

    @Shadow
    @Nullable
    private Holder<JukeboxSong> song;

    @Shadow
    private long ticksSinceSongStarted;

    @Inject(method = "play", at = @At(value = "TAIL"))
    private void simpleradio$startPlaying_audioGathering(LevelAccessor $$0, Holder<JukeboxSong> $$1, CallbackInfo ci) {
        RadioManager.getInstance().onSoundPlayed(
                (ServerLevel) $$0,
                blockPos.getCenter(),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder($$1.value().soundEvent().value()),
                1, 1, blockPos.asLong()
        );
    }

    @Inject(method = "stop", at = @At(value = "TAIL"))
    private void simpleradio$stopPlaying_audioGathering(LevelAccessor $$0, BlockState $$1, CallbackInfo ci) {
        RadioManager.getInstance().onSoundPlayed(
                (ServerLevel) $$0,
                blockPos.getCenter(),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),
                0, 1, blockPos.asLong()
        );
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    shift = At.Shift.BEFORE,
                    target = "Lnet/minecraft/world/item/JukeboxSongPlayer;spawnMusicParticles(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;)V"
            ), method = "tick"
    )
    private void simpleradio$tick_audioGathering(LevelAccessor $$0, BlockState $$1, CallbackInfo ci) {
        float offset = ticksSinceSongStarted / 20f;

        RadioManager.getInstance().onSoundPlayed(
                (ServerLevel) $$0,
                blockPos.getCenter(),
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(song.value().soundEvent().value()),
                1, 1,  offset, blockPos.asLong()
        );
    }
}