package com.mmall.service;

import com.mmall.common.ServerResponse;
import com.mmall.vo.CartVO;

public interface CarService {
    ServerResponse<CartVO> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVO> update(Integer userId,Integer productId,Integer count);

    ServerResponse<CartVO> deletedProduct(Integer userId,String productIds);

    ServerResponse<CartVO> list(Integer userId);

    ServerResponse<CartVO> selectOrUnSelect(Integer userId,Integer productId,Integer checked);

    ServerResponse<Integer> getCartProductCount(Integer userId);
}
