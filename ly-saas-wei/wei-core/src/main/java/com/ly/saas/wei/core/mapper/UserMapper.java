package com.ly.saas.wei.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.wei.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository("weiUserMapper")
public interface UserMapper extends BaseMapper<User> {
}