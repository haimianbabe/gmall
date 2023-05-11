package com.wang.gmall.cart.config;

import com.mysql.cj.util.StringUtils;
import com.wang.common.to.UserInfoTo;
import com.wang.gmall.cart.constant.CartConstant;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 配置拦截器
 * 使用ThreadLocal进行用户身份判断
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        //如果用户已登录，获取session中的userId,设置永久登录
        //HttpSession session = request.getSession();
        userInfoTo.setUserId("10002");
        //如果cookie中有user-key则放入userInfoTo
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie:cookies){
            if(cookie.getName().equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                userInfoTo.setIsTempUser(true);
                userInfoTo.setUserKey(cookie.getValue());
            }
        }
        //如果没有user-key则生成
        if (StringUtils.isNullOrEmpty(userInfoTo.getUserKey())){
            String userKey = UUID.randomUUID().toString();
            userInfoTo.setUserKey(userKey);
            userInfoTo.setIsTempUser(false);
        }
        //将userInfoTo放入threadlocal中
        threadLocal.set(userInfoTo);
        //放行返回true
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        //若cookie没有user-key则放入
        if (!userInfoTo.getIsTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserKey());
        //cookie.setDomain("");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
