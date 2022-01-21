package xyz.nuark.tillassist;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class GlobalEventHandler {
    private static final List<Block> tillableBlocks = Arrays.asList(
            Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.FARMLAND
    );

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public static void userLooking(PlayerEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = event.getPlayer();

        if (!(player.getMainHandItem().getItem() instanceof HoeItem)) {
            return;
        }

        Entity entity = mc.getCameraEntity();
        if (entity == null) {
            return;
        }

        ClientLevel level = mc.level;
        if (level == null) {
            return;
        }

        HitResult blockHitResult = entity.pick(20.0D, 0.0F, false);

        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos blockpos = ((BlockHitResult) blockHitResult).getBlockPos();
        BlockState blockstate = level.getBlockState(blockpos);
        Block block = blockstate.getBlock();

        if (!tillableBlocks.contains(block)) {
            return;
        }

        String messageText = "This block will be " + ChatFormatting.BOLD;
        if (hasWaterNearby(level, blockpos)) {
            messageText += ChatFormatting.BLUE + "MOIST";
        } else {
            messageText += ChatFormatting.YELLOW + "DRY";
        }
        messageText += ChatFormatting.RESET;

        player.displayClientMessage(new TextComponent(messageText), true);
    }

    private static boolean hasWaterNearby(Level level, BlockPos startingPos) {
        for (int y = 0; y < 2; y++) {
            for (int z = -4; z < 5; z++) {
                for (int x = -4; x < 5; x++) {
                    BlockPos cbp = startingPos.above(y).east(z).north(x);
                    FluidState fluidState = level.getBlockState(cbp).getFluidState();
                    if (fluidState.getType() == Fluids.WATER || fluidState.getType() == Fluids.FLOWING_WATER) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
