package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.pipeline;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.component.Register;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.mipscontrol.MainController;
import org.jetbrains.annotations.NotNull;

public class WriteBack implements Stage {

    @NotNull
    private final MemoryAccessToWriteBackRegister memWb;

    @NotNull
    private final Register register;

    public WriteBack(@NotNull final MemoryAccessToWriteBackRegister memWb, @NotNull final Register register) {
        this.memWb = memWb;
        this.register = register;
    }

    @Override
    public void run() {
        register.setRegisterWrite(memWb.getRegisterWrite());
        if (memWb.getRegisterWrite() == MainController.RegisterWrite.TRUE) {
            register.setWriteAddress(memWb.getWriteRegisterAddress());
            register.write(memWb.getWriteRegisterData());
        }
    }

    @Override
    public boolean hasInstruction() {
        return memWb.getRegisterWrite() == MainController.RegisterWrite.TRUE;
    }
}
