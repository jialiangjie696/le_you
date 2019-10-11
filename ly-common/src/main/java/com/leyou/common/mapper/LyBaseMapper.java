package com.leyou.common.mapper;


import tk.mybatis.mapper.additional.idlist.IdListMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;

public interface LyBaseMapper<T> extends Mapper<T>, IdsMapper<T>, IdListMapper<T,Long> {


}
