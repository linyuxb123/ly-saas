package com.ly.saas.chun.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.chun.core.entity.Dept;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 部门Mapper接口
 */
@Mapper
@Repository("chunDeptMapper")
public interface DeptMapper extends BaseMapper<Dept> {
}