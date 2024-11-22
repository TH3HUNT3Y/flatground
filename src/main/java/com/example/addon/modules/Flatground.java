package com.example.addon.modules;

import meteordevelopment.meteorclient.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PlaceObsidianAddon extends MeteorAddon {

    @Override
    public void onInitialize() {
        Modules.get().add(new PlaceObsidianModule());
    }

    @Override
    public void onRegisterCategories() {
        // Register custom categories if needed.
    }

    public static class PlaceObsidianModule extends Module {
        private final MinecraftClient mc = MinecraftClient.getInstance();

        public PlaceObsidianModule() {
            super("PlaceObsidianAddon", "Places obsidian or ender chests under a player's feet in a 3x3 area.", Category.Misc);
        }

        @EventHandler
        private void onTick(TickEvent.Post event) {
            if (mc.player == null || mc.world == null) return;

            // Get player position
            Vec3d playerPos = mc.player.getPos();
            BlockPos center = new BlockPos(playerPos.x, playerPos.y - 1, playerPos.z);

            // Loop through the 3x3 area around the player's feet
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos blockPos = center.add(x, 0, z);
                    
                    // Check if the block is replaceable (air, grass, etc.)
                    if (mc.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
                        placeBlock(blockPos);
                    }
                }
            }
        }

        private void placeBlock(BlockPos blockPos) {
            Block blockToPlace = getAvailableBlock();
            if (blockToPlace == null) return; // No obsidian or ender chest in inventory.

            // Place the block
            BlockUtils.place(blockPos, InvUtils.findInHotbar(itemStack -> 
                itemStack.getItem() == Items.OBSIDIAN || itemStack.getItem() == Items.ENDER_CHEST), false);
        }

        private Block getAvailableBlock() {
            if (InvUtils.findInHotbar(Items.OBSIDIAN) != -1) return Blocks.OBSIDIAN;
            if (InvUtils.findInHotbar(Items.ENDER_CHEST) != -1) return Blocks.ENDER_CHEST;
            return null;
        }
    }
}
