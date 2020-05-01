package com.cceking.rtc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EchoController {
    @RequestMapping("/")
    String index() {
        return "Hello World!";
    }
}
