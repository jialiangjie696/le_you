package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.entity.Item;
import org.springframework.stereotype.Service;

@Service
public class ItemService {


    public Item save(Item item){
        if (item.getPrice()==null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        item.setId(100);
        return item;
    }

}
