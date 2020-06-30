package dev.hephaestus.fiblib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class ItemFib {
    public abstract boolean matches(ServerPlayerEntity player, ItemStack stack);
    public abstract ItemStack transform(ServerPlayerEntity player, ItemStack stack);
}
