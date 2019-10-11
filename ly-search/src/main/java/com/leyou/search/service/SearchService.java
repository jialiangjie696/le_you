package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;

import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.common.utlis.JsonUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.*;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.pojo.Goods;
import org.apache.commons.lang3.StringUtils;

import org.elasticsearch.index.query.BoolQueryBuilder;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.reflect.generics.tree.ReturnType;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private ElasticsearchTemplate esTemplate;



    public Goods buildGoods(SpuDTO spuDTO) {
        /**
         *准备all数据
         */
//        关键字搜索的位置是商品名称+品牌名称+分类名称
        String all = spuDTO.getName()+spuDTO.getBrandName()+spuDTO.getCategoryName();


        /**
         *准备price数据 来自于这个spu的所有sku中的price
         */
//        根据spuId查询sku
        List<SkuDTO> skuDTOList = itemClient.findSkuDTOListBySpuId(spuDTO.getId());
//        Set<Long>  price = new HashSet<Long> ();
//        for (SkuDTO skuDTO : skuDTOList) {
//            price.add(skuDTO.getPrice());
//        }
        Set<Long> price = skuDTOList.stream().//变成流
                map(SkuDTO::getPrice). //获取每个对象的price的值
                collect(Collectors.toSet()); //把获取到的值收集到set集合中
        /**
         *准备skus数据
         * skus:[
         *        {"id":2, "title":"xx", "price":299900, "image":""},
         *        {"id":3, "title":"xxx", "price":399900, "image":""}
         *  ],
         */
        List<Map> skuMapList = new ArrayList<>(); //准备一个集合用来存放skuMap
        Map<String,Object> skuMap ;
        for (SkuDTO skuDTO : skuDTOList) {  //想索引库中放入sku数据只要4个属性
            skuMap = new HashMap<String,Object>();
//            {"id":2, "title":"xx", "price":299900, "image":""},
            skuMap.put("id", skuDTO.getId());
            skuMap.put("title", skuDTO.getTitle());
            skuMap.put("price", skuDTO.getPrice());
            skuMap.put("image", skuDTO.getImages().split(",")[0]);  //skuDTO.getImages()有多张图片，我们只要第一张图片http://image.leyou.com/Gm5JQScmPcnA7skpJAk8.jpg,http://image.leyou.com/AHe78YNkSKBrzyBYm7PC.jpg
            skuMapList.add(skuMap);
        }
        /**
         *准备specs数据
         */
//        根据spuId查询SPUDetail
        SpuDetailDTO spuDetail = itemClient.findSpuDetailBySpuId(spuDTO.getId());
        String specialSpec = spuDetail.getSpecialSpec();//{"4":["白色","金色","玫瑰金"],"12":["3GB"],"13":["16GB"]}
//        把字符串转成对象
        Map<Long, List<String>> specialSpecMap = JsonUtils.nativeRead(specialSpec, new TypeReference<Map<Long, List<String>>>() {});
        String genericSpec = spuDetail.getGenericSpec();//{"1":"华为","2":"G9青春版（全网通版）"}
//        把字符串转成对象
        Map<Long, Object> genericSpecMap = JsonUtils.toMap(genericSpec, Long.class, Object.class);
//        查询规格参数spec_param
//        查询此分类下的需要用来搜索的规格参数
        List<SpecParamDTO> specParamList = itemClient.findSpecParamByCategoryIdOrGroupId(spuDTO.getCid3(), null, true);

        Map<String ,Object> specsMap = new HashMap<>();//存储的数specialSpecMap和genericSpecMap两个map的组合
        for (SpecParamDTO specParam : specParamList) {
            String key = specParam.getName(); //规格参数的名称作为key
            Object value =null;
            if(specParam.getGeneric()){ //通用的规格
                value = genericSpecMap.get(specParam.getId());  //原始map中的value最为value
            }else{
                value = specialSpecMap.get(specParam.getId());
            }
            if(specParam.getNumeric()){
//                处理区间范围  11:2.1 ---->CPU频率：2.0-2.5
                value = chooseSegment(value, specParam);
            }
            specsMap.put(key,value);
        }

        /**
         * 构建Goods对象
         */
        Goods goods = new Goods();

        goods.setAll(all); //商品名称+品牌名称+分类名称
        goods.setPrice(price);  // 放入的是所有的skus的price
        goods.setSkus(JsonUtils.toString(skuMapList));  //放的是这个spu下的sku的json字符串[{id:1,title:,"",price:120.00,image:""},{id:1,title:,"",price:120.00,image:""},{id:1,title:,"",price:120.00,image:""}]
        goods.setSpecs(specsMap);



        goods.setSubTitle(spuDTO.getSubTitle());
        goods.setId(spuDTO.getId());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setBrandId(spuDTO.getBrandId());

        return goods;

    }



    private String chooseSegment(Object value, SpecParamDTO p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 根据关键字查询
     * @param searchRequest
     * @return
     */
    public PageResult<GoodsDTO> findByPage(SearchRequest searchRequest) {
//        只要三个属性：id  subTitle skus
//        查询的域名：all 并且要分词  term   小米手机
//        需要分页
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
//        使用SpringDataElastic结合原生的查询方式
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder baseQuery = buildBaseQuery(searchRequest);
        nativeSearchQueryBuilder.withQuery(baseQuery);

//        过滤显示的字段 id subTitle skus
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","subTitle","skus"},null));

//        注意分页是页码需要减一，因为SpringData页码的起始值是0  当前页码（从0开始）  每页显示的条数
        PageRequest pageRequest = PageRequest.of(searchRequest.getPage()-1, searchRequest.getSize());
        nativeSearchQueryBuilder.withPageable(pageRequest);

        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
        List<Goods> goodsList = aggregatedPage.getContent(); //当前页的结果
        Long total = aggregatedPage.getTotalElements(); //总条数
        Integer totalPage = aggregatedPage.getTotalPages();//总页数

        List<GoodsDTO> goodsDTOS = BeanHelper.copyWithCollection(goodsList, GoodsDTO.class);

        return new PageResult<GoodsDTO>(total,totalPage,goodsDTOS);

    }

    //    获取页面上面过滤条件的方法
    public Map<String, List<?>> queryFilter(SearchRequest searchRequest) {

        Map<String, List<?>> filterMap = new HashMap<>(); //用来返回的结果

//   有条件的通过聚合  分类和品牌
        String key = searchRequest.getKey();
        if(StringUtils.isBlank(key)){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
//        使用SpringDataElastic结合原生的查询方式
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder baseQuery = buildBaseQuery(searchRequest);
        nativeSearchQueryBuilder.withQuery(baseQuery);
//        注意分页是页码需要减一，因为SpringData页码的起始值是0  当前页码（从0开始）  每页显示的条数
        PageRequest pageRequest = PageRequest.of(0, 1);  //api规定 size不能小于1
        nativeSearchQueryBuilder.withPageable(pageRequest);
//        构建聚合分类和品牌两个条件
//        添加分类的聚合条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("categoryAgg").field("categoryId"));
//        添加品牌的聚合条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brandAgg").field("brandId").size(20));//默认只显示10个，调整成20个


        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);

        Aggregations aggregations = aggregatedPage.getAggregations();
//        获取分类的聚合结果
        Terms categoryTerms = aggregations.get("categoryAgg");
        List<? extends Terms.Bucket> categoryBuckets = categoryTerms.getBuckets();
        List<Long> categoryIds = categoryBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
//        List categoryIds = new ArrayList();
//        for (Terms.Bucket categoryBucket : categoryBuckets) {
//            long longValue = categoryBucket.getKeyAsNumber().longValue();
//            categoryIds.add(longValue);
//        }
//        根据categoryIds查询对象 LyBaseMapper中有selectByIdList
        if(!CollectionUtils.isEmpty(categoryIds)){
            List<CategoryDTO> categoryList = itemClient.findCategoryByCids(categoryIds);
            filterMap.put("分类",categoryList);
//        根据第一个分类查询用来搜索的规格参数
            Long cid = categoryIds.get(0);
            List<SpecParamDTO> specParamList = itemClient.findSpecParamByCategoryIdOrGroupId(cid, null, true);
//        根据规格参数构建聚合条件
            filterMap = buildSpecFilterMap(filterMap,searchRequest,specParamList);

        }
        //        获取品牌的聚合结果
        Terms brandTerms = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> brandBuckets = brandTerms.getBuckets();
        List<Long> brandIds = brandBuckets.stream().map(Terms.Bucket::getKeyAsNumber).map(Number::longValue).collect(Collectors.toList());
//        根据brandIds查询品牌对象集合 LyBaseMapper中有selectByIdList
        List<BrandDTO> brandList = itemClient.findBrandByBrandIds(brandIds);
        filterMap.put("品牌",brandList);


        return filterMap;
    }

    private Map<String, List<?>> buildSpecFilterMap(Map<String, List<?>> filterMap,SearchRequest searchRequest,List<SpecParamDTO> specParamList) {
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        QueryBuilder baseQuery = buildBaseQuery(searchRequest);
        nativeSearchQueryBuilder.withQuery(baseQuery);
        PageRequest pageRequest = PageRequest.of(0, 1);  //api规定 size不能小于1
        nativeSearchQueryBuilder.withPageable(pageRequest);
//        先构建聚合条件
        specParamList.forEach(specParam -> {
            String name  = specParam.getName();
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms(name+"Agg").field("specs."+name));
        });
//        执行查询
        AggregatedPage<Goods> aggregatedPage = esTemplate.queryForPage(nativeSearchQueryBuilder.build(), Goods.class);
        Aggregations aggregations = aggregatedPage.getAggregations();

        //      获取聚合的结果 放入到 filterMap
        specParamList.forEach(specParam -> {
            String name  = specParam.getName();
            Terms specTerms = aggregations.get(name + "Agg");
            List<? extends Terms.Bucket> specBuckets = specTerms.getBuckets();
            List<String> specValues = specBuckets.stream() //集合变成流
                    .map(Terms.Bucket::getKeyAsString) //获取每个key值
                    .filter(StringUtils::isNotBlank) //只取不为空的key值
                    .collect(Collectors.toList());  //收集结果
            filterMap.put(name,specValues);
        });

        return     filterMap;
    }

//    构建基本查询的方法

    private QueryBuilder buildBaseQuery(SearchRequest searchRequest){
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        QueryBuilder baseQuery = QueryBuilders.matchQuery("all", searchRequest.getKey());

        boolQuery.must(baseQuery);

        Map<String, Object> filter = searchRequest.getFilter();
//        {"品牌":13234,"分类"：76，"CPU核数":"四核"}
        ;
        for (String key : filter.keySet()) {
            if(key.equals("品牌")){
                boolQuery.filter(QueryBuilders.termQuery("brandId",filter.get("品牌")));
            }else if(key.equals("分类")){
                boolQuery.filter(QueryBuilders.termQuery("categoryId",filter.get("分类")));
            }else{
                boolQuery.filter(QueryBuilders.termQuery("specs."+key,filter.get(key)));
            }
        }

        return boolQuery;
    }


}
