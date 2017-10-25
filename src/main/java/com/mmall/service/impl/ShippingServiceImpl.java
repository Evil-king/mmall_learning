package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ServerResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.ShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ShippingServiceImpl implements ShippingService{

    @Autowired
    private ShippingMapper shippingMapper;


    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippongId",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");
    }

    public ServerResponse<String> del(Integer userId, Integer shippingId){
        int resultCount = shippingMapper.deletedByShippingIdUserId(userId,shippingId);
        if(resultCount > 0 ){
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse<String> update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByShipping(shipping);
        if(resultCount > 0 ){
            return ServerResponse.createBySuccessMessage("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址成功");
    }

    public ServerResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null ){
            return ServerResponse.createByErrorMessage("无法查证到改地址");
        }
        return ServerResponse.createBySuccess("更新地址成功",shipping);
    }

    public ServerResponse<PageInfo> list(Integer userId,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo payInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(payInfo);
    }

    /**
     * cl_crosshairthickness 6 ] cl_crosshairstyle 3 ] cl_crosshairsize 20 ] cl_crosshairgap 11
     * @param args
     */
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();

    }
}
