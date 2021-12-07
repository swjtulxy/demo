package com.example.xiaoyang.mips.cpu.demo.mipsprocessor.api;

import org.springframework.web.client.RestTemplate;

public class RestUtil {

    private static RestTemplate restTemplate;

    static {
        // 使用SpringUtil 设置 RestUtil 的静态属性 restTemplate
        restTemplate = SpringUtil.getBean(RestTemplate.class);
    }

    private RestUtil() {
    }

    public static <T> T get(String url, Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }
}
