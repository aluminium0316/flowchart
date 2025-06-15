package com.aluminium.flowchart.gui.tools;

import com.aluminium.flowchart.gui.EditorWidget;
import com.aluminium.flowchart.gui.Line;
import com.aluminium.flowchart.gui.NodeWidget;
import com.aluminium.flowchart.utils.Arrow;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widget.ScrollWidget;

import java.util.ArrayList;

public class Tools {
    public MoveTool moveTool;
    public EnabledTool enabled = EnabledTool.NONE;
    public ModularGuiContext ctx;
    public Node selection;
    public int subSelection;
    public Line line;
    public EditorWidget node;

    public Interactable.Result onMousePressed(int mouseButton) {
        if (enabled == EnabledTool.MOVETOOL) {
            if (selection != null) {
                selection.setPos(new MoveTool.Position(ctx.getMouseX(), ctx.getMouseY()));
                selection = null;
                line.pos0 = null;
                return Interactable.Result.SUCCESS;
            }
        }
        if (enabled == EnabledTool.ARROWTOOL) {
            if (selection != null) {
                selection = null;
                line.pos0 = null;
                return Interactable.Result.SUCCESS;
            }
        }

        selection = null;
        line.pos0 = null;
        return Interactable.Result.STOP;
    }

    public void select(Node other, ArrayList<Arrow> arrows, int subSelection) {
        if (enabled == EnabledTool.ARROWTOOL) {
            if (selection != null) {
                Arrow arrow = new Arrow(selection, other);
                arrow.in = selection.getArrowIndex(this.subSelection);
                arrow.out = other.getArrowIndex(subSelection);
                if (arrow.in[0].isInput && !arrow.out[0].isInput) {
                    arrows.add(arrow);
                }
                selection = null;
                line.pos0 = null;
                return;
            }
        }
        this.subSelection = subSelection;
        this.selection = other;
        line.pos0 = other;
    }

    public enum EnabledTool {
        NONE,
        MOVETOOL,
        ARROWTOOL,
    }
}
