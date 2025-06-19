package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.Flowchart;
import com.aluminium.flowchart.utils.Arrow;
import com.aluminium.flowchart.utils.Node;
import com.cleanroommc.modularui.widget.ScrollWidget;

import java.util.ArrayList;
import java.util.List;

public class EditorWidget extends ScrollWidget<EditorWidget> {
    List<Node> nodes;
    List<Arrow> arrows;

    public EditorWidget(List<Node> nodes, List<Arrow> arrows) {
        this.nodes = nodes;
        this.arrows = arrows;
    }

    @Override
    public Result onMousePressed(int mouseButton) {
        Result res = super.onMousePressed(mouseButton);
        if (res.equals(Result.IGNORE)) {
            return Gui.tool.onMousePressed(mouseButton, nodes, arrows);
        }
        return res;
    }
}
