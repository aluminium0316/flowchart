package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.utils.ItemorfluidStack;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.WidgetTree;
import com.cleanroommc.modularui.widget.scroll.HorizontalScrollData;
import com.cleanroommc.modularui.widget.scroll.VerticalScrollData;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.slot.PhantomItemSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import java.util.function.Consumer;

public class NodeWidget extends ScrollWidget<NodeWidget> {
    private final Consumer<Int> c;
    public Node node;
    private Int sub = new Int(0);

    public static class Int {
        public int i;
        public Int(int i) {
            this.i = i;
        }
    }

    private NodeWidget(VerticalScrollData data, Consumer<Int> c) {
        super(data);
        this.getScrollArea().setScrollData(new HorizontalScrollData(false, 1));
        this.c = c;
    }

    public NodeWidget(VerticalScrollData data, Consumer<Int> c, Node node) {
        this(data, c);
        this.node = node;
        this.node();
        this.pos(this.node.x, this.node.y);
    }

    public void updatePosition() {
        this.pos(this.node.x, this.node.y);
        WidgetTree.resize(this.getParent());
    }

    @Override
    public Result onMousePressed(int mouseButton) {
        Result res = super.onMousePressed(mouseButton);
        if (res.equals(Result.IGNORE)) {
            c.accept(sub);
            return Result.SUCCESS;
        }
        return res;
    }

    private void node() {
        int i = 0;
        int maxJ = 0;
        for (ItemorfluidStack[] items : node.input) {
            for (int j = 0; j < items.length; j++) {
                this.child(ItemorfluidUi.slot(i, j, items[j], sub));
                if (j > maxJ) maxJ = j;
            }
            i++;
        }
        i++;
        for (ItemorfluidStack[] items : node.output) {
            for (int j = 0; j < items.length; j++) {
                this.child(ItemorfluidUi.slot(i, j, items[j], sub));
                if (j > maxJ) maxJ = j;
            }
            i++;
        }

        this.getScrollArea().getScrollY().setScrollSize(18*i + 8);
        this.getScrollArea().getScrollX().setScrollSize(18*maxJ + 24);
    }
}
