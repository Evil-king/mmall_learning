package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.OrderService;
import com.mmall.service.UserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;

    @RequestMapping("list.do")
    public ServerResponse<PageInfo> orderList(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return orderService.manageList(pageNum,pageSize);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }

    @RequestMapping("detail.do")
    public ServerResponse<OrderVo> manageDetail(HttpSession session, Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return orderService.manageDetail(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }

    @RequestMapping("search.do")
    public ServerResponse<PageInfo> orderSearch(HttpSession session,
                                              @RequestParam(value = "pageNum",defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return orderService.manageSearch(pageNum,pageSize,orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }

    @RequestMapping("send_goods.do")
    public ServerResponse<String> orderSendsGoods(HttpSession session,Long orderNo){
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),"用户未登录，请登录");
        }
        //校验一下是否是管理员
        if(userService.checkAdminRole(user).isSuccess()){
            return orderService.manageSendGoods(orderNo);
        }else{
            return ServerResponse.createByErrorMessage("无权限操作,需要管理员权限");
        }

    }


}
