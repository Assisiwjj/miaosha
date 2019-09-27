package com.miaosha.controller;

import com.miaosha.domain.User;
import com.miaosha.result.CodeMsg;
import com.miaosha.result.Result;
import com.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class SampleController {

    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @RequestMapping("/hello/thymeleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name","Joshua");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTX(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/db/test")
    @ResponseBody
    public Result<String> dbTest(){
        redisTemplate.opsForValue().set("1","123");
        String str=(String) redisTemplate.opsForValue().get("1");
        return Result.success(str);
    }
}
