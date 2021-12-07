package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline;

public interface Stage {

    void run();

    boolean hasInstruction();
}
