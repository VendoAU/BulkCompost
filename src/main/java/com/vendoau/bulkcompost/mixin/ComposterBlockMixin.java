package com.vendoau.bulkcompost.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Shadow
    static BlockState addItem(@Nullable Entity entity, BlockState blockState, LevelAccessor levelAccessor,
                              BlockPos blockPos, ItemStack itemStack) {
        throw new AssertionError();
    }

    // Intellij thinks levelIncreased is always false for some reason, it definitely isn't though
    @SuppressWarnings("ConstantValue")
    @Inject(method = "useItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/ComposterBlock;addItem(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/level/block/state/BlockState;"), cancellable = true)
    private void useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player,
                           InteractionHand interactionHand, BlockHitResult blockHitResult,
                           CallbackInfoReturnable<InteractionResult> cir) {
        BlockState blockState2 = blockState;
        while (blockState.getValue(ComposterBlock.LEVEL) < 7 && itemStack.getCount() > 0) {
            blockState2 = addItem(player, blockState, level, blockPos, itemStack);
            player.awardStat(Stats.ITEM_USED.get(itemStack.getItem()));
            itemStack.consume(1, player);
        }
        boolean levelIncreased = blockState != blockState2;
        level.levelEvent(1500, blockPos, levelIncreased ? 1 : 0);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}