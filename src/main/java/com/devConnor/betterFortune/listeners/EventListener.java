package com.devConnor.betterFortune.listeners;

import com.devConnor.betterFortune.BetterFortune;
import com.devConnor.betterFortune.managers.BlockDupeManager;
import com.devConnor.betterFortune.managers.FortuneManager;
import lombok.Builder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@Builder
public class EventListener implements Listener {

    private final BetterFortune betterFortune;
    private final FortuneManager fortuneManager;
    private final BlockDupeManager blockDupeManager;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();

        // Skip processing if the block is blacklisted
        if (fortuneManager.isMaterialBlacklisted(block.getType())) {
            return;
        }

        if (block.getDrops().isEmpty()) {
            return;
        }

        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Material material = block.getType();
        Double playerBlockDupe = blockDupeManager.getPlayerBlockDupe(player.getUniqueId());

        if (!doesItemHaveFortune(itemInHand) && playerBlockDupe == null) {
            return;
        }

        e.setDropItems(false);
        for (ItemStack drop : block.getDrops()) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(drop.getType()));
        }
    }

    private boolean doesItemHaveFortune(ItemStack item) {
        return item.containsEnchantment(org.bukkit.enchantments.Enchantment.LOOT_BONUS_BLOCKS);
    }
}
