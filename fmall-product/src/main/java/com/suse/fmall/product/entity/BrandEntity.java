package com.suse.fmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.suse.common.valid.AddGroup;
import com.suse.common.valid.ListValue;
import com.suse.common.valid.UpdateGroup;
import com.suse.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author liujing
 * @email 18384623913@163.com
 * @date 2021-02-17 21:02:27
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 * groups JSR303的分组校验功能
	 */
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@NotNull(message = "修改必须指定id",groups = {UpdateGroup.class})
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@URL(message = "logo必须是一个合法的url地址",groups = {AddGroup.class,UpdateGroup.class})
	@NotBlank(groups = {AddGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = {AddGroup.class,UpdateStatusGroup.class})
	@ListValue(values = {0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 * @Pattern 特殊校验
	 */
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索首字母必须是一个字母",groups = {AddGroup.class,UpdateGroup.class})
	@NotEmpty(groups = {AddGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序必须大于等于0",groups = {AddGroup.class,UpdateGroup.class})
	@NotNull(groups = {AddGroup.class})
	private Integer sort;

}
