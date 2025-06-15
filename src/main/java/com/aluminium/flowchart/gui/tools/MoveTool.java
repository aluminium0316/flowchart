package com.aluminium.flowchart.gui.tools;

public class MoveTool {

    public interface IPosition {
        Position getPos();
        void setPos(Position pos);
    }

    public static class Position implements IPosition {
        public int x;
        public int y;

        public Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public Position getPos() {
            return this;
        }

        @Override
        public void setPos(Position pos) {
            this.x = pos.x;
            this.y = pos.y;
        }
    }
}
