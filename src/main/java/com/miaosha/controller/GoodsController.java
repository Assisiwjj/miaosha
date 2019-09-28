package com.miaosha.controller;

import com.miaosha.domain.MiaoshaUser;

import com.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @RequestMapping("/to_list")
    public String toList(Model model,MiaoshaUser miaoshaUser){
        model.addAttribute("user",miaoshaUser);
        return "goods_list";
    }

    @RequestMapping("/to_detail")
    public String detail(Model model, HttpServletResponse response,
                         @CookieValue(value = MiaoshaUserService.COOKI_NAME_TOKEN, required = false) String cookieToken,
                         @RequestParam(value = MiaoshaUserService.COOKI_NAME_TOKEN, required = false) String paramToken
    ){
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)){
            return "login";
        }
        String token = StringUtils.isEmpty(paramToken)?cookieToken:paramToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response,token);
        model.addAttribute("user",miaoshaUser);
        return "goods_list";
    }
}
