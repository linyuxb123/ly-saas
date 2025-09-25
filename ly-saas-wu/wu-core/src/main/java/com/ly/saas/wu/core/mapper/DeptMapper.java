package com.ly.saas.wu.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.wu.core.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 部门Mapper接口
 */
@Mapper
@Repository("wuDeptMapper")
public interface DeptMapper extends BaseMapper<Dept> {
}