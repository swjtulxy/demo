package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.HazardDetectionUnit;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.memory.Memory;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.ProgramCounter;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class InstructionFetch implements Stage {

    @NotNull
    private final Memory instructionMemory;
    @NotNull
    private final ProgramCounter programCounter = new ProgramCounter();
    @NotNull
    private Instruction currentInstruction = Instruction.NOP;
    @Nullable
    private InstructionDecode instructionDecode;
    @Nullable
    private HazardDetectionUnit hazardDetectionUnit;

    public InstructionFetch(@NotNull final Memory instructionMemory) {
        this.instructionMemory = instructionMemory;
        this.instructionMemory.setMemoryRead(MainController.MemoryRead.TRUE);
    }

    public void setInstructionDecode(@NotNull final InstructionDecode instructionDecode) {
        this.instructionDecode = instructionDecode;
    }

    public void setHazardDetectionUnit(@NotNull final HazardDetectionUnit hazardDetectionUnit) {
        this.hazardDetectionUnit = hazardDetectionUnit;
    }

    public int getProgramCounter() {
        return programCounter.getCounter();
    }

    @NotNull
    public Instruction getInstruction() {
        return currentInstruction;
    }

    @Override
    public void run() {
        assert hazardDetectionUnit != null;
        assert instructionDecode != null;

        var nextAddress = programCounter.getCounter();
        if (!hazardDetectionUnit.mustStall()) {
            if (instructionDecode.shouldBranch()) nextAddress = instructionDecode.getBranchAdderResult();
            else nextAddress = programCounter.getCounter() + 4;
        }

        programCounter.setCounter(nextAddress);

        updateCurrentInstruction(nextAddress - 4);
    }

    private void updateCurrentInstruction(final int currentAddress) {
        instructionMemory.setAddress(currentAddress);
        currentInstruction = instructionMemory.readInstruction();
    }

    @Override
    public boolean hasInstruction() {
        return currentInstruction != Instruction.NOP || programCounter.getCounter() <= instructionMemory.getWrittenAddresses().size() * 4;
    }
}
