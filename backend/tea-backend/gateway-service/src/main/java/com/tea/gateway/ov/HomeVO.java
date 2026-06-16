package com.tea.gateway.ov;

import lombok.Data;
import java.util.List;

@Data
public class HomeVO {
    private List<Object> articles;    // 文章列表
    private List<Object> exhibitions; // 展览列表
    private List<Object> products;   // 商品列表
}