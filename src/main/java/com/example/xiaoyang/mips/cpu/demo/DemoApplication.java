package com.example.xiaoyang.mips.cpu.demo;

import com.example.xiaoyang.mips.cpu.demo.mipsprocessor.vo.VaInfos;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
        VaInfos.registerInfo.put(0,4);
        VaInfos.registerInfo.put(1,0);
        VaInfos.registerInfo.put(2,3);
        VaInfos.registerInfo.put(3,1);
        VaInfos.registerInfo.put(4,7);
        VaInfos.registerInfo.put(5,10);
        VaInfos.registerInfo.put(6,20);
        VaInfos.registerInfo.put(7,11);
        VaInfos.registerInfo.put(8,5);
        VaInfos.registerInfo.put(9,1);


    }

}
