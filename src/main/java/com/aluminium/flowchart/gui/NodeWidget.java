package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.api.layout.IViewport;
import com.cleanroommc.modularui.api.layout.IViewportStack;
import com.cleanroommc.modularui.api.widget.IDraggable;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.HoveredWidgetList;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widget.sizer.Area;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

public class NodeWidget<W extends NodeWidget<W>> extends ScrollWidget<W> {
    private final Runnable c;
    public Node node;

    public NodeWidget(VerticalScrollData data, Runnable c) {
        super(data);
        this.c = c;
    }

    public NodeWidget(VerticalScrollData data, Runnable c, Node node) {
        this(data, c);
        this.node = node;
        this.node();
    }


    @Override
    public Result onMousePressed(int mouseButton) {
        Result res = super.onMousePressed(mouseButton);
        if (res.equals(Result.IGNORE)) {
            c.run();
            return Result.SUCCESS;
        }
        return res;
    }

    private void node() {
        int i = 0;
        for (ItemStack item : node.inputItems) {
            ItemStackHandler itemHandler = new ItemStackHandler();
            itemHandler.setStackInSlot(0, item);
            this.child(new PhantomItemSlot() {
                @Override
                public Result onMousePressed(int mouseButton) {
                    return Result.IGNORE;
                }

                @Override
                public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
                    return false;
                }
            }
                    .pos(1, i * 18)
                    .slot(itemHandler, 0));
            i++;
        }
        for (FluidStack item : node.inputFluids) {
            FluidSlotSyncHandler fluid = new FluidSlotSyncHandler(new FluidTank(item, item.amount));
            fluid.phantom(true);
            fluid.updateCacheFromSource(true);

            this.child(new FluidSlot() {
                @Override
                public Result onMousePressed(int mouseButton) {
                    return Result.IGNORE;
                }

                @Override
                public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
                    return false;
                }
            }
                    .pos(1, i * 18)
                    .syncHandler(fluid));
            i++;
        }
        i++;
        for (ItemStack item : node.outputItems) {
            ItemStackHandler itemHandler = new ItemStackHandler();
            itemHandler.setStackInSlot(0, item);
            this.child(new PhantomItemSlot() {
                @Override
                public Result onMousePressed(int mouseButton) {
                    return Result.IGNORE;
                }

                @Override
                public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
                    return false;
                }
            }
                    .pos(1, i * 18)
                    .slot(itemHandler, 0));
            i++;
        }
        for (FluidStack item : node.outputFluids) {
            FluidSlotSyncHandler fluid = new FluidSlotSyncHandler(new FluidTank(item, item.amount));
            fluid.phantom(true);
            fluid.updateCacheFromSource(true);

            this.child(new FluidSlot() {
                @Override
                public Result onMousePressed(int mouseButton) {
                    return Result.IGNORE;
                }

                @Override
                public boolean onMouseScroll(ModularScreen.UpOrDown scrollDirection, int amount) {
                    return false;
                }
            }
                    .pos(1, i * 18)
                    .syncHandler(fluid));
            i++;
        }

        this.getScrollArea().getScrollY().setScrollSize(18*i + 8);
    }
}
