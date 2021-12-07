package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.api.RestUtil;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.memory.Memory;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VaInfos;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VirtualMemInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MemoryAccess implements Stage {

    @NotNull
    private final ExecutionToMemoryAccessRegister exeMem;

    @NotNull
    private final Memory dataMemory;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

    private int memoryReadData;
    private int aluResult;
    private int writeRegisterAddress;

    public MemoryAccess(
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            @NotNull final Memory dataMemory
    ) {
        this.exeMem = exeMem;
        this.dataMemory = dataMemory;
    }

    @Override
    public void run() {
        passProperties();
        accessMemory();
    }

    @Override
    public boolean hasInstruction() {
        return exeMem.getRegisterWrite() == MainController.RegisterWrite.TRUE
                || exeMem.getMemoryWrite() == MainController.MemoryWrite.TRUE
                || exeMem.getMemoryRead() == MainController.MemoryRead.TRUE
                || exeMem.getBranch() == MainController.Branch.TRUE;
    }

    private void passProperties() {
        registerWrite = exeMem.getRegisterWrite();
        memoryToRegister = exeMem.getMemoryToRegister();
        aluResult = exeMem.getAluResult();
        writeRegisterAddress = exeMem.getWriteRegisterAddress();
    }

    private void accessMemory() {
        dataMemory.setAddress(exeMem.getAluResult());
        dataMemory.setMemoryRead(exeMem.getMemoryRead());
        dataMemory.setMemoryWrite(exeMem.getMemoryWrite());
        if (exeMem.getMemoryRead() == MainController.MemoryRead.TRUE){
            System.out.println("111111111111");

            System.out.println("exeMem.getAluResult()"+exeMem.getAluResult());
            String url = "http://127.0.0.1:9999/mem/addr="+exeMem.getAluResult();
            VirtualMemInfo vmInfo = RestUtil.get(url, VirtualMemInfo.class);
            VaInfos.virtualMemInfos.put(exeMem.getAluResult(), vmInfo);
            VaInfos.pageFaultRate = vmInfo.getPageFaultRate().length()<4?vmInfo.getPageFaultRate():vmInfo.getPageFaultRate().substring(0,4);
            VaInfos.tlbHitRate = vmInfo.getTlbHitRate().length()<4?vmInfo.getTlbHitRate():vmInfo.getTlbHitRate().substring(0,4);
            VaInfos.tlb = vmInfo.getTlb();
            System.out.println(vmInfo);
            memoryReadData = dataMemory.read();
        }
        else
            memoryReadData = 0;
        if (exeMem.getMemoryWrite() == MainController.MemoryWrite.TRUE){
            System.out.println("222222222222");
            dataMemory.write(exeMem.getRegisterData2());}
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

    @NotNull
    public Set<Integer> getWrittenDataMemoryAddresses() {
        return dataMemory.getWrittenAddresses();
    }

    public int readDataMemory(final int address) {
        dataMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        dataMemory.setAddress(address);
        final var data = dataMemory.read();
        dataMemory.setMemoryRead(MainController.MemoryRead.FALSE);
        return data;
    }
}
