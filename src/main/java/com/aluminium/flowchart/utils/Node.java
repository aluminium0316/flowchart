package com.aluminium.flowchart.utils;

import com.aluminium.flowchart.gui.NodeWidget;
import com.aluminium.flowchart.gui.tools.MoveTool;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class Node implements MoveTool.IPosition {
    public ItemorfluidStack[][] input;
    public ItemorfluidStack[][] output;

    public Arrow[] index;

    public int x;
    public int y;
    public NodeWidget widget;

    public int type;

    public Node(List<List<ItemStack>> inputItems, List<List<FluidStack>> inputFluids, List<List<ItemStack>> outputItems, List<List<FluidStack>> outputFluids) {
        this.input = new ItemorfluidStack[inputItems.size() + inputFluids.size()][];
        this.output = new ItemorfluidStack[outputItems.size() + outputFluids.size()][];

        for (int i = 0; i < inputItems.size(); i++) {
            this.input[i] = new ItemorfluidStack[inputItems.get(i).size()];
            for (int j = 0; j < inputItems.get(i).size(); j++) {
                this.input[i][j] = new ItemorfluidStack(inputItems.get(i).get(j), true);
            }
        }
        for (int i = 0; i < inputFluids.size(); i++) {
            int i2 = inputItems.size();
            this.input[i + i2] = new ItemorfluidStack[inputFluids.get(i).size()];
            for (int j = 0; j < inputFluids.get(i).size(); j++) {
                this.input[i + i2][j] = new ItemorfluidStack(inputFluids.get(i).get(j), true);
            }
        }
        for (int i = 0; i < outputItems.size(); i++) {
            this.output[i] = new ItemorfluidStack[outputItems.get(i).size()];
            for (int j = 0; j < outputItems.get(i).size(); j++) {
                this.output[i][j] = new ItemorfluidStack(outputItems.get(i).get(j), false);
            }
        }
        for (int i = 0; i < outputFluids.size(); i++) {
            int i2 = outputItems.size();
            this.output[i + i2] = new ItemorfluidStack[outputFluids.get(i).size()];
            for (int j = 0; j < outputFluids.get(i).size(); j++) {
                this.output[i + i2][j] = new ItemorfluidStack(outputFluids.get(i).get(j), false);
            }
        }

        this.index = new Arrow[this.input.length + 1 + this.output.length];

        this.x = 24;
        this.y = 24;
        this.type = 0;
    }

//    public Node(ItemorfluidStack[] input, ItemorfluidStack[] output) {
//        this.input = input;
//        this.output = output;
//
//        index = new Arrow[this.input.length + 1 + this.output.length];
//
//        this.x = 24;
//        this.y = 24;
//    }

    public Node(ItemorfluidStack input, int[] inputRatio, int[] outputRatio) {
        this.index = new Arrow[this.input.length + 1 + this.output.length];

        this.x = 24;
        this.y = 24;
        this.type = 2;
    }

    public Node(ItemorfluidStack input) {
        this.input = new ItemorfluidStack[][] { { new ItemorfluidStack(input, true) } };
        this.output = new ItemorfluidStack[][] { { new ItemorfluidStack(input, false) } };

        this.index = new Arrow[this.input.length + 1 + this.output.length];

        this.x = 24;
        this.y = 24;
        this.type = 1;
    }

    public ItemorfluidStack[] getArrowIndex(int i) {
        if (i < this.input.length) {
            return input[i];
        }
        return output[i - 1 - this.input.length];
    }

    @Override
    public MoveTool.Position getPos() {
        return new MoveTool.Position(this.x + 12, this.y + 12);
    }

    @Override
    public void setPos(MoveTool.Position pos) {
        this.x = pos.x / 24 * 24;
        this.y = pos.y / 24 * 24;
        widget.updatePosition();
    }
}
