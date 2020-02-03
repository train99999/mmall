package com.quan.service;

import com.quan.common.ServerResponse;
import com.quan.vo.CartVo;

public interface ICartService {
    ServerResponse<CartVo> list(Integer userId);
    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);
    ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count);
    ServerResponse<CartVo> delectProduct(Integer userId,String productIds);
    ServerResponse<CartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);
    ServerResponse<Integer> getCartProductCount(Integer userId);
}
