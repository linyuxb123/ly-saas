package com.ly.saas.wu.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.saas.wu.core.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper接口
 */
@Mapper
@Repository("wuUserMapper")
public interface UserMapper extends BaseMapper<User> {
}