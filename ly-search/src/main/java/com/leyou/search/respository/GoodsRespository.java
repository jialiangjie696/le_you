package com.leyou.search.respository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface GoodsRespository extends ElasticsearchCrudRepository<Goods,Long> {
}
