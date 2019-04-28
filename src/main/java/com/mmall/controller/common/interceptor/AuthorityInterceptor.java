package com.mmall.controller.common.interceptor;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolutil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hwq
 * @date 2019/04/28
 * <p>
 *     拦截器
 * </p>
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        log.info("preHandle");
        //获取Controller的方法名
        HandlerMethod handlerMethod = (HandlerMethod)handler;
        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        //遍历paramMap集合
        Iterator it = paramMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry entry= (Map.Entry) it.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;
            Object object = entry.getValue();
            if(object instanceof String[]){
                String[] str = (String[]) object;
                mapValue = Arrays.toString(str);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }

        User user = null;

        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            String userStr = RedisShardedPoolutil.get(loginToken);
            user = JsonUtil.string2Obj(userStr, User.class);
        }

        if(user == null || (user.getRole() != Const.Role.ROLE_ADMIN)){
            //返回false，不会调用controller里面的方法
            httpServletResponse.reset();//这里要添加reset(),否则会报异常
            httpServletResponse.setCharacterEncoding("UTF-8");//要设置编码，否则会乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型

            PrintWriter out = httpServletResponse.getWriter();

            if(user == null){
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器,用户为登陆")));
            } else {
                out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器,用户无权操作")));
            }
            out.flush();
            out.close();

            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
