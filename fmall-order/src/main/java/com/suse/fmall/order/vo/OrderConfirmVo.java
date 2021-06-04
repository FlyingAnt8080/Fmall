package com.suse.fmall.order.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author LiuJing
 * @Date: 2021/04/14/ 22:24
 * @Description 订单确认页需要的数据
 */
@ToString
public class OrderConfirmVo {
   //收货地址
   @Getter
   @Setter
   private List<MemberAddressVo> address;
   //所有选中的购物项
   @Getter
   @Setter
   private List<OrderItemVo> items;
   //发票记录..
   //优惠券信息
   @Getter
   @Setter
   private Integer integration;
   @Getter
   @Setter
   private Map<Long,Boolean> stocks;

   //TODO 防重令牌
   @Getter
   @Setter
   private String orderToken;

   public Integer getCount(){
       Integer count = 0;
       if (!CollectionUtils.isEmpty(items)){

           for (OrderItemVo item : items) {
             count += item.getCount();
           }
       }
       return count;
   }

   public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal(0);
        if (!CollectionUtils.isEmpty(items)){
            for (OrderItemVo item : items) {
              BigDecimal price =   item.getPrice().multiply(new BigDecimal(item.getCount()));
              sum = sum.add(price);
            }
        }
        return sum;
    }

    public BigDecimal getPayPrice() {
      return getTotal();
    }
}
