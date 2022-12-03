package com.example.demo.controller;

import com.example.demo.service.ITriggerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author elliott
 */
@RestController
@RequestMapping("/trigger")
@Api(tags = "批量保存测试")
public class TriggerController {

    @Resource
    private ITriggerService triggerService;

    @GetMapping
    @ApiOperation("批量保存")
    public String insert() throws InterruptedException {
        triggerService.insert();
        return "success";
    }
}
