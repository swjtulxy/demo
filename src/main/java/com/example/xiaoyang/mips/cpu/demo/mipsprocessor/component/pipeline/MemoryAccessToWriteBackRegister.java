package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import org.jetbrains.annotations.NotNull;

public class MemoryAccessToWriteBackRegister implements PipelineRegister {

    @NotNull
    private final MemoryAccess memoryAccess;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

    private int memoryReadData;
    private int aluResult;
    private int writeRegisterAddress;

    public MemoryAccessToWriteBackRegister(@NotNull final MemoryAccess memoryAccess) {
        this.memoryAccess = memoryAccess;
    }

    @Override
    public void update() {
        registerWrite = memoryAccess.getRegisterWrite();
        memoryToRegister = memoryAccess.getMemoryToRegister();
        memoryReadData = memoryAccess.getMemoryReadData();
        aluResult = memoryAccess.getAluResult();
        writeRegisterAddress = memoryAccess.getWriteRegisterAddress();
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    public int getMemoryReadData() {
        return memoryReadData;
    }

    public int getAluResult() {
        return aluResult;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }

    public int getWriteRegisterData() {
        if (getMemoryToRegister() == MainController.MemoryToRegister.FROM_ALU_RESULT)
            return getAluResult();
        else
            return getMemoryReadData();
    }
}
