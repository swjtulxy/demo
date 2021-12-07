package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo;

import com.fasterxml.jackson.core.JsonToken;
import lombok.Data;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class VirtualMemInfo {
    private String va;
    private String pageNumber;
    //button show page detail
    private String offset;
    private String pa;
    private String data;
    private String tlbHit;
    private String pageTableTrue;
    //is in page table
    private String pageFaultRate;
    //is there a page fault
    private String tlbHitRate;
    private List tlb = new LinkedList();
}
