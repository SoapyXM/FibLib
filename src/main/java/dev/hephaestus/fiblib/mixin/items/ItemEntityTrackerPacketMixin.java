package dev.hephaestus.fiblib.mixin.items;

import dev.hephaestus.fiblib.FibLib;
import dev.hephaestus.fiblib.Fibber;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(EntityTrackerUpdateS2CPacket.class)
public class ItemEntityTrackerPacketMixin implements Fibber {

    @Shadow private List<DataTracker.Entry<?>> trackedValues;

    // This whole copying thing is necessary to prevent it from *actually* setting these values on the server.
    @Override
    public void fix(ServerPlayerEntity player) {
        List<DataTracker.Entry<?>> trackedValuesCopy = new ArrayList<>(trackedValues);
        for(int i=0; i < trackedValuesCopy.size(); i++) {
            DataTracker.Entry<?> entryCopy = trackedValuesCopy.get(i).copy();
            if(entryCopy.getData() == ItemEntity.STACK) {
                ((DataTracker.Entry<ItemStack>)entryCopy).set(FibLib.Items.transform((ItemStack)entryCopy.get(), player));
                trackedValues.set(i, entryCopy);
            }
        }
    }
}
