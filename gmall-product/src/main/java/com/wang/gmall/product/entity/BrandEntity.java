package com.wang.gmall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * 品牌
 * 
 * @author wyp
 * @email sunlightcs@gmail.com
 * @date 2023-04-10 14:17:31
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotNull(message = "品牌名不能为空")
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotNull(message = "品牌logo地址不能为空")
	@URL(message = "logo不合法")
	private String logo;
	/**
	 * 介绍
	 */
	@NotNull(message = "介绍不能为空")
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "状态不能为空")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotNull(message = "品牌名称不为空")
	@Pattern(regexp = "^[a-zA-Z]$",message = "检索第一位必须为字母")
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空")
	@Min(value = 0,message = "排序必须大于等于0")
	private Integer sort;

}
