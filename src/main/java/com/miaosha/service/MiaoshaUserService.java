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
        //取缓存
        MiaoshaUser user = (MiaoshaUser)redisTemplate.opsForValue().get("id" + id);
        if (user != null){
            return user;
        }

        //取数据库
        user = miaoshaUserDao.getById(id);
        if (user!=null){
            redisTemplate.opsForValue().set("id" + id ,user);
        }
        return user;
    }


    public boolean updatePassword(String token, long id, String formPass){
        //取user
        MiaoshaUser user = getById(id);
        if (user == null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //更新数据库
        MiaoshaUser toBeUpdate = new MiaoshaUser();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDbPass(formPass,user.getSalt()));
        miaoshaUserDao.update(toBeUpdate);

        //处理缓存
        redisTemplate.delete("id" + id);
        user.setPassword(toBeUpdate.getPassword());
        redisTemplate.opsForValue().set("user"+token,user,30, TimeUnit.MINUTES);

        return true;
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
        String token = UUIDutil.uuid();
        addCookie(response,token,miaoshaUser);
        return true;
    }

    private void addCookie(HttpServletResponse response,String token,MiaoshaUser miaoshaUser){
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
            addCookie(response,token,miaoshaUser);
        }
        return miaoshaUser;
    }
}
