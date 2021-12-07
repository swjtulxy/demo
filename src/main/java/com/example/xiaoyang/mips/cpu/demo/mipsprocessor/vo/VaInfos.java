package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo;

import lombok.Data;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class VaInfos {
    public static Map<Integer, VirtualMemInfo> virtualMemInfos = new HashMap<>();
    public static String pageFaultRate = "0";
    public static String tlbHitRate = "0";
    public static List tlb = new LinkedList();
    public static Map<Integer, Integer> registerInfo = new HashMap<>();
}
