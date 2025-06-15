package com.aluminium.flowchart.utils;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class ItemorfluidStack {

    public ItemStack item;
    public FluidStack fluid;
    public boolean isInput;

    public ItemorfluidStack(ItemStack item, boolean isInput) {
        this.item = item;
        this.isInput = isInput;
    }

    public ItemorfluidStack(FluidStack item, boolean isInput) {
        this.fluid = item;
        this.isInput = isInput;
    }
}
