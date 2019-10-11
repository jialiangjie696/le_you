package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utlis.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;



    @Autowired
    private SpecParamMapper specParamMapper;





    public List<SpecGroupDTO> findSpecGroupBycategoryId(Long id) {

        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(id);
        List<SpecGroup> specGroupList = specGroupMapper.select(specGroup);

        if (CollectionUtils.isEmpty(specGroupList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(specGroupList,SpecGroupDTO.class);
    }


    /**
     * 根据规格组或分类id查询规格参数
     *
     *     查询时有可能根据规格组 或者分类id,但一定要有一个
     * @param cid  分类id
     * @param gid   规格组id
     * @param searching 是否作为搜索
     * @return
     */
    public List<SpecParamDTO> findSpecParamByCategoryIdOrGroupId(Long cid, Long gid, Boolean searching) {



        if (cid==null&&gid==null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }


        SpecParam specParam = new SpecParam();
        specParam.setCid(cid);
        specParam.setGroupId(gid);
        specParam.setSearching(searching);
        List<SpecParam> specParamList = specParamMapper.select(specParam);

        if (CollectionUtils.isEmpty(specParamList)){

            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }

        return BeanHelper.copyWithCollection(specParamList,SpecParamDTO.class);
    }
}
