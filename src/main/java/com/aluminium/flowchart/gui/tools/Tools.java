package com.aluminium.flowchart.gui.tools;

import com.aluminium.flowchart.gui.EditorWidget;
import com.aluminium.flowchart.gui.Gui;
import com.aluminium.flowchart.gui.Line;
import com.aluminium.flowchart.gui.NodeWidget;
import com.aluminium.flowchart.utils.Arrow;
import com.aluminium.flowchart.utils.ItemorfluidStack;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.api.widget.Interactable;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widget.ScrollWidget;
import com.cleanroommc.modularui.widget.WidgetTree;

import java.util.ArrayList;
import java.util.List;

public class Tools {
    public MoveTool moveTool;
    public EnabledTool enabled = EnabledTool.NONE;
    public ModularGuiContext ctx;
    public Node selection;
    public int subSelection;
    public Line line;
    public EditorWidget node;

    public Interactable.Result onMousePressed(int mouseButton, List<Node> nodes, List<Arrow> arrows) {
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
                ItemorfluidStack[] in = selection.getArrowIndex(this.subSelection);

                Node other = new Node(in[0]);
                int x = node.getContext().getMouseX();
                int y = node.getContext().getMouseY();

                other.x = x / 24 * 24;
                other.y = y / 24 * 24;
                nodes.add(other);

                Arrow arrow = new Arrow(selection, other);
                arrow.in = in;
                arrow.out = other.getArrowIndex(0);
                if (!arrow.in[0].isInput && arrow.out[0].isInput) {
                    arrows.add(arrow);
                }

                selection.index[this.subSelection] = arrow;
                other.index[0] = arrow;
                arrow.inputSub = this.subSelection;
                arrow.outputSub = 0;
                arrows.removeIf(arrow1 -> arrow1.input.index[arrow1.inputSub] != arrow1);
                arrows.removeIf(arrow1 -> arrow1.output.index[arrow1.outputSub] != arrow1);

//                Gui.log.clearText();
//                for (Node node1 : nodes) {
//                    StringBuilder str = new StringBuilder();
//                    str.append(node1.toString()).append(": ");
//                    for (Arrow arrow1 : node1.index) {
//                        if (arrow1 == null) {
//                            str.append("null");
//                        }
//                        else {
//                            str.append(arrow1);
//                        }
//                        str.append(", ");
//                    }
//                    Gui.log.getRichText().addLine(str.toString());
//                }
//                Gui.log.getRichText().addLine("------------------------");
//                for (Arrow arrow1 : arrows) {
//                    StringBuilder str = new StringBuilder();
//                    str.append(arrow1.toString())
//                            .append(": ")
//                            .append(arrow1.input);
//                    Gui.log.getRichText().addLine(str.toString());
//                }
//                Gui.log.markDirty();

                Gui.renderNodes(node);
                Gui.renderArrows(node);
                WidgetTree.resize(node);

                subSelection = 2;
                selection = other;
                line.pos0 = other;
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
                if (!arrow.in[0].isInput && arrow.out[0].isInput) {
                    arrows.add(arrow);
                }

                selection.index[this.subSelection] = arrow;
                other.index[subSelection] = arrow;
                arrow.inputSub = this.subSelection;
                arrow.outputSub = subSelection;
                arrows.removeIf(arrow1 -> arrow1.input.index[arrow1.inputSub] != arrow1);
                arrows.removeIf(arrow1 -> arrow1.output.index[arrow1.outputSub] != arrow1);

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
