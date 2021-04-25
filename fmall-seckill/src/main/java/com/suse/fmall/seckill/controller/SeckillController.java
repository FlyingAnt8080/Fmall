package com.suse.fmall.seckill.controller;

import com.suse.common.utils.R;
import com.suse.fmall.seckill.service.SeckillService;
import com.suse.fmall.seckill.to.SeckillSKuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author LiuJing
 * @Date: 2021/04/21/ 23:30
 * @Description
 */
@Controller
public class SeckillController {
    @Autowired
    private SeckillService seckillService;

    /**
     * 返回当前时间可以参与的秒杀商品信息接口
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SeckillSKuRedisTo> sKuRedisTos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(sKuRedisTos);
    }

    /**
     * 根据商品的skuId获取秒杀信息接口
     * @param skuId
     * @return
     */
    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        SeckillSKuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
        return R.ok().setData(to);
    }

    /**
     * 秒杀接口
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping("/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model){
        String orderSn = seckillService.kill(killId,key,num);
        model.addAttribute("orderSn",orderSn);
        return "success";
    }
}
