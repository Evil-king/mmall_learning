package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.CarService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVO;
import com.mmall.vo.CartVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements CarService{

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse<CartVO> add(Integer userId,Integer productId,Integer count){

        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),ResponseCode.ILLEGA_ARGUMENT.getDesc());
        }


        Cart cart = cartMapper.selectCartByUserIdProductId(productId,userId);
        if(cart == null){
            //这个产品不在购物车中，需要新增这个产品记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insert(cartItem);
        }else {
            //这个产品已经在购物车里了
            //如果产品已存在，数量增加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKey(cart);
        }
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    public ServerResponse<CartVO> update(Integer userId,Integer productId,Integer count){
        if(productId == null || count == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),ResponseCode.ILLEGA_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(productId,userId);
        if(cart != null){
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKey(cart);
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }


    public ServerResponse<CartVO> deletedProduct(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(CollectionUtils.isNotEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGA_ARGUMENT.getCode(),ResponseCode.ILLEGA_ARGUMENT.getDesc());
        }
          cartMapper.deleteByUserIdProductIds(userId,productList);
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    public ServerResponse<CartVO> list(Integer userId){
        CartVO cartVO = this.getCartVOLimit(userId);
        return ServerResponse.createBySuccess(cartVO);
    }

    public ServerResponse<CartVO> selectOrUnSelect(Integer userId,Integer pridoctId,Integer checked){
          cartMapper.checkedOrUncheckedProduct(userId,checked,pridoctId);
          return this.list(userId);
    }

    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId == null){
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }


    /**
     * 购物车计算
     * @param userId
     * @return
     */
    private CartVO getCartVOLimit(Integer userId){
        CartVO  cartVo = new CartVO();
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVO>  cartProductVOList = Lists.newArrayList();

        //初始化购物车总价
        BigDecimal carTotalPrice = new BigDecimal("0");

        if(CollectionUtils.isNotEmpty(cartList)){
            for(Cart carItem : cartList){
                CartProductVO cartProductVO = new CartProductVO();
                cartProductVO.setId(carItem.getId());
                cartProductVO.setUserId(userId);
                cartProductVO.setProductId(carItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(carItem.getProductId());
                if(product != null){
                    cartProductVO.setProductPrice(product.getPrice());
                    cartProductVO.setProductMainImage(product.getMainImage());
                    cartProductVO.setProductName(product.getName());
                    cartProductVO.setProductSubtitle(product.getSubtitle());
                    cartProductVO.setProductStatus(product.getStatus());
                    cartProductVO.setProductStock(product.getStock());

                    //判断库存
                    int buyLimitCount = 0;
                    if(product.getStock() >= cartProductVO.getQuantity()){
                        //库存充足的时候
                        buyLimitCount = carItem.getQuantity();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        buyLimitCount = product.getStock();
                        cartProductVO.setLimitQuantity(Const.Cart.LIMIT_NUM_FILE);
                        //购物车中更新有效库存
                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(carItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }
                    cartProductVO.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVO.setProductTotalPrice(BigDecimalUtil
                            .mul(product.getPrice().doubleValue(),cartProductVO.getQuantity().doubleValue()));
                    cartProductVO.setProductChecked(carItem.getChecked());
                }
                if(carItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个的购物车总价中
                    carTotalPrice = BigDecimalUtil.add(carTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
                }
                cartProductVOList.add(cartProductVO);
            }
        }
        cartVo.setCarTotalPrice(carTotalPrice);
        cartVo.setCartProductVOList(cartProductVOList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }



    private boolean getAllCheckedStatus(Integer userId){
        if(userId == null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }















}
