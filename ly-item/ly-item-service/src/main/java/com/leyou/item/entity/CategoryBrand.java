package com.leyou.item.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;

@Table(name="tb_category_brand")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBrand {

    private Long categoryId;
    private Long brandId;
}
