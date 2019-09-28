package com.miaosha.service;

import com.miaosha.dao.MiaoshaUserDao;
import com.miaosha.domain.MiaoshaUser;
import com.miaosha.exception.GlobalException;
import com.miaosha.result.CodeMsg;
import com.miaosha.util.MD5Util;
import com.miaosha.util.UUIDutil;
import com.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
public class MiaoshaUserService {
    public static final String COOKI_NAME_TOKEN = "token";

    private static final int COOKI_TIME = 24*60*60;

    @Resource
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    public MiaoshaUser getById(Long id){
        return miaoshaUserDao.getById(id);
    }

    public Boolean login(HttpServletResponse response,LoginVo loginVo) {
        if (loginVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPassword = loginVo.getPassword();
        //判断手机号是否存在
        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));
        if (miaoshaUser == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbpassWord = miaoshaUser.getPassword();
        String saltDB = miaoshaUser.getSalt();
        String calcPassWord = MD5Util.formPassToDbPass(formPassword,saltDB);
        if (!calcPassWord.equals(dbpassWord)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //生成cookie
        addCookie(response,miaoshaUser);
        return true;
    }

    private void addCookie(HttpServletResponse response,MiaoshaUser miaoshaUser){
        String token = UUIDutil.uuid();
        redisTemplate.opsForValue().set("user"+token,miaoshaUser,30, TimeUnit.MINUTES);
        Cookie cookie =new Cookie(COOKI_NAME_TOKEN,token);
        cookie.setMaxAge(COOKI_TIME);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if (StringUtils.isEmpty(token)){
            return null;
        }
        MiaoshaUser miaoshaUser= (MiaoshaUser) redisTemplate.opsForValue().get("user"+token);
        if (miaoshaUser!=null){
            addCookie(response,miaoshaUser);
        }
        return miaoshaUser;
    }
}
