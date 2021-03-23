package com.suse.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author LiuJing
 * @Date: 2021/03/06/ 11:23
 * @Description
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
    private Set<Integer> set = new HashSet<>();
    //初始化
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] values = constraintAnnotation.values();
        for(int value : values){
            set.add(value);
        }
    }
    //判断是否校验成功

    /**
     *
     * @param value 需要效验的值
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (set.contains(value))return true;
        else return false;
    }
}
