package top.fanzhengke.emos.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import top.fanzhengke.emos.common.util.Result;
import top.fanzhengke.emos.controller.form.TestSayHelloForm;

import javax.validation.Valid;

@Api("测试接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("最简单的测试方法")
    @PostMapping("/sayHello")
    public Result sayHello(@Valid @RequestBody TestSayHelloForm form) {
        return Result.ok().put("data", "Hello," + form.getName());
    }

    @ApiOperation("测试权限认证的方法")
    @GetMapping("/sayHello")
    @RequiresPermissions(value = {"ROOT","USER:ADD"},logical = Logical.OR)
    public Result sayHelloWorld() {
        return Result.ok().put("data", "Hello World");
    }
}
