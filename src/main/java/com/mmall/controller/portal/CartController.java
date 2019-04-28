package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.CarService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisPoolUtil;
import com.mmall.util.RedisShardedPoolutil;
import com.mmall.vo.CartVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private CarService carService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<CartVO> lits(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse<CartVO> add(HttpServletRequest request, Integer count, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.add(user.getId(), productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse<CartVO> update(HttpServletRequest request, Integer count, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.update(user.getId(), productId, count);
    }

    @RequestMapping("deleted_product.do")
    @ResponseBody
    public ServerResponse<CartVO> deletedProduct(HttpServletRequest request, String productIds) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.deletedProduct(user.getId(), productIds);
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> selectAll(HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelectAll(HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    //单独选
    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<CartVO> Select(HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.selectOrUnSelect(user.getId(), productId, Const.Cart.CHECKED);
    }


    //单独反选
    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse<CartVO> unSelect(HttpServletRequest request, Integer productId) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.selectOrUnSelect(user.getId(), productId, Const.Cart.UN_CHECKED);
    }

    //查询当前用户的购物车里面的产品数量，如果一个产品有10个，那么数量就是10
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ServerResponse<Integer> getCartProductCount(HttpServletRequest request) {
        String loginToken = CookieUtil.readLoginToken(request);
        if (StringUtils.isEmpty(loginToken)) {
            return ServerResponse.createByErrorMessage("用户未登录,无法获取当前用户的信息");
        }
        String userStr = RedisShardedPoolutil.get(loginToken);
        User user = JsonUtil.string2Obj(userStr, User.class);
        return carService.getCartProductCount(user.getId());
    }

}
