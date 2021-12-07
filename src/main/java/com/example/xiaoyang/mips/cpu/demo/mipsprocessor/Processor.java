package com.example.xiaoyang.mips.cpu.demo.mipsprocessor;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.*;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.memory.Memory;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline.*;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public final class Processor {

    @NotNull
    private final Collection<Stage> stages = new ArrayList<>();
    @NotNull
    private final Collection<PipelineRegister> pipelineRegisters = new ArrayList<>();
    @NotNull
    private final Collection<ProcessorLogger> loggers = new ArrayList<>();
    @NotNull
    private final InstructionFetchToInstructionDecodeRegister ifId;
    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private final MemoryAccessToWriteBackRegister memWb;
    @NotNull
    private final InstructionDecode instructionDecode;
    @NotNull
    private final MemoryAccess memoryAccess;

    private Processor(
            @NotNull final Stage instructionFetch,
            @NotNull final InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull final InstructionDecode instructionDecode,
            @NotNull final InstructionDecodeToExecutionRegister idExe,
            @NotNull final Stage execution,
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            @NotNull final MemoryAccess memoryAccess,
            @NotNull final MemoryAccessToWriteBackRegister memWb,
            @NotNull final Stage writeBack
    ) {
        this.ifId = ifId;
        this.idExe = idExe;
        this.exeMem = exeMem;
        this.memWb = memWb;
        this.instructionDecode = instructionDecode;
        this.memoryAccess = memoryAccess;
        stages.add(instructionFetch);
        stages.add(instructionDecode);
        stages.add(execution);
        stages.add(memoryAccess);
        stages.add(writeBack);
        pipelineRegisters.add(ifId);
        pipelineRegisters.add(idExe);
        pipelineRegisters.add(exeMem);
        pipelineRegisters.add(memWb);
    }

    public void run() {
        while (hasUnfinishedInstructions()) {
            stages.forEach(Stage::run);
            pipelineRegisters.forEach(PipelineRegister::update);
            loggers.forEach(printer -> printer.onClockCycleFinished(ifId, instructionDecode, idExe, exeMem, memoryAccess, memWb));
        }
    }

    private boolean hasUnfinishedInstructions() {
        for (final var stage : stages)
            if (stage.hasInstruction()) return true;
        return false;
    }

    public void addLogger(@NotNull final ProcessorLogger logger) {
        loggers.add(logger);
    }

    public static class Builder {

        @NotNull
        private final Memory instructionMemory = new Memory();
        @NotNull
        private Register register = new Register();
        @NotNull
        private Memory dataMemory = new Memory();

        @NotNull
        public Builder setInstructions(@NotNull final Iterable<Instruction> instructions) {
            instructionMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
            var address = 0x00;
            for (final var instruction : instructions) {
                instructionMemory.setAddress(address);
                instructionMemory.write(instruction);
                address += 4;
            }
            instructionMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
            return this;
        }

        @NotNull
        public Builder setRegister(@NotNull final Register register) {
            this.register = register;
            return this;
        }

        @NotNull
        public Builder setRegisterValues(@NotNull final Map<Integer, Integer> values) {
            register.setRegisterWrite(MainController.RegisterWrite.TRUE);
            values.forEach((key, value) -> {
                if (key != 0) {
                    register.setWriteAddress(key);
                    register.write(value);
                }
            });
            register.setRegisterWrite(MainController.RegisterWrite.FALSE);
            return this;
        }

        @NotNull
        public Builder setDataMemory(@NotNull final Memory dataMemory) {
            this.dataMemory = dataMemory;
            return this;
        }

        @NotNull
        public Builder setDataMemoryValues(@NotNull final Map<Integer, Integer> values) {
            dataMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
            values.forEach((key, value) -> {
                dataMemory.setAddress(key);
                dataMemory.write(value);
            });
            dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
            return this;
        }

        @NotNull
        public Processor build() {
            final var instructionFetch = new InstructionFetch(instructionMemory);
            final var ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
            final var instructionDecode = new InstructionDecode(ifId, new MainController(), register, new Alu());
            final var idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
            final var execution = new Execution(idExe, new Alu());
            final var exeMem = new ExecutionToMemoryAccessRegister(execution);
            final var memoryAccess = new MemoryAccess(exeMem, dataMemory);
            final var memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
            final var writeBack = new WriteBack(memWb, register);
            final var hazardDetectionUnit = new HazardDetectionUnit(ifId, idExe, exeMem, memWb);

            instructionFetch.setInstructionDecode(instructionDecode);
            instructionFetch.setHazardDetectionUnit(hazardDetectionUnit);
            ifId.setInstructionDecode(instructionDecode);
            ifId.setHazardDetectionUnit(hazardDetectionUnit);
            instructionDecode.setHazardDetectionUnit(hazardDetectionUnit);
            execution.setForwardingUnit(new ForwardingUnit(idExe, exeMem, memWb));
            execution.setExecutionToMemoryAccessRegister(exeMem);
            execution.setMemoryAccessToWriteBackRegister(memWb);

            return new Processor(
                    instructionFetch,
                    ifId,
                    instructionDecode,
                    idExe,
                    execution,
                    exeMem,
                    memoryAccess,
                    memWb,
                    writeBack
            );
        }
    }
}
