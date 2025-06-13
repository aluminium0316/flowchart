package com.aluminium.flowchart.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class Node {
    public ItemStack[] inputItems;
    public FluidStack[] inputFluids;
    public ItemStack[] outputItems;
    public FluidStack[] outputFluids;

    public int[] len;
    public Arrow[] index;

    public int x;
    public int y;

    public Node(ItemStack[] inputItems, FluidStack[] inputFluids, ItemStack[] outputItems, FluidStack[] outputFluids) {
        this.inputFluids = inputFluids;
        this.inputItems = inputItems;
        this.outputFluids = outputFluids;
        this.outputItems = outputItems;
        len = new int[]{
                inputItems.length,
                inputItems.length + inputFluids.length,
                inputItems.length + inputFluids.length + outputItems.length,
                inputItems.length + inputFluids.length + outputItems.length + outputFluids.length
        };
        index = new Arrow[len[3]];

        this.x = 24;
        this.y = 24;
    }
}
