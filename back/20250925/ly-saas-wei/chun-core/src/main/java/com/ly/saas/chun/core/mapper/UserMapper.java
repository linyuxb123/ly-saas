package com.ly.saas.chun.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.chun.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository("chunUserMapper")
public interface UserMapper extends BaseMapper<User> {
}