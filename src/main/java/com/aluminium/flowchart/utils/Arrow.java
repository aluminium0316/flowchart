package com.aluminium.flowchart.utils;

public class Arrow {
    public ItemorfluidStack[] in;
    public ItemorfluidStack[] out;
    public Node input;
    public Node output;

    public Arrow(Node input, Node output) {
        this.input = input;
        this.output = output;
    }
}
