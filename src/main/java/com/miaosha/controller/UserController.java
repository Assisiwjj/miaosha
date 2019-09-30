package com.miaosha.controller;

import com.miaosha.domain.MiaoshaUser;
import com.miaosha.result.Result;
import com.miaosha.service.GoodsService;
import com.miaosha.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    GoodsService goodsService;

    private static Logger log = LoggerFactory.getLogger(UserController.class);

    //windows端口连接数限制，并发20000以上会出错
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(Model model, MiaoshaUser user){
//        model.addAttribute("user", user);
        return Result.success(user);
    }

}
