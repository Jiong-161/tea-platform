package com.tea.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 收货地址请求DTO
 */
@Data
public class AddressDTO {

    /** 收货人姓名 */
    @NotBlank(message = "收货人不能为空")
    private String receiverName;

    /** 收货人手机号 */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 省份 */
    @NotBlank(message = "省份不能为空")
    private String province;

    /** 城市 */
    @NotBlank(message = "城市不能为空")
    private String city;

    /** 区县 */
    @NotBlank(message = "区县不能为空")
    private String district;

    /** 详细地址 */
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /** 是否默认地址：0-非默认，1-默认，默认值为0 */
    private Integer isDefault = 0;
}