package com.aluminium.flowchart.gui;

import com.aluminium.flowchart.Flowchart;
import com.cleanroommc.modularui.widget.ScrollWidget;

public class EditorWidget extends ScrollWidget<EditorWidget> {
    @Override
    public Result onMousePressed(int mouseButton) {
        Result res = super.onMousePressed(mouseButton);
        if (res.equals(Result.IGNORE)) {
            return Gui.tool.onMousePressed(mouseButton);
        }
        return res;
    }
}
