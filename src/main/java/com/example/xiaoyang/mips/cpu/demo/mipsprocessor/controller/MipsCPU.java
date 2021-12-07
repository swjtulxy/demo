package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.controller;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.Processor;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.ProcessorLogger;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.Instruction;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VaInfos;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MipsCPU {

    public String mipsDemo(List<Instruction> instructions){
        @NotNull final var initRegisterValues = VaInfos.registerInfo;
        Map<Integer, Integer> initDataMemoryValues = new HashMap<>();
        for(int i= 0; i<256*256; i++){
            initDataMemoryValues.put(i, 0);
        }

        // Create a MIPS pipelined processor with the given instructions, initial register values and data memory values.
        @NotNull final var processor = new Processor.Builder()
                .setInstructions(instructions)
                .setRegisterValues(initRegisterValues)
                .setDataMemoryValues(initDataMemoryValues)
                .build();

        // Create and add a logger to log the variables in the processor.
        @NotNull final var logger = new ProcessorLogger();
        processor.addLogger(logger);

        // Start the pipeline.
        processor.run();

        // Get and print the log.
        logger.getLog();
        return logger.getLog();
    }
}
