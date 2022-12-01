package com.example.demo.controller;

import com.example.demo.service.ITriggerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author elliott
 */
@RestController
@RequestMapping("/trigger")
public class TriggerController {

    @Resource
    private ITriggerService triggerService;

    @GetMapping
    public String insert() throws InterruptedException {
        triggerService.insert2();
        return "success";
    }
}
