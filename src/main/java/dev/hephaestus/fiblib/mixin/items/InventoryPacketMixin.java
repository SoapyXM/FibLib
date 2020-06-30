package dev.hephaestus.fiblib.mixin.items;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(InventoryS2CPacket.class)
public class InventoryPacketMixin implements Fibber {
    @Shadow private List<ItemStack> contents;

    @Override
    public void fix(ServerPlayerEntity player) {
        for(int i=0; i < contents.size(); i++) {
            contents.set(i, FibLib.Items.transform(contents.get(i), player));
        }
    }
}
