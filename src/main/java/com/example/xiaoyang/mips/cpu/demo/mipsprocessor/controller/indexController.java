package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.controller;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.signal.Instruction;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.User;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VaInfos;
import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VirtualMemInfo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Controller
public class indexController {
    @GetMapping("/index")
    public String index(Model model){
        model.addAttribute("pageFaultRate", VaInfos.pageFaultRate);
        model.addAttribute("tlbHitRate", VaInfos.tlbHitRate);
        model.addAttribute("tlb", VaInfos.tlb);
        model.addAttribute("user", new User());
        model.addAttribute("runningResult", "default");
        for (int i = 0; i < 10; i++){
            model.addAttribute("register"+i, VaInfos.registerInfo.get(i));
        }
        List<VirtualMemInfo> virtualMemInfos = new LinkedList<>();
        VaInfos.virtualMemInfos.forEach((k,v)->{
            virtualMemInfos.add(v);
        });
        model.addAttribute("virtualMemInfos", virtualMemInfos);
        return "index";
    }

    @RequestMapping("/run")
    public String Login(@ModelAttribute User user, Model model){
        System.out.println("程序运行");
        String code = user.getUserName();
        StringBuilder code_new = new StringBuilder();
        for(int i = 0; i<code.length(); i++){
            if(code.charAt(i)=='0'||code.charAt(i)=='1'||code.charAt(i)==';'){
                code_new.append(code.charAt(i));
            }
        }
        List<Instruction> instructions = new ArrayList<>();

        String str = code_new.toString();
        String[] inputInstructions = str.split(";");

        for (int i = 0; i<inputInstructions.length; i++){
            System.out.println(inputInstructions[i].length()+inputInstructions[i]);
            if (inputInstructions[i].isEmpty()){
                continue;
            }else {
                instructions.add(new Instruction(inputInstructions[i]));
            }
        }
        MipsCPU mipsCPU = new MipsCPU();
        String runningResult = mipsCPU.mipsDemo(instructions);


        model.addAttribute("pageFaultRate", VaInfos.pageFaultRate);
        model.addAttribute("tlbHitRate", VaInfos.tlbHitRate);
        model.addAttribute("tlb", VaInfos.tlb);
        model.addAttribute("user", new User());
        model.addAttribute("runningResult", runningResult);
        for (int i = 0; i < 10; i++){
            model.addAttribute("register"+i, VaInfos.registerInfo.get(i));
        }
        List<VirtualMemInfo> virtualMemInfos = new LinkedList<>();
        VaInfos.virtualMemInfos.forEach((k,v)->{
            virtualMemInfos.add(v);
        });
        model.addAttribute("virtualMemInfos", virtualMemInfos);
        return "index";
    }
}
