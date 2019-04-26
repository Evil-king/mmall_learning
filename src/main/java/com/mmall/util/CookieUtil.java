package com.mmall.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hwq
 * @date 2019/04/25
 * <p>
 * CookieUtil工具类
 * </p>
 */
@Slf4j
public class CookieUtil {
    private static final String COOKIE_DOMAIN = ".happymmall.com";

    private static final String COOKIE_NAME = "mmall_login_token";

    public static String readLoginToken(HttpServletRequest request) {
        Cookie[] cks = request.getCookies();
        if (cks != null) {
            for (Cookie cookie : cks) {
                log.info("read cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                if (StringUtils.equals(cookie.getName(), COOKIE_NAME))
                    log.info("return cookieName:{},cookieValue:{}", cookie.getName(), cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public static void writerLoginToken(HttpServletResponse response, String token) {
        Cookie ck = new Cookie(COOKIE_NAME, token);
        ck.setDomain(COOKIE_DOMAIN);
        ck.setHttpOnly(true);
        ck.setPath("/");//代表设置在根目录
        //单位是秒。
        //如果这个maxage不设置的话，cookie就不会写入硬盘，而是写在内存。只在当前页面有效。
        ck.setMaxAge(60 * 60 * 24 * 365);
        log.info("write cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
        response.addCookie(ck);
    }


    public void delToken(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        if(cks != null){
            for(Cookie ck : cks){
                ck.setDomain(COOKIE_DOMAIN);
                ck.setPath("/");
                ck.setMaxAge(0);//设置成0，代表删除此cookie。
                log.info("del cookieName:{},cookieValue:{}",ck.getName(),ck.getValue());
                response.addCookie(ck);
                return;
            }
        }
    }
}
