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


@Controller
@RequestMapping(value = "/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     * @param session
     * @param shippingId
     * @return
     */
    @RequestMapping(value = "craete.do")
    @ResponseBody
    public ServerResponse craete(HttpSession session, Integer shippingId){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.createOrder(user.getId(),shippingId);
    }

    /**
     * 取消订单
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "cancle.do")
    @ResponseBody
    public ServerResponse cancle(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.cancle(user.getId(),orderNo);
    }

    /**
     * 获取购物车中已经选中的商品详情
     * @param session
     * @return
     */
    @RequestMapping(value = "get_order_cart_product.do")
    @ResponseBody
    public ServerResponse getOrdrCartProduct(HttpSession session){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrderCartProduct(user.getId());
    }

    /**
     * 订单详情
     * @param session
     * @param orderNo
     * @return
     */
    @RequestMapping(value = "deatil.do")
    @ResponseBody
    public ServerResponse deatil(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrerDetail(user.getId(),orderNo);
    }

    /**
     * 查看订单的List页
     * @param session
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(HttpSession session,
                                         @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return orderService.getOrderList(user.getId(),pageNum,pageSize);
    }

















    @RequestMapping(value = "pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
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
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());

        //非常重要，验证回调的正确性，是不是支付宝发的，并且

        params.remove("sign_type");
        try {
            boolean alipayRSCheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8", Configs.getSignType());
            if(!alipayRSCheckedV2){
                return ServerResponse.createByErrorMessage("非法请求,验证不通过");
            }
        } catch (AlipayApiException e) {
            logger.error("支付宝验证回调异常",e);
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
    public ServerResponse<Boolean> pay(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        ServerResponse serverResponse =  orderService.queryOrderPay(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }




}
