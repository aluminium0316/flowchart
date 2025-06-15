package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.utils.ItemorfluidStack;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class ItemorfluidUi {

    public static IWidget slot(int i, int j, ItemorfluidStack item, NodeWidget.Int sub) {
        if (item.item != null) {
            ItemStackHandler itemHandler = new ItemStackHandler();
            itemHandler.setStackInSlot(0, item.item);
            return new ItemSlot2(i, sub).pos(1 + j * 18, i * 18)
                    .slot(itemHandler, 0);
        }
        else if (item.fluid != null) {
            FluidSlotSyncHandler fluid = new FluidSlotSyncHandler(new FluidTank(item.fluid, item.fluid.amount));
            fluid.phantom(true);
            fluid.updateCacheFromSource(true);

            return new FluidSlot2(i, sub).pos(1 + j * 18, i * 18)
                    .syncHandler(fluid);
        }
        return null;
    }

    private static class FluidSlot2 extends FluidSlot {
        public NodeWidget.Int sub;
        public int index;

        public FluidSlot2(int i, NodeWidget.Int sub) {
            super();
            index = i;
            this.sub = sub;
        }

        @Override
        public Result onMousePressed(int mouseButton) {
            return Result.IGNORE;
        }

        @Override
        public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
            return false;
        }

        @Override
        public void onMouseStartHover() {
            super.onMouseStartHover();
            sub.i = index;
        }
    }

    private static class ItemSlot2 extends PhantomItemSlot {
        public NodeWidget.Int sub;
        public int index;

        public ItemSlot2(int i, NodeWidget.Int sub) {
            super();
            index = i;
            this.sub = sub;
        }

        @Override
        public Result onMousePressed(int mouseButton) {
            return Result.IGNORE;
        }

        @Override
        public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
            return false;
        }

        @Override
        public void onMouseStartHover() {
            super.onMouseStartHover();
            sub.i = index;
        }
    }
}
