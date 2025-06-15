package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.gui.tools.MoveTool;
import com.cleanroommc.modularui.api.drawable.IDrawable;
import com.cleanroommc.modularui.drawable.GuiDraw;
import com.cleanroommc.modularui.screen.viewport.GuiContext;
import com.cleanroommc.modularui.theme.WidgetTheme;
import com.cleanroommc.modularui.utils.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class Line implements IDrawable {

    public MoveTool.IPosition pos0;
    public MoveTool.IPosition pos1;
    private int color0;
    private int color1;

    public Line(MoveTool.IPosition pos0, MoveTool.IPosition pos1, int color0, int color1) {
        this.pos0 = pos0;
        this.pos1 = pos1;
        this.color1 = color1;
        this.color0 = color0;
    }

    public Line(MoveTool.IPosition pos0, MoveTool.IPosition pos1) {
        this(pos0, pos1, Color.rgb(255, 0, 0), Color.rgb(0, 255, 255)   );
    }

    @Override
    public void draw(GuiContext context, int x, int y, int width, int height, WidgetTheme widgetTheme) {
        if (pos0 == null || pos1 == null) return;

        MoveTool.Position pos2 = pos0.getPos();
        MoveTool.Position pos3 = pos1.getPos();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(pos2.x, pos2.y, 0.0f).color(Color.getRed(color0), Color.getGreen(color0), Color.getBlue(color0), Color.getAlpha(color0)).endVertex();
        bufferbuilder.pos(pos3.x, pos3.y, 0.0f).color(Color.getRed(color1), Color.getGreen(color1), Color.getBlue(color1), Color.getAlpha(color1)).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
