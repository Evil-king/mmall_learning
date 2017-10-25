package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

public class CartVO {

    private List<CartProductVO> cartProductVOList;
    private BigDecimal carTotalPrice;
    private Boolean allChecked;//是否已经勾选
    private String imageHost;

    public List<CartProductVO> getCartProductVOList() {
        return cartProductVOList;
    }

    public void setCartProductVOList(List<CartProductVO> cartProductVOList) {
        this.cartProductVOList = cartProductVOList;
    }

    public BigDecimal getCarTotalPrice() {
        return carTotalPrice;
    }

    public void setCarTotalPrice(BigDecimal carTotalPrice) {
        this.carTotalPrice = carTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
