package fr.anthonus.pickuptools.mixin.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MiddleClickMixin {

    @Inject(method = "doItemPick", at = @At("HEAD"), cancellable = true)
    private void onPickBlock(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.options.sneakKey.isPressed()) {
            HitResult hitResult = client.crosshairTarget;
            if (hitResult instanceof BlockHitResult bhr && hitResult.getType() == HitResult.Type.BLOCK) {
                BlockState state = client.world.getBlockState(bhr.getBlockPos());
                ItemStack bestTool = findBestToolForBlock(client.player, state);

                if (!bestTool.isEmpty()) {
                    switchItemInHotbar(client.player, bestTool);
                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private ItemStack findBestToolForBlock(PlayerEntity player, BlockState state) {
        ItemStack bestTool =ItemStack.EMPTY;
        float bestSpeed = 1.0f;

        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty()) continue;

            float speed = stack.getMiningSpeedMultiplier(state);
            if (speed > bestSpeed) {
                bestTool = stack;
                bestSpeed = speed;
            }
        }
        return bestTool;
    }

    @Unique
    private void switchItemInHotbar(PlayerEntity player, ItemStack target) {
        int hotbarStart = 0;
        int hotbarEnd = 8;
        int selectedSlot = player.getInventory().getSelectedSlot();

        for (int i = hotbarStart; i <= hotbarEnd; i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack == target) {
                player.getInventory().setSelectedSlot(i);
                return;
            }
        }

        for (int i = hotbarEnd + 1; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack == target) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client.interactionManager != null) {
                    client.interactionManager.clickSlot(
                            player.playerScreenHandler.syncId,
                            i,
                            selectedSlot,
                            SlotActionType.SWAP,
                            player
                    );
                }
            }
        }
    }
}
