package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline.ExecutionToMemoryAccessRegister;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline.MemoryAccessToWriteBackRegister;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.Instruction;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.OpCode;
import org.jetbrains.annotations.NotNull;


public class HazardDetectionUnit {

    @NotNull
    private final InstructionFetchToInstructionDecodeRegister ifId;
    @NotNull
    private final InstructionDecodeToExecutionRegister idExe;
    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;
    @NotNull
    private final MemoryAccessToWriteBackRegister memWb;

    public HazardDetectionUnit(
            @NotNull final InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull final InstructionDecodeToExecutionRegister idExe,
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            @NotNull final MemoryAccessToWriteBackRegister memWb
    ) {
        this.ifId = ifId;
        this.idExe = idExe;
        this.exeMem = exeMem;
        this.memWb = memWb;
    }

    public boolean mustStall() {
        final var fetchedInstruction = ifId.getInstruction();
        return isDataHazardWithLoadWord(fetchedInstruction) || isDataHazardWithBranch(fetchedInstruction);
    }

    private boolean isDataHazardWithLoadWord(@NotNull final Instruction fetchedInstruction) {
        return idExe.getMemoryRead() == MainController.MemoryRead.TRUE
                && (idExe.getRt() == fetchedInstruction.getRs() || idExe.getRt() == fetchedInstruction.getRt());
    }

    private boolean isDataHazardWithBranch(@NotNull final Instruction fetchedInstruction) {
        return isBranch(fetchedInstruction.getOpCode())
                && (isDataHazardWithBranchInRs(fetchedInstruction) || isDataHazardWithBranchInRt(fetchedInstruction));
    }

    private boolean isDataHazardWithBranchInRs(@NotNull final Instruction fetchedInstruction) {
        return fetchedInstruction.getRs() != 0
                && (fetchedInstruction.getRs() == idExe.getWriteRegisterAddress()
                || fetchedInstruction.getRs() == exeMem.getWriteRegisterAddress()
                || fetchedInstruction.getRs() == memWb.getWriteRegisterAddress());
    }

    private boolean isDataHazardWithBranchInRt(@NotNull final Instruction fetchedInstruction) {
        return fetchedInstruction.getRt() != 0
                && (fetchedInstruction.getRt() == idExe.getWriteRegisterAddress()
                || fetchedInstruction.getRt() == exeMem.getWriteRegisterAddress()
                || fetchedInstruction.getRt() == memWb.getWriteRegisterAddress());
    }

    private boolean isBranch(@NotNull final OpCode opCode) {
        return opCode == OpCode.BRANCH_ON_EQUAL || opCode == OpCode.BRANCH_ON_NOT_EQUAL;
    }
}
