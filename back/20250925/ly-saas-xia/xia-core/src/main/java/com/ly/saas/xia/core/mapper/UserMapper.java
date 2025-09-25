package com.ly.saas.xia.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.xia.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository("xiaUserMapper")
public interface UserMapper extends BaseMapper<User> {
}