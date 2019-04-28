package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolutil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;


@Slf4j
@Controller
@RequestMapping(value = "/order/")
public class OrderController {


    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param request
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "craete.do")
    @ResponseBody
    public ServerResponse craete(HttpServletRequest request, Integer shippingId){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return orderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 取消订单
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "cancle.do")
    @ResponseBody
    public ServerResponse cancle(HttpServletRequest request, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return orderService.cancle(user.getId(),orderNo);
    }

    /**
     * 获取购物车中已经选中的商品详情
     * @param request
     * @return
     */
    @RequestMapping(value = "get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrdrCartProduct(HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return orderService.getOrderCartProduct(user.getId());
    }

    /**
     * 订单详情
     * @param request
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "deatil.do")
    @ResponseBody
    public ServerResponse deatil(HttpServletRequest request,Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return orderService.getOrerDetail(user.getId(),orderNo);
    }

    /**
     * 查看订单的List页
     * @param request
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpServletRequest request,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

















    @RequestMapping(value = "pay.do")
    @ResponseBody
    public ServerResponse pay(Long orderNo, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        String path = request.getSession().getServletContext().getRealPath("upload");
        return orderService.pay(orderNo,user.getId(),path);
    }

    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap();
        for(Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();){
            String name = (String) iterator.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i=0;i<values.length;i++){
                valueStr =(i==values.length-1)?valueStr + values[i]:valueStr+values[i]+",";
            }
            params.put("name",valueStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要，验证回调的正确性，是不是支付宝发的，并且

        params.remove("sign_type");
        try {
            boolean alipayRSCheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
            if(!alipayRSCheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }
        //todo 验证各种数据

        //
        ServerResponse serverResponse = orderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }


    @RequestMapping(value = "query_order_pay_status.do")
    @ResponseBody
    public ServerResponse<Boolean> pay(HttpServletRequest request, Long orderNo){
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        ServerResponse serverResponse =  orderService.queryOrderPay(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }




}
