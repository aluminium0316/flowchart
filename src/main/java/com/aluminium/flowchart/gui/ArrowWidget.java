package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.Flowchart;
import com.aluminium.flowchart.utils.Arrow;
import com.aluminium.flowchart.utils.ItemorfluidStack;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.screen.RichTooltip;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widget.ParentWidget;
import com.cleanroommc.modularui.widget.Widget;
import com.cleanroommc.modularui.widget.sizer.Area;
import net.minecraftforge.fluids.FluidStack;

public class ArrowWidget extends ParentWidget<ArrowWidget> {
    private Arrow arrow;
    IDrawable.DrawableWidget draw;

    public ArrowWidget(Arrow arrow) {
        super();
        this.arrow = arrow;
        this.draw = new IDrawable.DrawableWidget(new Line(arrow.input, arrow.output, Color.rgb(127, 63, 63), Color.rgb(255, 127, 127)));
        this.child(draw);
        RichTooltip tooltip = new RichTooltip().parent(this);
        tooltip.add("Input: ");
        for (ItemorfluidStack item : this.arrow.in) {
            if (item.item != null) {
                tooltip.add(new ItemDrawable(item.item));
            }
            else {
                tooltip.add(new FluidDrawable(item.fluid));
            }
        }
        tooltip.addLine("");
        tooltip.add("Output: ");
        for (ItemorfluidStack item : this.arrow.out) {
            if (item.item != null) {
                tooltip.add(new ItemDrawable(item.item));
            }
            else {
                tooltip.add(new FluidDrawable(item.fluid));
            }
        }
        this.tooltip(tooltip);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
    }

//        int x = this.getContext().getMouseX() + this.getArea().x;
//        int y = this.getContext().getMouseY() + this.getArea().y;
//
//        int x1 = this.arrow.input.x;
//        int y1 = this.arrow.input.y;
//
//        int x2 = this.arrow.output.x;
//        int y2 = this.arrow.output.y;
//
//        double len = Math.sqrt((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
//        double distance = Math.abs((y2 - y1) * x - (x2 - x1) * y + x2 * y1 - x1 * y2) / len;
//        double a = ((y2 - y1) * (y - y1) - (x2 - x1) * (x - x1));
//
//        return distance < 2.0 && 0.0 < a && a < len;

    @Override
    public Area getArea() {
        Area area = super.getArea();
        area.x(Integer.min(arrow.input.x, arrow.output.x) + 12);
        area.y(Integer.min(arrow.input.y, arrow.output.y) + 12);
        area.w(Math.abs(arrow.input.x - arrow.output.x));
        area.h(Math.abs(arrow.input.y - arrow.output.y));
        this.pos(area.x, area.y);
        this.draw.pos(-area.x, -area.y);
        return area;
    }
}

class FluidDrawable implements IDrawable {
    FluidStack item;

    FluidDrawable(FluidStack fluid) {
        this.item = fluid;
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        GuiDraw.drawFluidTexture(this.item, x, y, width, height, context.getCurrentDrawingZ());
    }
}
