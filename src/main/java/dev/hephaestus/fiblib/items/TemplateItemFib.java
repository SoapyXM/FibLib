package dev.hephaestus.fiblib.items;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public abstract class TemplateItemFib extends ItemFib {
    private ItemStack inputTemplate;
    private ItemStack outputTemplate;

    public TemplateItemFib(ItemStack inputTemplate, ItemStack outputTemplate) {
        this.inputTemplate = inputTemplate;
        this.outputTemplate = outputTemplate;
    }

    @Override
    public boolean matches(ServerPlayerEntity player, ItemStack stack) {
        return stack.isItemEqual(inputTemplate) && condition(player);
    }

    public abstract boolean condition(ServerPlayerEntity player);

    @Override
    public ItemStack transform(ServerPlayerEntity player, ItemStack stack) {
        ItemStack newStack = outputTemplate.copy();
        newStack.setCustomName(stack.getName());
        newStack.setCount(stack.getCount());
        return newStack;
    }
}
